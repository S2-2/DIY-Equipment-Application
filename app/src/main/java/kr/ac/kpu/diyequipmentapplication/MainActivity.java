package kr.ac.kpu.diyequipmentapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

//Firebase 인증을 통해 접근 가능한 메인 액티비티 클래스
public class MainActivity extends AppCompatActivity {
    private FirebaseAuth nFirebaseAuth;     //FirebaseAuth 참조 변수 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nFirebaseAuth = FirebaseAuth.getInstance();     //FirebaseAuth 참조
/*
        //DIY 등록 액티비티로 이동
        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EquipmentRegistrationActivity.class);
                startActivity(intent);
            }
        });
*/

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

    }
}