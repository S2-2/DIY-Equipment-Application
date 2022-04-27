package kr.ac.kpu.diyequipmentapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MenuActivity extends AppCompatActivity {
    private FirebaseFirestore menuFirebaseFirestore = null;     //파이어스토어 참조 변수 선언
    private TextView tvNickname = null;
    private TextView tvuserLocation = null;

    private ImageButton imgBtn_back = null;
    private Button btn_logout = null;
    private Button btn_withdraw = null;

    private FirebaseAuth menuFirebaseAuth = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        imgBtn_back = (ImageButton) findViewById(R.id.menu_btn_back);
        btn_logout = (Button) findViewById(R.id.menu_btn_logout);
        btn_withdraw = (Button) findViewById(R.id.menu_btn_withdraw);

        tvNickname = (TextView) findViewById(R.id.menu_tv_nickname);    //사용자 닉네임 참조
        tvuserLocation = (TextView) findViewById(R.id.menu_tv_userlocation);    //사용자 닉네임 참조
        menuFirebaseFirestore = FirebaseFirestore.getInstance();        //파이어스토어 초기화 및 객체 참조

        menuFirebaseAuth = FirebaseAuth.getInstance();

        //DIY_Signup DB에서 사용자 계정에 맞는 닉네임 가져오는 기능 구현.
        //사용자 이메일 정보와 일치하는 데이터를 DIY_Signup DB에서 찾아서 etNickname 참조 변수에 닉네임 값 참조.
        menuFirebaseFirestore.collection("DIY_Signup")
                .whereEqualTo("userEmail", menuFirebaseAuth.getCurrentUser().getEmail().toString().trim())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                Log.d("menu SignupDB", queryDocumentSnapshot.get("userNickname").toString().trim());
                                tvNickname.setText(queryDocumentSnapshot.get("userNickname").toString().trim());
                            }
                        }
                    }
                });

        imgBtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuFirebaseAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuFirebaseAuth.getCurrentUser().delete();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}