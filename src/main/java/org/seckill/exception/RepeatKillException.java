package org.seckill.exception;

/**
 * 重复秒杀异常（运行期异常）
 * spring的事物回滚只接受运行期异常
 * 如果是非运行期异常，spring事务是不会进行回滚的！
 * Created by huaaijia on 2016/10/13.
 */
public class RepeatKillException extends SeckillException {
    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
