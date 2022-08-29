package kr.ac.kpu.diyequipmentapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import kr.ac.kpu.diyequipmentapplication.chat.ChatDTO;
import kr.ac.kpu.diyequipmentapplication.menu.LocationSearchActivity;

public class ScheduleReturnActivity extends AppCompatActivity {

    private ImageButton imgBtnBack;

    private Button btnTransactionDate;      //거래날짜
    private Button btnTransactionTime;      //거래시간
    private Button btnTransactionLocation;  //거래장소
    private Button btnSetup;                //거래정보 설정
    private Button btnUpdate;               //거래정보 수정
    private Button btnDelete;               //거래정보 삭제

    private FirebaseAuth scheduleFirebaseAuth;
    private FirebaseFirestore scheduleFirebaseFirestore;
    private DatabaseReference scheduleRef;
    private ProgressDialog scheduleProgressDialog;

    private Dialog transactionDateDialog;       //거래날짜 커스텀 다이얼로그
    private Dialog transactionTimeDialog;       //거래시간 커스텀 다이얼로그
    private Dialog transactionLocationDialog;   //거래장소 커스텀 다이얼로그
    private Boolean controlDateFlag;        //대여기간(true), 거래날짜(false) 제어변수
    private Boolean controlFlag;            //대여기간 시작일(true), 종료일(false) 제어변수

    // 시스템 메세지를 위한 Database, Firestore, chatDTO 참조
    private DatabaseReference scheduleReturnRef;
    private FirebaseDatabase scheduleReturnDatabase;
    private  ChatDTO chatDTO;   // 시스템 메세지

    private TextView tvTransactionDate, tvTransactionTime, tvGetTransactionLocation;
    private TextView tvTransDay, tvTransTime, tvTransPlace;

    private ScheduleReturnDB scheduleReturnDB;
    private String getTransactionId;
    private String getScheduleReturnId;
    private String scheduleId;

