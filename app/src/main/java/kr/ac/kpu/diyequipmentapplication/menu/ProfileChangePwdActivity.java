package kr.ac.kpu.diyequipmentapplication.menu;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import kr.ac.kpu.diyequipmentapplication.R;

public class ProfileChangePwdActivity extends AppCompatActivity {

    private FirebaseAuth profileFirebaseAuth;               //FirebaseAuth 참조 변수 선언
    private FirebaseFirestore profileFirebaseFirestore;     //파이어스토어 참조 변수 선언
    private EditText etProfileId;
    private EditText etNewPwd;
    private EditText etCheckPwd;
    private Button btnUpdate;
    private ImageButton imgBtnBack;
    private String getUserId, getSignupDBID;
    private Boolean flagPwd = false;
    private TextView tvCheckPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change_pwd);

        profileFirebaseAuth = FirebaseAuth.getInstance();                  //FirebaseAuth 초기화 및 객체 참조
        profileFirebaseFirestore = FirebaseFirestore.getInstance();        //파이어스토어 초기화 및 객체 참조
        etProfileId = (EditText) findViewById(R.id.changePwd_et_checkId);
        etNewPwd = (EditText) findViewById(R.id.changePwd_et_Pwd);
        etCheckPwd = (EditText) findViewById(R.id.changePwd_et_Pwd2);
        btnUpdate = (Button) findViewById(R.id.changePwd_btn_check);
        imgBtnBack = (ImageButton) findViewById(R.id.signup_btn_back);
        tvCheckPwd = (TextView) findViewById(R.id.changePwd_tv_checkPwd);

        //패스워드 일치, 불일치 검사!
        etCheckPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if (etNewPwd.getText().toString().trim().equals(etCheckPwd.getText().toString().trim())) {
                    flagPwd = true;
                    tvCheckPwd.setTextColor(Color.BLUE);
                    tvCheckPwd.setText("패스워드 일치!");
                    etCheckPwd.setBackgroundResource(R.drawable.blue_etittext);

                } else if (etCheckPwd.getText().toString().trim().isEmpty()) {
                    flagPwd = false;
                    tvCheckPwd.setText("");
                    etCheckPwd.setBackgroundResource(R.drawable.white_edittext);

                } else {
                    flagPwd = false;
                    tvCheckPwd.setTextColor(Color.RED);
                    tvCheckPwd.setText("패스워드 불일치!");
                    etCheckPwd.setBackgroundResource(R.drawable.red_etittext);
                }
            }
        });

        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //DIY_Signup DB에서 사용자 계정에 맞는 닉네임 가져오는 기능 구현.
        //사용자 이메일 정보와 일치하는 데이터를 DIY_Signup DB에서 찾아서 etProfileNickname 참조 변수에 닉네임 값 참조.
        profileFirebaseFirestore.collection("DIY_Signup")
                .whereEqualTo("userEmail", profileFirebaseAuth.getCurrentUser().getEmail().toString().trim())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                etProfileId.setText(queryDocumentSnapshot.get("userID").toString().trim());
                                getSignupDBID = queryDocumentSnapshot.getId();
                            }
                        }
                    }
                });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(ProfileChangePwdActivity.this);
                dlg.setTitle("DIY_Profile");
                dlg.setMessage("사용자 비밀번호 변경 하시겠습니까?");
                dlg.setIcon(R.mipmap.ic_launcher);

                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String newPwd = etCheckPwd.getText().toString().trim();

                        if (newPwd != null) {
                            Map<String, Object> signupUpdate = new HashMap<String, Object>();
                            signupUpdate.put("userPwd1", newPwd);
                            signupUpdate.put("userPwd2", newPwd);

                            profileFirebaseFirestore.collection("DIY_Signup")
                                    .document(getSignupDBID)
                                    .update(signupUpdate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("사용자 비밀번호 업데이트 성공!", "DocumentSnapshot successfully update!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("사용자 비밀번호 업데이트 실패", "Error updating document", e);
                                        }
                                    });
                        }
                        Toast.makeText(ProfileChangePwdActivity.this, "사용자 비밀번호 변경되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                });

                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ProfileChangePwdActivity.this, "사용자 비밀번호 변경 취소되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });
    }
}