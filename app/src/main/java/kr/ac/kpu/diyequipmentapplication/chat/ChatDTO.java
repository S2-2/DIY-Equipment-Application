package kr.ac.kpu.diyequipmentapplication.chat;

public class ChatDTO
{
    String chatNum;
    String userNickname;
    String userEmail;
    String otherEmail;
//    String otherEmail;
    String userMsg;
    String timestamp;
    Boolean userReceived;

    public ChatDTO(){};

    public ChatDTO(String chatNum, String userNickname, String userEmail, String otherEmail, String userMsg, String timestamp) {
        this.chatNum = chatNum;
        this.userNickname = userNickname;
        this.userEmail = userEmail;
        this.otherEmail = otherEmail;
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

    public String getOtherEmail() { return otherEmail; }

    public void setOtherEmail(String otherEmail) { this.otherEmail = otherEmail; }

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

}
