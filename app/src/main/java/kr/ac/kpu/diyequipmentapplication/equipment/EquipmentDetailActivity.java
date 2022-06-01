package kr.ac.kpu.diyequipmentapplication.equipment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import kr.ac.kpu.diyequipmentapplication.MainActivity;
import kr.ac.kpu.diyequipmentapplication.R;
import kr.ac.kpu.diyequipmentapplication.chat.ChatActivity;
import kr.ac.kpu.diyequipmentapplication.chat.ChatStartActivity;
import kr.ac.kpu.diyequipmentapplication.community.CommunityRecyclerview;
import kr.ac.kpu.diyequipmentapplication.login.LoginActivity;
import kr.ac.kpu.diyequipmentapplication.menu.MenuSettingActivity;


//미완성 장비
// 목록 클릭시 상세화면으로 전환되는 액티비티클래스
public class EquipmentDetailActivity extends AppCompatActivity {
    private ImageView ivRentalImage;
    private TextView etUserNickname, etTitle, etExplanation, etRentalType, etRentalCost, etUserLocation, etRentalPeriod, etCategory;
    private Button btnChat;
    private String getImageUrl;
    private DecimalFormat decimalFormat;
    private String getRentalFeeCost, temp, getRentalAddress;
    private int temNum;
    private ImageButton imgBtn_back = null;
    private ImageButton imgBtn_home = null;


