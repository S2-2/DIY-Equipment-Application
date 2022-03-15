package kr.ac.kpu.diyequipmentapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import kr.ac.kpu.diyequipmentapplication.front.signIn.auth.AuthLoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_next = findViewById(R.id.btn_nextLogin);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그인 화면으로 이동동
                Intent intent = new Intent(MainActivity.this, AuthLoginActivity.class);
                startActivity(intent);
            }
        });
    }
}