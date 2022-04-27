package kr.ac.kpu.diyequipmentapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import kr.ac.kpu.diyequipmentapplication.chat.ChatActivity;


//미완성 장비
// 목록 클릭시 상세화면으로 전환되는 액티비티클래스
public class EquipmentDetailActivity extends AppCompatActivity {
    private ImageView ivRentalImage;
    private TextView etUserNickname, etTitle, etExplanation, etRentalType, etRentalCost, etUserLocation, etRentalPeriod, etCategory;
    private Button btnMenu, btnBack, btnHome, btnChat;
    private String getImageUrl;
    private DecimalFormat decimalFormat;
    private String getRentalFeeCost, temp, getRentalAddress;
    private int temNum;

    private ImageButton imgBtn_menu = null;
    private ImageButton imgBtn_back = null;
    private ImageButton imgBtn_home = null;
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

        imgBtn_menu = (ImageButton)findViewById(R.id.registrationRecyclerview_btn_drawer);
        imgBtn_back = (ImageButton)findViewById(R.id.signup_btn_back);
        imgBtn_home = (ImageButton)findViewById(R.id.registrationRecyclerview_btn_home);


        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EquipmentDetailActivity.this, ChatActivity.class);
                startActivity(intent); }
        });

        //메뉴 버튼 클릭시 메뉴 페이지 이동
        imgBtn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
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
                Intent intent = new Intent(getApplicationContext(), DiyMainActivity.class);
                startActivity(intent);
            }
        });
    }
}