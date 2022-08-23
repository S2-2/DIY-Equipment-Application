package kr.ac.kpu.diyequipmentapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import kr.ac.kpu.diyequipmentapplication.cart.CartRecyclerview;
import kr.ac.kpu.diyequipmentapplication.chat.ChatStartActivity;
import kr.ac.kpu.diyequipmentapplication.chat.FcmDTO;
import kr.ac.kpu.diyequipmentapplication.community.CommunityAdapter;
import kr.ac.kpu.diyequipmentapplication.community.CommunityRecyclerview;
import kr.ac.kpu.diyequipmentapplication.community.CommunityRegistration;
import kr.ac.kpu.diyequipmentapplication.equipment.RegistrationAdapter;
import kr.ac.kpu.diyequipmentapplication.equipment.RegistrationDTO;
import kr.ac.kpu.diyequipmentapplication.equipment.RegistrationRecyclerview;
import kr.ac.kpu.diyequipmentapplication.equipment.RentalGoogleMap;
import kr.ac.kpu.diyequipmentapplication.login.LoginActivity;
import kr.ac.kpu.diyequipmentapplication.menu.MenuSettingActivity;

//Firebase 인증을 통해 접근 가능한 메인 액티비티 클래스
public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mainFirebaseAuth;     //FirebaseAuth 참조 변수 선언
    private EditText etNickname;        //사용자 별명 참조할 뷰 참조 변수 선언
    private FirebaseFirestore mainFirebaseFirestore;     //파이어스토어 참조 변수 선언
    private FirebaseDatabase mainFirebaseDatabase;      // 파이어데이터베이스 참조 변수 선언
    private FirebaseUser mainFirebaseUser;   //사용자 정보 참조 변수 선언

    //장비 목록 리사이클러뷰에 사용할 참조 변수 추가
    FirebaseStorage mStorage;
    RecyclerView recyclerView;
    RegistrationAdapter registrationAdapter;
    ArrayList<RegistrationDTO> equipmentRegistrationList;
    ArrayList<RegistrationDTO> filteredEquipementList;
    private FirebaseFirestore mainFirebaseFirestoreDB;
    private FirebaseFirestore mainFirebaseFirestoreDB2;

    // 커뮤니티 리사이클러뷰에 사용할 참조 변수 추가
    RecyclerView recyclerView2;
    CommunityAdapter communityAdapter;
    ArrayList<CommunityRegistration> communityRegistrationList;

    //네비게이션 드로어 참조 변수
    private DrawerLayout mDrawerLayout;
    private Context context = this;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFirebaseAuth = FirebaseAuth.getInstance();                  //FirebaseAuth 초기화 및 객체 참조
        etNickname = (EditText) findViewById(R.id.main_et_nickname);    //사용자 닉네임 참조
        mainFirebaseFirestore = FirebaseFirestore.getInstance();        //파이어스토어 초기화 및 객체 참조

        etNickname.setEnabled(false);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View nav_header_view = navigationView.getHeaderView(0);
        TextView nav_header_nickname = (TextView) nav_header_view.findViewById(R.id.navi_header_tv_nickname);
        TextView nav_header_address = (TextView) nav_header_view.findViewById(R.id.navi_header_tv_userLocation);
        ImageButton nav_header_setting = (ImageButton) nav_header_view.findViewById(R.id.navi_header_btn_setting);

        nav_header_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MenuSettingActivity.class);
                startActivity(intent);
            }
        });

        // 사용자 토큰 및 정보 업데이트
        updateUserProfile();

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
                                nav_header_nickname.setText(queryDocumentSnapshot.get("userNickname").toString().trim());
                            }
                        }
                    }
                });

        mainFirebaseFirestore.collection("DIY_Equipment_Rental")
                .whereEqualTo("userEmail", mainFirebaseAuth.getCurrentUser().getEmail().toString().trim())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                nav_header_address.setText(queryDocumentSnapshot.get("rentalAddress").toString().trim());
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
                Intent intent = new Intent(MainActivity.this, CommunityRecyclerview.class);
                startActivity(intent);
            }
        });

        //장비 목록 RecyclerView 필드 참조
        mainFirebaseFirestoreDB = FirebaseFirestore.getInstance();
        mainFirebaseFirestoreDB2 = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        recyclerView = findViewById(R.id.main_recyclerview);
        recyclerView.setHasFixedSize(true);

        //recyclerView.setLayoutManager(new LinearLayoutManager(this));   //리사이클러뷰 세로 화면모드
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)); //리사이클러뷰 가로 화면모드

        //RecyclerView에 RegistrationAdapter 클래스 등록 구현
        equipmentRegistrationList = new ArrayList<RegistrationDTO>();
        registrationAdapter = new RegistrationAdapter(MainActivity.this,equipmentRegistrationList);

        //검색에 의해 필터링 될 EquipmentRegistration 리스트
        filteredEquipementList = new ArrayList<RegistrationDTO>();
        recyclerView.setAdapter(registrationAdapter);

        //Firestore DB에 등록된 장비 등록 정보 읽기 기능 구현
        mainFirebaseFirestoreDB.collection("DIY_Equipment_Rental")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                if(queryDocumentSnapshot.get("modelLikeNum")!= null) {
                                    Log.e("MainActivity","null아님");
                                    RegistrationDTO registrationDTO = new RegistrationDTO(
                                            queryDocumentSnapshot.get("modelName").toString().trim(),
                                            queryDocumentSnapshot.get("modelInform").toString().trim(),
                                            queryDocumentSnapshot.get("rentalImage").toString().trim(),
                                            queryDocumentSnapshot.get("rentalType").toString().trim(),
                                            queryDocumentSnapshot.get("rentalCost").toString().trim(),
                                            queryDocumentSnapshot.get("rentalAddress").toString().trim(),
                                            queryDocumentSnapshot.get("userEmail").toString().trim(),
                                            queryDocumentSnapshot.get("rentalDate").toString().trim(),
                                            queryDocumentSnapshot.get("modelCategory1").toString().trim(),
                                            queryDocumentSnapshot.get("modelCategory2").toString().trim(),
                                            queryDocumentSnapshot.get("modelLikeNum").toString().trim(),
                                            queryDocumentSnapshot.getId());
                                    equipmentRegistrationList.add(registrationDTO);
                                    filteredEquipementList.add(registrationDTO);
                                }
                                else{
                                    Log.e("MainActivity","null");
                                    RegistrationDTO registrationDTO = new RegistrationDTO(
                                            queryDocumentSnapshot.get("modelName").toString().trim(),
                                            queryDocumentSnapshot.get("modelInform").toString().trim(),
                                            queryDocumentSnapshot.get("rentalImage").toString().trim(),
                                            queryDocumentSnapshot.get("rentalType").toString().trim(),
                                            queryDocumentSnapshot.get("rentalCost").toString().trim(),
                                            queryDocumentSnapshot.get("rentalAddress").toString().trim(),
                                            queryDocumentSnapshot.get("userEmail").toString().trim(),
                                            queryDocumentSnapshot.get("rentalDate").toString().trim(),
                                            queryDocumentSnapshot.get("modelCategory1").toString().trim(),
                                            queryDocumentSnapshot.get("modelCategory2").toString().trim(),
                                            "00",
                                            queryDocumentSnapshot.getId());
                                    equipmentRegistrationList.add(registrationDTO);
                                    filteredEquipementList.add(registrationDTO);
                                }
                                registrationAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });


        // 커뮤니티 목록 RecyclerView 필드 참조
        recyclerView2 = findViewById(R.id.main_community_recyclerview);
        recyclerView2.setHasFixedSize(true);

        recyclerView2.setLayoutManager(new LinearLayoutManager(this));   //리사이클러뷰 세로 화면모드

        //RecyclerView2에 CommunityAdapter 클래스 등록 구현
        communityRegistrationList = new ArrayList<CommunityRegistration>();
        communityAdapter = new CommunityAdapter(MainActivity.this,communityRegistrationList);
        recyclerView2.setAdapter(communityAdapter);

        //Firestore DB에 등록된 장비 등록 정보 읽기 기능 구현
        mainFirebaseFirestoreDB2.collection("DIY_Equipment_Community")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                CommunityRegistration communityRegistration = new CommunityRegistration(
                                        queryDocumentSnapshot.get("communityTitle").toString().trim(),
                                        queryDocumentSnapshot.get("communityContent").toString().trim(),
                                        queryDocumentSnapshot.get("communityImage").toString().trim(),
                                        queryDocumentSnapshot.get("communityNickname").toString().trim(),
                                        queryDocumentSnapshot.get("communityDateAndTime").toString().trim());
                                communityRegistrationList.add(communityRegistration);
                                communityAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });


        //네비게이션 드로어 기능 구현(툴바)
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24); //뒤로가기 버튼 이미지 지정
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if(id == R.id.tradedetail){
                    //Toast.makeText(context, title + ": 거래내역.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "거래내역으로 접속되었습니다!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, RentalHistoryRecyclerviewActivity.class);
                    startActivity(intent);
                }
                else if(id == R.id.startchatting){
                    //Toast.makeText(context, title + ": 채팅.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "채팅창으로 접속되었습니다!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, ChatStartActivity.class);
                    startActivity(intent);
                } else if (id == R.id.diymap) {
                    Toast.makeText(MainActivity.this, "장비대여맵으로 접속되었습니다!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, RentalGoogleMap.class);
                    startActivity(intent);
                }
                else if(id == R.id.mycommunity){
                    Toast.makeText(context, title + ": 내가 쓴 커뮤니티", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.tradelist){
                    //Toast.makeText(context, title + ": 거래 목록", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "거래목록으로 접속되었습니다!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, RegistrationRecyclerview.class);
                    startActivity(intent);
                }
                else if(id == R.id.communitylist){
                    //Toast.makeText(context, title + ": 커뮤니티 목록", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "커뮤니티 목록으로 접속되었습니다!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, CommunityRecyclerview.class);
                    startActivity(intent);
                }
                else  if(id == R.id.mycart){
                    Intent intent = new Intent(MainActivity.this, CartRecyclerview.class);
                    startActivity(intent);
                    finish();
                }
//                else if(id == R.id.locationset){
//                    Toast.makeText(context, title + ": 위치설정", Toast.LENGTH_SHORT).show();
//                }
                else if(id == R.id.logout){
                    //Toast.makeText(context, title + ": 로그아웃", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                    dlg.setTitle("로그아웃");
                    dlg.setMessage("로그아웃 하시겠습니까?");
                    dlg.setIcon(R.mipmap.ic_launcher);

                    dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mainFirebaseAuth.signOut();
                            Toast.makeText(MainActivity.this, "로그아웃 되었습니다!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MainActivity.this, "로그아웃 취소되었습니다!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dlg.show();
                }
                else if(id == R.id.withdraw){
                    //Toast.makeText(context, title + ": 회원탈퇴", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                    dlg.setTitle("회원 탈퇴");
                    dlg.setMessage("회원 탈퇴하시겠습니까?");
                    dlg.setIcon(R.mipmap.ic_launcher);

                    dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mainFirebaseAuth.getCurrentUser().delete();
                            Toast.makeText(MainActivity.this, "회원 탈퇴되었습니다!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MainActivity.this, "회원 탈퇴 취소되었습니다!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dlg.show();
                }

                return true;
            }
        });
    }

    private void updateUserProfile() {
        mainFirebaseUser = mainFirebaseAuth.getCurrentUser();
        mainFirebaseDatabase = FirebaseDatabase.getInstance();
        String email;

        if(mainFirebaseUser == null){

        }else{
            FcmDTO fcmData = new FcmDTO();
            fcmData.setUserEmail(mainFirebaseUser.getEmail().toString());
            email = fcmData.getUserEmail();
            fcmData.setFcmToken(FirebaseMessaging.getInstance().getToken().toString());
            email = email.substring(0,email.indexOf('@'));

            mainFirebaseDatabase.getReference("DIY_FcmUserData").child(email).setValue(fcmData);
            Log.e("MainActivity","updateProfile: " + fcmData.getUserEmail() + " / " + fcmData.getFcmToken());
            Log.e("MainActivity","updateProfile: " + email);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}