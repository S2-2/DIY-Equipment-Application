package kr.ac.kpu.diyequipmentapplication.login;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import kr.ac.kpu.diyequipmentapplication.MainActivity;
import kr.ac.kpu.diyequipmentapplication.R;

//로그인 액티비티 클래스
public class LoginActivity extends AppCompatActivity {

    //로그인 액티비티 클래스 필드 선언
    private EditText etUserId = null, etUserPwd = null;
    private Button btnLogin = null, btnFindId = null, btnFindPwd = null, btnSignup = null;
    private SignInButton loginSignInButton = null;
    private GoogleSignInClient loginGoogleSignInClient = null;
    private FirebaseAuth loginFirebaseAuth = null;
    private FirebaseFirestore loginFirebaseFirestore = null;
    private String loginGetEmail = null;
    private String loginGetPwd = null;
    private String loginGetId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);   //activity_auth_login.xml파일  화면 출력

        //activity_login.xml파일 해당 뷰 참조
        etUserId = (EditText) findViewById(R.id.login_et_id);
        etUserPwd = (EditText) findViewById(R.id.login_et_pwd);
        btnLogin = (Button) findViewById(R.id.login_btn_login);
        btnFindId = (Button) findViewById(R.id.login_btn_findId);
        btnFindPwd = (Button) findViewById(R.id.login_btn_findPwd);
        btnSignup = (Button) findViewById(R.id.login_btn_signup);
        loginSignInButton = (SignInButton) findViewById(R.id.login_btn_loginGoogle);
        loginFirebaseAuth = FirebaseAuth.getInstance();
        loginFirebaseFirestore = FirebaseFirestore.getInstance();

        //로그인 버튼 이벤트
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String tempId = etUserId.getText().toString().trim();
                final String tempPwd = etUserPwd.getText().toString().trim();



                if (tempId.isEmpty() && tempPwd.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "아이디, 패스워드 미입력!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this, "아이디, 패스워드 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if (tempId.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "아이디 미입력!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this, "아이디 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if (tempPwd.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "패스워드 미입력!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this, "패스워드 입력하세요!", Toast.LENGTH_SHORT).show();
                }  else{
                    loginFirebaseFirestore.collection("DIY_Signup")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                            if (tempId.equals(queryDocumentSnapshot.get("userID").toString().trim())
                                                    && tempPwd.equals(queryDocumentSnapshot.get("userPwd1").toString().trim())) {
                                                loginGetEmail = queryDocumentSnapshot.get("userEmail").toString().trim();
                                                loginGetPwd = queryDocumentSnapshot.get("userPwd1").toString().trim();
                                                firebaseAuthWithEmail(loginGetEmail, loginGetPwd);
                                            }
                                            else if (tempId.equals(queryDocumentSnapshot.get("userID").toString().trim())
                                                    && !tempPwd.equals(queryDocumentSnapshot.get("userPwd1").toString().trim())) {
                                                loginGetId = queryDocumentSnapshot.get("userID").toString().trim();
                                                Toast.makeText(LoginActivity.this, "로그인 실패!", Toast.LENGTH_SHORT).show();
                                                Toast.makeText(LoginActivity.this, "패스워드가 틀립니다!", Toast.LENGTH_SHORT).show();
                                            } else { continue; }
                                        }
                                        if (loginGetId == null && loginGetEmail == null)
                                            Toast.makeText(LoginActivity.this, "등록된 계정이 아닙니다!\n회원가입 하세요!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "로그인 실패!!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        //아이디 찾기 버튼 이벤트
        btnFindId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentFindId = new Intent(LoginActivity.this, FindIdActivity.class);
                startActivity(intentFindId);
            }
        });

        //비밀번호 찾기 버튼 이벤트
        btnFindPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentFindPwd = new Intent(LoginActivity.this, FindPwdActivity.class);
                startActivity(intentFindPwd);
            }
        });

        //회원가입 버튼 이벤트트
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSignup = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intentSignup);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        loginGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        loginSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultLauncher.launch(new Intent(loginGoogleSignInClient.getSignInIntent()));
            }
        });
    }

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            assert account != null;
                            firebaseAuthWithGoogle(account.getIdToken());
                        } catch (ApiException e) { }
                    }
                }
            });

    //Firebase 등록된 구글 메일로 로그인 기능 구현 메서드
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        loginFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {  //구글 로그인 성공인 경우
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                            Toast.makeText(LoginActivity.this, "Google Login Success!", Toast.LENGTH_SHORT).show();
                        } else { //구글 로그인 실패인 경우
                            Toast.makeText(LoginActivity.this, "Google Login Failure!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    //Firebase에 등록된 인증 메일로 로그인 기능 구현 메서드
    private void firebaseAuthWithEmail(String userEmail, String userPwd) {
        loginFirebaseAuth.signInWithEmailAndPassword(userEmail, userPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {    //task객체로 로그인 유무 파악
                    if (task.isSuccessful()) {  //Firebase 인증 및 로그인 성공인 경우
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class); //Intent객체 생성 및 초기화
                        startActivity(intent);  //AuthMainActivity 이동
                        finish();   //현재 액티비티 파괴
                        Toast.makeText(LoginActivity.this, "Login Success!",Toast.LENGTH_SHORT).show();
                    } else {    //Firebase 인증 성공이지만, 로그인 실패인 경우
                        Toast.makeText(LoginActivity.this, "Login Failure!", Toast.LENGTH_SHORT).show();
                    }
                }
        });
    }
}