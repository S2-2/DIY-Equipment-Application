package kr.ac.kpu.diyequipmentapplication.cart;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CartActivty {

    private FirebaseFirestore cartFirebaseFirestore;                  // 파이어스토어 초기화 및 객체 참조
    private CartDTO cartDTO;

    public CartActivty() {
        this.cartFirebaseFirestore = FirebaseFirestore.getInstance();
        this.cartDTO = new CartDTO();
    }

    public void addCart(String email, String title){
        cartDTO.setUserEmail(email);
        cartDTO.setEquipTitle(title);
        cartFirebaseFirestore.collection("DIY_MyCart").document(email).set(cartDTO).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("DB-Add", "DocumentSnapshot successfully added!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("DB-Add", "DocumentSnapshot added failed!");
            }
        });
    }

    public void removeCart(String email, String title){

        Map<String, Object> updates = new HashMap<>();
        updates.put("equipTitle", FieldValue.delete());
        cartDTO.setUserEmail(email);
        cartDTO.setEquipTitle(title);
        cartFirebaseFirestore.collection("DIY_MyCart").document(email).update(updates).addOnCompleteListener(new OnCompleteListener<Void>(){

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e("DB-Update", "DocumentSnapshot updated!");
            }
        });
    }



}
