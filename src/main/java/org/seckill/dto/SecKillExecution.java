package org.seckill.dto;

import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SecKillStatEnum;

/*封装秒杀执行后的结果*/
public class SecKillExecution {
    private long secKillId;

    //秒杀执行结果状态
    private int state;

    //状态的表示
    private String stateInfo;

    //秒杀成功对象
    private SuccessKilled successKilled;

    public SecKillExecution(long secKillId, SecKillStatEnum statEnum, SuccessKilled successKilled) {
        this.secKillId = secKillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getStateInfo();
        this.successKilled = successKilled;
    }

    public SecKillExecution(long secKillId, SecKillStatEnum statEnum) {
        this.secKillId = secKillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getStateInfo();
    }

    public long getSecKillId() {
        return secKillId;
    }

    public void setSecKillId(long secKillId) {
        this.secKillId = secKillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }

    @Override
    public String toString() {
        return "SecKillExecution{" +
                "secKillId=" + secKillId +
                ", state=" + state +
                ", stateInfo='" + stateInfo + '\'' +
                ", successKilled=" + successKilled +
                '}';
    }
}
