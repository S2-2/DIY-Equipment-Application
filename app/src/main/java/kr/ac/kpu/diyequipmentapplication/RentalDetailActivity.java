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


//미완성 장비
// 목록 클릭시 상세화면으로 전환되는 액티비티클래스
public class RentalDetailActivity extends AppCompatActivity {
    private ImageView ivRentalImage;
    private TextView etUserEmail, etModelName, etModelInform, etRentalType, etRentalCost, etRentalAddress, etRentalDate;
    private Button btn_back, btnRentalMap;
    private String getImageUrl;
    private DecimalFormat decimalFormat;
    private String getRentalFeeCost, temp, getRentalAddress;
    private int temNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_detail);

        ivRentalImage = findViewById(R.id.iv_rentalImage);
        etUserEmail = findViewById(R.id.ed_userEmail);
        etModelName = findViewById(R.id.ed_modelName);
        etModelInform = findViewById(R.id.ed_modelInform);
        etRentalType = findViewById(R.id.ed_rentalType);
        etRentalCost = findViewById(R.id.ed_rentalCost);
        etRentalAddress = findViewById(R.id.ed_rentalAddress);
        etRentalDate = findViewById(R.id.ed_rentalDate);
        btn_back = findViewById(R.id.btn_back);
        btnRentalMap = findViewById(R.id.btn_rentalGoogleMap);
        decimalFormat = new DecimalFormat("###,###");

        Intent intent = getIntent();
        getImageUrl = intent.getStringExtra("RentalImage");
        Picasso.get().load(getImageUrl).into(ivRentalImage);
        etModelName.setText("ModelName : "+intent.getStringExtra("ModelName"));
        etModelInform.setText("ModelInform : "+intent.getStringExtra("ModelInform"));
        etRentalType.setText("RentalType : "+intent.getStringExtra("RentalType"));
        //etRentalAddress.setText("RentalAddress : "+intent.getStringExtra("RentalAddress"));
        etUserEmail.setText("UserEmail : "+intent.getStringExtra("UserEmail"));
        etRentalDate.setText("RentalDate : "+intent.getStringExtra("RentalDate"));
        //etRentalCost.setText("RentalCost : "+intent.getStringExtra("RentalCost"));
        temp = intent.getStringExtra("RentalCost");
        getRentalAddress = intent.getStringExtra("RentalAddress");
        etRentalAddress.setText("RentalAddress : "+getRentalAddress);

        if (temp.equals("무료"))
            etRentalCost.setText("RentalCost : "+temp);
        else {
            temNum = Integer.parseInt(temp);
            getRentalFeeCost = decimalFormat.format(temNum);
            etRentalCost.setText("RentalCost : "+getRentalFeeCost+"원");
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();   //현재 액티비티 종료
            }
        });

        //상세 페이지에서 구글맵으로 이동하는 버튼 이벤트
        btnRentalMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(RentalDetailActivity.this, RentalGoogleMap.class);
                intent2.putExtra("GetRentalAddress", getRentalAddress);
                startActivity(intent2);
            }
        });
    }
}