package kr.ac.kpu.diyequipmentapplication;

//Firebase DB에 등록하기 위해 인증된 사용자 계정 클래스 선언
public class AuthUserAccount {
    private String userEmail;     //Firebase 인증 이메일을 참조하기 위한 변수 선언
    private String userID;    //Firebase 인증 패스워드을 참조하기 위한 변수 선언
    private String userName;
    private String userNickname;
    private String userPwd1;
    private String userPwd2;

    public AuthUserAccount() { }    //기본 생성자

    public AuthUserAccount(String userEmail, String userID, String userName, String userNickname, String userPwd1, String userPwd2) {
        this.userEmail = userEmail;
        this.userID = userID;
        this.userName = userName;
        this.userNickname = userNickname;
        this.userPwd1 = userPwd1;
        this.userPwd2 = userPwd2;
    }
    //클래스 필드 getter & setter 메서드 구현
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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
}