    //DatePickerDialog 사용 관련 참조위젯
    private Dialog lendingPeriodDialog;
    private Calendar scheduleCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener scheduleDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            scheduleCalendar.set(Calendar.YEAR, year);
            scheduleCalendar.set(Calendar.MONTH, month);
            scheduleCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            transactionDateLabel();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_return);

        scheduleReturnDB = new ScheduleReturnDB();
        imgBtnBack = (ImageButton) findViewById(R.id.schedule_btn_back);

        btnTransactionDate = (Button) findViewById(R.id.schedule_btn_transactionDate);
        btnTransactionTime = (Button) findViewById(R.id.schedule_btn_transactionTime);
        btnTransactionLocation = (Button) findViewById(R.id.schedule_btn_transactionLocation);
        btnSetup = (Button) findViewById(R.id.schedule_btn_setup);
        btnUpdate = (Button) findViewById(R.id.schedule_btn_update);
        btnDelete = (Button) findViewById(R.id.schedule_btn_delete);

        tvTransDay = (TextView) findViewById(R.id.schedule_tv_transactionDate);
        tvTransTime = (TextView) findViewById(R.id.schedule_tv_transactionTime);
        tvTransPlace = (TextView) findViewById(R.id.schedule_tv_transactionLocation);

        //거래날짜 커스텀 다이얼로그 참조
        transactionDateDialog = new Dialog(ScheduleReturnActivity.this);
        transactionDateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        transactionDateDialog.setContentView(R.layout.schedule_transactiondate_window_item);
        tvTransactionDate= transactionDateDialog.findViewById(R.id.transactionDateWindow_tv_transactionDate);  //거래날짜

        //거래시간 커스텀 다이얼로그 참조
        transactionTimeDialog = new Dialog(ScheduleReturnActivity.this);
        transactionTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        transactionTimeDialog.setContentView(R.layout.schedule_transactiontime_window_item);
        tvTransactionTime= transactionTimeDialog.findViewById(R.id.transactionTimeWindow_tv_transactionTime);  //거래시간

        //거래장소 커스텀 다이얼로그 참조
        transactionLocationDialog = new Dialog(ScheduleReturnActivity.this);
        transactionLocationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        transactionLocationDialog.setContentView(R.layout.schedule_transactionlocation_window_item);
        tvGetTransactionLocation= transactionLocationDialog.findViewById(R.id.transactionLocationWindow_tv_transactionLocation);

        Intent intent = getIntent();
        getTransactionId = intent.getStringExtra("RentalHistoryId");
        scheduleReturnDB.setsChatNum(intent.getStringExtra("ReturnChatNum"));
        scheduleId = intent.getStringExtra("ReturnScheduleID");
        scheduleReturnDB.setsTransactionId(getTransactionId);

        scheduleFirebaseAuth = FirebaseAuth.getInstance();
        scheduleReturnDB.setsUserEmail(scheduleFirebaseAuth.getCurrentUser().getEmail().toString());

        scheduleFirebaseFirestore = FirebaseFirestore.getInstance();
        scheduleProgressDialog = new ProgressDialog(this);

        scheduleReturnDatabase = FirebaseDatabase.getInstance();
        scheduleReturnRef = scheduleReturnDatabase.getReference().child("DIY_Chat");

        //DIY_Schedule DB에서 sUserEmail에 해당하는 계정이 있는지 확인하는 메소드
        scheduleFirebaseFirestore.collection("DIY_ScheduleReturn")
                .whereEqualTo("sTransactionId", getTransactionId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                getScheduleReturnId = queryDocumentSnapshot.getId();
                                scheduleReturnDB.setsTransactionDate(queryDocumentSnapshot.get("sTransactionDate").toString().trim());
                                scheduleReturnDB.setsTransactionTime(queryDocumentSnapshot.get("sTransactionTime").toString().trim());
                                scheduleReturnDB.setsTransactionLocation(queryDocumentSnapshot.get("sTransactionLocation").toString().trim());


                                tvTransactionDate.setText(scheduleReturnDB.getsTransactionDate());
                                tvTransactionTime.setText(scheduleReturnDB.getsTransactionTime());
                                tvGetTransactionLocation.setText(scheduleReturnDB.getsTransactionLocation());

                                tvTransDay.setText(scheduleReturnDB.getsTransactionDate());
                                tvTransTime.setText(scheduleReturnDB.getsTransactionTime());
                                tvTransPlace.setText(scheduleReturnDB.getsTransactionLocation());

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

        //거래날짜 버튼 클릭 이벤트
        btnTransactionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTransactionDateDialog();
            }
        });

        //거래시간 버튼 클릭 이벤트
        btnTransactionTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTransactionTimeDialog();
            }
        });

        //거래장소 버튼 클릭 이벤트
        btnTransactionLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTransactionLocationDialog();
            }
        });

        //거래정보 설정 버튼 클릭 이벤트
        btnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(ScheduleReturnActivity.this);
                dlg.setTitle("DIY_반납정보");
                dlg.setMessage("반납정보 설정 하시겠습니까?");
                dlg.setIcon(R.mipmap.ic_launcher);

                scheduleFirebaseFirestore.collection("DIY_Transaction")
                        .whereEqualTo("tScheduleId",scheduleId)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot snapshot : task.getResult()){
                                if(snapshot.get("tOtherEmail").toString() != scheduleReturnDB.getsUserEmail()){
                                    scheduleReturnDB.setsOtherEmail(snapshot.get("tOtherEmail").toString());
                                    Log.e("scheduleReturnDB", snapshot.get("tOtherEmail").toString());
                                }else{
                                    scheduleReturnDB.setsOtherEmail(snapshot.get("tUserEmail").toString());
                                    Log.e("scheduleReturnDB", snapshot.get("tUserEmail").toString());
                                }
                            }
                        }
                    }
                });

                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (scheduleReturnDB.getsTransactionDate() != null && scheduleReturnDB.getsTransactionTime() != null
                        && scheduleReturnDB.getsTransactionLocation() != null && scheduleReturnDB.getsOtherEmail()!=null && scheduleReturnDB.getsChatNum() != null) {
                            scheduleProgressDialog.setTitle("DIY ScheduleReturn Uploading...");
                            scheduleProgressDialog.show();

                            //firestore DB에 저장
                            scheduleFirebaseFirestore.collection("DIY_ScheduleReturn").document().set(scheduleReturnDB);
                            Toast.makeText(ScheduleReturnActivity.this, "반납정보 설정 완료되었습니다!", Toast.LENGTH_SHORT).show();

                            // 시스템 채팅 등록
                            systemScheduleMsg(scheduleReturnDB, "1");

                        } else {
                            Toast.makeText(ScheduleReturnActivity.this, "반납정보 설정을 할 수 없습니다!", Toast.LENGTH_SHORT).show();
                            Toast.makeText(ScheduleReturnActivity.this, "반납정보 설정 다시 입력하세요!", Toast.LENGTH_SHORT).show();
                        }
                        scheduleProgressDialog.dismiss();
                        finish();
                    }
                });

                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ScheduleReturnActivity.this, "반납정보 설정 취소되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });

        //거래정보 수정 버튼 클릭 이벤트
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(ScheduleReturnActivity.this);
                dlg.setTitle("DIY_반납정보");
                dlg.setMessage("반납정보 수정 하시겠습니까?");
                dlg.setIcon(R.mipmap.ic_launcher);

                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String userEmail = scheduleFirebaseAuth.getCurrentUser().getEmail().toString();   //사용자 이메일
                        Map<String, Object> scheduleUpdate = new HashMap<String, Object>();
                        scheduleUpdate.put("sTransactionDate", scheduleReturnDB.getsTransactionDate());
                        scheduleUpdate.put("sTransactionTime", scheduleReturnDB.getsTransactionTime());
                        scheduleUpdate.put("sTransactionLocation", scheduleReturnDB.getsTransactionLocation());

                        if (scheduleReturnDB.getsTransactionDate() != null && scheduleReturnDB.getsTransactionTime() != null
                                && scheduleReturnDB.getsTransactionLocation() != null) {
                            scheduleFirebaseFirestore.collection("DIY_Schedule")
                                    .document(getScheduleReturnId)
                                    .update(scheduleUpdate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("반납정보 업데이트 성공!", "DocumentSnapshot successfully update!");
                                            // 시스템 채팅 등록
                                            systemScheduleMsg(scheduleReturnDB, "2");

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("반납정보 업데이트 실패", "Error updating document", e);
                                        }
                                    });
                            Toast.makeText(ScheduleReturnActivity.this, "반납정보 수정 완료되었습니다!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ScheduleReturnActivity.this, "반납정보 수정 취소되었습니다!", Toast.LENGTH_SHORT).show();

                        }
                        scheduleProgressDialog.dismiss();
                        finish();
                    }
                });

                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ScheduleReturnActivity.this, "반납정보 수정 취소되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });

        //거래정보 삭제 버튼 클릭 이벤트
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(ScheduleReturnActivity.this);
                dlg.setTitle("DIY_반납정보");
                dlg.setMessage("반납정보 삭제 하시겠습니까?");
                dlg.setIcon(R.mipmap.ic_launcher);

                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        scheduleFirebaseFirestore.collection("DIY_ScheduleReturn")
                                .document(getScheduleReturnId)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("반납정보 삭제 성공!", "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("반납정보 삭제 실패", "Error deleting document", e);
                                    }
                                });
                        Toast.makeText(ScheduleReturnActivity.this, "반납정보 삭제되었습니다!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ScheduleReturnActivity.this, "반납정보 삭제 취소되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });
    }

    //거래날짜 계산 메소드
    private void transactionDateLabel() {
        String myFormat = "yyyy/MM/dd";    // 출력형식   2021/07/26
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

        scheduleReturnDB.setsTransactionDate(sdf.format(scheduleCalendar.getTime())); //거래일
        tvTransactionDate.setText(scheduleReturnDB.getsTransactionDate());
        tvTransDay.setText(scheduleReturnDB.getsTransactionDate());
    }

    //거래날짜 커스텀 다이얼로그 기능 메소드
    private void showTransactionDateDialog() {
        tvTransactionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlDateFlag = false;
                new DatePickerDialog(ScheduleReturnActivity.this, scheduleDatePicker, scheduleCalendar.get(Calendar.YEAR),
                        scheduleCalendar.get(Calendar.MONTH), scheduleCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        transactionDateDialog.getWindow().setGravity(Gravity.BOTTOM);
        transactionDateDialog.show();

        ImageButton imgBtnCancel = transactionDateDialog.findViewById(R.id.transactionDateWindow_imgBtn_cancel);
        imgBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transactionDateDialog.dismiss();
            }
        });
    }



    //거래시간 커스텀 다이얼로그 기능 메소드
    private void showTransactionTimeDialog() {
        tvTransactionTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY) + 9;                //한국시간 : +9
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ScheduleReturnActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String state = "AM";
                        // 선택한 시간이 12를 넘을경우 "PM"으로 변경 및 -12시간하여 출력 (ex : PM 6시 30분)
                        if (selectedHour > 12) {
                            selectedHour -= 12;
                            state = "PM";
                        }
                        // EditText에 출력할 형식 지정
                        tvTransactionTime.setText(state + " " + selectedHour + "시 " + selectedMinute + "분");
                        tvTransTime.setText(state + " " + selectedHour + "시 " + selectedMinute + "분");
                        scheduleReturnDB.setsTransactionTime(tvTransactionTime.getText().toString()); //거래시간
                    }
                }, hour, minute, false); // true의 경우 24시간 형식의 TimePicker 출현
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        transactionTimeDialog.getWindow().setGravity(Gravity.BOTTOM);
        transactionTimeDialog.show();

        ImageButton imgBtnCancel = transactionTimeDialog.findViewById(R.id.transactionTimeWindow_imgBtn_cancel);
        imgBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transactionTimeDialog.dismiss();
            }
        });
    }

    //거래장소 커스텀 다이얼로그 기능 메소드
    private void showTransactionLocationDialog() {
        tvGetTransactionLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScheduleReturnActivity.this, LocationSearchActivity.class);
                getSearchResult.launch(intent);
            }
        });

        transactionLocationDialog.getWindow().setGravity(Gravity.BOTTOM);
        transactionLocationDialog.show();

        ImageButton imgBtnCancel = transactionLocationDialog.findViewById(R.id.transactionLocationWindow_imgBtn_cancel);
        imgBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transactionLocationDialog.dismiss();
            }
        });
    }

    //도로명주소 WebView API에서 검색 데이터 읽어오는 기능 메소드
    private final ActivityResultLauncher<Intent> getSearchResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                //LocationSearchActivity로부터 결과 값이 이곳으로 전달
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        String data = result.getData().getStringExtra("data");
                        tvGetTransactionLocation.setText(data);
                        tvTransPlace.setText(data);
                        scheduleReturnDB.setsTransactionLocation(data);
                    }
                }
            }
    );

    private void systemScheduleMsg(ScheduleReturnDB scheduleReturnDB, String tag){
        String result = null;

        Calendar calendar = Calendar.getInstance();
        String timestamp = calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
        if(tag.equals("1")){
            result = String.format("반납일정 안내입니다. \n 반납일정 요청자: %s\n \n 반납일: %s\n 반납시간: %s\n 반납장소: %s",
                    scheduleReturnDB.getsUserEmail(),
                    scheduleReturnDB.getsTransactionDate(),
                    scheduleReturnDB.getsTransactionTime(),
                    scheduleReturnDB.getsTransactionLocation());
        } else if(tag.equals("2")){
            result = String.format("반납일정이 수정되었습니다.\n 반납일정 요청자: %s\n \n 반납일: %s\n 반납시간: %s\n 반납장소: %s",
                    scheduleReturnDB.getsUserEmail(),
                    scheduleReturnDB.getsTransactionDate(),
                    scheduleReturnDB.getsTransactionTime(),
                    scheduleReturnDB.getsTransactionLocation());
        } else{
            return;
        }
        Log.e("scheduleReturnDB2", scheduleReturnDB.getsOtherEmail());
        chatDTO = new ChatDTO(scheduleReturnDB.getsChatNum(), "거래도우미", scheduleReturnDB.getsUserEmail(), scheduleReturnDB.getsOtherEmail(),result, timestamp);
        scheduleReturnRef.child(scheduleReturnDB.getsChatNum()).push().setValue(chatDTO);
    }
}