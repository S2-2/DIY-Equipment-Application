package kr.ac.kpu.diyequipmentapplication.cart;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.WriteResult;

import java.util.HashMap;
import java.util.Map;

public class CartActivty {

    private FirebaseFirestore cartFirebaseFirestore;                  // 파이어스토어 초기화 및 객체 참조
    private DocumentReference equipFirestoreDocRef;
    private CartDTO cartDTO;
    private String cartDocID, equipDocID;

    public CartActivty() {
        this.cartFirebaseFirestore = FirebaseFirestore.getInstance();
        this.cartDocID = null;
        this.equipDocID = null;
        this.cartDTO = new CartDTO();
    }

    public void addCart(String email, String title){
        cartDTO.setUserEmail(email);
        cartDTO.setEquipTitle(title);
        cartFirebaseFirestore.collection("DIY_MyCart").document().set(cartDTO).addOnSuccessListener(new OnSuccessListener<Void>() {
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

        cartFirebaseFirestore.collection("DIY_Equipment_Rental")
                .whereEqualTo("modelName",title)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String likeNum = "0";
                        int operator = 0;
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                if(queryDocumentSnapshot.get("modelLikeNum") != null){
                                    likeNum = queryDocumentSnapshot.get("modelLikeNum").toString().trim();
                                    operator = Integer.parseInt(likeNum);
                                    operator++;
                                    likeNum = Integer.toString(operator);
                                }
                            }
                        }
                        equipDocID = task.getResult().getDocuments().get(0).getId();
                        equipFirestoreDocRef = cartFirebaseFirestore.collection("DIY_Equipment_Rental").document(equipDocID);
                        equipFirestoreDocRef.update("modelLikeNum",likeNum);
                    }
                });

    }

    public void removeCart(String email, String title){

        Map<String, Object> updates = new HashMap<>();
        updates.put("equipTitle", FieldValue.delete());
        cartDTO.setUserEmail(email);
        cartDTO.setEquipTitle(title);

        cartFirebaseFirestore.collection("DIY_MyCart")
                .whereEqualTo("equipTitle",title)
                .whereEqualTo("userEmail",email)
                .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                cartDocID = task.getResult().getDocuments().get(0).getId();
                cartFirebaseFirestore.collection("DIY_MyCart").document(cartDocID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e("DB-Removed", "DocumentSnapshot removed successfully!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("DB-Removed", "DocumentSnapshot removed failed!");
                    }
                });
            }
        });

        cartFirebaseFirestore.collection("DIY_Equipment_Rental")
                .whereEqualTo("modelName",title)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String likeNum = "0";
                        int operator = 0;
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                if(queryDocumentSnapshot.get("modelLikeNum")!=null){
                                    likeNum = queryDocumentSnapshot.get("modelLikeNum").toString().trim();
                                    operator = Integer.parseInt(likeNum);
                                    operator--;
                                    likeNum = Integer.toString(operator);
                                }
                            }
                        }
                        equipDocID = task.getResult().getDocuments().get(0).getId();
                        equipFirestoreDocRef = cartFirebaseFirestore.collection("DIY_Equipment_Rental").document(equipDocID);
                        equipFirestoreDocRef.update("modelLikeNum",likeNum);
                    }
                });


    }
}
