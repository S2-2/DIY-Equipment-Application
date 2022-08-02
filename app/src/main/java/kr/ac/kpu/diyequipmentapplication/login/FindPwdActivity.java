package kr.ac.kpu.diyequipmentapplication.login;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import kr.ac.kpu.diyequipmentapplication.R;

public class FindPwdActivity extends AppCompatActivity {

    private EditText etFindPwdId;
    private EditText etFindPwdName;
    private EditText etFindPwdEmail;
    private Button btnFindPwdAuthEmail;

    private FirebaseAuth findPwdFirebaseAuth;
    private FirebaseUser findPwdFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd);

        etFindPwdId = (EditText) findViewById(R.id.findPwd_et_id);
        etFindPwdName = (EditText) findViewById(R.id.findPwd_et_name);
        etFindPwdEmail = (EditText) findViewById(R.id.findPwd_et_email);
        btnFindPwdAuthEmail = (Button) findViewById(R.id.findPwd_btn_authNum);
        findPwdFirebaseAuth = FirebaseAuth.getInstance();


        btnFindPwdAuthEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findPwdFirebaseUser = findPwdFirebaseAuth.getCurrentUser();
                findPwdFirebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(FindPwdActivity.this, "인증 메일 전송했습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FindPwdActivity.this, "인증 메일 전송 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                /*
                String userEmail = etFindPwdEmail.getText().toString();
                String userPwd = "test123";
                findPwdFirebaseAuth.signInWithEmailAndPassword(userEmail,userPwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        findPwdFirebaseUser = findPwdFirebaseAuth.getCurrentUser();
                        findPwdFirebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(FindPwdActivity.this, "인증 메일 전송했습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(FindPwdActivity.this, "인증 메일 전송 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                });

                 */

                //findPwd();
            }
        });
    }

    private void findPwd() {
        String userEmail = etFindPwdEmail.getText().toString();

        if (userEmail.length() != 0) {
            findPwdFirebaseAuth.sendPasswordResetEmail(userEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(FindPwdActivity.this, "인증 메일을 보냈습니다!", Toast.LENGTH_SHORT).show();
                                Log.d("findPwd", "Email sent.");
                            }
                        }
                    });
        } else {
            Toast.makeText(FindPwdActivity.this, "입력을 완료하여 주십시오!", Toast.LENGTH_SHORT).show();
        }
    }
}