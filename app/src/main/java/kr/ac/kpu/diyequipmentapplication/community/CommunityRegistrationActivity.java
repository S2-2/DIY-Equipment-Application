package kr.ac.kpu.diyequipmentapplication.community;

import android.app.ProgressDialog;
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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import kr.ac.kpu.diyequipmentapplication.R;

public class CommunityRegistrationActivity extends AppCompatActivity {
    //커뮤니티 등록 참조 변수 선언
    private ImageButton imgBtnBack;       //뒤로가기 버튼
    private ImageButton imgBtnImage;      //커뮤니티 이미지
    private EditText etTitle;            //커뮤니티 타이틀
    private EditText etContent;          //커뮤니티 내용
    private Button btnCommunityReg;     //커뮤니티 등록 버튼
    private String communityNickname;
    private SimpleDateFormat communityDateFormat;   //등록 날짜 형식 참조할 변수
    private Date communityDate;   //등록 날짜 참조할 변수
    private String communityGetDate;      //장비 등록 날짜 참조 변수

    private static final int Gallery_Code = 1;             //갤러리 코드 상수 및 초기화
    private Uri registrationImageUrl;               //장비 이미지 Url 참조 변수
    private ProgressDialog registrationProgressDialog;          //progressDialog 뷰 참조 변수

    //Firebase 참조 변수 선언
    private FirebaseStorage communityRegFirebaseStorage;
    private FirebaseFirestore communityRegFirebaseFirestoreDB;
    private FirebaseAuth communityRegFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_registration);

        //커뮤니티 등록 액티비티 필드 초기화
        imgBtnBack = (ImageButton) findViewById(R.id.communityRegistration_btn_back);
        etTitle = (EditText)findViewById(R.id.communityRegistration_et_title);
        imgBtnImage = (ImageButton)findViewById(R.id.communityRegistration_btn_image);
        etContent = (EditText)findViewById(R.id.communityRegistration_et_contents);
        btnCommunityReg = (Button)findViewById(R.id.communityRegistration_btn_registration);
        communityRegFirebaseFirestoreDB = FirebaseFirestore.getInstance();
        communityRegFirebaseStorage = FirebaseStorage.getInstance();
        communityRegFirebaseAuth = FirebaseAuth.getInstance();
        registrationProgressDialog = new ProgressDialog(this);
        communityDateFormat = new SimpleDateFormat("yyyy-MM-dd E, hh:mm:ss");    //날짜 형식 설정 객체 생성 및 초기화
        communityDate = new Date();  //날짜 객체 생성 및 초기화
        communityGetDate = communityDateFormat.format(communityDate);  //장비 등록일 참조

        //사용자 이메일 정보와 일치하는 데이터를 DIY_Signup DB에서 찾아서 etNickname 참조 변수에 닉네임 값 참조.
        communityRegFirebaseFirestoreDB.collection("DIY_Signup")
                .whereEqualTo("userEmail", communityRegFirebaseAuth.getCurrentUser().getEmail().toString().trim())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                Log.d("main SignupDB", queryDocumentSnapshot.get("userNickname").toString().trim());
                                communityNickname = queryDocumentSnapshot.get("userNickname").toString().trim();
                            }
                        }
                    }
                });

        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Insert 버튼 이벤트 구현
        btnCommunityReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //공급자가 입력한 모델명 및 공구 설명
                final String title = etTitle.getText().toString().trim();
                final String content = etContent.getText().toString().trim();
                final String nickname = communityNickname;
                final String dateAndTime = communityGetDate;
                final String tempImage= "no";

                //공급자가 입력한 데이터 등록 성공
                if (!(title.isEmpty() && content.isEmpty()))
                {
                    CommunityRegistration communityRegistration = new CommunityRegistration(title,content,tempImage, nickname, dateAndTime);
                    communityRegFirebaseFirestoreDB.collection("DIY_Equipment_Community").document().set(communityRegistration);
                    Toast.makeText(CommunityRegistrationActivity.this, "커뮤니티 등록 완료되었습니다!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        //Image 버튼 클릭 이벤트 구현
        imgBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, Gallery_Code);
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
            registrationImageUrl = data.getData();
            imgBtnImage.setImageURI(registrationImageUrl);
        }

        //Insert 버튼 이벤트 구현
        btnCommunityReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //공급자가 입력한 모델명 및 공구 설명
                final String title = etTitle.getText().toString().trim();
                final String content = etContent.getText().toString().trim();
                final String nickname = communityNickname;
                final String dateAndTime = communityGetDate;

                //공급자가 입력한 데이터 등록 성공
                if (!(title.isEmpty() && content.isEmpty()))
                {
                    registrationProgressDialog.setTitle("DIY Community Uploading...");
                    registrationProgressDialog.show();

                    //Firebase storage에 등록된 이미지 경로 참조
                    StorageReference filepath = communityRegFirebaseStorage.getReference().child("DIY_Community_Image").child(registrationImageUrl.getLastPathSegment());
                    filepath.putFile(registrationImageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    //Firebase DB에 공급자가 입력한 데이터 등록
                                    String t = task.getResult().toString();
                                    CommunityRegistration communityRegistration = new CommunityRegistration(title,content,task.getResult().toString(), nickname, dateAndTime);
                                    communityRegFirebaseFirestoreDB.collection("DIY_Equipment_Community").document().set(communityRegistration);
                                    registrationProgressDialog.dismiss();

                                    finish();
                                }
                            });
                        }
                    });
                }
            }
        });
    }
}