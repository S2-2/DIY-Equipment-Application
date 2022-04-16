package kr.ac.kpu.diyequipmentapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//로그인 액티비티 클래스
public class LoginActivity extends AppCompatActivity{

    //로그인 액티비티 클래스 필드
    private FirebaseAuth authLoginFirebaseAuth = null;                  //파이어베이스 인증 객체 참조 변수
    private DatabaseReference authLoginDatabaseReference = null;        //RealTimeDB 객체 참조 변수
    private EditText authLoginEmail = null, authLoginPwd = null;        //사용자 이메일, 패스워드 뷰 참조 변수
    private Button login = null, signup = null, backBtn;    //로그인 버튼, 회원가입 버튼 뷰 참조 변수
    private SignInButton authLoginSignInButton = null;                  //구글 로그인 뷰 참조 변수
    private GoogleSignInClient authLoginGoogleSignInClient = null;      //구글 계정 클라이언트 객체 참조 변수
    private String userEmail = null, userPwd = null;                    //사용자 입력 이메일, 패스워드 참조 변수

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);   //activity_auth_login.xml파일  화면 출력

        authLoginFirebaseAuth = FirebaseAuth.getInstance();     //Firebase 인증 객체 참조
        authLoginDatabaseReference = FirebaseDatabase.getInstance().getReference("DIY-Auth-DB"); //Firebase DB 객체 참조
        authLoginEmail = findViewById(R.id.login_et_id);              //et_email 뷰 객체 참조
        authLoginPwd = findViewById(R.id.login_et_pwd);                  //et_pwd 뷰 객체 참조
        login = findViewById(R.id.login_btn_login);                 //btn_login 뷰 객체 참조
        signup = findViewById(R.id.login_btn_signup);      //btn_signup 뷰 객체 참조
        authLoginSignInButton = findViewById(R.id.login_btn_loginGoogle);    //btn_google 뷰 객체 참조



        //이메일 계정 인증 단계
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userEmail = authLoginEmail.getText().toString().trim();     //사용자가 입력한 이메일 참조
                userPwd = authLoginPwd.getText().toString().trim();         //사용자가 입력한 패스워드 참조

                if (userEmail.isEmpty() && userPwd.isEmpty()) {       // 사용자 이메일, 패스워드 미입력인 경우
                    Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else if(userEmail.isEmpty())     //사용자 이메일 미입력인 경우
                {
                    Toast.makeText(LoginActivity.this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else if (userPwd.isEmpty())      //사용자 패스워드 미입력인 경우
                {
                    Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else{   //사용자 이메일, 패스워드 입력한 경우
                    firebaseAuthWithEmail(userEmail, userPwd);  //Firebase Authentication 메서드 호출
                }
            }
        });
        
        //회원가입 화면으로 전환
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });

        //1. Google Login 인증 단계
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        authLoginGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        authLoginSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultLauncher.launch(new Intent(authLoginGoogleSignInClient.getSignInIntent()));
            }
        });
    }

    //2. Google 인증 절차 화면 기능 구현
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK)
                    {
                        Intent intent = result.getData();   //구글 계정 정보를 가져와서 intent 참조변수가 참조

                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                        try{
                            GoogleSignInAccount account = task.getResult(ApiException.class);

                            assert account != null;
                            firebaseAuthWithGoogle(account.getIdToken());       //사용자 계정 idToken인자를 갖고서 구글 로그인 메서드 호출
                        }
                        catch (ApiException e) { }
                    }
                }
            });


    //Firebase에 등록된 인증 메일로 로그인 기능 구현 메서드
    private void firebaseAuthWithEmail(String userEmail, String userPwd) {
        authLoginFirebaseAuth.signInWithEmailAndPassword(userEmail, userPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {    //task객체로 로그인 유무 파악
                if (authLoginFirebaseAuth.getCurrentUser().isEmailVerified()) { //파이어베이스에 등록된 계정으로 인증 메일 전송
                    Toast.makeText(LoginActivity.this, "Email Authentication Success!", Toast.LENGTH_SHORT).show();

                    if (task.isSuccessful()) {  //Firebase 인증 및 로그인 성공인 경우
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class); //Intent객체 생성 및 초기화
                        startActivity(intent);  //MainActivity 이동
                        finish();   //현재 액티비티 파괴
                        Toast.makeText(LoginActivity.this, "로그인 성공",Toast.LENGTH_SHORT).show();
                    } else {    //Firebase 인증 성공이지만, 로그인 실패인 경우
                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    }
                } else {    //Firebase에 등록된 계정으로 인증 메일 전송 실패인 경우
                   Toast.makeText(LoginActivity.this, "Email Authentication Failure!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Firebase 등록된 구글 메일로 로그인 기능 구현 메서드
    private void firebaseAuthWithGoogle(String idToken)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        authLoginFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {  //구글 로그인 성공인 경우
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                            Toast.makeText(LoginActivity.this, "구글 계정으로 로그인하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else { //구글 로그인 실패인 경우
                            Toast.makeText(LoginActivity.this, "구글 계정으로 로그인에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}