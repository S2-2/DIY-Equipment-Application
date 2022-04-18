package kr.ac.kpu.diyequipmentapplication;

public class SignupAccountDB {
    private String userID;
    private String userPwd1;
    private String userPwd2;
    private String userName;
    private String userNickname;
    private String userEmail;

    public SignupAccountDB() {
    }

    public SignupAccountDB(String userID, String userPwd1, String userPwd2, String userName, String userNickname, String userEmail) {
        this.userID = userID;
        this.userPwd1 = userPwd1;
        this.userPwd2 = userPwd2;
        this.userName = userName;
        this.userNickname = userNickname;
        this.userEmail = userEmail;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserPwd1() {
        return userPwd1;
    }

    public void setUserPwd1(String userPwd1) {
        this.userPwd1 = userPwd1;
    }

    public String getUserPwd2() {
        return userPwd2;
    }

    public void setUserPwd2(String userPwd2) {
        this.userPwd2 = userPwd2;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
