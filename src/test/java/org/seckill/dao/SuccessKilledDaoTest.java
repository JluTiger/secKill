package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
    @Resource
    private  SuccessKilledDao successKilledDao;

    @Test
    public void testInsertSuccessKilled() throws Exception{
        long id = 1000L;
        long phone = 13544564548L;
        int insertCount=successKilledDao.insertSuccessKilled(id,phone);
        System.out.println("insertCount:"+insertCount);
    }

    @Test
    public void testQueryByIdWithSeckill() throws Exception{

        long id = 1000L;
        long phone = 13544564548L;
        SuccessKilled successKilled= successKilledDao.queryByIdWithSeckill(id,phone);
        System.out.println("successKilled: "+successKilled);
        System.out.println(successKilled.getSeckill());
        /*
        successKilled:
        SuccessKilled{seckillId=1000, userPhone=13544564548, state=0, createTime=Sun Feb 24 12:08:02 CST 2019}
        Seckill{seckillId=1000, name='1000元秒杀Iphone6', number=100, startTime=Fri Nov 01 00:00:00 CST 2019, endTime=Sat Nov 02 00:00:00 CST 2019, createTime=Sat Feb 23 16:26:40 CST 2019}

        * */
    }
}