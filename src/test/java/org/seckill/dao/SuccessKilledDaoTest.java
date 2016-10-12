package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by huaaijia on 2016/10/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void testInsertSuccessKilled() throws Exception {
        long id = 1001L;
        long phone = 13810000000L;
        int state = 0;
        int insertCount = successKilledDao.insertSuccessKilled(id, phone, state);
        System.out.println("insertCount="+insertCount);
        /**
         * 第一次结果：insertCount=1
         * 第二次结果：insertCount=0
         */
    }

    @Test
    public void testQueryByIdWithSeckill() throws Exception {
        long successKilledId = 1001;
        long phone = 13810000000L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(successKilledId, phone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
        /**
         * 结果
         * SuccessKilled{seckillId=1000, userPhone=13810000000, state=-1, create_time=null}
         Seckill{seckillId=1000, name='1000元秒杀iphone6', number=99, startTime=Wed Oct 12 00:00:00 CST 2016, endtime=Thu Oct 13 00:00:00 CST 2016, createTime=Tue Oct 11 17:49:52 CST 2016}
         */

    }
}