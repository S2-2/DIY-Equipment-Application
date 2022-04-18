package kr.ac.kpu.diyequipmentapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

//회원가입 액티비티 클래스
public class SignupActivity extends AppCompatActivity {
    //회원가입 액티비티 클래스 필드 선언
    private FirebaseAuth signupFirebaseAuth = null;
    private FirebaseFirestore signupFirestoreDB = null;
    private EditText etUserId = null, etUserPwd1 = null, etUserPwd2 = null,
            etUserName = null, etUserNickname = null, etUserEmail = null;  //사용자 이메일, 패스워드 뷰 참조 변수
    private Button btnSignup = null;                           //회원가입 버튼 뷰 참조 변수
    private ImageButton imgBtnBack = null;

    private FirebaseFirestore signupFirebaseFirestore = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //회원가입 액티비티 클래스 필드 초기화
        signupFirebaseAuth = FirebaseAuth.getInstance();
        signupFirestoreDB = FirebaseFirestore.getInstance();

        etUserId = (EditText) findViewById(R.id.signup_et_id);
        etUserPwd1 = (EditText) findViewById(R.id.signup_et_pwd);
        etUserPwd2 = (EditText) findViewById(R.id.signup_et_pwd2);
        etUserName = (EditText) findViewById(R.id.signup_et_name);
        etUserNickname = (EditText) findViewById(R.id.signup_et_nickname);
        etUserEmail = (EditText) findViewById(R.id.signup_et_email);
        btnSignup = (Button) findViewById(R.id.signup_btn_signup);
        imgBtnBack = (ImageButton) findViewById(R.id.signup_btn_back);
        signupFirebaseFirestore = FirebaseFirestore.getInstance();

        //이미지 버튼 클릭 이벤트
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();   //회원가입 액티비티 종료
            }
        });

        //회원가입 버튼 클릭 이벤트
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userId = etUserId.getText().toString().trim();
                final String userPwd1 = etUserPwd1.getText().toString().trim();
                final String userPwd2 = etUserPwd2.getText().toString().trim();
                final String userName = etUserName.getText().toString().trim();
                final String userNickname = etUserNickname.getText().toString().trim();
                final String userEmail = etUserEmail.getText().toString().trim();

                if (userId.isEmpty() && userPwd1.isEmpty() && userPwd2.isEmpty() && userName.isEmpty() && userNickname.isEmpty()
                        && userEmail.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "회원가입 정보 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if (userId.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "사용자 아이디 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if (userPwd1.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "사용자 패스워드 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if (userPwd2.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "재확인 패스워드 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if (userName.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "사용자 이름 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if (userNickname.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "사용자 별명 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if (userEmail.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "사용자 본인 확인 이메일 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if (!userPwd1.isEmpty() && (etUserPwd1.getText().toString().length() < 6)) {
                    Toast.makeText(SignupActivity.this, "패스워드 6자 이상 입력하세요!", Toast.LENGTH_SHORT).show();
                    etUserPwd1.setText("");
                    etUserPwd2.setText("");
                } else {
                    if (etUserPwd1.getText().toString().equals(etUserPwd2.getText().toString())) {
                        firebaseAuthCreateUser(userId, userPwd1, userPwd2, userName, userNickname, userEmail);
                    } else {
                        Toast.makeText(SignupActivity.this, "사용자 재확인 패스워드 불일치!", Toast.LENGTH_SHORT).show();
                        Toast.makeText(SignupActivity.this, "사용자 재확인 패스워드 다시 입력하세요!", Toast.LENGTH_SHORT).show();
                        etUserPwd2.setText("");
                    }
                }
            }
        });
    }
    private void firebaseAuthCreateUser(String userId, String userPwd1, String userPwd2, String userName, String userNickname, String userEmail) {
        signupFirebaseAuth.createUserWithEmailAndPassword(userEmail, userPwd1).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    SignupAccountDB signupAccountDB = new SignupAccountDB(userId, userPwd1, userPwd2, userName, userNickname, userEmail);
                    signupFirebaseFirestore.collection("DIY_Signup").document(userId).set(signupAccountDB);
                    Toast.makeText(SignupActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SignupActivity.this, "회원가입 실패!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}