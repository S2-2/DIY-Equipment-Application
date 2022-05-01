package kr.ac.kpu.diyequipmentapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

//회원가입 액티비티 클래스
public class SignupActivity extends AppCompatActivity {
    //회원가입 액티비티 클래스 필드 선언
    private FirebaseAuth signupFirebaseAuth = null;
    private FirebaseFirestore signupFirestoreDB = null;
    private EditText etUserId = null, etUserPwd = null, etCheckUserPwd = null,
            etUserName = null, etUserNickname = null, etUserEmail = null;  //사용자 이메일, 패스워드 뷰 참조 변수
    private Button btnSignup = null;                           //회원가입 버튼 뷰 참조 변수
    private ImageButton imgBtnBack = null;

    private TextView tvCheckId = null;
    private TextView tvCheckPwd = null;
    private TextView tvCheckEmail = null;

    private Boolean flagId = false;
    private Boolean flagPwd = false;
    private Boolean flagEmail = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //회원가입 액티비티 클래스 필드 초기화
        signupFirebaseAuth = FirebaseAuth.getInstance();
        signupFirestoreDB = FirebaseFirestore.getInstance();

        etUserId = (EditText) findViewById(R.id.signup_et_id);
        etUserPwd = (EditText) findViewById(R.id.signup_et_pwd);
        etCheckUserPwd = (EditText) findViewById(R.id.signup_et_pwd2);
        etUserName = (EditText) findViewById(R.id.signup_et_name);
        etUserNickname = (EditText) findViewById(R.id.signup_et_nickname);
        etUserEmail = (EditText) findViewById(R.id.signup_et_email);
        btnSignup = (Button) findViewById(R.id.signup_btn_signup);
        imgBtnBack = (ImageButton) findViewById(R.id.signup_btn_back);

        tvCheckId = (TextView) findViewById(R.id.signup_tv_checkId);
        tvCheckPwd = (TextView) findViewById(R.id.signup_tv_checkPwd);
        tvCheckEmail = (TextView) findViewById(R.id.signup_tv_checkEmail);

