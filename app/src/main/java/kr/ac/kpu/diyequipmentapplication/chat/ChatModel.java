package kr.ac.kpu.diyequipmentapplication.chat;

public class ChatModel
{
    String chatNum;
    String userNickname;
    String userEmail;
    String userMsg;
    String timestamp;
    Boolean userReceived;

    public ChatModel(){};

    public ChatModel(String chatNum, String userNickname, String userEmail, String userMsg, String timestamp) {
        this.chatNum = chatNum;
        this.userNickname = userNickname;
        this.userEmail = userEmail;
        this.userMsg = userMsg;
        this.timestamp = timestamp;
        this.userReceived = false;
    }

    public String getChatNum() {
        return chatNum;
    }

    public void setChatNum(String chatNum) {
        this.chatNum = chatNum;
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

    public Boolean getUserReceived() {
        return userReceived;
    }

    public void setUserReceived(Boolean userReceived) {
        this.userReceived = userReceived;
    }

// firebaseDB에 객체로 값 읽어오기기
}
