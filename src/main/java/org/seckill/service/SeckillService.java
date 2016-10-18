package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.springframework.test.annotation.Repeat;

import java.util.List;

/**
 * 业务接口：站在"使用者"角度设计接口
 * 三个方面：方法定义粒度，参数，返回类型（return 类型／异常）
 *
 * Created by huaaijia on 2016/10/13.
 */
public interface SeckillService {

    /**
     * 查询所有秒杀记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开启时输出秒杀接口地址
     * 否则输出系统时间和秒杀时间
     * 秒杀没有开始的时候谁也不知道秒杀地址，防止未开启的秒杀被拼凑地址
     *
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws RepeatKillException, SeckillCloseException, SeckillException;

    /**
     * 使用存储过程实现executeSeckill秒杀逻辑
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws RepeatKillException
     * @throws SeckillCloseException
     * @throws SeckillException
     */
    SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5);

}
