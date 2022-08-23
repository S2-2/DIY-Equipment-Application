package kr.ac.kpu.diyequipmentapplication.equipment;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.ac.kpu.diyequipmentapplication.MainActivity;
import kr.ac.kpu.diyequipmentapplication.R;
import kr.ac.kpu.diyequipmentapplication.cart.CartRecyclerview;

//공급자가 DIY장비 등록하는 액티비티
public class EquipmentRegistrationActivity extends AppCompatActivity {

    //DIY장비 등록 액티비티 필드 선언
    private FirebaseUser registrationFirebaseAuth = null;      //파이어베이스 인증 객체 참조 변수
    private FirebaseFirestore registrationFirestore = null;     // 파이어스토어 객체 참조 변수
    private CollectionReference registrationColRef = null;       // 파이어스토어 DB Collection 참조 변수
    private DocumentReference registrationDocRef = null;
    private DocumentReference registrationDocRef2 = null;
    private FirebaseStorage registrationStorage = null;        //Storage 객체 참조 변수
    private ImageButton registrationImgBtn = null;             //이미지 버튼 뷰 참조 변수
    private ImageButton imgBtn_back = null;
    private EditText registrationModelName = null, registrationModelInform = null;      //장비 모델명, 장비 정보 뷰 참조 변수
    private Button registrationBtnAdd = null;              //장비 등록 버튼 뷰 참조 변수
    private Button registrationBtnModify = null;
    private static final int Gallery_Code = 1;             //갤러리 코드 상수 및 초기화
    private Uri registrationImageUrl = null;               //장비 이미지 Url 참조 변수
    private ProgressDialog registrationProgressDialog = null;          //progressDialog 뷰 참조 변수
    private RadioGroup registrationRentalGroup = null;                  //라디오그룹 뷰 참조 변수
    private RadioButton registrationFreeRental = null, registrationFeeRental = null;   //라디오버튼 뷰 참조 변수
    private TextView registrationUserNickname;
    private EditText  registrationRentalCost = null,      //이메일, 렌탈가격, 렌탈주소, 렌탈종류 뷰 참조변수
            registrationRentalAddress = null, registrationRentalType = null;
    private String registrationGetUserEmail = null;     //사용자 이메일을 참조할 변수
    private SimpleDateFormat registrationDateFormat = null;   //등록 날짜 형식 참조할 변수
    private Date registrationDate = null;   //등록 날짜 참조할 변수
    private String registrationGetDate = null;      //장비 등록 날짜 참조 변수

    // Spinner 관련 변수 모음
    private Spinner sprModelCat1 = null;   // 장비 카테고리1 참조 변수
    private Spinner sprModelCat2 = null;   // 장비 카테고리2 참조 변수
    private List<String> cat1Subjects = null;
    private ArrayAdapter<String> cat1Adapter = null;
    private List<String> cat2Subjects = null;
    private ArrayAdapter<String> cat2Adapter = null;
    private String cat1String = null;

    // 게시물 수정시 이용될 변수 선언
    private Boolean registrationModify = false;
    private String  registrationModifyObject = null;

