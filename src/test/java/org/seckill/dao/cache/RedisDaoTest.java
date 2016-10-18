package org.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by huaaijia on 2016/10/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {

    private long id = 1001;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SeckillDao seckillDao;

    @Test
    public void testGetSeckill() throws Exception {

    }

    @Test
    public void testPutSeckill() throws Exception {

    }

    @Test
    public void testSeckill() throws Exception{
        //get and set
        Seckill seckill = redisDao.getSeckill(id);
        if(seckill == null){
            seckill = seckillDao.queryById(id);
            if(seckill != null){
                String result = redisDao.putSeckill(seckill);
                System.out.println(result);
                seckill = redisDao.getSeckill(id);
                System.out.println(seckill);
                /**
                 * OK

                 Disconnected from the target VM, address: '127.0.0.1:58646', transport: 'socket'
                 Seckill{seckillId=1001, name='500元秒杀ipad2', number=186, startTime=Wed Oct 12 00:00:00 CST 2016, endTime=Mon Oct 31 00:00:00 CST 2016, createTime=Thu Oct 13 15:53:49 CST 2016}

                 */
            }
        }
    }
}