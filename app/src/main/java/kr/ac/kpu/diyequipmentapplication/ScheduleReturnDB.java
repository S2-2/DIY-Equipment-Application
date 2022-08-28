package kr.ac.kpu.diyequipmentapplication;

import java.io.Serializable;

public class ScheduleReturnDB implements Serializable {
    private String sUserEmail;              // 본인 이메일
    //<거래날짜>
    private String sTransactionDate;        //거래일

    //<거래시간>
    private String sTransactionTime;        //거래시간

    //<거래장소>
    private String sTransactionLocation;    //거래장소

    private String sTransactionId;

    public ScheduleReturnDB() {
    }

    public ScheduleReturnDB(String sUserEmail, String sTransactionDate, String sTransactionTime, String sTransactionLocation, String sTransactionId) {
        this.sUserEmail = sUserEmail;
        this.sTransactionDate = sTransactionDate;
        this.sTransactionTime = sTransactionTime;
        this.sTransactionLocation = sTransactionLocation;
        this.sTransactionId = sTransactionId;
    }

    public String getsUserEmail() {
        return sUserEmail;
    }

    public void setsUserEmail(String sUserEmail) {
        this.sUserEmail = sUserEmail;
    }

    public String getsTransactionDate() {
        return sTransactionDate;
    }

    public void setsTransactionDate(String sTransactionDate) {
        this.sTransactionDate = sTransactionDate;
    }

    public String getsTransactionTime() {
        return sTransactionTime;
    }

    public void setsTransactionTime(String sTransactionTime) {
        this.sTransactionTime = sTransactionTime;
    }

    public String getsTransactionLocation() {
        return sTransactionLocation;
    }

    public void setsTransactionLocation(String sTransactionLocation) {
        this.sTransactionLocation = sTransactionLocation;
    }

    public String getsTransactionId() {
        return sTransactionId;
    }

    public void setsTransactionId(String sTransactionId) {
        this.sTransactionId = sTransactionId;
    }
}
