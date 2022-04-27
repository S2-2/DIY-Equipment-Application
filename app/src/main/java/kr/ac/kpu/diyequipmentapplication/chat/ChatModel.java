package kr.ac.kpu.diyequipmentapplication.chat;

public class ChatModel
{
    String userEmail;
    String userNickname;
    String userMsg;
    String timestamp;

    public ChatModel(){};

    public ChatModel(String userEmail, String userNickname, String userMsg, String timestamp) {
        this.userEmail = userEmail;
        this.userNickname = userNickname;
        this.userMsg = userMsg;
        this.timestamp = timestamp;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserMsg() {
        return userMsg;
    }

    public void setUserMsg(String userMsg) {
        this.userMsg = userMsg;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

// firebaseDB에 객체로 값 읽어오기기
}
