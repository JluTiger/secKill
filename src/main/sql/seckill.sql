-- 秒杀执行存储过程
delimiter $$ -- ; 转换为$$

-- 定义存储过程
-- 参数：in  输入参数；out 输出参数
-- row_count():返回上一条修改类型sql（delete、insert、update）的影响行数
-- row_count:0:未修改数据 >0:表示修改的修改行数 <0：sql错误/未执行修改sql
create procedure `seckill`.`execute_seckill`
  (in v_seckill_id bigint,in v_phone bigint,
    in v_kill_time timestamp,out r_result int)

  begin
    declare insert_count int default 0;
    start transaction ;
    insert ignore into success_killed(seckill_id,user_phone,create_time,state)
      values (v_seckill_id,v_phone,v_kill_time,0);
    select row_count() into insert_count;
    if (insert_count<0) then
      rollback ;
      set r_result=-1;
    elseif (insert_count<0) then
      rollback ;
      set r_result=-2;
    else
      update seckill
        set number = number-1
        where seckill_id = v_seckill_id
          and end_time > v_kill_time
          and start_time < v_kill_time
          and number > 0;
      select row_count() into insert_count;
      if (insert_count = 0 ) then
        rollback ;
        set r_result = 0;
      elseif (insert_count < 0) then
        rollback ;
        set r_result = -2;
      else
        commit ;
        set r_result = 1;
      end if;
    end if;
    end;
$$

-- 存储过程定义结束
delimiter ;
set @r_result = -3;
-- 执行存储过程
call seckill.execute_seckill(1001,13502178891,now(),@r_result);
-- 获取结果
select @r_result;

-- 存储过程
-- 1、存储过程优化：事务行级锁持有的时间
-- 2、不要过度依赖存储过程
-- 3、简单的逻辑，可以应用存储过程
-- 4、QPS：一个秒杀单接近6000左右的qps