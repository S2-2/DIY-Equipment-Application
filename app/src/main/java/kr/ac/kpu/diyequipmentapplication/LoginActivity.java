package kr.ac.kpu.diyequipmentapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

//로그인 액티비티 클래스
public class LoginActivity extends AppCompatActivity{

    //로그인 액티비티 클래스 필드 선언
    private EditText etUserId = null, etUserPwd = null;
    private Button btnLogin = null, btnFindId = null, btnFindPwd = null, btnSignup = null;
    private SignInButton loginSignInButton = null;
    private GoogleSignInClient loginGoogleSignInClient = null;
    private FirebaseAuth loginFirebaseAuth = null;
    private FirebaseFirestore loginFirestoreDB = null;

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
        loginFirestoreDB = FirebaseFirestore.getInstance();

        //로그인 버튼 이벤트
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etUserId.getText().toString().isEmpty() && etUserPwd.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "아이디, 패스워드 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if (etUserId.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "아이디 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if (etUserPwd.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "패스워드 입력하세요!", Toast.LENGTH_SHORT).show();
                } else {
                    loginFirestoreDB.collection("DIY_Signup")
                            .whereEqualTo("userID", etUserId.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot ds : task.getResult()) {
                                                Log.d("hyh", ds.get("userID").toString().trim() + "  " + ds.get("userPwd1").toString().trim());
                                                Log.d("hyh", ds.getId());

                                                if (ds.get("userID").toString().trim().equals(etUserId.getText().toString()) && ds.get("userPwd1").toString().trim().equals(etUserPwd.getText().toString())) {
                                                    Toast.makeText(getApplicationContext(), "로그인 성공!", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "패스워드 오류!", Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(getApplicationContext(), "패스워드 다시 입력하세요!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "로그인 실패!", Toast.LENGTH_SHORT).show();
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
                Intent intentFindId = new Intent(getApplicationContext(), FindIdActivity.class);
                startActivity(intentFindId);
            }
        });

        //비밀번호 찾기 버튼 이벤트
        btnFindPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentFindPwd = new Intent(getApplicationContext(), FindPwdActivity.class);
                startActivity(intentFindPwd);
            }
        });

        //회원가입 버튼 이벤트트
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSignup = new Intent(getApplicationContext(), SignupActivity.class);
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
}