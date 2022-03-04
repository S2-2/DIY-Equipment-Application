package kr.ac.kpu.diyequipmentapplication.front.signIn.auth;

import android.content.Intent;
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

import kr.ac.kpu.diyequipmentapplication.R;
import kr.ac.kpu.diyequipmentapplication.front.signIn.terms;

//회원가입 액티비티 클래스
public class AuthRegisterActivity extends AppCompatActivity {

    private FirebaseAuth nFirebaseAuth; //파이어베이스 인증 참조 변수 선언
    private DatabaseReference nDatabaseRef; //실시간 데이터베이스 참조 변수 선언
    private EditText nEtEmail, nEtPwd;  //사용자가 입력한 이메일, 패스워드 참조 변수 선언
    private Button nBtnRegister;        //회원가입 버튼 참조 변수 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_register);

        nFirebaseAuth = FirebaseAuth.getInstance();     //Firebase 인증 객체 참조
        nDatabaseRef = FirebaseDatabase.getInstance().getReference("DIY-Auth-DB");     //Firebase DB 객체 참조

        nEtEmail = findViewById(R.id.et_email);         //et_email 뷰 객체 참조
        nEtPwd = findViewById(R.id.et_pwd);             //et_pwd 뷰 객체 참조
        nBtnRegister = findViewById(R.id.btn_register); //btn_register 뷰 객체 참조

        nBtnRegister.setOnClickListener(new View.OnClickListener() {    //nBtnRegister 이벤트 리스너 등록
            @Override
            public void onClick(View view) {
                //회원가입 처리 이벤트 리스너
                String strEmail = nEtEmail.getText().toString().trim();    //사용자가 입력한 아이디 가져옴
                String strPwd = nEtPwd.getText().toString().trim();        //사용자가 입력한 패스워드 가져옴

                //Firebase 인증 계정 등록
                nFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(AuthRegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())     //Firebase 인증 성공
                        {
                            FirebaseUser firebaseUser = nFirebaseAuth.getCurrentUser();     //Firebase 사용자 객체 참조

                            AuthUserAccount account = new AuthUserAccount();        // 인증된 사용자 계정 객체 생성
                            account.setIdToken(firebaseUser.getUid());              //인증된 사용자 계정에  firebaseUser.getUid()참조
                            account.setEmailId(firebaseUser.getEmail());            //인증된 사용자 계정에 firebaseUser.getEmail()참조
                            account.setPassword(strPwd);                            //인증된 사용자 계정에 strPwd 참조
                            account.setTerms("false");                                //인증된 사용자 계정에 약관여부 참조

                            nDatabaseRef.child("AuthUserAccount").child(firebaseUser.getUid()).setValue(account);   //Firebase DB에 인증된 사용자 계정 정보 등록!

                            Toast.makeText(AuthRegisterActivity.this, "회원가입에 성공!",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else    //Firebase 인증 실패
                        {
                            Toast.makeText(AuthRegisterActivity.this, "회원가입에 실패!",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }
}