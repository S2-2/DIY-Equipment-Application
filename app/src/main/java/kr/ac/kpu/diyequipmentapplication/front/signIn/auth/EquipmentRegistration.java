package kr.ac.kpu.diyequipmentapplication.front.signIn.auth;

//Firebase DB에 등록하기 위해 공급자가 등록한 DIY공구 클래스 선언
public class EquipmentRegistration {
    private String ModelName;
    private String ModelText;
    private String image;
    private String rentalType;
    private String rentalCost;
    private String rentalAddress;



    public EquipmentRegistration() {}

    public EquipmentRegistration(String modelName, String modelText, String image, String rentalType,
                                 String rentalCost, String rentalAddress)
    {
        this.ModelName = modelName;
        this.ModelText = modelText;
        this.image = image;
        this.rentalType = rentalType;
        this.rentalCost = rentalCost;
        this.rentalAddress = rentalAddress;
    }

    public String getModelName() {
        return ModelName;
    }

    public void setModelName(String modelName) {
        this.ModelName = modelName;
    }

    public String getModelText() {
        return ModelText;
    }

    public void setModelText(String modelText) {
        this.ModelText = modelText;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRentalType() {
        return rentalType;
    }

    public void setRentalType(String rentalType) {
        this.rentalType = rentalType;
    }

    public String getRentalCost() {
        return rentalCost;
    }

    public void setRentalCost(String rentalCost) {
        this.rentalCost = rentalCost;
    }

    public String getRentalAddress() {
        return rentalAddress;
    }

    public void setRentalAddress(String rentalAddress) {
        this.rentalAddress = rentalAddress;
    }
}