    //장비 등록 파이어스토어 DB 참조 변수 선언
    private FirebaseFirestore registrationFirebaseFirestoreDB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_registration);

        //DIY장비 등록 액티비티 필드 초기화
        registrationImgBtn = findViewById(R.id.imgBtn_diyRental);
        registrationUserNickname = findViewById(R.id.equipmentRegistration_tv_nickname);
        registrationModelName = findViewById(R.id.equipmentRegistration_et_modelName);
        registrationModelInform = findViewById(R.id.et_registrationModelInform);
        registrationBtnAdd = findViewById(R.id.equipmentRegistration_btn_registration);
        registrationBtnModify = findViewById(R.id.equipmentRegistration_btn_modification);
        registrationRentalGroup = findViewById(R.id.rg_registrationRentalGroup);
        registrationFeeRental = findViewById(R.id.rBtn_registrationFeeRental);
        registrationFreeRental = findViewById(R.id.rBtn_registrationFree);
        registrationRentalType = findViewById(R.id.et_registrationRentalType);
        registrationRentalCost = findViewById(R.id.et_registrationRentalCost);
        imgBtn_back = (ImageButton) findViewById(R.id.equipmentRegistration_btn_back);

        registrationRentalAddress = findViewById(R.id.et_registrationRentalAddress);
        registrationStorage = FirebaseStorage.getInstance();
        registrationProgressDialog = new ProgressDialog(this);
        registrationDateFormat = new SimpleDateFormat("yyyy-MM-dd");    //날짜 형식 설정 객체 생성 및 초기화
        registrationDate = new Date();  //날짜 객체 생성 및 초기화
        registrationGetDate = registrationDateFormat.format(registrationDate);  //장비 등록일 참조

        // Firestore에 있는 항목을 Spinner에 가져오기
        registrationFirestore = FirebaseFirestore.getInstance();
        registrationColRef = registrationFirestore.collection("DIY_Equipment_Category");
        registrationDocRef = registrationFirestore.document("DIY_Equipment_Category/Category1");
        registrationDocRef2 = registrationFirestore.document("DIY_Equipment_Category/Category2");
        sprModelCat1 = findViewById(R.id.spr_registrationCat1);
        sprModelCat2 = findViewById(R.id.spr_registrationCat2);

        cat1Subjects = new ArrayList<>();
        cat1Adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,cat1Subjects);
        cat1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprModelCat1.setAdapter(cat1Adapter);

        //장비 등록 Firestore DB 참조 및 초기화
        registrationFirebaseFirestoreDB = FirebaseFirestore.getInstance();

        registrationDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    List list = (List) document.getData().get("list");
                    for(int i=0; i<list.size();i++){
                        String cat1Subject = list.get(i).toString();
                        Log.i("Test","cat1Subject["+i+"] >" + cat1Subject);
                        cat1Subjects.add(cat1Subject);
                    }
                    cat1Adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
