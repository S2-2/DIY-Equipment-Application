package kr.ac.kpu.diyequipmentapplication.community;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import kr.ac.kpu.diyequipmentapplication.MainActivity;
import kr.ac.kpu.diyequipmentapplication.R;
import kr.ac.kpu.diyequipmentapplication.chat.ChatStartActivity;
import kr.ac.kpu.diyequipmentapplication.equipment.RegistrationRecyclerview;
import kr.ac.kpu.diyequipmentapplication.equipment.RentalGoogleMap;
import kr.ac.kpu.diyequipmentapplication.login.LoginActivity;
import kr.ac.kpu.diyequipmentapplication.menu.MenuSettingActivity;

public class CommunityDetailActivity extends AppCompatActivity {
    //커뮤니티 상세페이지 참조변수 선언
    private ImageView ivUserProfilePhoto, ivEquipmentImage;
    private TextView tvNickname, tvLocation, tvTime, tvContents;
    private String getUserProfileImageUrl;
    private String getEquipmentImageUrl;
    private String getUserEmail;
    private String tempNickname;

    private ImageButton imgBtn_back;
    private ImageButton imgBtn_home;

    //커뮤니티 댓글 참조 변수
    private ImageButton imgBtnCommentLike;
    private EditText etComment;
    private Button btnComment;
    private Boolean likeFlag;
    private SimpleDateFormat commentDateFormat;   //등록 날짜 형식 참조할 변수
    private Date commentDate;   //등록 날짜 참조할 변수
    private String commentGetDate;      //장비 등록 날짜 참조 변수
    private Button btnCommentList;

