package kr.ac.kpu.diyequipmentapplication;

import java.io.Serializable;

public class ScheduleReturnDB implements Serializable {
    private String sUserEmail;              // 본인 이메일
    private String sOtherEmail;             // 상대방 이메일

    //<거래날짜>
    private String sTransactionDate;        //거래일

    //<거래시간>
    private String sTransactionTime;        //거래시간

    //<거래장소>
    private String sTransactionLocation;    //거래장소

    private String sTransactionId;

    private String sChatNum;            // 채팅방번호

    public ScheduleReturnDB() {
    }

    public ScheduleReturnDB(String sUserEmail, String sOtherEmail, String sTransactionDate, String sTransactionTime, String sTransactionLocation, String sTransactionId, String sChatNum) {
        this.sUserEmail = sUserEmail;
        this.sOtherEmail = sOtherEmail;
        this.sTransactionDate = sTransactionDate;
        this.sTransactionTime = sTransactionTime;
        this.sTransactionLocation = sTransactionLocation;
        this.sTransactionId = sTransactionId;
        this.sChatNum = sChatNum;
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

    public String getsOtherEmail() {
        return sOtherEmail;
    }

    public void setsOtherEmail(String sOtherEmail) {
        this.sOtherEmail = sOtherEmail;
    }

    public String getsChatNum() {
        return sChatNum;
    }

    public void setsChatNum(String sChatNum) {
        this.sChatNum = sChatNum;
    }
}