//                if(){
//                    registrationDocRef = registrationFirestore.document("DIY_Equipment_Category/"+cat1String);
//                }
            }
        });


        sprModelCat1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                cat2Subjects = new ArrayList<>();
                cat2Adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,cat2Subjects);
                cat2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sprModelCat2.setAdapter(cat2Adapter);
                cat1String = sprModelCat1.getItemAtPosition(position).toString();
                Log.i("Test","cat1String >" + cat1String);

                registrationDocRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            List list2 = (List) document.getData().get(cat1String);

                            for(int j=0; j<list2.size();j++){
                                String cat2Subject = list2.get(j).toString();
                                Log.i("Test","cat2Subject["+j+"] >" + cat2Subject);
                                cat2Subjects.add(cat2Subject);
                            }
                            cat2Adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(EquipmentRegistrationActivity.this, "아무것도 선택되지 않음", Toast.LENGTH_SHORT).show();
            }
        });


        //사용자 인증 이메일 가져오는 구간
        registrationFirebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
        if (registrationFirebaseAuth != null)   //firebase인증에 등록된 계정이 null이 아닌 경우
        {
            registrationGetUserEmail = registrationFirebaseAuth.getEmail(); //등록된 계정 정보에서 이메일 값 참조

            registrationFirebaseFirestoreDB.collection("DIY_Signup")
                    .whereEqualTo("userEmail", registrationGetUserEmail)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                    registrationUserNickname.setText(queryDocumentSnapshot.get("userNickname").toString().trim());
                                }
                            }
                        }
                    });
        }
        else{       //계정 정보가 없는 경우
            Toast.makeText(EquipmentRegistrationActivity.this, "인증 이메일 가져오기 실패!", Toast.LENGTH_SHORT).show();
        }

        //렌탈 종류, 렌탈 가격 뷰 비활성화
        registrationRentalType.setEnabled(false);
        registrationRentalCost.setEnabled(false);

        //라디오버튼 클릭시 변경되는 이벤트 리스너
        registrationRentalGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rBtn_registrationFeeRental:     //유료인 경우
                        registrationRentalCost.setEnabled(true);
                        registrationRentalCost.setText("");
                        registrationRentalType.setText(registrationFeeRental.getText().toString().trim());
                        break;

                    case R.id.rBtn_registrationFree:    //무료인 경우
                        registrationRentalCost.setEnabled(false);
                        registrationRentalCost.setText("무료");
                        registrationRentalType.setText(registrationFreeRental.getText().toString().trim());
                        break;
                }
            }
        });

        // 장비가 등록이 아닌 수정이면 아래 이벤트 발생
        Intent intent = getIntent();
        if(intent.getStringExtra("EquipmentDetail")!=null&&intent.getStringExtra("EquipmentDetail").equals("modify")){
            final String[] modifyDocId = {null};
            final String[] registrationLikeNum = {null};

            registrationModify = true;
            registrationModifyObject = intent.getStringExtra("EquipmentObject");
            registrationBtnAdd.setEnabled(false);
            registrationBtnAdd.setVisibility(View.GONE);

            registrationFirebaseFirestoreDB.collection("DIY_Equipment_Rental")
                    .whereEqualTo("rentalImage",registrationModifyObject)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @SuppressLint("ResourceType")
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            modifyDocId[0] = queryDocumentSnapshot.getId();
                            registrationImageUrl = Uri.parse(registrationModifyObject);
                            Picasso.get().load(registrationModifyObject).into(registrationImgBtn);
                            registrationLikeNum[0] = queryDocumentSnapshot.get("modelLikeNum").toString().trim();

                            registrationModelInform.setText(queryDocumentSnapshot.get("modelInform").toString().trim());
                            registrationModelName.setText(queryDocumentSnapshot.get("modelName").toString().trim());
                            registrationRentalAddress.setText(queryDocumentSnapshot.get("rentalAddress").toString().trim());
                            sprModelCat1.setSelection(getIndex(sprModelCat1,queryDocumentSnapshot.get("modelCategory1")));
                            sprModelCat2.setSelection(getIndex(sprModelCat2,queryDocumentSnapshot.get("modelCategory2")));

                            if(queryDocumentSnapshot.get("rentalType").equals("무료나눔")){   // 대여비 무료인 경우
                                registrationRentalGroup.check(R.id.rBtn_registrationFree);
                                registrationRentalCost.setEnabled(false);
                            }else{  // 유료인 경우
                                registrationRentalGroup.check(R.id.rBtn_registrationFeeRental);
                                registrationRentalCost.setEnabled(true);
                            }
                            registrationRentalType.setText(queryDocumentSnapshot.get("rentalType").toString().trim());
                            registrationRentalCost.setText(queryDocumentSnapshot.get("rentalCost").toString().trim());
                        }
                    }
                }
            });
            //            modifyEquipment(intent.getStringExtra("getImageUrl").toString());
            // Modify 버튼 이벤트 구현
            registrationBtnModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //공급자가 입력한 모델명 및 공구 설명
                    final String mn = registrationModelName.getText().toString().trim();
                    final String mt = registrationModelInform.getText().toString().trim();
                    final String t = registrationImageUrl.toString().trim();
                    final String rt = registrationRentalType.getText().toString().trim();
                    final String ra = registrationRentalAddress.getText().toString().trim();
                    final String rc = registrationRentalCost.getText().toString().trim();
                    final String mc1 = sprModelCat1.getSelectedItem().toString().trim();
                    final String mc2 = sprModelCat2.getSelectedItem().toString().trim();

                    //공급자가 입력한 데이터 등록 성공
                    if (!(mn.isEmpty() && mt.isEmpty() && rt.isEmpty() && ra.isEmpty() && rc.isEmpty() && mc1.isEmpty() && mc2.isEmpty() && registrationImageUrl != null))
                    {
                        registrationProgressDialog.setTitle("DIY Rental Registration Uploading...");
                        registrationProgressDialog.show();

                        EquipmentRegistration equipmentRegistration = new EquipmentRegistration(mn,mt,t, rt, rc, ra, registrationGetUserEmail, registrationGetDate, mc1, mc2);
                        equipmentRegistration.setModelLikeNum(registrationLikeNum[0]);
                        registrationFirebaseFirestoreDB.collection("DIY_Equipment_Rental").document(modifyDocId[0]).set(equipmentRegistration).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(EquipmentRegistrationActivity.this, "수정 되었습니다!", Toast.LENGTH_SHORT).show();
                                registrationProgressDialog.dismiss();
                            }
                        });
                        Intent intent = new Intent(EquipmentRegistrationActivity.this, MainActivity.class);
                        startActivity(intent);

                    }
                    else{
                        Log.e("modify-error","something is null");
                    }
                }
            });
        }
        else{
            registrationBtnModify.setEnabled(false);
            registrationBtnModify.setVisibility(View.GONE);
        }

        imgBtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Image 버튼 클릭 이벤트 구현
        registrationImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, Gallery_Code);
            }
        });
    }

    private int getIndex(Spinner spr, Object item) {
        for (int i=0; i< spr.getCount(); i++){
            if(spr.getItemAtPosition(i).toString().equalsIgnoreCase(item.toString())){
                return i;
            }
        }
        Log.e("getIndex","failed..");
        return 0;
    }

    //공급자가 입력한 데이터를 Firebase DB 및 Storage에 저장 구현
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String rbCheckedResult = null;

        if (requestCode == Gallery_Code && resultCode == RESULT_OK)
        {
            registrationImageUrl = data.getData();
            registrationImgBtn.setImageURI(registrationImageUrl);
        }

        // Insert 버튼 이벤트 구현
        registrationBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //공급자가 입력한 모델명 및 공구 설명
                final String mn = registrationModelName.getText().toString().trim();
                final String mt = registrationModelInform.getText().toString().trim();
                final String rt = registrationRentalType.getText().toString().trim();
                final String ra = registrationRentalAddress.getText().toString().trim();
                final String rc = registrationRentalCost.getText().toString().trim();
                final String mc1 = sprModelCat1.getSelectedItem().toString().trim();
                final String mc2 = sprModelCat2.getSelectedItem().toString().trim();

                //공급자가 입력한 데이터 등록 성공
                if (!(mn.isEmpty() && mt.isEmpty() && rt.isEmpty() && ra.isEmpty() && rc.isEmpty() && mc1.isEmpty() && mc2.isEmpty() && registrationImageUrl != null))
                {
                    registrationProgressDialog.setTitle("DIY Rental Registration Uploading...");
                    registrationProgressDialog.show();

                    //Firebase storage에 등록된 이미지 경로 참조
                    StorageReference filepath = registrationStorage.getReference().child("DIY_Rental_Image").child(registrationImageUrl.getLastPathSegment());
                    filepath.putFile(registrationImageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    //Firebase DB에 공급자가 입력한 데이터 등록
                                    String t = task.getResult().toString();
                                    EquipmentRegistration equipmentRegistration = new EquipmentRegistration(mn,mt,task.getResult().toString(), rt, rc, ra, registrationGetUserEmail, registrationGetDate, mc1, mc2);
                                    registrationFirebaseFirestoreDB.collection("DIY_Equipment_Rental").document().set(equipmentRegistration);
                                    registrationProgressDialog.dismiss();

                                    //공급자가 입력한 DIY 등록 액티비티에서 DIY 메인 액티비티로 이동
                                    Intent intent = new Intent(EquipmentRegistrationActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }
                else{
                    Log.e("modify-error","something is null");
                }
            }
        });
    }
}