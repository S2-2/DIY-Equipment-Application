package kr.ac.kpu.diyequipmentapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

//공급자가 입력한 데이터를 RecyclerView를 이용해 DIY-목록으로 보여주는 액티비티 클래스 구현
public class RegistrationRecyclerview extends AppCompatActivity {

    //RecyclerView 필드
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseStorage mStorage;
    RecyclerView recyclerView;
    RegistrationAdapter registrationAdapter;
    //List<EquipmentRegistration> equipmentRegistrationList;
    ArrayList<EquipmentRegistration> equipmentRegistrationList;

    // 장비등록 페이지로 이동하는 버튼
    FloatingActionButton btnModelEnroll;    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_recyclerview);

        //RecyclerView 필드 참조
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("DIY_Equipment_Rental");
        mStorage = FirebaseStorage.getInstance();
        recyclerView = findViewById(R.id.registrationRecyclerview_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //RecyclerView에 RegistrationAdapter 클래스 등록 구현
        equipmentRegistrationList = new ArrayList<EquipmentRegistration>();
        registrationAdapter = new RegistrationAdapter(RegistrationRecyclerview.this,equipmentRegistrationList);

        recyclerView.setAdapter(registrationAdapter);
        btnModelEnroll = findViewById(R.id.registrationRecyclerview_fab);      // 장비등록 버튼

        // 이거다.
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                EquipmentRegistration equipmentRegistration = snapshot.getValue(EquipmentRegistration.class);
                equipmentRegistrationList.add(equipmentRegistration);
                registrationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        btnModelEnroll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationRecyclerview.this, EquipmentRegistrationActivity.class); // 장비등록 페이지로 이동
                startActivity(intent);  //MainActivity 이동
            }
        });
    }
}