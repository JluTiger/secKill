package org.seckill.exception;
/*秒杀关闭异常*/
public class SecKillCloseException extends RuntimeException {
    public SecKillCloseException(String message) {
        super(message);
    }

    public SecKillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
