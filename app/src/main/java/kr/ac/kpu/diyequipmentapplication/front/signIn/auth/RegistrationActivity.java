package kr.ac.kpu.diyequipmentapplication.front.signIn.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import kr.ac.kpu.diyequipmentapplication.R;

//공급자가 DIY장비 등록하는 액티비티
public class RegistrationActivity extends AppCompatActivity {

    //DIY장비 등록 액티비티 필드
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseStorage mStorage;
    ImageButton imageButton;
    EditText editModelName, editModelText;
    Button btnInsert;
    private static final int Gallery_Code = 1;
    Uri imageUrl = null;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        //R.layout.activity_registration xml파일에서 해당 뷰 객체 참조
        imageButton = findViewById(R.id.imageButton2);
        editModelName = findViewById(R.id.modelName);
        editModelText = findViewById(R.id.modelText);
        btnInsert = findViewById(R.id.btn_Insert);

        //Firebase DB 및 storage 객체 참조
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("DIY_Model");
        mStorage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);

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

                //공급자가 입력한 데이터 등록 성공
                if (!(mn.isEmpty() && mt.isEmpty() && imageUrl != null))
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