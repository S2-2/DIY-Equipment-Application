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

public class DiyMainActivity extends AppCompatActivity {
    private FirebaseAuth mainFirebaseAuth;     //FirebaseAuth 참조 변수 선언
    private EditText etNickname = null;        //사용자 별명 참조할 뷰 참조 변수 선언
    private FirebaseFirestore mainFirebaseFirestore = null;     //파이어스토어 참조 변수 선언

    private ImageButton imgBtn_rentalList = null;
    private ImageButton imgBtn_rentalMap = null;
    private ImageButton imgBtn_rentalCommunity = null;
    private ImageButton imgBtn_drawerMenu = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diy_main);

        mainFirebaseAuth = FirebaseAuth.getInstance();                  //FirebaseAuth 초기화 및 객체 참조
        etNickname = (EditText) findViewById(R.id.main_et_nickname);    //사용자 닉네임 참조
        mainFirebaseFirestore = FirebaseFirestore.getInstance();        //파이어스토어 초기화 및 객체 참조

        imgBtn_rentalList = (ImageButton) findViewById(R.id.main_imgBtn_rentalList);
        imgBtn_rentalMap = (ImageButton) findViewById(R.id.main_imgBtn_rentalMap);
        imgBtn_rentalCommunity = (ImageButton) findViewById(R.id.main_imgBtn_rentalCommunity);
        imgBtn_drawerMenu = (ImageButton)findViewById(R.id.registrationRecyclerview_btn_drawer);

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

        //장비 대여 목록 이동
        imgBtn_rentalList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistrationRecyclerview.class);
                startActivity(intent);
            }
        });

        //장비 대여 맵 이동
        imgBtn_rentalMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RentalGoogleMap.class);
                startActivity(intent);
            }
        });

        //장비 대여 커뮤니티 이동
        imgBtn_rentalCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CommunityActivity.class);
                startActivity(intent);
            }
        });

        imgBtn_drawerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
            }
        });
    }
}