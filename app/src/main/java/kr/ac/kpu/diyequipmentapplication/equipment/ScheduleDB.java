package kr.ac.kpu.diyequipmentapplication.equipment;

import java.io.Serializable;

public class ScheduleDB implements Serializable {

    //<거래자>
    private String sUserEmail;              // 본인 이메일
    private String sOtherEmail;             // 상대방 이메일

    //<대여기간>
    private String sStartDate;              //대여일
    private String sExpirationDate;         //반납일
    private String sTotalLendingPeriod;    //대여일수
    private String startDate;               //대여일수의 대여일
    private String finishDate;              //대여일수의 반납일

    //<대여비>
    private String sDailyRental;            //일일대여비
    private String sTotalRental;            //총 대여비

    //<거래날짜>
    private String sTransactionDate;        //거래일

    //<거래시간>
    private String sTransactionTime;        //거래시간

    //<거래장소>
    private String sTransactionLocation;    //거래장소

    private String sCollectionId;
    private String sChatNum;

    public ScheduleDB() {
    }

    public ScheduleDB(String sUserEmail, String sOtherEmail, String sStartDate, String sExpirationDate, String sTotalLendingPeriod, String startDate,
                      String finishDate, String sDailyRental, String sTotalRental, String sTransactionDate, String sTransactionTime,
                      String sTransactionLocation, String sCollectionId, String sChatNum) {
        this.sUserEmail = sUserEmail;
        this.sOtherEmail = sOtherEmail;
        this.sStartDate = sStartDate;
        this.sExpirationDate = sExpirationDate;
        this.sTotalLendingPeriod = sTotalLendingPeriod;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.sDailyRental = sDailyRental;
        this.sTotalRental = sTotalRental;
        this.sTransactionDate = sTransactionDate;
        this.sTransactionTime = sTransactionTime;
        this.sTransactionLocation = sTransactionLocation;
        this.sCollectionId = sCollectionId;
        this.sChatNum = sChatNum;
    }

    public String getsUserEmail() {
        return sUserEmail;
    }

    public void setsUserEmail(String sUserEmail) {
        this.sUserEmail = sUserEmail;
    }

    public String getsOtherEmail() {
        return sOtherEmail;
    }

    public void setsOtherEmail(String sOtherEmail) {
        this.sOtherEmail = sOtherEmail;
    }

    public String getsStartDate() {
        return sStartDate;
    }

    public void setsStartDate(String sStartDate) {
        this.sStartDate = sStartDate;
    }

    public String getsExpirationDate() {
        return sExpirationDate;
    }

    public void setsExpirationDate(String sExpirationDate) {
        this.sExpirationDate = sExpirationDate;
    }

    public String getsTotalLendingPeriod() {
        return sTotalLendingPeriod;
    }

    public void setsTotalLendingPeriod(String sTotalLendingPeriod) {
        this.sTotalLendingPeriod = sTotalLendingPeriod;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public String getsDailyRental() {
        return sDailyRental;
    }

    public void setsDailyRental(String sDailyRental) {
        this.sDailyRental = sDailyRental;
    }

    public String getsTotalRental() {
        return sTotalRental;
    }

    public void setsTotalRental(String sTotalRental) {
        this.sTotalRental = sTotalRental;
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

    public String getsCollectionId() {
        return sCollectionId;
    }

    public void setsCollectionId(String sCollectionId) {
        this.sCollectionId = sCollectionId;
    }

    public String getsChatNum() {
        return sChatNum;
    }

    public void setsChatNum(String sChatNum) {
        this.sChatNum = sChatNum;
    }
}
