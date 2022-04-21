package kr.ac.kpu.diyequipmentapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private TextView etUserNickname, etTitle, etExplanation, etRentalType, etRentalCost, etUserLocation, etRentalPeriod;
    private Button btnMenu, btnBack, btnHome, btnChat;
    private String getImageUrl;
    private DecimalFormat decimalFormat;
    private String getRentalFeeCost, temp, getRentalAddress;
    private int temNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_detail);

        ivRentalImage = findViewById(R.id.iv_EquipmentImage);
        etUserNickname = findViewById(R.id.equipmentDetail_et_nickname);
        etTitle = findViewById(R.id.et_title);
        etExplanation = findViewById(R.id.equipmentDetail_et_explanation);
        etRentalType = findViewById(R.id.equipmentDetail_et_rentalType);
        etRentalCost = findViewById(R.id.equipmentDetail_et_rentalCost);
        etUserLocation = findViewById(R.id.equipmentDetail_et_location);
        etRentalPeriod = findViewById(R.id.equipmentDetail_et_rentalPeriod);
        btnChat =findViewById(R.id.equipmentDetail_btn_chatting);

        decimalFormat = new DecimalFormat("###,###");

        Intent intent = getIntent();
        getImageUrl = intent.getStringExtra("RentalImage");
        Picasso.get().load(getImageUrl).into(ivRentalImage);
        etTitle.setText("ModelName : "+intent.getStringExtra("ModelName"));
        etExplanation.setText("ModelInform : "+intent.getStringExtra("ModelInform"));
        etRentalType.setText("RentalType : "+intent.getStringExtra("RentalType"));
        //etRentalAddress.setText("RentalAddress : "+intent.getStringExtra("RentalAddress"));
        etUserNickname.setText("UserEmail : "+intent.getStringExtra("UserEmail"));
        etRentalPeriod.setText("RentalDate : "+intent.getStringExtra("RentalDate"));
        //etRentalCost.setText("RentalCost : "+intent.getStringExtra("RentalCost"));
        temp = intent.getStringExtra("RentalCost");
        getRentalAddress = intent.getStringExtra("RentalAddress");
        etUserLocation.setText("RentalAddress : "+getRentalAddress);

        if (temp.equals("무료"))
            etRentalCost.setText("RentalCost : "+temp);
        else {
            temNum = Integer.parseInt(temp);
            getRentalFeeCost = decimalFormat.format(temNum);
            etRentalCost.setText("RentalCost : "+getRentalFeeCost+"원");
        }

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EquipmentDetailActivity.this, ChatActivity.class);
                startActivity(intent); }
        });
    }
}