package kr.ac.kpu.diyequipmentapplication.front.signIn.auth;

//Firebase DB에 등록하기 위해 인증된 사용자 계정 클래스 선언
public class AuthUserAccount {
    private String emailId;     //Firebase 인증 이메일을 참조하기 위한 변수 선언
    private String password;    //Firebase 인증 패스워드을 참조하기 위한 변수 선언
    private String idToken;     //Firebase Uid 고유 토큰정보 참조하기 위한 변수 선언

    public AuthUserAccount() { }    //기본 생성자

    //클래스 필드 getter & setter 메서드 구현
    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
