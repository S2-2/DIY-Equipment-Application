package kr.ac.kpu.diyequipmentapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//회원가입 액티비티 클래스
public class SignupActivity extends AppCompatActivity {

    //회원가입 액티비티 클래스 필드 선언
    private FirebaseAuth authRegisterFirebaseAuth = null;               //파이어베이스 인증 참조 변수
    private DatabaseReference authRegisterDatabaseRef = null;           //RealTimeDB 참조 변수
    private EditText email = null, /*name = null,*/ password = null;  //사용자 이메일, 패스워드 뷰 참조 변수
    private Button signup = null;                           //회원가입 버튼 뷰 참조 변수
    private String registerEmail = null,/* registerName = null,*/ registerPwd = null;            //사용자 이메일, 패스워드 뷰 값을 참조할 변수
    private FirebaseUser authRegisterFirebaseUser = null;               //Firebase인증에 등록된 사용자 계정 참조 변수

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        //회원가입 액티비티 클래스 필드 초기화
        authRegisterFirebaseAuth = FirebaseAuth.getInstance();                                          //Firebase 인증 객체 참조
        authRegisterDatabaseRef = FirebaseDatabase.getInstance().getReference("DIY-Auth-DB");     //Firebase DB 객체 참조

        email = findViewById(R.id.et_signupEmail);                                   //et_authRegisterEmail 뷰 객체 참조
//        name = findViewById(R.id.et_signupName);
        password = findViewById(R.id.et_signupPwd);                                       //et_authRegisterPwd 뷰 객체 참조
        signup = findViewById(R.id.btn_signup);                                      //btn_authRegister 뷰 객체 참조

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerEmail = email.getText().toString().trim();  //authRegisterEmail 뷰 객체 값 참조
 //               registerName = name.getText().toString().trim();  //authRegisterEmail 뷰 객체 값 참조
                registerPwd = password.getText().toString().trim();      //authRegisterPwd 뷰 객체 값 참조

                if (registerEmail.isEmpty() /*&& registerName.isEmpty()*/ && registerPwd.isEmpty()) {       // 등록할 이메일, 패스워드 미입력인 경우
                    Toast.makeText(SignupActivity.this, "입력하지 않은 정보가 있습니다.", Toast.LENGTH_SHORT).show();
                }
                else if(registerEmail.isEmpty())     //이메일 미입력인 경우
                {
                    Toast.makeText(SignupActivity.this, "등록할 이메일을 입력하세요", Toast.LENGTH_SHORT).show();
                }
 /*               else if(registerName.isEmpty())     //이름 미입력인 경우
                {
                    Toast.makeText(SignupActivity.this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
                }*/
                else if (registerPwd.isEmpty())      //패스워드 미입력인 경우
                {
                    Toast.makeText(SignupActivity.this, "등록할 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                }
                else{   //등록할 이메일, 패스워드 입력한 경우
                    firebaseAuthCreateUser(registerEmail,registerPwd); //사용자가 등록할 이메일, 패스워드 인자를 가지고 Firebase 인증계정 생성 메서드 호출
                }
            }
        });
    }


    //Firebase 인증계정 생성 및 RealTimeDB 등록 메서드
    private void firebaseAuthCreateUser(String registerEmail, String registerName) {
        authRegisterFirebaseAuth.createUserWithEmailAndPassword(registerEmail, registerPwd).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())     //Firebase인증 등록 성공
                {
                    authRegisterFirebaseUser = authRegisterFirebaseAuth.getCurrentUser();     //Firebase인증에 등록된 계정 정보 참조

                    //Firebase에 등록된 이메일 계정으로 인증 확인 메일 전송
                    authRegisterFirebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {   //인증 메일 전송 성공인 경우
                                Toast.makeText(SignupActivity.this, "Verification email sent to "+ authRegisterFirebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                            } else {    //인증 메일 전송 실패인 경우
                                Toast.makeText(SignupActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    AuthUserAccount account = new AuthUserAccount();                    //인증된 사용자 계정 객체 생성 및 초기화
                    account.setIdToken(authRegisterFirebaseUser.getUid());              //Firebase인증에 등록된 계정 Uid 참조
                    account.setEmailId(authRegisterFirebaseUser.getEmail());            //Firebase인증에 등록된 계정 Email 참조
            //        account.setPassword(registerName);                                   //사용자 이름 참조
                    account.setPassword(registerPwd);                                   //사용자 패스워드 참조

                    authRegisterDatabaseRef.child("AuthUserAccount").child(authRegisterFirebaseUser.getUid()).setValue(account);   //Firebase DB에 인증된 사용자 계정 정보 등록!

                    Toast.makeText(SignupActivity.this, "Sign Up Success!",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else    //Firebase 인증 실패
                {
                    Toast.makeText(SignupActivity.this, "Sign Up Failure!",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}