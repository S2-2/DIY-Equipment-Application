package kr.ac.kpu.diyequipmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MenuSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_setting);

        // 프로필 수정으로 이동
        Button btn_profile = findViewById(R.id.menuSetting_btn_profile);
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuSettingActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // 위치 설정으로 이동
        Button btn_location = findViewById(R.id.menuSetting_btn_location);
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuSettingActivity.this, LocationActivity.class);
                startActivity(intent);
            }
        });

    }
}