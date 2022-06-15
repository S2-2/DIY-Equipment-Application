package kr.ac.kpu.diyequipmentapplication.menu;

import java.io.Serializable;

public class ProfileDB implements Serializable {
    private String profileNickname;
    private String profileEmail;
    private String profileImage;

    public ProfileDB() {
    }

    public ProfileDB(String profileNickname, String profileEmail, String profileImage) {
        this.profileNickname = profileNickname;
        this.profileEmail = profileEmail;
        this.profileImage = profileImage;
    }

    public String getProfileNickname() {
        return profileNickname;
    }

    public void setProfileNickname(String profileNickname) {
        this.profileNickname = profileNickname;
    }

    public String getProfileEmail() {
        return profileEmail;
    }

    public void setProfileEmail(String profileEmail) {
        this.profileEmail = profileEmail;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
