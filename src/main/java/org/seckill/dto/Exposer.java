package org.seckill.dto;
/*暴露秒杀地址DTO*/
public class Exposer {
    // 是否开启秒杀
    private  boolean exposed;
    //一种加密机制
    private String md5;
    //id
    private long secKillId;
    //系统当前时间
    private long now;

    @Override
    public String toString() {
        return "Exposer{" +
                "exposed=" + exposed +
                ", md5='" + md5 + '\'' +
                ", secKillId=" + secKillId +
                ", now=" + now +
                ", start=" + start +
                ", end=" + end +
                '}';
    }

    private long start;
    private long end;

    public Exposer(boolean exposed, String md5, long secKillId) {
        this.exposed = exposed;
        this.md5 = md5;
        this.secKillId = secKillId;
    }

    public Exposer(boolean exposed, long secKillId,long now, long start, long end) {
        this.exposed = exposed;
        this.now = now;
        this.start = start;
        this.end = end;
        this.secKillId = secKillId;
    }

    public Exposer(boolean exposed, long secKillId) {
        this.exposed = exposed;
        this.secKillId = secKillId;
    }

    public boolean isExposed() {
        return exposed;
    }

    public void setExposed(boolean exposed) {
        this.exposed = exposed;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getSecKillId() {
        return secKillId;
    }

    public void setSecKillId(long secKillId) {
        this.secKillId = secKillId;
    }

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
