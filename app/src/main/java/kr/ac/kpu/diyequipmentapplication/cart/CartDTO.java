package kr.ac.kpu.diyequipmentapplication.cart;

public class CartDTO {
    String userEmail;
    String equipList;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getEquipList() {
        return equipList;
    }

    public void setEquipList(String equpList) {
        this.equipList = equpList;
    }
}
