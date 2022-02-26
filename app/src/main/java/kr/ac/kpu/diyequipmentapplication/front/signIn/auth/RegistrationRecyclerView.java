package kr.ac.kpu.diyequipmentapplication.front.signIn.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import kr.ac.kpu.diyequipmentapplication.R;

//공급자가 입력한 데이터를 RecyclerView를 이용해 DIY-목록으로 보여주는 액티비티 클래스 구현
public class RegistrationRecyclerView extends AppCompatActivity {

    //RecyclerView 필드
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseStorage mStorage;
    RecyclerView recyclerView;
    RegistrationAdapter registrationAdapter;
    List<EquipmentRegistration> equipmentRegistrationList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_recycler_view);


        //RecyclerView 필드 참조
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("DIY_Model");
        mStorage = FirebaseStorage.getInstance();
        recyclerView = findViewById(R.id.recyclerview_id);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //RecyclerView에 RegistrationAdapter 클래스 등록 구현
        equipmentRegistrationList = new ArrayList<EquipmentRegistration>();
        registrationAdapter = new RegistrationAdapter(RegistrationRecyclerView.this,equipmentRegistrationList);

        recyclerView.setAdapter(registrationAdapter);

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                EquipmentRegistration equipmentRegistration = snapshot.getValue(EquipmentRegistration.class);
                equipmentRegistrationList.add(equipmentRegistration);
                registrationAdapter.notifyDataSetChanged();;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}