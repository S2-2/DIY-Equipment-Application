package kr.ac.kpu.diyequipmentapplication.chat;

public class FcmDTO {
    String fcmToken;
    String userEmail;

    public FcmDTO(){
    }

    public FcmDTO(String fcmToken, String userEmail) {
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