    //네비게이션 드로어 참조 변수
    private DrawerLayout mDrawerLayout;
    private Context context = this;
    private FirebaseAuth communityDetailFirebaseAuth;     //FirebaseAuth 참조 변수 선언
    private FirebaseFirestore communityDetailFirebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_detail);

        ivUserProfilePhoto = (ImageView) findViewById(R.id.iv_UserProfilePhoto);
        ivEquipmentImage = (ImageView) findViewById(R.id.iv_EquipmentImage);
        tvNickname = (TextView) findViewById(R.id.communityDetail_tv_nickname);
        tvLocation = (TextView) findViewById(R.id.communityDetail_tv_location);
        tvTime = (TextView) findViewById(R.id.communityDetail_tv_time);
        tvContents = (TextView) findViewById(R.id.communityDetail_tv_contents);
        imgBtn_back = (ImageButton) findViewById(R.id.signup_btn_back);
        imgBtn_home = (ImageButton) findViewById(R.id.registrationRecyclerview_btn_home);

        btnCommentList = (Button) findViewById(R.id.communityDetail_btn_commentList);
        etComment = (EditText) findViewById(R.id.communityDetail_et_comment);
        btnComment = (Button) findViewById(R.id.communityDetail_btn_commentAdd);
        likeFlag = false;
        commentDateFormat = new SimpleDateFormat("yyyy-MM-dd E, hh:mm:ss");    //날짜 형식 설정 객체 생성 및 초기화
        commentDate = new Date();  //날짜 객체 생성 및 초기화
        commentGetDate = commentDateFormat.format(commentDate);  //장비 등록일 참조

        communityDetailFirebaseAuth = FirebaseAuth.getInstance();                  //FirebaseAuth 초기화 및 객체 참조
        communityDetailFirebaseFirestore = FirebaseFirestore.getInstance();        //파이어스토어 초기화 및 객체 참조

        Intent intent = getIntent();
        getUserProfileImageUrl = intent.getStringExtra("CommunityImage");
        Picasso.get().load(getUserProfileImageUrl).into(ivUserProfilePhoto);
        getEquipmentImageUrl = getUserProfileImageUrl;
        Picasso.get().load(getEquipmentImageUrl).into(ivEquipmentImage);
        tempNickname = intent.getStringExtra("CommunityNickname");
        tvNickname.setText("작성자: " + tempNickname);
        //tvLocation.setText("작성자 위치: ");
        tvTime.setText("작성 시간: " + intent.getStringExtra("CommunityDateAndTime"));
        tvContents.setText(intent.getStringExtra("CommunityContents"));

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View nav_header_view = navigationView.getHeaderView(0);
        TextView nav_header_nickname = (TextView) nav_header_view.findViewById(R.id.navi_header_tv_nickname);
        TextView nav_header_address = (TextView) nav_header_view.findViewById(R.id.navi_header_tv_userlocation);
        ImageButton nav_header_setting = (ImageButton) nav_header_view.findViewById(R.id.navi_header_btn_setting);

        nav_header_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommunityDetailActivity.this, MenuSettingActivity.class);
                startActivity(intent);
            }
        });

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //공급자가 입력한 모델명 및 공구 설명
                final Boolean getLike = likeFlag;
                final String getComment = etComment.getText().toString();
                final String getNickname = nav_header_nickname.getText().toString();
                final String getDate = commentGetDate;

                //공급자가 입력한 데이터 등록 성공
                if (!(getComment.isEmpty()))
                {
                    CommunityComment communityComment = new CommunityComment(getLike, getComment, getNickname, getDate);
                    communityDetailFirebaseFirestore.collection("DIY_Equipment_CommunityComment").document().set(communityComment);
                    Toast.makeText(getApplicationContext(), "댓글 추가했습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "댓글 입력하세요!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCommentList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommunityDetailActivity.this, CommunityCommentRecyclerview.class);
                startActivity(intent);
            }
        });

        //DIY_Signup DB에서 사용자 계정에 맞는 닉네임 가져오는 기능 구현.
        //사용자 이메일 정보와 일치하는 데이터를 DIY_Signup DB에서 찾아서 etNickname 참조 변수에 닉네임 값 참조.
        communityDetailFirebaseFirestore.collection("DIY_Signup")
                .whereEqualTo("userEmail", communityDetailFirebaseAuth.getCurrentUser().getEmail().toString().trim())
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

        communityDetailFirebaseFirestore.collection("DIY_Equipment_Rental")
                .whereEqualTo("userEmail", communityDetailFirebaseAuth.getCurrentUser().getEmail().toString().trim())
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

        communityDetailFirebaseFirestore.collection("DIY_Signup")
                .whereEqualTo("userNickname",tempNickname )
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                Log.d("main SignupDB", queryDocumentSnapshot.get("userEmail").toString().trim());
                                getUserEmail = queryDocumentSnapshot.get("userEmail").toString().trim();

                                communityDetailFirebaseFirestore.collection("DIY_Equipment_Rental")
                                        .whereEqualTo("userEmail", getUserEmail)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                        tvLocation.setText("작성자 위치: "+queryDocumentSnapshot.get("rentalAddress").toString().trim());
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
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
                Intent intent = new Intent(CommunityDetailActivity.this, MainActivity.class);
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
                    Intent intent = new Intent(CommunityDetailActivity.this, ChatStartActivity.class);
                    startActivity(intent);
                } else if (id == R.id.diymap) {
                    Toast.makeText(context, title + " 이동.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CommunityDetailActivity.this, RentalGoogleMap.class);
                    startActivity(intent);
                } else if(id == R.id.mycommunity){
                    Toast.makeText(context, title + " 이동.", Toast.LENGTH_SHORT).show();
                } else if(id == R.id.tradelist){
                    Toast.makeText(context, title + " 이동.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CommunityDetailActivity.this, RegistrationRecyclerview.class);
                    startActivity(intent);
                } else if(id == R.id.communitylist) {
                    Toast.makeText(context, title + " 이동.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CommunityDetailActivity.this, CommunityRecyclerview.class);
                    startActivity(intent);
                } else if(id == R.id.logout){
                    //Toast.makeText(context, title + ": 로그아웃", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder dlg = new AlertDialog.Builder(CommunityDetailActivity.this);
                    dlg.setTitle("로그아웃");
                    dlg.setMessage("로그아웃 하시겠습니까?");
                    dlg.setIcon(R.mipmap.ic_launcher);

                    dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            communityDetailFirebaseAuth.signOut();
                            Toast.makeText(CommunityDetailActivity.this, "로그아웃 되었습니다!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CommunityDetailActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(CommunityDetailActivity.this, "로그아웃 취소되었습니다!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dlg.show();
                }
                else if(id == R.id.withdraw){
                    //Toast.makeText(context, title + ": 회원탈퇴", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder dlg = new AlertDialog.Builder(CommunityDetailActivity.this);
                    dlg.setTitle("회원 탈퇴");
                    dlg.setMessage("회원 탈퇴하시겠습니까?");
                    dlg.setIcon(R.mipmap.ic_launcher);

                    dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            communityDetailFirebaseAuth.getCurrentUser().delete();
                            Toast.makeText(CommunityDetailActivity.this, "회원 탈퇴되었습니다!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CommunityDetailActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(CommunityDetailActivity.this, "회원 탈퇴 취소되었습니다!", Toast.LENGTH_SHORT).show();
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