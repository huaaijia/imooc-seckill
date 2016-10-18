package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by huaaijia on 2016/10/13.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void testGetSeckillList() throws Exception {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}",list);
        /**
         * 15:50:04.404 [main] INFO  o.seckill.service.SeckillServiceTest - list=[Seckill{seckillId=1000, name='1000元秒杀iphone6', number=99, startTime=Wed Oct 12 00:00:00 CST 2016, endtime=Thu Oct 13 00:00:00 CST 2016, createTime=Tue Oct 11 17:49:52 CST 2016}, Seckill{seckillId=1001, name='500元秒杀ipad2', number=200, startTime=Wed Oct 12 00:00:00 CST 2016, endtime=Thu Oct 13 00:00:00 CST 2016, createTime=Tue Oct 11 17:49:52 CST 2016}, Seckill{seckillId=1002, name='300元秒杀小米4', number=300, startTime=Wed Oct 12 00:00:00 CST 2016, endtime=Thu Oct 13 00:00:00 CST 2016, createTime=Tue Oct 11 17:49:52 CST 2016}, Seckill{seckillId=1003, name='200元秒杀红米note', number=400, startTime=Wed Oct 12 00:00:00 CST 2016, endtime=Thu Oct 13 00:00:00 CST 2016, createTime=Tue Oct 11 17:49:52 CST 2016}]
         */
    }

    @Test
    public void testGetById() throws Exception {
        long id = 1000L;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}", seckill);
        /**
         * 15:51:06.145 [main] INFO  o.seckill.service.SeckillServiceTest - seckill=Seckill{seckillId=1000, name='1000元秒杀iphone6', number=99, startTime=Wed Oct 12 00:00:00 CST 2016, endtime=Thu Oct 13 00:00:00 CST 2016, createTime=Tue Oct 11 17:49:52 CST 2016}
         */
    }

    @Test
    public void testExportSeckillUrl() throws Exception {
        long id = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer={}", exposer);
        /**
         * 16:01:56.030 [main] INFO  o.seckill.service.SeckillServiceTest - exposer=Exposer{exposed=true, md5='2c9b645e0088cdb03cdd8f1020621e65', seckillId=1000, now=0, start=0, end=0}
         */
    }

    @Test
    public void testExecuteSeckill() throws Exception {
//        long id = 1000;
//        long phone = 1234123413;
//        String md5 = "2c9b645e0088cdb03cdd8f1020621e65";
//        SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
//        logger.info("execution={}", execution);
//
//        /**
//         * 第一次：org.seckill.exception.SeckillException: seckill data rewrite
//         * 第二次：16:12:21.184 [main] INFO  o.seckill.service.SeckillServiceTest - execution=SeckillExecution{successKilled=SuccessKilled{seckillId=1000, userPhone=1234123413, state=1, create_time=null}, stateInfo='秒杀成功', state=1, seckillId=1000}
//         * 第三次：org.seckill.exception.RepeatKillException: seckill repeated
//         */

        long id = 1000;
        long phone = 1234123413;
        String md5 = "2c9b645e0088cdb03cdd8f1020621e65";
        try {
            SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
            logger.info("execution={}", execution);
        } catch (RepeatKillException e) {
            logger.error(e.getMessage());
        } catch (SeckillCloseException e) {
            logger.error(e.getMessage());
        } catch (SeckillException e) {
            logger.error(e.getMessage());
        }

    }

    /**
     * 上面两个方法在test中每次执行结果不同，且第二个方法需要第一个方法的md5值作为参数，所以在整整开发业务中，应该将这两个方法整合成一个方法测试
     * 集成测试徐亚科重复执行
     * @throws Exception
     */
    @Test
    public void testSeckillLogic() throws Exception {
        long id = 1001;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if(exposer.isExposed()){
            logger.info("exposer={}", exposer);
            long phone = 1234123413;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
                logger.info("execution={}", execution);
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            } catch (SeckillCloseException e) {
                logger.error(e.getMessage());
            } catch (SeckillException e) {
                logger.error(e.getMessage());
            }
        }else{
            //秒杀未开启
            logger.warn("exposer={}", exposer);
        }
    }

    @Test
    public void executeSeckillProcedure(){
        long seckillId = 1001;
        long phone = 10000000000L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if(exposer.isExposed()){
            String md5 = exposer.getMd5();
            SeckillExecution seckillExecution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
            logger.info(seckillExecution.getStateInfo());
            /**
             * 18:22:31.961 [main] INFO  o.seckill.service.SeckillServiceTest - 秒杀成功
             * 18:22:52.542 [main] INFO  o.seckill.service.SeckillServiceTest - 重复秒杀
             */
        }
    }

}