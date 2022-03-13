package kr.ac.kpu.diyequipmentapplication.front.signIn.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import kr.ac.kpu.diyequipmentapplication.R;


//미완성 장비
// 목록 클릭시 상세화면으로 전환되는 액티비티클래스
public class RentalDetailActivity extends AppCompatActivity {
    private ImageView ivRentalImage;
    private TextView etUserEmail, etModelName, etModelInform, etRentalType, etRentalCost, etRentalAddress, etRentalDate;
    private Button btCheckOk;
    private String getImageUrl;
    private DecimalFormat decimalFormat;
    private String getRentalFeeCost, temp;
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
        btCheckOk = findViewById(R.id.btn_checkOK);
        decimalFormat = new DecimalFormat("###,###");

        Intent intent = getIntent();
        getImageUrl = intent.getStringExtra("RentalImage");
        Picasso.get().load(getImageUrl).into(ivRentalImage);
        etModelName.setText("ModelName : "+intent.getStringExtra("ModelName"));
        etModelInform.setText("ModelInform : "+intent.getStringExtra("ModelInform"));
        etRentalType.setText("RentalType : "+intent.getStringExtra("RentalType"));
        etRentalAddress.setText("RentalAddress : "+intent.getStringExtra("RentalAddress"));
        etUserEmail.setText("UserEmail : "+intent.getStringExtra("UserEmail"));
        etRentalDate.setText("RentalDate : "+intent.getStringExtra("RentalDate"));
        //etRentalCost.setText("RentalCost : "+intent.getStringExtra("RentalCost"));
        temp = intent.getStringExtra("RentalCost");

        if (temp.equals("무료"))
            etRentalCost.setText("RentalCost : "+temp);
        else {
            temNum = Integer.parseInt(temp);
            getRentalFeeCost = decimalFormat.format(temNum);
            etRentalCost.setText("RentalCost : "+getRentalFeeCost+"원");
        }

        btCheckOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(RentalDetailActivity.this, AuthMainActivity.class);
                startActivity(intent1);
                finish();
            }
        });
    }
}