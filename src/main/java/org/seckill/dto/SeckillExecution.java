package org.seckill.dto;

import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;

import java.io.Serializable;

/**
 * 封装秒杀执行后的结果
 * Created by huaaijia on 2016/10/13.
 */
public class SeckillExecution {

    private long seckillId;

    /**
     * 状态标识
     */
    private int state;

    /**
     * 状态解释
     */
    private String stateInfo;

    /**
     * 秒杀成功对象
     */
    private SuccessKilled successKilled;

    public SeckillExecution(long seckillId, SeckillStatEnum statEnum, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getStateInfo();
        this.successKilled = successKilled;
    }

    public SeckillExecution(long seckillId, SeckillStatEnum statEnum) {
        this.seckillId = seckillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getStateInfo();
    }

    @Override
    public String toString() {
        return "SeckillExecution{" +
                "successKilled=" + successKilled +
                ", stateInfo='" + stateInfo + '\'' +
                ", state=" + state +
                ", seckillId=" + seckillId +
                '}';
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }
}
