package kr.ac.kpu.diyequipmentapplication.cart;

public class CartDTO {
    String userEmail;
    String equipTitle;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getEquipTitle() { return equipTitle; }

    public void setEquipTitle(String equipTitle) { this.equipTitle = equipTitle; }
}
