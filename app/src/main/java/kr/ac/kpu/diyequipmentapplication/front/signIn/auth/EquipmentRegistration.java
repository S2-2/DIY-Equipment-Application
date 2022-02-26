package kr.ac.kpu.diyequipmentapplication.front.signIn.auth;

//Firebase DB에 등록하기 위해 공급자가 등록한 DIY공구 클래스 선언
public class EquipmentRegistration {
    String ModelName;
    String ModelText;
    String image;

    public EquipmentRegistration() {}

    public EquipmentRegistration(String modelName, String modelText, String image)
    {
        ModelName = modelName;
        ModelText = modelText;
        this.image = image;
    }

    public String getModelName() {
        return ModelName;
    }

    public void setModelName(String modelName) {
        ModelName = modelName;
    }

    public String getModelText() {
        return ModelText;
    }

    public void setModelText(String modelText) {
        ModelText = modelText;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
