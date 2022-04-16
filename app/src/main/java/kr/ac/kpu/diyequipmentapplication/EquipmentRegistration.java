package kr.ac.kpu.diyequipmentapplication;

import java.io.Serializable;

//Firebase RealTime DB에서 등록된 데이터들을 JSON타입으로 가져오기 때문에 이것을 읽어오기 위한 클래스 구현
public class EquipmentRegistration implements Serializable {
    //해당 필드명은 Firebase RealTime DB에 등록된 속성명과 동일해야 함!
    //필드명과 Firebase RealTime DB에 등록된 속성명과 동일하지 않으면 DB에서 데이터를 읽을 수 없음!
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

    public EquipmentRegistration() {}   //기본 생성자

    public EquipmentRegistration(String modelName, String modelInform, String rentalImage, String rentalType, String rentalCost, String rentalAddress, String userEmail, String rentalDate, String modelCategory1, String modelCategory2) {
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
    }

    //장비 필드에 대한 Getter / Setter 메서드 기능 구현
    public String getModelName() { return ModelName; }
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
    public void setRentalDate(String rentalDate) { RentalDate = rentalDate; }

    public String getModelCategory1() { return ModelCategory1; }
    public void setModelCategory1(String modelCategory1) { ModelCategory1 = modelCategory1; }

    public String getModelCategory2() { return ModelCategory2; }
    public void setModelCategory2(String modelCategory2) { ModelCategory2 = modelCategory2; }
}
