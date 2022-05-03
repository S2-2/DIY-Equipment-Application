package kr.ac.kpu.diyequipmentapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

//공급자가 입력한 데이터를 RecyclerView를 이용해 DIY-목록으로 보여주는 액티비티 클래스 구현
public class RegistrationRecyclerview extends AppCompatActivity {
    FirebaseStorage mStorage;
    RecyclerView recyclerView;
    RegistrationAdapter registrationAdapter;
    ArrayList<EquipmentRegistration> equipmentRegistrationList;
    ArrayList<EquipmentRegistration> filteredEquipementList;

    private FirebaseFirestore rRfirebaseFirestoreDB = null;

    // 장비등록 페이지로 이동하는 버튼
    FloatingActionButton btnModelEnroll;
    ImageButton btnModelMap;
    EditText etSearch; //  검색필터링

    private ImageButton imgBtn_menu = null;
    private ImageButton imgBtn_back = null;
    private ImageButton imgBtn_home = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_recyclerview);

        //RecyclerView 필드 참조
        rRfirebaseFirestoreDB = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        recyclerView = findViewById(R.id.registrationRecyclerview_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));   //리사이클러뷰 세로 화면
        //recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)); //리사이클러뷰 가로 화면

        //RecyclerView에 RegistrationAdapter 클래스 등록 구현
        equipmentRegistrationList = new ArrayList<EquipmentRegistration>();
        registrationAdapter = new RegistrationAdapter(RegistrationRecyclerview.this,equipmentRegistrationList);

        //검색에 의해 필터링 될 EquipmentRegistration 리스트
        filteredEquipementList = new ArrayList<EquipmentRegistration>();

        recyclerView.setAdapter(registrationAdapter);
        btnModelEnroll = findViewById(R.id.registrationRecyclerview_fab);      // 장비등록 버튼
        btnModelMap = findViewById(R.id.registrationRecyclerview_btn_search);   // 구글맵으로 이동
        etSearch = findViewById(R.id.registrationRecyclerview_et_search);

        imgBtn_menu = (ImageButton)findViewById(R.id.registrationRecyclerview_btn_drawer);
        imgBtn_back = (ImageButton)findViewById(R.id.registrationRecyclerview_btn_back);
        imgBtn_home = (ImageButton)findViewById(R.id.registrationRecyclerview_btn_home);

        //Firestore DB 변경
        //Firestore DB에 등록된 장비 등록 정보 읽기 기능 구현
        rRfirebaseFirestoreDB.collection("DIY_Equipment_Rental")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                EquipmentRegistration equipmentRegistration = new EquipmentRegistration(
                                        queryDocumentSnapshot.get("modelName").toString().trim(),
                                        queryDocumentSnapshot.get("modelInform").toString().trim(),
                                        queryDocumentSnapshot.get("rentalImage").toString().trim(),
                                        queryDocumentSnapshot.get("rentalType").toString().trim(),
                                        queryDocumentSnapshot.get("rentalCost").toString().trim(),
                                        queryDocumentSnapshot.get("rentalAddress").toString().trim(),
                                        queryDocumentSnapshot.get("userEmail").toString().trim(),
                                        queryDocumentSnapshot.get("rentalDate").toString().trim(),
                                        queryDocumentSnapshot.get("modelCategory1").toString().trim(),
                                        queryDocumentSnapshot.get("modelCategory2").toString().trim());
                                equipmentRegistrationList.add(equipmentRegistration);
                                filteredEquipementList.add(equipmentRegistration);
                                registrationAdapter.notifyDataSetChanged();
                            }
                        }
                    }
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

        //메뉴 버튼 클릭시 메뉴 페이지 이동
        imgBtn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationRecyclerview.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        //뒤로가기 버튼 클릭시 장비 목록 페이지에서 장비 메인 페이지 이동
        imgBtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //홈 버튼 클릭시 장비 메인 페이지 이동
        imgBtn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationRecyclerview.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}