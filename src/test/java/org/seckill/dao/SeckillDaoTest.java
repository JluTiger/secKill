package org.seckill.dao;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/*
* 配置spring和junit整合，junit启动时加载springIOC容器
* spring-test, junit
* */

@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    //注入Dao实现类依赖
    @Resource
    private SeckillDao seckillDao;

    /*
    * */
    @Test
    public void testQueryById() throws Exception{
        long id = 1000;
        Seckill seckill= seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
        /*
         * 1000元秒杀Iphone6
         * Seckill{seckiddId=1000, name='1000元秒杀Iphone6', number=100,
         * startTime=Fri Nov 01 00:00:00 CST 2019,
         * endTime=Sat Nov 02 00:00:00 CST 2019,
         * createTime=Sat Feb 23 16:26:40 CST 2019}
         * 二月 23,
         * */
    }

    @Test
    public void testQueryAll() throws Exception{

        //java没有保存形参的记录
        List<Seckill>  seckills = seckillDao.queryAll(0,100);
        for (Seckill seckill:seckills){
            System.out.println(seckill);
        }
        /*
        * Seckill{seckillId=1000, name='1000元秒杀Iphone6', number=100, startTime=Fri Nov 01 00:00:00 CST 2019, endTime=Sat Nov 02 00:00:00 CST 2019, createTime=Sat Feb 23 16:26:40 CST 2019}
            Seckill{seckillId=1001, name='500元秒杀小米9', number=200, startTime=Fri Nov 01 00:00:00 CST 2019, endTime=Sat Nov 02 00:00:00 CST 2019, createTime=Sat Feb 23 16:26:40 CST 2019}
            Seckill{seckillId=1002, name='600元秒杀华为', number=300, startTime=Fri Nov 01 00:00:00 CST 2019, endTime=Sat Nov 02 00:00:00 CST 2019, createTime=Sat Feb 23 16:26:40 CST 2019}
            Seckill{seckillId=1003, name='2000元秒杀笔记本', number=400, startTime=Fri Nov 01 00:00:00 CST 2019, endTime=Sat Nov 02 00:00:00 CST 2019, createTime=Sat Feb 23 16:26:40 CST 2019}
        * */
    }

    @Test
    public void testReduceNumber() throws Exception{
        Date killTime=new Date();
        int updateCount = seckillDao.reduceNumber(1000L,killTime);
        System.out.println("updateCount="+updateCount);
    }

}