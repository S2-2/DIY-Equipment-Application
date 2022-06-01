package kr.ac.kpu.diyequipmentapplication.community;
import java.io.Serializable;

public class CommunityRegistration implements Serializable {
    private String CommunityTitle;      //커뮤니티 타이틀
    private String CommunityContent;    //커뮤니티 내용
    private String CommunityImage;      //커뮤니티 이미지
    private String CommunityNickname;   //커뮤니티 닉네임
    private String CommunityDateAndTime;   //커뮤니티 등록일

    public CommunityRegistration() {
    }

    public CommunityRegistration(String communityTitle, String communityContent, String communityImage, String communityNickname, String communityDateAndTime) {
        CommunityTitle = communityTitle;
        CommunityContent = communityContent;
        CommunityImage = communityImage;
        CommunityNickname = communityNickname;
        CommunityDateAndTime = communityDateAndTime;
    }

    public String getCommunityTitle() {
        return CommunityTitle;
    }

    public void setCommunityTitle(String communityTitle) {
        CommunityTitle = communityTitle;
    }

    public String getCommunityContent() {
        return CommunityContent;
    }

    public void setCommunityContent(String communityContent) {
        CommunityContent = communityContent;
    }

    public String getCommunityImage() {
        return CommunityImage;
    }

    public void setCommunityImage(String communityImage) {
        CommunityImage = communityImage;
    }

    public String getCommunityNickname() {
        return CommunityNickname;
    }

    public void setCommunityNickname(String communityNickname) {
        CommunityNickname = communityNickname;
    }

    public String getCommunityDateAndTime() {
        return CommunityDateAndTime;
    }

    public void setCommunityDateAndTime(String communityDateAndTime) {
        CommunityDateAndTime = communityDateAndTime;
    }
}
