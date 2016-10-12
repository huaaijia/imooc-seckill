package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by huaaijia on 2016/10/12.
 * 配置spring和junit整合, junit启动时加载springIOC容器
 * spring-test, junit 这两个依赖实现上述描述
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    //注入Dao实现类依赖
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void testQueryById() throws Exception {
        long id = 1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
        /**
         * 1000元秒杀iphone6
         Seckill{seckillId=1000,
         name='1000元秒杀iphone6',
         number=100,
         startTime=Wed Oct 12 00:00:00 CST 2016,
         endtime=Thu Oct 13 00:00:00 CST 2016,
         createTime=Tue Oct 11 17:49:52 CST 2016}
         */
    }

    @Test
    public void testQueryAll() throws Exception {
        List<Seckill> seckills = seckillDao.queryAll(0, 100);
        for(Seckill seckill : seckills){
            System.out.println(seckill);
        }
        /**
         * Caused by: org.apache.ibatis.binding.BindingException: Parameter 'offset' not found. Available parameters are [1, 0, param1, param2]
         * List<Seckill> queryAll(int offset, int limit);
         * java没有保存形参的记录：queryAll(int offset, int limit) -> quaryAll(arg0, arg1)
         * 以至于转换到xml对应的时候，多参数情况下会找不到对应关系
         *
         * 需要添加@Param注解，将形参赋名字
         * List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
         *
         * 结果
         * Seckill{seckillId=1000, name='1000元秒杀iphone6', number=100, startTime=Wed Oct 12 00:00:00 CST 2016, endtime=Thu Oct 13 00:00:00 CST 2016, createTime=Tue Oct 11 17:49:52 CST 2016}
         Seckill{seckillId=1001, name='500元秒杀ipad2', number=200, startTime=Wed Oct 12 00:00:00 CST 2016, endtime=Thu Oct 13 00:00:00 CST 2016, createTime=Tue Oct 11 17:49:52 CST 2016}
         Seckill{seckillId=1002, name='300元秒杀小米4', number=300, startTime=Wed Oct 12 00:00:00 CST 2016, endtime=Thu Oct 13 00:00:00 CST 2016, createTime=Tue Oct 11 17:49:52 CST 2016}
         Seckill{seckillId=1003, name='200元秒杀红米note', number=400, startTime=Wed Oct 12 00:00:00 CST 2016, endtime=Thu Oct 13 00:00:00 CST 2016, createTime=Tue Oct 11 17:49:52 CST 2016}
         */
    }

    @Test
    public void testReduceNumber() throws Exception {
        Date killTime = new Date();
        int updateCount = seckillDao.reduceNumber(1000L, killTime);
        System.out.println("updateCount="+updateCount);
        /**
         * 结果
         * updateCount=1
         */
    }

}