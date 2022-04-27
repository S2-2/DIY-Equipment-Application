package kr.ac.kpu.diyequipmentapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

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
    ArrayList<EquipmentRegistration> filteredEquipementList;

    // 장비등록 페이지로 이동하는 버튼
    FloatingActionButton btnModelEnroll;
    ImageButton btnModelMap;
    EditText etSearch; //  검색필터링

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

        //검색에 의해 필터링 될 EquipmentRegistration 리스트
        filteredEquipementList = new ArrayList<EquipmentRegistration>();

        recyclerView.setAdapter(registrationAdapter);
        btnModelEnroll = findViewById(R.id.registrationRecyclerview_fab);      // 장비등록 버튼
        btnModelMap = findViewById(R.id.registrationRecyclerview_btn_map);   // 구글맵으로 이동
        etSearch = findViewById(R.id.registrationRecyclerview_et_search);

        // 이거다.
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                EquipmentRegistration equipmentRegistration = snapshot.getValue(EquipmentRegistration.class);
                equipmentRegistrationList.add(equipmentRegistration);
                filteredEquipementList.add(equipmentRegistration);
                registrationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = etSearch.getText().toString();
                // 검색 필터링 구현
                equipmentRegistrationList.clear();
                if(searchText.length()==0){
                    equipmentRegistrationList.addAll(filteredEquipementList);
                }
                else{
                    for( EquipmentRegistration equipment : filteredEquipementList)
                    {
                        if(equipment.getModelName().contains(searchText)||equipment.getModelInform().contains(searchText))
                        {
                            equipmentRegistrationList.add(equipment);
                        }
                    }
                }
                registrationAdapter.notifyDataSetChanged();
            }
        });

        btnModelEnroll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationRecyclerview.this, EquipmentRegistrationActivity.class); // 장비등록 페이지로 이동
                startActivity(intent);  //MainActivity 이동
            }
        });

        btnModelMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationRecyclerview.this, RentalGoogleMap.class);
                startActivity(intent);
            }
        });
    }
}