        //뒤로가기 버튼 아이콘 클릭 이벤트
        //회원가입 액티비티 종료
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish();}
        });

        //아이디 중복 검사!
        etUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                signupFirestoreDB.collection("DIY_Signup")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                final String getId = etUserId.getText().toString().trim();

                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        if (getId.equals(queryDocumentSnapshot.get("userID").toString().trim())) {
                                            flagId = false;
                                            tvCheckId.setTextColor(Color.RED);
                                            tvCheckId.setText("중복된 아이디입니다!");
                                            etUserId.setBackgroundResource(R.drawable.red_etittext);

                                        } else if (getId.isEmpty()) {
                                            flagId = false;
                                            tvCheckId.setText("");
                                            etUserId.setBackgroundResource(R.drawable.white_edittext);

                                        } else {
                                            flagId = true;
                                            tvCheckId.setTextColor(Color.BLUE);
                                            tvCheckId.setText("사용 가능한 아이디입니다!");
                                            etUserId.setBackgroundResource(R.drawable.blue_etittext);
                                            continue;
                                        }
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "DIY_Signup DB 읽기 실패!!!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        //패스워드 일치, 불일치 검사!
        etCheckUserPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if (etUserPwd.getText().toString().trim().equals(etCheckUserPwd.getText().toString().trim())) {
                    flagPwd = true;
                    tvCheckPwd.setTextColor(Color.BLUE);
                    tvCheckPwd.setText("패스워드 일치!");
                    etCheckUserPwd.setBackgroundResource(R.drawable.blue_etittext);

                } else if (etCheckUserPwd.getText().toString().trim().isEmpty()) {
                    flagPwd = false;
                    tvCheckPwd.setText("");
                    etCheckUserPwd.setBackgroundResource(R.drawable.white_edittext);

                } else {
                    flagPwd = false;
                    tvCheckPwd.setTextColor(Color.RED);
                    tvCheckPwd.setText("패스워드 불일치!");
                    etCheckUserPwd.setBackgroundResource(R.drawable.red_etittext);
                }
            }
        });

        //이메일 계정 중복 검사!
        etUserEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                signupFirestoreDB.collection("DIY_Signup")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                final String getEmail = etUserEmail.getText().toString().trim();

                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        if (getEmail.equals(queryDocumentSnapshot.get("userEmail").toString().trim())) {
                                            flagEmail = false;
                                            tvCheckEmail.setTextColor(Color.RED);
                                            tvCheckEmail.setText("등록된 이메일 계정입니다!");
                                            etUserEmail.setBackgroundResource(R.drawable.red_etittext);

                                        } else if (getEmail.isEmpty()) {
                                            flagEmail = false;
                                            tvCheckEmail.setText("");
                                            etUserEmail.setBackgroundResource(R.drawable.white_edittext);

                                        } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(editable.toString()).matches()){
                                            flagEmail = true;
                                            tvCheckEmail.setTextColor(Color.BLUE);
                                            tvCheckEmail.setText("사용 가능한 이메일 계정입니다!");
                                            etUserEmail.setBackgroundResource(R.drawable.blue_etittext);
                                        } else {
                                            flagEmail = false;
                                            tvCheckEmail.setText("");
                                            etUserEmail.setBackgroundResource(R.drawable.white_edittext);
                                            continue;
                                        }
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "DIY_Signup DB 읽기 실패!!!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        //회원가입 버튼 클릭 이벤트
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userId = etUserId.getText().toString().trim();
                final String userPwd = etUserPwd.getText().toString().trim();
                final String userCheckPwd = etCheckUserPwd.getText().toString().trim();
                final String userName = etUserName.getText().toString().trim();
                final String userNickname = etUserNickname.getText().toString().trim();
                final String userEmail = etUserEmail.getText().toString().trim();

                if (userId.isEmpty() && userPwd.isEmpty() && userCheckPwd.isEmpty() && userName.isEmpty() && userNickname.isEmpty()
                        && userEmail.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "회원가입 정보 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if (userId.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "사용자 아이디 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if (userPwd.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "사용자 패스워드 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if (userCheckPwd.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "재확인 패스워드 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if (userName.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "사용자 이름 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if (userNickname.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "사용자 별명 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if (userEmail.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "사용자 본인 확인 이메일 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if (!userPwd.isEmpty() && (etUserPwd.getText().toString().length() < 6)) {
                    Toast.makeText(SignupActivity.this, "패스워드 6자 이상 입력하세요!", Toast.LENGTH_SHORT).show();
                    etUserPwd.setText("");
                    etCheckUserPwd.setText("");
                } else if (flagId == false && flagPwd == false && flagEmail == false) {
                    Toast.makeText(SignupActivity.this, "중복된 아이디입니다!\n패스워드 불일치!\n파이어베이스 계정에 등록된 이메일 계정입니다!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(SignupActivity.this, "아이디, 패스워드, 이메일 계정 다시 입력하세요!", Toast.LENGTH_SHORT).show();
                    etUserId.setText("");
                    etCheckUserPwd.setText("");
                    etUserEmail.setText("");
                } else if (flagId == true && flagPwd == false && flagEmail == false) {
                    Toast.makeText(SignupActivity.this, "패스워드 불일치!\n파이어베이스 계정에 등록된 이메일 계정입니다!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(SignupActivity.this, "패스워드, 이메일 계정 다시 입력하세요!", Toast.LENGTH_SHORT).show();
                    etCheckUserPwd.setText("");
                    etUserEmail.setText("");
                } else if (flagId == true && flagPwd == true && flagEmail == false) {
                    Toast.makeText(SignupActivity.this, "파이어베이스 계정에 등록된 이메일 계정입니다!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(SignupActivity.this, "이메일 계정 다시 입력하세요!", Toast.LENGTH_SHORT).show();
                    etUserEmail.setText("");
                } else if (flagId == true && flagPwd == false && flagEmail == true) {
                    Toast.makeText(SignupActivity.this, "패스워드 불일치!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(SignupActivity.this, "패스워드 다시 입력하세요!", Toast.LENGTH_SHORT).show();
                    etCheckUserPwd.setText("");
                } else if (flagId == false && flagPwd == true && flagEmail == false) {
                    Toast.makeText(SignupActivity.this, "중복된 아이디입니다!\n파이어베이스 계정에 등록된 이메일 계정입니다!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(SignupActivity.this, "아이디, 이메일 계정 다시 입력하세요!", Toast.LENGTH_SHORT).show();
                    etUserId.setText("");
                    etUserEmail.setText("");
                } else if (flagId == false && flagPwd == true && flagEmail == true) {
                    Toast.makeText(SignupActivity.this, "중복된 아이디입니다!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(SignupActivity.this, "아이디 다시 입력하세요!", Toast.LENGTH_SHORT).show();
                    etUserId.setText("");
                } else if (flagId == false && flagPwd == false && flagEmail == true) {
                    Toast.makeText(SignupActivity.this, "중복된 아이디입니다!\n패스워드 불일치!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(SignupActivity.this, "아이디, 패스워드 다시 입력하세요!", Toast.LENGTH_SHORT).show();
                    etUserId.setText("");
                    etCheckUserPwd.setText("");
                }
                else {
                    if (etUserPwd.getText().toString().equals(etCheckUserPwd.getText().toString())) {
                        firebaseAuthCreateUser(userId, userPwd, userCheckPwd, userName, userNickname, userEmail);
                    } else {
                        Toast.makeText(SignupActivity.this, "사용자 재확인 패스워드 불일치!", Toast.LENGTH_SHORT).show();
                        Toast.makeText(SignupActivity.this, "사용자 재확인 패스워드 다시 입력하세요!", Toast.LENGTH_SHORT).show();
                        etCheckUserPwd.setText("");
                    }
                }
            }
        });
    }
    private void firebaseAuthCreateUser(String userId, String userPwd, String checkUserPwd, String userName, String userNickname, String userEmail) {
        signupFirebaseAuth.createUserWithEmailAndPassword(userEmail, userPwd).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    SignupAccountDB signupAccountDB = new SignupAccountDB(userId, userPwd, checkUserPwd, userName, userNickname, userEmail);
                    signupFirestoreDB.collection("DIY_Signup").document(userId).set(signupAccountDB);
                    Toast.makeText(SignupActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SignupActivity.this, "회원가입 실패!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}