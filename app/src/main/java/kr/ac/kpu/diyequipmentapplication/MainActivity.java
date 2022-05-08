package kr.ac.kpu.diyequipmentapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import kr.ac.kpu.diyequipmentapplication.chat.ChatStartActivity;

//Firebase 인증을 통해 접근 가능한 메인 액티비티 클래스
public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mainFirebaseAuth;     //FirebaseAuth 참조 변수 선언
    private EditText etNickname = null;        //사용자 별명 참조할 뷰 참조 변수 선언
    private FirebaseFirestore mainFirebaseFirestore = null;     //파이어스토어 참조 변수 선언


    //장비 목록 리사이클러뷰에 사용할 참조 변수 추가
    FirebaseStorage mStorage;
    RecyclerView recyclerView;
    RegistrationAdapter registrationAdapter;
    ArrayList<EquipmentRegistration> equipmentRegistrationList;
    ArrayList<EquipmentRegistration> filteredEquipementList;
    private FirebaseFirestore mainFirebaseFirestoreDB = null;

    //메뉴 아이콘 참조 변수
    private ImageButton imgBtn_drawerMenu = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFirebaseAuth = FirebaseAuth.getInstance();                  //FirebaseAuth 초기화 및 객체 참조
        etNickname = (EditText) findViewById(R.id.main_et_nickname);    //사용자 닉네임 참조
        mainFirebaseFirestore = FirebaseFirestore.getInstance();        //파이어스토어 초기화 및 객체 참조

        imgBtn_drawerMenu = (ImageButton)findViewById(R.id.registrationRecyclerview_btn_drawer);    //메뉴 아이콘 객체 참조

        //DIY_Signup DB에서 사용자 계정에 맞는 닉네임 가져오는 기능 구현.
        //사용자 이메일 정보와 일치하는 데이터를 DIY_Signup DB에서 찾아서 etNickname 참조 변수에 닉네임 값 참조.
        mainFirebaseFirestore.collection("DIY_Signup")
                .whereEqualTo("userEmail", mainFirebaseAuth.getCurrentUser().getEmail().toString().trim())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                Log.d("main SignupDB", queryDocumentSnapshot.get("userNickname").toString().trim());
                                etNickname.setText(queryDocumentSnapshot.get("userNickname").toString().trim());
                            }
                        }
                    }
                });

        // 공구 거래 목록으로 이동
        ImageButton btn_rentalList = findViewById(R.id.main_btn_rentalList);
        btn_rentalList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegistrationRecyclerview.class);
                startActivity(intent);
            }
        });

        // 커뮤니티로 이동
        ImageButton btn_community = findViewById(R.id.main_btn_community);
        btn_community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CommunityActivity.class);
                startActivity(intent);
            }
        });


        //장비 목록 RecyclerView 필드 참조
        mainFirebaseFirestoreDB = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        recyclerView = findViewById(R.id.main_recyclerview);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));   //리사이클러뷰 세로 화면모드
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)); //리사이클러뷰 가로 화면모드

        //RecyclerView에 RegistrationAdapter 클래스 등록 구현
        equipmentRegistrationList = new ArrayList<EquipmentRegistration>();
        registrationAdapter = new RegistrationAdapter(MainActivity.this,equipmentRegistrationList);

        //검색에 의해 필터링 될 EquipmentRegistration 리스트
        filteredEquipementList = new ArrayList<EquipmentRegistration>();

        recyclerView.setAdapter(registrationAdapter);

        //Firestore DB 변경
        //Firestore DB에 등록된 장비 등록 정보 읽기 기능 구현
        mainFirebaseFirestoreDB.collection("DIY_Equipment_Rental")
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

        //메뉴 아이콘 클릭 이벤트
        imgBtn_drawerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
    }
}