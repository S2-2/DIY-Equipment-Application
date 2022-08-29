package kr.ac.kpu.diyequipmentapplication.chat;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import kr.ac.kpu.diyequipmentapplication.equipment.RegistrationDTO;

public class TransactionDTO implements Serializable {
    private String tScheduleId;
    private String tImgView;
    private String tCategory;
    private String tModelName;
    private String tUserName;
    private String tRentalType;
    private String tRentalDate;
    private String tRentalCost;
    private String tStartDate;
    private String tExpirationDate;
    private String tTotalLendingPeriod;
    private String tTotalRental;
    private String tTransactionDate;
    private String tTransactionTime;
    private String tTransactionLocation;
    private String tTransactionCondition;
    private String tUserEmail;
    private String tOtherEmail;
    private String tLastTime;

    public TransactionDTO() {
    }

    public TransactionDTO(String tScheduleId, String tImgView, String tCategory, String tModelName, String tUserName,
                          String tRentalType, String tRentalDate, String tRentalCost, String tStartDate, String tExpirationDate,
                          String tTotalLendingPeriod, String tTotalRental, String tTransactionDate, String tTransactionTime,
                          String tTransactionLocation, String tTransactionCondition, String tUserEmail, String tOtherEmail, String tLastTime) {
        this.tScheduleId = tScheduleId;
        this.tImgView = tImgView;
        this.tCategory = tCategory;
        this.tModelName = tModelName;
        this.tUserName = tUserName;
        this.tRentalType = tRentalType;
        this.tRentalDate = tRentalDate;
        this.tRentalCost = tRentalCost;
        this.tStartDate = tStartDate;
        this.tExpirationDate = tExpirationDate;
        this.tTotalLendingPeriod = tTotalLendingPeriod;
        this.tTotalRental = tTotalRental;
        this.tTransactionDate = tTransactionDate;
        this.tTransactionTime = tTransactionTime;
        this.tTransactionLocation = tTransactionLocation;
        this.tTransactionCondition = tTransactionCondition;
        this.tUserEmail = tUserEmail;
        this.tOtherEmail = tOtherEmail;
        this.tLastTime = tLastTime;
    }

    public String gettScheduleId() {
        return tScheduleId;
    }

    public void settScheduleId(String tScheduleId) {
        this.tScheduleId = tScheduleId;
    }

    public String gettImgView() {
        return tImgView;
    }

    public void settImgView(String tImgView) {
        this.tImgView = tImgView;
    }

    public String gettCategory() {
        return tCategory;
    }

    public void settCategory(String tCategory) {
        this.tCategory = tCategory;
    }

    public String gettModelName() {
        return tModelName;
    }

    public void settModelName(String tModelName) {
        this.tModelName = tModelName;
    }

    public String gettUserName() {
        return tUserName;
    }

    public void settUserName(String tUserName) {
        this.tUserName = tUserName;
    }

    public String gettRentalType() {
        return tRentalType;
    }

    public void settRentalType(String tRentalType) {
        this.tRentalType = tRentalType;
    }

    public String gettRentalDate() {
        return tRentalDate;
    }

    public void settRentalDate(String tRentalDate) {
        this.tRentalDate = tRentalDate;
    }

    public String gettRentalCost() {
        return tRentalCost;
    }

    public void settRentalCost(String tRentalCost) {
        this.tRentalCost = tRentalCost;
    }

    public String gettStartDate() {
        return tStartDate;
    }

    public void settStartDate(String tStartDate) {
        this.tStartDate = tStartDate;
    }

    public String gettExpirationDate() {
        return tExpirationDate;
    }

    public void settExpirationDate(String tExpirationDate) {
        this.tExpirationDate = tExpirationDate;
    }

    public String gettTotalLendingPeriod() {
        return tTotalLendingPeriod;
    }

    public void settTotalLendingPeriod(String tTotalLendingPeriod) {
        this.tTotalLendingPeriod = tTotalLendingPeriod;
    }

    public String gettTotalRental() {
        return tTotalRental;
    }

    public void settTotalRental(String tTotalRental) {
        this.tTotalRental = tTotalRental;
    }

    public String gettTransactionDate() {
        return tTransactionDate;
    }

    public void settTransactionDate(String tTransactionDate) {
        this.tTransactionDate = tTransactionDate;
    }

    public String gettTransactionTime() {
        return tTransactionTime;
    }

    public void settTransactionTime(String tTransactionTime) {
        this.tTransactionTime = tTransactionTime;
    }

    public String gettTransactionLocation() {
        return tTransactionLocation;
    }

    public void settTransactionLocation(String tTransactionLocation) {
        this.tTransactionLocation = tTransactionLocation;
    }

    public String gettTransactionCondition() {
        return tTransactionCondition;
    }

    public void settTransactionCondition(String tTransactionCondition) {
        this.tTransactionCondition = tTransactionCondition;
    }

    public String gettUserEmail() {
        return tUserEmail;
    }

    public void settUserEmail(String tUserEmail) {
        this.tUserEmail = tUserEmail;
    }

    public String gettOtherEmail() {
        return tOtherEmail;
    }

    public void settOtherEmail(String tOtherEmail) {
        this.tOtherEmail = tOtherEmail;
    }

    public String gettLastTime() {
        return tLastTime;
    }

    public void settLastTime(String tLastTime) {
        this.tLastTime = tLastTime;
    }

}