    //네비게이션 드로어 참조 변수
    private DrawerLayout mDrawerLayout;
    private Context context = this;
    private FirebaseAuth equipmentDetailFirebaseAuth;     //FirebaseAuth 참조 변수 선언
    private FirebaseFirestore equipmentDetailFirebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_detail);

        ivRentalImage = findViewById(R.id.iv_EquipmentImage);
        etUserNickname = findViewById(R.id.equipmentDetail_et_nickname);
        etTitle = findViewById(R.id.et_title);
        etExplanation = findViewById(R.id.equipmentDetail_et_explanation);
        etCategory = findViewById(R.id.equipmentDetail_et_category);
        etRentalType = findViewById(R.id.equipmentDetail_et_rentalType);
        etRentalCost = findViewById(R.id.equipmentDetail_et_rentalCost);
        etUserLocation = findViewById(R.id.equipmentDetail_et_location);
        etRentalPeriod = findViewById(R.id.equipmentDetail_et_rentalPeriod);
        btnChat =findViewById(R.id.equipmentDetail_btn_chatting);

        decimalFormat = new DecimalFormat("###,###");

        Intent intent = getIntent();
        getImageUrl = intent.getStringExtra("RentalImage");
        Picasso.get().load(getImageUrl).into(ivRentalImage);
        etTitle.setText("장비명: " + intent.getStringExtra("ModelName"));
        etExplanation.setText(intent.getStringExtra("ModelInform"));
        etCategory.setText(intent.getStringExtra("ModelCategory1") + " > " + intent.getStringExtra("ModelCategory2"));
        etRentalType.setText(intent.getStringExtra("RentalType"));
        //etRentalAddress.setText("RentalAddress : "+intent.getStringExtra("RentalAddress"));
        etUserNickname.setText("등록자 이메일: " + intent.getStringExtra("UserEmail"));
        etRentalPeriod.setText(intent.getStringExtra("RentalDate"));
        //etRentalCost.setText("RentalCost : "+intent.getStringExtra("RentalCost"));
        temp = intent.getStringExtra("RentalCost");
        getRentalAddress = intent.getStringExtra("RentalAddress");
        etUserLocation.setText(getRentalAddress);

        if (temp.equals("무료"))
            etRentalCost.setText("RentalCost : "+temp);
        else {
            temNum = Integer.parseInt(temp);
            getRentalFeeCost = decimalFormat.format(temNum);
            etRentalCost.setText("RentalCost : "+getRentalFeeCost+"원");
        }

        // 수정불가능
        etTitle.setEnabled(false);
        etExplanation.setEnabled(false);
        etUserNickname.setEnabled(false);
        etRentalType.setEnabled(false);
        etRentalPeriod.setEnabled(false);
        etCategory.setEnabled(false);
        etUserLocation.setEnabled(false);
        etRentalCost.setEnabled(false);

        imgBtn_back = (ImageButton)findViewById(R.id.signup_btn_back);
        imgBtn_home = (ImageButton)findViewById(R.id.registrationRecyclerview_btn_home);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View nav_header_view = navigationView.getHeaderView(0);
        TextView nav_header_nickname = (TextView) nav_header_view.findViewById(R.id.navi_header_tv_nickname);
        TextView nav_header_address = (TextView) nav_header_view.findViewById(R.id.navi_header_tv_userlocation);
        ImageButton nav_header_setting = (ImageButton) nav_header_view.findViewById(R.id.navi_header_btn_setting);

        nav_header_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EquipmentDetailActivity.this, MenuSettingActivity.class);
                startActivity(intent);
            }
        });


        equipmentDetailFirebaseAuth = FirebaseAuth.getInstance();                  //FirebaseAuth 초기화 및 객체 참조
        equipmentDetailFirebaseFirestore = FirebaseFirestore.getInstance();        //파이어스토어 초기화 및 객체 참조

        //DIY_Signup DB에서 사용자 계정에 맞는 닉네임 가져오는 기능 구현.
        //사용자 이메일 정보와 일치하는 데이터를 DIY_Signup DB에서 찾아서 etNickname 참조 변수에 닉네임 값 참조.
        equipmentDetailFirebaseFirestore.collection("DIY_Signup")
                .whereEqualTo("userEmail", equipmentDetailFirebaseAuth.getCurrentUser().getEmail().toString().trim())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                Log.d("main SignupDB", queryDocumentSnapshot.get("userNickname").toString().trim());
                                nav_header_nickname.setText(queryDocumentSnapshot.get("userNickname").toString().trim());
                            }
                        }
                    }
                });

        equipmentDetailFirebaseFirestore.collection("DIY_Equipment_Rental")
                .whereEqualTo("userEmail", equipmentDetailFirebaseAuth.getCurrentUser().getEmail().toString().trim())
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

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EquipmentDetailActivity.this, ChatActivity.class);
                startActivity(intent); }
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
                Intent intent = new Intent(EquipmentDetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //네비게이션 드로어 기능 구현
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
                    Toast.makeText(context, title + " 이동.", Toast.LENGTH_SHORT).show();
                } else if(id == R.id.startchatting){
                    Toast.makeText(context, title + " 이동.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EquipmentDetailActivity.this, ChatStartActivity.class);
                    startActivity(intent);
                } else if (id == R.id.diymap) {
                    Toast.makeText(context, title + " 이동.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EquipmentDetailActivity.this, RentalGoogleMap.class);
                    startActivity(intent);
                } else if(id == R.id.mycommunity){
                    Toast.makeText(context, title + " 이동.", Toast.LENGTH_SHORT).show();
                } else if(id == R.id.tradelist){
                    Toast.makeText(context, title + " 이동.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EquipmentDetailActivity.this, RegistrationRecyclerview.class);
                    startActivity(intent);
                } else if(id == R.id.communitylist) {
                    Toast.makeText(context, title + " 이동.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EquipmentDetailActivity.this, CommunityRecyclerview.class);
                    startActivity(intent);
                } else if(id == R.id.logout){
                    //Toast.makeText(context, title + ": 로그아웃", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder dlg = new AlertDialog.Builder(EquipmentDetailActivity.this);
                    dlg.setTitle("로그아웃");
                    dlg.setMessage("로그아웃 하시겠습니까?");
                    dlg.setIcon(R.mipmap.ic_launcher);

                    dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            equipmentDetailFirebaseAuth.signOut();
                            Toast.makeText(EquipmentDetailActivity.this, "로그아웃 되었습니다!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EquipmentDetailActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(EquipmentDetailActivity.this, "로그아웃 취소되었습니다!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dlg.show();
                }
                else if(id == R.id.withdraw){
                    //Toast.makeText(context, title + ": 회원탈퇴", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder dlg = new AlertDialog.Builder(EquipmentDetailActivity.this);
                    dlg.setTitle("회원 탈퇴");
                    dlg.setMessage("회원 탈퇴하시겠습니까?");
                    dlg.setIcon(R.mipmap.ic_launcher);

                    dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            equipmentDetailFirebaseAuth.getCurrentUser().delete();
                            Toast.makeText(EquipmentDetailActivity.this, "회원 탈퇴되었습니다!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EquipmentDetailActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(EquipmentDetailActivity.this, "회원 탈퇴 취소되었습니다!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dlg.show();
                }
                return true;
            }
        });
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