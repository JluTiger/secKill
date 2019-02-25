package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SecKillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SecKillCloseException;
import org.seckill.exception.SecKillException;

import java.util.List;

/*
业务接口：站在“使用者”角度设计接口
* 三个方面：方法定义粒度，参数，返回类型（return类型/异常）
*
* */
public interface SecKillService {
    /*查询所有秒杀记录
    * */
    List<Seckill> getSecKillList();
    /*查询单个秒杀记录*/
    Seckill getById(long secKillId);
    /*输出秒杀接口地址，
    * 秒杀开始输出秒杀接口地址
    * 否则输出系统时间和秒杀时间*/
    Exposer exportSecKillUrl(long secKillId);

    /*执行秒杀操作 */
    SecKillExecution excuteSecKillId(long secKillId, long userPhone, String md5)
    throws SecKillException, RepeatKillException, SecKillCloseException;
    /*执行秒杀操作 by 存储过程 */
    SecKillExecution excuteSecKillProcedure(long secKillId, long userPhone, String md5);

}
