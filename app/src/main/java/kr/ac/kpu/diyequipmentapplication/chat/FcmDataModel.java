package kr.ac.kpu.diyequipmentapplication.chat;

public class FcmDataModel {
    String fcmToken;
    String userEmail;

    public FcmDataModel(){
    }

    public FcmDataModel(String fcmToken, String userEmail) {
        this.fcmToken = fcmToken;
        this.userEmail = userEmail;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
