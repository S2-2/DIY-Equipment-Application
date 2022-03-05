package kr.ac.kpu.diyequipmentapplication.front.signIn.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import kr.ac.kpu.diyequipmentapplication.R;

//공급자가 DIY장비 등록하는 액티비티
public class RegistrationActivity extends AppCompatActivity {

    //DIY장비 등록 액티비티 필드
    private FirebaseUser nFirebaseAuth; //파이어베이스 인증
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private FirebaseStorage mStorage;
    private ImageButton imageButton;
    private EditText editModelName, editModelText;
    private Button btnInsert;
    private static final int Gallery_Code = 1;
    private Uri imageUrl = null;
    private ProgressDialog progressDialog;

    //라디오 그룹, 주소, 금액
    private RadioGroup rgRental;
    private RadioButton rbFreeRental, rbFeeRental;
    private EditText etUserEmail, edRentalCost, edRentalAddress, edRentalType;
    private String userGetEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        //R.layout.activity_registration xml파일에서 해당 뷰 객체 참조
        imageButton = findViewById(R.id.imageButton2);
        etUserEmail = findViewById(R.id.et_userEmail);
        editModelName = findViewById(R.id.modelName);
        editModelText = findViewById(R.id.modelText);
        btnInsert = findViewById(R.id.btn_Insert);

        rgRental = findViewById(R.id.rental_Group);
        rbFeeRental = findViewById(R.id.rg_FeeRental);
        rbFreeRental = findViewById(R.id.rg_FreeRental);
        edRentalType = findViewById(R.id.ed_rgResult);

        edRentalCost = findViewById(R.id.ed_rentalCost);
        edRentalAddress = findViewById(R.id.ed_rentalAddress);


        //Firebase DB 및 storage 객체 참조
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("DIY_Equipment_Rental");
        mStorage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);

        //사용자 인증 이메일 가져오는 구간
        nFirebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
        if (nFirebaseAuth != null)
        {
            userGetEmail = nFirebaseAuth.getEmail();
            etUserEmail.setText(userGetEmail);
        }
        else{
            Toast.makeText(RegistrationActivity.this, "이메일 가져오기 실패!", Toast.LENGTH_SHORT).show();
        }


        //해당 뷰 비활성화
        edRentalType.setEnabled(false);
        edRentalCost.setEnabled(false);

        //라디오버튼 클릭시 변경되는 이벤트 리스너
        rgRental.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rg_FeeRental:     //유료인 경우
                        edRentalCost.setEnabled(true);
                        edRentalType.setText(rbFeeRental.getText().toString().trim());
                        break;

                    case R.id.rg_FreeRental:    //무료인 경우
                        edRentalCost.setEnabled(false);
                        edRentalCost.setText("무료");
                        edRentalType.setText(rbFreeRental.getText().toString().trim());
                        break;
                }

            }
        });

        //Image 버튼 클릭 이벤트 구현
        imageButton.setOnClickListener(new View.OnClickListener() {
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
            imageUrl = data.getData();
            imageButton.setImageURI(imageUrl);
        }

        //Insert 버튼 이벤트 구현
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //공급자가 입력한 모델명 및 공구 설명
                final String mn = editModelName.getText().toString().trim();
                final String mt = editModelText.getText().toString().trim();

                final String rt = edRentalType.getText().toString().trim();
                final String ra = edRentalAddress.getText().toString().trim();
                final String rc = edRentalCost.getText().toString().trim();


                //공급자가 입력한 데이터 등록 성공
                if (!(mn.isEmpty() && mt.isEmpty() && rt.isEmpty() && ra.isEmpty() && rc.isEmpty() && imageUrl != null))
                {
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();

                    //Firebase storage에 등록된 이미지 경로 참조
                    StorageReference filepath = mStorage.getReference().child("imagePost").child(imageUrl.getLastPathSegment());
                    filepath.putFile(imageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    //Firebase DB에 공급자가 입력한 데이터 등록
                                    String t = task.getResult().toString();
                                    DatabaseReference newPost = mRef.push();

                                    newPost.child("ModelName").setValue(mn);
                                    newPost.child("ModelText").setValue(mt);
                                    newPost.child("RentalType").setValue(rt);
                                    newPost.child("RentalCost").setValue(rc);
                                    newPost.child("RentalAddress").setValue(ra);
                                    newPost.child("image").setValue(task.getResult().toString());

                                    progressDialog.dismiss();

                                    //공급자가 입력한 DIY 등록 액티비티에서 DIY 목록 액티비티로 이동
                                    Intent intent = new Intent(RegistrationActivity.this, RegistrationRecyclerView.class);
                                    startActivity(intent);
                                }
                            });

                        }
                    });

                }
            }
        });
    }
}