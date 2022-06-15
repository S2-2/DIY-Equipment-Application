package kr.ac.kpu.diyequipmentapplication.equipment;

import java.io.Serializable;

public class ScheduleDB implements Serializable {
    private String sUserEmail;              //사용자 이메일

    //<대여기간>
    private String sStartDate;              //대여일
    private String sExpirationDate;         //반납일
    private String sTotalLendingPeriod;    //대여일수

    //<대여비>
    private String sDailyRental;            //일일대여비
    private String sTotalRental;            //총 대여비

    //<거래날짜>
    private String sTransactionDate;        //거래일

    //<거래시간>
    private String sTransactionTime;        //거래시간

    //<거래장소>
    private String sTransactionLocation;    //거래장소

    public ScheduleDB() {
    }

    public ScheduleDB(String sUserEmail, String sStartDate, String sExpirationDate, String sTotalLendingPeriod,
                      String sDailyRental, String sTotalRental, String sTransactionDate, String sTransactionTime,
                      String sTransactionLocation) {
        this.sUserEmail = sUserEmail;
        this.sStartDate = sStartDate;
        this.sExpirationDate = sExpirationDate;
        this.sTotalLendingPeriod = sTotalLendingPeriod;
        this.sDailyRental = sDailyRental;
        this.sTotalRental = sTotalRental;
        this.sTransactionDate = sTransactionDate;
        this.sTransactionTime = sTransactionTime;
        this.sTransactionLocation = sTransactionLocation;
    }

    public String getsUserEmail() {
        return sUserEmail;
    }

    public void setsUserEmail(String sUserEmail) {
        this.sUserEmail = sUserEmail;
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
}
