package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SecKillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SecKillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SecKillCloseException;
import org.seckill.exception.SecKillException;
import org.seckill.service.SecKillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Component @Service @Dao @Controller
@Service
public class SecKillServiceImpl implements SecKillService {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    //注入Service依赖
    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    //md5盐值字符串，用于混淆md5
    private final String slat="dsgajkdgsayuwteay%&^$S85gdjkgasd8*^9871qt1639783@$%376fjhgj";


    public List<Seckill> getSecKillList() {
        return seckillDao.queryAll(0,4);
    }

    public Seckill getById(long secKillId) {
        return seckillDao.queryById(secKillId);
    }

    public Exposer exportSecKillUrl(long secKillId) {
        //秒杀暴露接口
        //优化数据库操作
        //所有的秒杀都要去调用秒杀调用接口这个方法，我们希望通过redis把它缓存起来，降低我们数据库访问的压力
        //优化点：缓存优化,在超时的基础上维护一致性
        //1.访问redis
        Seckill seckill=redisDao.getSeckill(secKillId);
        //为空说明缓存中没有
        if (seckill==null){
            //2:访问数据库
            seckill = seckillDao.queryById(secKillId);
            if (seckill ==null){
                //如果数据库也没有，返回false，代表秒杀单不存在
                return new Exposer(false,secKillId);
            }else {
                //如果存在
                //3：放入redis
                redisDao.putSeckill(seckill);
            }
        }

        /*
        * get from cache
        * if null
        *   get db
        *   else
        *       put cache
        *  locgoin
        *
        * */

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //系统当前时间
        Date nowTime = new Date();
        if (nowTime.getTime()<startTime.getTime()||nowTime.getTime()>endTime.getTime()){
            return new Exposer(false,secKillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
        }

        //转换特定字符串的过程，不可逆
        String md5 =getMD5(secKillId);
        return new Exposer(true,md5,secKillId);
    }

    private String getMD5(long secKillId){
        String base = secKillId+"/"+slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Transactional
    /*
    * 使用注解控制事务方法的优点
    * 1：开发团队达成一致约定，明确标注事务方法的编程风格
    * 2：保证事务方法的执行时间尽可能短，不要穿插其他的网络操作（RPC/HTTP请求），如果需要这些网络操作获取剥离到事务外部
    * 3：不是所有的方法都需要事务，如只有一条修改操作，或者是只读操作不需要事务控制
    * */
    public SecKillExecution excuteSecKillId(long secKillId, long userPhone, String md5) throws SecKillException, RepeatKillException, SecKillCloseException {
        if(md5==null||!md5.equals(getMD5(secKillId))){
            throw new SecKillException("secKill Data Review");
        }
        //执行秒杀逻辑:减库存、记录购买行为
        Date nowTime =new Date();

        try {

            //减库存成功了，记录购买行为
            int insertCount = successKilledDao.insertSuccessKilled(secKillId,userPhone);
            // 唯一的id和phone
            if (insertCount<=0){
                //重复秒杀
                throw new RepeatKillException("secKill Repeated");
            }else {
                // 减库存，热点商品竞争
                int updateCount = seckillDao.reduceNumber(secKillId,nowTime);
                if (updateCount<=0){
                    //没有更新到记录,秒杀结束，rollback
                    throw new SecKillCloseException("seKill is Closed");
                }else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(secKillId,userPhone);
                    return new SecKillExecution(secKillId, SecKillStatEnum.SUCCESS,successKilled);
                }
            }
        }catch (SecKillCloseException e1) {
            throw e1;
        }catch (RepeatKillException e2){
            throw e2;
        }catch
         (Exception e){
            logger.error(e.getMessage(),e);
            //所有编译器异常转换为运行期异常
            throw  new SecKillException("secKill inner error:"+e.getMessage());
        }
    }

    public SecKillExecution excuteSecKillProcedure(long secKillId, long userPhone, String md5) {
        if (md5==null||!md5.equals(getMD5(secKillId))){
            return new SecKillExecution(secKillId,SecKillStatEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("seckillId",secKillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);
        //执行存储过程，result被赋值
        try{
            seckillDao.killByProcedure(map);
            //获取result
            int result = MapUtils.getInteger(map,"result",-2);
            if (result==1){
                SuccessKilled sk = successKilledDao.
                        queryByIdWithSeckill(secKillId,userPhone);
                return new SecKillExecution(secKillId,SecKillStatEnum.SUCCESS,sk);
            }else {
                return new SecKillExecution(secKillId,SecKillStatEnum.stateOf(result));
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return new SecKillExecution(secKillId,SecKillStatEnum.INNER_ERROR);
        }

    }
}
