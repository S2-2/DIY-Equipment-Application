package kr.ac.kpu.diyequipmentapplication.menu;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import kr.ac.kpu.diyequipmentapplication.MainActivity;
import kr.ac.kpu.diyequipmentapplication.R;

public class LocationActivity extends AppCompatActivity {
    private EditText etAddress;
    private Button btnLocationUpdate;
    private ImageButton imgBtnBack;
    private ImageButton imgBtnHome;
    private TextView tvMyAddress;

    private FirebaseAuth locationFirebaseAuth;               //FirebaseAuth 참조 변수 선언
    private FirebaseFirestore locationFirebaseFirestore;     //파이어스토어 참조 변수 선언
    private ProgressDialog locationProgressDialog;          //progressDialog 뷰 참조 변수
    private String myLocation;
    private String getLocationEmail;
    private String getLocation;
    private String getLocationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        locationFirebaseAuth = FirebaseAuth.getInstance();                  //FirebaseAuth 초기화 및 객체 참조
        locationFirebaseFirestore = FirebaseFirestore.getInstance();        //파이어스토어 초기화 및 객체 참조
        locationProgressDialog = new ProgressDialog(this);
        imgBtnBack = (ImageButton) findViewById(R.id.location_btn_back);
        imgBtnHome = (ImageButton) findViewById(R.id.location_btn_home);
        btnLocationUpdate = (Button) findViewById(R.id.location_btn_update);
        etAddress = (EditText) findViewById(R.id.location_et_address);
        tvMyAddress = (TextView) findViewById(R.id.location_et_myLocation);

        //DIY_Location DB에서 locationEmail 가져오는 기능!
        locationFirebaseFirestore.collection("DIY_Location")
                .whereEqualTo("locationEmail", locationFirebaseAuth.getCurrentUser().getEmail().toString().trim())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                if (queryDocumentSnapshot.get("location").toString().trim() != null || !(queryDocumentSnapshot.get("location").toString().trim().isEmpty())) {
                                    getLocation = queryDocumentSnapshot.get("location").toString().trim();
                                    tvMyAddress.setText(getLocation);
                                } else
                                    tvMyAddress.setText("등록된 주소가 없습니다!");
                                getLocationEmail = queryDocumentSnapshot.get("locationEmail").toString().trim();
                                getLocationID = queryDocumentSnapshot.getId().toString().trim();

                            }
                        }
                    }
                });

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
                Intent intent = new Intent(LocationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        etAddress.setFocusable(false);
        etAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //주소 검색 웹뷰 화면으로 이동
                Intent intent = new Intent(LocationActivity.this, LocationSearchActivity.class);
                getSearchResult.launch(intent);
            }
        });


        btnLocationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(LocationActivity.this);
                dlg.setTitle("DIY_Location");
                dlg.setMessage("위치 변경 하시겠습니까?");
                dlg.setIcon(R.mipmap.ic_launcher);

                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //공급자가 입력한 모델명 및 공구 설명
                        final String userEmail = locationFirebaseAuth.getCurrentUser().getEmail().toString().trim();
                        final String locationEmail = getLocationEmail;
                        final String location = myLocation;

                        //DIY_Location DB에 locationEmail이 있는 경우 위치 변경
                        if (locationEmail != null && getLocation != null) {
                            locationProgressDialog.setTitle("DIY Location Uploading...");
                            locationProgressDialog.show();

                            Map<String, Object> locationUpdate = new HashMap<String, Object>();
                            locationUpdate.put("location", location);

                            locationFirebaseFirestore.collection("DIY_Location")
                                    .document(getLocationID)
                                    .update(locationUpdate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("위치 업데이트 성공!", "DocumentSnapshot successfully update!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("위치 업데이트 실패", "Error updating document", e);
                                        }
                                    });
                        } else { //DIY_Location DB에 locationEmail이 동일한 계정이 없는 경우 DIY_Location DB생성
                            LocationDB locationDB = new LocationDB(userEmail, location);
                            locationFirebaseFirestore.collection("DIY_Location").document().set(locationDB);
                        }

                        locationProgressDialog.dismiss();
                        Toast.makeText(LocationActivity.this, "위치 수정되었습니다!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(LocationActivity.this, "위치 변경 취소되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });
    }

    private final ActivityResultLauncher<Intent> getSearchResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                //LocationSearchActivity로부터 결과 값이 이곳으로 전달
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        String data = result.getData().getStringExtra("data");
                        myLocation = data;
                        etAddress.setText(data);
                    }
                }
            }
    );
}