package kr.ac.kpu.diyequipmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

public class terms extends AppCompatActivity {

    // 다음 페이지로 진행하는 버튼
    public Button btn_next1;

    // 체크박스 체크여부
    public boolean TERMS_AGREE_ALL = false; // 전체동의 받는 변수 (true or false)
    public boolean TERMS_AGREE_1 = false;   // 서비스 이용약관
    public boolean TERMS_AGREE_2 = false;   // 개인정보처리방침
    public boolean TERMS_AGREE_3 = false;   // 위치기반서비스 이용약관

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        // 체크박스
        CheckBox check_all = (CheckBox) findViewById(R.id.cb_termsAll); // 전체약관 체크박스
        CheckBox check1 = (CheckBox) findViewById(R.id.cb_terms1);  // 서비스 이용약관 체크박스
        CheckBox check2 = (CheckBox) findViewById(R.id.cb_terms2);  // 개인정보 처리방침 체크박스
        CheckBox check3 = (CheckBox) findViewById(R.id.cb_terms3);  // 위치정보 이용약관 체크박스

        // 다음버튼
        btn_next1 = (Button) findViewById(R.id.btn_next1);

        // 전체동의
        check_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
                if(isChecked){      // 전체 약관 체크시 모든 약관 체크
                    check1.setChecked(true);
                    check2.setChecked(true);
                    check3.setChecked(true);
                    TERMS_AGREE_ALL = true;
                }
                else{               // 전체 약관 체크 해제시 모든 약관 체크 해제
                    check1.setChecked(false);
                    check2.setChecked(false);
                    check3.setChecked(false);
                    TERMS_AGREE_ALL = false;
                }
            }
        });

        // 서비스 이용약관 동의
        check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
                if (isChecked){
                    TERMS_AGREE_1 = true;
                } else{
                    TERMS_AGREE_1 = false;
                }
            }
        });

        // 서비스 이용약관 동의
        check2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
                if (isChecked){
                    TERMS_AGREE_2 = true;
                } else{
                    TERMS_AGREE_2 = false;
                }
            }
        });

        // 서비스 이용약관 동의
        check3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
                if (isChecked){
                    TERMS_AGREE_3 = true;
                } else{
                    TERMS_AGREE_3 = false;

                }
            }
        });

        btn_next1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TERMS_AGREE_ALL){  // 전체 동의 체크 안된 경우
                    if(TERMS_AGREE_1&&TERMS_AGREE_2&&TERMS_AGREE_3){
                        startActivity(new Intent(terms.this, MainActivity.class));
                    }
                    else{
                        Toast myToast = Toast.makeText(terms.this.getApplicationContext(),"약관을 체크해주세요.",Toast.LENGTH_SHORT);
                        return;
                    }
                }
                else{       // 전체 동의 체크 된 경우
                    startActivity(new Intent(terms.this, MainActivity.class));
                }
            }
            // commit test 02.212222222
        });
    }
}