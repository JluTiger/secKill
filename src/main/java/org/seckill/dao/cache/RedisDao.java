package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private JedisPool jedisPool;

    //构造方法，连接redis的ip和port
    public RedisDao(String ip,int port){
        //jedisPool初始化
        jedisPool = new JedisPool(ip,port);
    }

    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    //通过redis去拿到Seckill这个对象
    public Seckill getSeckill(long seckillId){
        //redis操作逻辑
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:"+seckillId;
                //redis并没有实现内部序列化操作
                //典型的缓存逻辑：
                //get->byte[]->反序列化->Object(Seckill)
                //采用自定义序列化方式，用开源的protostuff序列化
                //protostuff：pojo.
                //序列化的本质：通过字节码、字节码的对应对象的属性，把字节码的数据传递给这些属性
                byte[] bytes=jedis.get(key.getBytes());
                //如果字节数组不为空，说明我们从缓存中获取到了
                if(bytes!=null){
                    //空对象
                    Seckill seckill = schema.newMessage();
                    ProtobufIOUtil.mergeFrom(bytes,seckill,schema);
                    //seckill被反序列了
                    return seckill;
                }
            }finally {
                jedis.close();
            }

        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    //缓存没有的时候去put一个seckill
    public String putSeckill(Seckill seckill){
        // set Object(Seckill)->序列化->byte[]
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:"+seckill.getSeckillId();
                byte[] bytes = ProtobufIOUtil.toByteArray(seckill,schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //超时缓存
                int timeout = 60*60;//缓存一小时
                String result=jedis.setex(key.getBytes(),timeout,bytes);
                return result;

            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }

}
