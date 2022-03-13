package kr.ac.kpu.diyequipmentapplication.front.signIn.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import kr.ac.kpu.diyequipmentapplication.R;

//Firebase 인증을 통해 접근 가능한 메인 액티비티 클래스
public class AuthMainActivity extends AppCompatActivity {
    private FirebaseAuth nFirebaseAuth;     //FirebaseAuth 참조 변수 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_main);


        nFirebaseAuth = FirebaseAuth.getInstance();     //FirebaseAuth 참조

        Button btn_logout = findViewById(R.id.btn_logout);  //btn_logout 뷰 객체 참조
        btn_logout.setOnClickListener(new View.OnClickListener() {  //btn_logout 이벤트 리스너 등록
            @Override
            public void onClick(View view) {
                //로그아웃 하기
                nFirebaseAuth.signOut();    //Firebase 인증된 계정 로그아웃

                //로그인 화면으로 이동
                Intent intent = new Intent(AuthMainActivity.this, AuthLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //DIY 등록 액티비티로 이동
        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AuthMainActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        //DIY 목록 액티비티로 이동
        Button btn_diyView = findViewById(R.id.btn_dyiView);
        btn_diyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AuthMainActivity.this, RegistrationRecyclerView.class);
                startActivity(intent);
            }
        });

        Button btn_diyGoogMap = findViewById(R.id.btn_dyiGoogleMap);
        btn_diyGoogMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(AuthMainActivity.this,"DIY Google Map 개발중.......", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(AuthMainActivity.this, RentalGoogleMap.class);
                startActivity(intent);
            }
        });
    }
}