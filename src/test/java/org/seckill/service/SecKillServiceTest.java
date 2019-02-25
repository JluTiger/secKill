package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SecKillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SecKillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
public class SecKillServiceTest {

    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecKillService secKillService;

    @Test
    public void testGetSecKillList() throws Exception{

        List<Seckill> list = secKillService.getSecKillList();
        logger.info("list={}",list);

    }

    @Test
    public void testGetById() throws Exception{

        long id = 1000;
        Seckill seckill = secKillService.getById(id);
        logger.info("seckill={}",seckill);
    }

    //测试代码完整逻辑，注意可重复性执行
    @Test
    public void testSecKillLogic() throws Exception{

        long id=1001;
        Exposer exposer = secKillService.exportSecKillUrl(id);
        if (exposer.isExposed()){
            logger.info("exposer={}",exposer);
            long phone = 13561343961L;
            String md5 = exposer.getMd5();

            try {
                SecKillExecution execution=secKillService.excuteSecKillId(id,phone,md5);
                logger.info("result={}",execution);
            }catch (RepeatKillException e){
                logger.error(e.getMessage());
            }catch (SecKillCloseException e){
                logger.error(e.getMessage());
            }

        }else {
            //秒杀未开启
            logger.warn("exposer={}",exposer);
        }

        /*
        * exposer=
        * Exposer{exposed=true,
        * md5='61d53dbaa99f8f5f96a742d0481fe48f',
        * secKillId=1000, now=0, start=0, end=0}
         */
    }

    @Test
    public void testExecuteSecKill() throws Exception{

        long id=1000;
        long phone = 13561343561L;
        String md5 = "61d53dbaa99f8f5f96a742d0481fe48f";

        try {
            SecKillExecution execution=secKillService.excuteSecKillId(id,phone,md5);
            logger.info("result={}",execution);
        }catch (RepeatKillException e){
            logger.error(e.getMessage());
        }catch (SecKillCloseException e){
            logger.error(e.getMessage());
        }
    }

    @Test
    public void excuteSecKillProcedure() {
        long seckillId = 1001;
        long phone = 1368011101;
        Exposer exposer=secKillService.exportSecKillUrl(seckillId);
        if (exposer.isExposed()){
            String md5 = exposer.getMd5();
            SecKillExecution execution=secKillService.excuteSecKillProcedure(seckillId,phone,md5);
            logger.info(execution.getStateInfo());
        }



    }


}