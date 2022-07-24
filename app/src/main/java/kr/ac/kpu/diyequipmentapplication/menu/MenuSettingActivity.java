package kr.ac.kpu.diyequipmentapplication.menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import kr.ac.kpu.diyequipmentapplication.R;

public class MenuSettingActivity extends AppCompatActivity {

    private ImageButton imgBtn_back = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_setting);

        // 프로필 수정으로 이동
        imgBtn_back = (ImageButton) findViewById(R.id.profileSetting_btn_back);

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

        imgBtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}