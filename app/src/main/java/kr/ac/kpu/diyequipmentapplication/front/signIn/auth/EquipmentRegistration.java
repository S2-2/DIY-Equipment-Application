package kr.ac.kpu.diyequipmentapplication.front.signIn.auth;

import android.content.Intent;

//Firebase DB에 등록하기 위해 공급자가 등록한 DIY공구 클래스 선언
public class EquipmentRegistration {
    String ModelName;
    String ModelText;
    String image;
    Integer myPick;

    public EquipmentRegistration() {}

    public EquipmentRegistration(String modelName, String modelText, String image, Integer myPick)
    {
        ModelName = modelName;
        ModelText = modelText;
        this.image = image;
        this.myPick = myPick;
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

    public Integer getMyPick() { return myPick; }

    public  void setMyPick(Integer myPick) { this.myPick = myPick; }
}
