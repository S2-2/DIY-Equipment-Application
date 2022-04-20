package kr.ac.kpu.diyequipmentapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

//Firebase 인증을 통해 접근 가능한 메인 액티비티 클래스
public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mainFirebaseAuth;     //FirebaseAuth 참조 변수 선언
    private EditText etNickname = null;        //사용자 별명 참조할 뷰 참조 변수 선언
    private FirebaseFirestore mainFirebaseFirestore = null;     //파이어스토어 참조 변수 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFirebaseAuth = FirebaseAuth.getInstance();                  //FirebaseAuth 초기화 및 객체 참조
        etNickname = (EditText) findViewById(R.id.main_et_nickname);    //사용자 닉네임 참조
        mainFirebaseFirestore = FirebaseFirestore.getInstance();        //파이어스토어 초기화 및 객체 참조

        //DIY_Signup DB에서 사용자 계정에 맞는 닉네임 가져오는 기능 구현.
        //사용자 이메일 정보와 일치하는 데이터를 DIY_Signup DB에서 찾아서 etNickname 참조 변수에 닉네임 값 참조.
        mainFirebaseFirestore.collection("DIY_Signup")
                .whereEqualTo("userEmail", mainFirebaseAuth.getCurrentUser().getEmail().toString().trim())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                Log.d("main SignupDB", queryDocumentSnapshot.get("userNickname").toString().trim());
                                etNickname.setText(queryDocumentSnapshot.get("userNickname").toString().trim());
                            }
                        }
                    }
                });
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