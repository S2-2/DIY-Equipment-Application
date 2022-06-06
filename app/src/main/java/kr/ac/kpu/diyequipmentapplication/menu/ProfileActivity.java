package kr.ac.kpu.diyequipmentapplication.menu;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import kr.ac.kpu.diyequipmentapplication.MainActivity;
import kr.ac.kpu.diyequipmentapplication.R;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth profileFirebaseAuth;               //FirebaseAuth 참조 변수 선언
    private EditText etProfileNickname;                     //사용자 별명 참조할 뷰 참조 변수 선언
    private FirebaseFirestore profileFirebaseFirestore;     //파이어스토어 참조 변수 선언
    private ProgressDialog profileProgressDialog;          //progressDialog 뷰 참조 변수

    private ImageButton imgBtnBack;
    private ImageButton imgBtnHome;
    private ImageButton imgBtnProfile;
    private Button btnUpdate;
    private Button btnDelete;
    private String getUserProfileImageUrl;
    private String getProfileID;
    private String getSignupDBID;
    private String getProfileEmail;

    private static final int Gallery_Code = 1;             //갤러리 코드 상수 및 초기화
    private Uri profileImageUrl;                           //프로필 이미지 Url 참조 변수
    private FirebaseStorage profileFirebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileFirebaseAuth = FirebaseAuth.getInstance();                  //FirebaseAuth 초기화 및 객체 참조
        etProfileNickname = (EditText) findViewById(R.id.profile_et_nickname);    //사용자 닉네임 참조
        profileFirebaseFirestore = FirebaseFirestore.getInstance();        //파이어스토어 초기화 및 객체 참조

        imgBtnBack = (ImageButton) findViewById(R.id.profile_btn_back);
        imgBtnHome = (ImageButton) findViewById(R.id.profile_btn_home);
        imgBtnProfile = (ImageButton) findViewById(R.id.profile_btn_profile);
        btnUpdate = (Button) findViewById(R.id.profile_btn_update);
        btnDelete = (Button) findViewById(R.id.profile_btn_delete);
        profileProgressDialog = new ProgressDialog(this);
        profileFirebaseStorage = FirebaseStorage.getInstance();

        //뒤로가기 버튼 클릭 이벤트
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //홈버튼 클릭 이벤트
        imgBtnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(homeIntent);
            }
        });

        //프로필 이미지 버튼 클릭 이벤트
        imgBtnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, Gallery_Code);
            }
        });

        //프로필 삭제 버튼 클릭 이벤트
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(ProfileActivity.this);
                dlg.setTitle("DIY_Profile");
                dlg.setMessage("프로필 삭제 하시겠습니까?");
                dlg.setIcon(R.mipmap.ic_launcher);

                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        profileFirebaseFirestore.collection("DIY_Profile")
                                .document(getProfileID)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("프로필 삭제 성공!", "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("프로필 삭제 실패", "Error deleting document", e);
                                    }
                                });
                        Toast.makeText(ProfileActivity.this, "프로필 삭제되었습니다!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ProfileActivity.this, "프로필 삭제 취소되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
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
                                etProfileNickname.setText(queryDocumentSnapshot.get("userNickname").toString().trim());
                                getSignupDBID = queryDocumentSnapshot.getId().toString();
                            }
                        }
                    }
                });

        profileFirebaseFirestore.collection("DIY_Profile")
                .whereEqualTo("profileEmail", profileFirebaseAuth.getCurrentUser().getEmail().toString().trim())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                getProfileID = queryDocumentSnapshot.getId().toString().trim();
                                getProfileEmail = queryDocumentSnapshot.get("profileEmail").toString().trim();
                                getUserProfileImageUrl = queryDocumentSnapshot.get("profileImage").toString().trim();
                                Picasso.get().load(getUserProfileImageUrl).into(imgBtnProfile);
                            }
                        }
                    }
                });
    }

    //공급자가 입력한 데이터를 Firebase DB 및 Storage에 저장 구현
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String rbCheckedResult = null;

        if (requestCode == Gallery_Code && resultCode == RESULT_OK)
        {
            profileImageUrl = data.getData();
            imgBtnProfile.setImageURI(profileImageUrl);
        }

        //프로필 수정 버튼 클릭 이벤트
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(ProfileActivity.this);
                dlg.setTitle("DIY_Profile");
                dlg.setMessage("프로필 수정 하시겠습니까?");
                dlg.setIcon(R.mipmap.ic_launcher);

                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //공급자가 입력한 모델명 및 공구 설명
                        final String nickname = etProfileNickname.getText().toString().trim();
                        final String userEmail = profileFirebaseAuth.getCurrentUser().getEmail().toString().trim();
                        final String profileEmail = getProfileEmail;
                        //profileUpdate.put("profileNickname", getNickname);


                        //공급자가 입력한 데이터 등록 성공
                        if (!(nickname.isEmpty() && profileImageUrl != null))
                        {
                            profileProgressDialog.setTitle("DIY Profile Uploading...");
                            profileProgressDialog.show();

                            //Firebase storage에 등록된 이미지 경로 참조
                            StorageReference filepath = profileFirebaseStorage.getReference().child("DIY_Profile_Image").child(profileImageUrl.getLastPathSegment());
                            filepath.putFile(profileImageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            //Firebase DB에 공급자가 입력한 데이터 등록

                                            //profileDB에 profileEmail하고 동일한 계정이 있는 경우
                                            if (profileEmail != null) {
                                                String getprofileImage = task.getResult().toString();
                                                Map<String, Object> signupUpdate = new HashMap<String, Object>();
                                                Map<String, Object> profileUpdate = new HashMap<String, Object>();
                                                signupUpdate.put("userNickname", nickname);
                                                profileUpdate.put("profileNickname", nickname);
                                                profileUpdate.put("profileImage", getprofileImage);

                                                profileFirebaseFirestore.collection("DIY_Signup")
                                                        .document(getSignupDBID)
                                                        .update(signupUpdate)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("프로필 업데이트 성공!", "DocumentSnapshot successfully update!");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w("프로필 업데이트 실패", "Error updating document", e);
                                                            }
                                                        });

                                                profileFirebaseFirestore.collection("DIY_Profile")
                                                        .document(getProfileID)
                                                        .update(profileUpdate)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("프로필 업데이트 성공!", "DocumentSnapshot successfully update!");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w("프로필 업데이트 실패", "Error updating document", e);
                                                            }
                                                        });

                                            } else { //profileDB에 profileEmail하고 동일한 계정이 없는 경우
                                                Map<String, Object> signupUpdate = new HashMap<String, Object>();
                                                signupUpdate.put("userNickname", nickname);
                                                String profileImage = task.getResult().toString();
                                                ProfileDB profileDB = new ProfileDB(nickname, userEmail, profileImage);
                                                profileFirebaseFirestore.collection("DIY_Profile").document().set(profileDB);

                                                profileFirebaseFirestore.collection("DIY_Signup")
                                                        .document(getSignupDBID)
                                                        .update(signupUpdate)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("프로필 업데이트 성공!", "DocumentSnapshot successfully update!");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w("프로필 업데이트 실패", "Error updating document", e);
                                                            }
                                                        });
                                            }
                                            profileProgressDialog.dismiss();
                                            finish();
                                        }
                                    });
                                }
                            });
                        }
                        Toast.makeText(ProfileActivity.this, "프로필 수정되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                });

                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ProfileActivity.this, "프로필 수정 취소되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });
    }
}