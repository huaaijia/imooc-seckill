package org.seckill.exception;

/**
 * 所有秒杀相关的业务异常
 * Created by huaaijia on 2016/10/13.
 */
public class SeckillException extends RuntimeException {
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
