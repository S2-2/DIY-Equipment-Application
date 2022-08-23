package kr.ac.kpu.diyequipmentapplication.equipment;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

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

class RegistrationPriceComparator implements Comparator<RegistrationDTO>{
    @Override
    public int compare(RegistrationDTO t1, RegistrationDTO t2) {
        int cost1= 0;
        int cost2= 0;

        if(!t1.getRentalCost().equals("무료")){
            cost1 = Integer.parseInt(t1.getRentalCost());
        }

        if(!t2.getRentalCost().equals("무료")){
            cost2 = Integer.parseInt(t2.getRentalCost());
        }

        if (cost1 > cost2) {
            return 1;
        }
        else if (cost1 < cost2) {
            return -1;
        }
        return 0;
    }
}

class RegistrationLikeComparator implements Comparator<RegistrationDTO>{
    @Override
    public int compare(RegistrationDTO t1, RegistrationDTO t2) {
        int like1 = 0;
        int like2 = 0;

        if(t1.getModelLikeNum()!=null){
            like1 = Integer.parseInt(t1.getModelLikeNum());
        }

        if(t2.getModelLikeNum()!=null){
            like2 = Integer.parseInt(t2.getModelLikeNum());
        }

        if (like1 < like2) {
            return 1;
        }
        else if (like1 > like2) {
            return -1;
        }
        return 0;
    }
}

class RegistrationDateComparator implements Comparator<RegistrationDTO>{
    @Override
    public int compare(RegistrationDTO t1, RegistrationDTO t2) {

        Date date1, date2;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date1 = new Date(dateFormat.parse(t1.getRentalDate()).getTime());
            date2 = new Date(dateFormat.parse(t2.getRentalDate()).getTime());
            int com = date1.compareTo(date2);
            return com;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }
}