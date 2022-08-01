package kr.ac.kpu.diyequipmentapplication.equipment;

import java.io.Serializable;

public class RegistrationDTO implements Serializable {
    private String ModelName;       //장비 모델명
    private String ModelInform;     //장비 모델 정보
    private String RentalImage;     //장비 이미지
    private String RentalType;      //장비 타입 ex) 유료 대여, 무료 대여
    private String RentalCost;      //장비 대여 요금
    private String RentalAddress;   //장비 대여 장소
    private String UserEmail;       //사용자 이메일
    private String RentalDate;      //장비 등록일
    private String ModelCategory1;  //장비 카테고리1
    private String ModelCategory2;  //장비 카테고리2
    private String ModelCollectionId;   //장비 컬렉션 아이디;
    private String ModelLikeNum;       // 찜 횟수

    public RegistrationDTO() {
    }

    public RegistrationDTO(String modelName, String modelInform, String rentalImage, String rentalType, String rentalCost,
                           String rentalAddress, String userEmail, String rentalDate, String modelCategory1, String modelCategory2,
                           String modelLikeNum, String modelCollectionId) {
        ModelName = modelName;
        ModelInform = modelInform;
        RentalImage = rentalImage;
        RentalType = rentalType;
        RentalCost = rentalCost;
        RentalAddress = rentalAddress;
        UserEmail = userEmail;
        RentalDate = rentalDate;
        ModelCategory1 = modelCategory1;
        ModelCategory2 = modelCategory2;
        ModelCollectionId = modelCollectionId;
        ModelLikeNum = modelLikeNum;
    }

    public String getModelName() {
        return ModelName;
    }

    public void setModelName(String modelName) {
        ModelName = modelName;
    }

    public String getModelInform() {
        return ModelInform;
    }

    public void setModelInform(String modelInform) {
        ModelInform = modelInform;
    }

    public String getRentalImage() {
        return RentalImage;
    }

    public void setRentalImage(String rentalImage) {
        RentalImage = rentalImage;
    }

    public String getRentalType() {
        return RentalType;
    }

    public void setRentalType(String rentalType) {
        RentalType = rentalType;
    }

    public String getRentalCost() {
        return RentalCost;
    }

    public void setRentalCost(String rentalCost) {
        RentalCost = rentalCost;
    }

    public String getRentalAddress() {
        return RentalAddress;
    }

    public void setRentalAddress(String rentalAddress) {
        RentalAddress = rentalAddress;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getRentalDate() {
        return RentalDate;
    }

    public void setRentalDate(String rentalDate) {
        RentalDate = rentalDate;
    }

    public String getModelCategory1() {
        return ModelCategory1;
    }

    public void setModelCategory1(String modelCategory1) {
        ModelCategory1 = modelCategory1;
    }

    public String getModelCategory2() {
        return ModelCategory2;
    }

    public void setModelCategory2(String modelCategory2) {
        ModelCategory2 = modelCategory2;
    }

    public String getModelCollectionId() {
        return ModelCollectionId;
    }

    public void setModelCollectionId(String modelCollectionId) {
        ModelCollectionId = modelCollectionId;
    }

    public String getModelLikeNum() {
        return ModelLikeNum;
    }

    public void setModelLikeNum(String modelLikeNum) {
        ModelLikeNum = modelLikeNum;
    }
}
