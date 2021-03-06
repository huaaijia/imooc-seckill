package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huaaijia on 2016/10/13.
 *
 * @Component
 * @Service
 * @Dao
 * @Controller
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //注入Service依赖: @Resource, @Autowired, @Inject
    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    //md5盐值字符串，用于混淆md5
    private final String salt = "89324ujo9093742j0(()**&*023jijffw81";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    /**
     * 通过redis优化暴露Url接口
     * @param seckillId
     * @return
     */
    public Exposer exportSeckillUrl(long seckillId) {
        //优化点：缓存优化
        /**
         * get from cache
         * if null
         *     get db
         * else
         *     put cache
         */
        //1:访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if(seckill==null){
            //2:访问数据库
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            }else{
                //3:放入redis
                redisDao.putSeckill(seckill);
            }
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //当前系统时间
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckill.getSeckillId(), nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        //转化特定字符串的过程：不可逆
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);

    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

//    @Transactional
//    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws RepeatKillException, SeckillCloseException, SeckillException {
//        if (md5 == null || !md5.equals(getMD5(seckillId))) {
//            throw new SeckillException("seckill data rewrite");
//        }
//        //执行秒杀逻辑：减库存+记录购买行为
//        try {
//            int updateCount = seckillDao.reduceNumber(seckillId, new Date());
//            if (updateCount <= 0) {
//                //没有更新到记录,意味着秒杀结束
//                throw new SeckillCloseException("seckill is closed");
//            } else {
//                //记录购买行为
//                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone, 1);
//                //唯一：seckillId, userPhone
//                if(insertCount <= 0){
//                    //重复秒杀
//                    throw new RepeatKillException("seckill repeated");
//                }else {
//                    //秒杀成功
//                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
//                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
//                }
//            }
//        } catch (SeckillCloseException e) {
//            throw e;
//        } catch (RepeatKillException e) {
//            throw e;
//        } catch (Exception e){
//            logger.error(e.getMessage(), e);
//            throw new SeckillException("seckill inner error:"+e.getMessage());
//        }
//    }

    /**
     * 使用注解控制事务方法的优点：
     * 1：开发团队达成一致约定，明确标注事务方法的编程风格
     * 2：保证事务方法的执行时间尽可能短！！！不要穿插其他的网络操作：缓存、RPC、HTTP、Redis、Memcache等请求，或者剥离到事务方法外面
     *      保持事务中的代码是纯粹干净的数据库sql处理方法
     * 3：不是所有的方法都需要事务，如只有一条修改操作，只读操作都不需要，两条以上修改操作同时完成时需要，或者select for update也需要
     *      仔细了解Mysql行级锁！！！
     */
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws RepeatKillException, SeckillCloseException, SeckillException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑：减库存+记录购买行为
        try {
            //记录购买行为
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone, 1);
            //唯一：seckillId, userPhone
            if(insertCount <= 0){
                //重复秒杀
                throw new RepeatKillException("seckill repeated");
            }else {
                int updateCount = seckillDao.reduceNumber(seckillId, new Date());
                if (updateCount <= 0) {
                    //没有更新到记录,意味着秒杀结束 rollback
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //秒杀成功 commit
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e) {
            throw e;
        } catch (RepeatKillException e) {
            throw e;
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            throw new SeckillException("seckill inner error:"+e.getMessage());
        }
    }

    /**
     * 这里已经不需要异常了，之前的异常为了告诉Spring的声明式事务进行commit和rollback
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        if(md5 == null || !md5.equals(getMD5(seckillId))){
            return new SeckillExecution(seckillId, SeckillStatEnum.DATA_REWRITE);
        }
        Date killTime = new Date();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);

        //执行存储过程，result被赋值
        try {
            seckillDao.killByProcedure(map);
            //获取result
            int result = MapUtils.getInteger(map, "result", -2);
            if(result == 1){
                SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS);
            }else {
                return new SeckillExecution(seckillId, SeckillStatEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
        }
    }
}
