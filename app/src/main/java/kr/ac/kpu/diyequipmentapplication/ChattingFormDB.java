package kr.ac.kpu.diyequipmentapplication;

public class ChattingFormDB
{
    String userName;
    String userMsg;
    String chatTime;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMsg() {
        return userMsg;
    }

    public void setUserMsg(String userMsg) {
        this.userMsg = userMsg;
    }

    public String getChatTime() {
        return chatTime;
    }

    public void setChatTime(String chatTime) {
        this.chatTime = chatTime;
    }

    public ChattingFormDB(String userName, String userMsg, String chatTime) {
        this.userName = userName;
        this.userMsg = userMsg;
        this.chatTime = chatTime;
    }

    // firebaseDB에 객체로 값 읽어오기기
}
