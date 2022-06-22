package kr.ac.kpu.diyequipmentapplication.equipment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import kr.ac.kpu.diyequipmentapplication.R;
import kr.ac.kpu.diyequipmentapplication.chat.ChatDTO;
import kr.ac.kpu.diyequipmentapplication.menu.LocationSearchActivity;

public class ScheduleActivity extends AppCompatActivity {
    private ImageButton imgBtnBack;         //뒤로가기
    private Button btnLendingPeriod;        //대여기간
    private Button btnRental;               //대여비
    private Button btnTransactionDate;      //거래날짜
    private Button btnTransactionTime;      //거래시간
    private Button btnTransactionLocation;  //거래장소
    private Button btnSetup;                //거래정보 설정
    private Button btnUpdate;               //거래정보 수정
    private Button btnDelete;               //거래정보 삭제
    private Boolean controlFlag;            //대여기간 시작일(true), 종료일(false) 제어변수
    private Boolean controlDateFlag;        //대여기간(true), 거래날짜(false) 제어변수
    private Long startDate;                 //대여기간 시작일
    private Long finishDate;                //대여기간 종료일
    private Long totLendingPeriod;          //대여기간 총 대여일수
    private String getScheduleDBID;
    final String[] getRentalCost = new String[1];
    private String getChatNum;

    //커스텀 다이얼로그 참조위젯
    private Dialog rentalDialog;                //대여비 커스텀 다이얼로그
    private Dialog transactionDateDialog;       //거래날짜 커스텀 다이얼로그
    private Dialog transactionTimeDialog;       //거래시간 커스텀 다이얼로그
    private Dialog transactionLocationDialog;   //거래장소 커스텀 다이얼로그

    //Firebase 참조
    private FirebaseAuth scheduleFirebaseAuth;
    private FirebaseFirestore scheduleFirebaseFirestore;
    private FirebaseDatabase scheduleFirebaseDatabase;
    private DatabaseReference scheduleRef;
    private ProgressDialog scheduleProgressDialog;

    private ScheduleDB scheduleDB;
    private ChatDTO chatDTO;

    private TextView tvStartDate, tvExpirationDate, tvTotalLendingPeriod, tvtTotalLendingPeriod, tvDailyRental,
            tvTotalRental, tvTransactionDate, tvTransactionTime, tvGetTransactionLocation;

    private TextView tvTransPeriod, tvTransCost, tvTransDay, tvTransTime, tvTransPlace;

    //DatePickerDialog 사용 관련 참조위젯
    private Dialog lendingPeriodDialog;
    private Calendar scheduleCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener scheduleDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            scheduleCalendar.set(Calendar.YEAR, year);
            scheduleCalendar.set(Calendar.MONTH, month);
            scheduleCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (controlDateFlag == true)
                updateLabel(controlFlag);
            else
                transactionDateLabel();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        scheduleDB = new ScheduleDB();
        Intent getIntent = getIntent();
        getChatNum = getIntent.getStringExtra("CHAT_NUM");
        scheduleDB.setsChatNum(getChatNum);
        scheduleDB.setsCollectionId(getIntent.getStringExtra("ModelCollectionId"));
        //scheduleDB.setsChatNum(getIntent.getStringExtra("CHAT_NUM"));
        imgBtnBack = (ImageButton) findViewById(R.id.schedule_btn_back);
        btnLendingPeriod = (Button) findViewById(R.id.schedule_btn_lendingPeriod);
        btnRental = (Button) findViewById(R.id.schedule_btn_rental);
        btnTransactionDate = (Button) findViewById(R.id.schedule_btn_transactionDate);
        btnTransactionTime = (Button) findViewById(R.id.schedule_btn_transactionTime);
        btnTransactionLocation = (Button) findViewById(R.id.schedule_btn_transactionLocation);
        btnSetup = (Button) findViewById(R.id.schedule_btn_setup);
        btnUpdate = (Button) findViewById(R.id.schedule_btn_update);
        btnDelete = (Button) findViewById(R.id.schedule_btn_delete);

        // 정보 설정시 변경될 텍스트뷰
        tvTransPeriod = (TextView) findViewById(R.id.schedule_tv_lendingPeriod);
        tvTransCost = (TextView) findViewById(R.id.schedule_tv_rental);
        tvTransDay = (TextView) findViewById(R.id.schedule_tv_transactionDate);
        tvTransTime = (TextView) findViewById(R.id.schedule_tv_transactionTime);
        tvTransPlace = (TextView) findViewById(R.id.schedule_tv_transactionLocation);


        //firebase 참조
        scheduleFirebaseAuth = FirebaseAuth.getInstance();
        scheduleFirebaseFirestore = FirebaseFirestore.getInstance();
        scheduleProgressDialog = new ProgressDialog(this);

        //대여기간 커스텀 다이얼로그 참조
        lendingPeriodDialog = new Dialog(ScheduleActivity.this);
        lendingPeriodDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        lendingPeriodDialog.setContentView(R.layout.schedule_lendingperiod_window_item);
        tvStartDate= lendingPeriodDialog.findViewById(R.id.lendingPeriodWindow_tv_startDate);
        tvExpirationDate = lendingPeriodDialog.findViewById(R.id.lendingPeriodWindow_tv_expirationDate);
        tvTotalLendingPeriod = lendingPeriodDialog.findViewById(R.id.lendingPeriodWindow_tv_totalLendingPeriod);

        //대여비 커스텀 다이얼로그 참조
        rentalDialog = new Dialog(ScheduleActivity.this);
        rentalDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rentalDialog.setContentView(R.layout.schedule_rental_window_item);
        tvtTotalLendingPeriod= rentalDialog.findViewById(R.id.rentalWindow_tv_totalLendingPeriod);  //대여일수
        tvDailyRental = rentalDialog.findViewById(R.id.rentalWindow_tv_dailyRental);               //일일대여비
        tvTotalRental = rentalDialog.findViewById(R.id.rentalWindow_tv_totalRental);               //총 대여비

        //거래날짜 커스텀 다이얼로그 참조
        transactionDateDialog = new Dialog(ScheduleActivity.this);
        transactionDateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        transactionDateDialog.setContentView(R.layout.schedule_transactiondate_window_item);
        tvTransactionDate= transactionDateDialog.findViewById(R.id.transactionDateWindow_tv_transactionDate);  //거래날짜

        //거래시간 커스텀 다이얼로그 참조
        transactionTimeDialog = new Dialog(ScheduleActivity.this);
        transactionTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        transactionTimeDialog.setContentView(R.layout.schedule_transactiontime_window_item);
        tvTransactionTime= transactionTimeDialog.findViewById(R.id.transactionTimeWindow_tv_transactionTime);  //거래시간

        //거래장소 커스텀 다이얼로그 참조
        transactionLocationDialog = new Dialog(ScheduleActivity.this);
        transactionLocationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        transactionLocationDialog.setContentView(R.layout.schedule_transactionlocation_window_item);
        tvGetTransactionLocation= transactionLocationDialog.findViewById(R.id.transactionLocationWindow_tv_transactionLocation);

        //DIY_Schedule DB에서 sUserEmail에 해당하는 계정이 있는지 확인하는 메소드
        scheduleFirebaseFirestore.collection("DIY_Schedule")
                .whereEqualTo("sChatNum", getChatNum)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                getScheduleDBID = queryDocumentSnapshot.getId();
                                scheduleDB.setsStartDate(queryDocumentSnapshot.get("sStartDate").toString().trim());
                                scheduleDB.setsExpirationDate(queryDocumentSnapshot.get("sExpirationDate").toString().trim());
                                scheduleDB.setsTotalLendingPeriod(queryDocumentSnapshot.get("sTotalLendingPeriod").toString().trim());
                                scheduleDB.setStartDate(queryDocumentSnapshot.get("startDate").toString().trim());
                                scheduleDB.setFinishDate(queryDocumentSnapshot.get("finishDate").toString().trim());
                                scheduleDB.setsDailyRental(queryDocumentSnapshot.get("sDailyRental").toString().trim());
                                scheduleDB.setsTotalRental(queryDocumentSnapshot.get("sTotalRental").toString().trim());
                                scheduleDB.setsTransactionDate(queryDocumentSnapshot.get("sTransactionDate").toString().trim());
                                scheduleDB.setsTransactionTime(queryDocumentSnapshot.get("sTransactionTime").toString().trim());
                                scheduleDB.setsTransactionLocation(queryDocumentSnapshot.get("sTransactionLocation").toString().trim());
                                scheduleDB.setsCollectionId(queryDocumentSnapshot.get("sCollectionId").toString().trim());
                                scheduleDB.setsChatNum(queryDocumentSnapshot.get("sChatNum").toString().trim());

                                tvStartDate.setText(scheduleDB.getsStartDate());
                                tvExpirationDate.setText(scheduleDB.getsExpirationDate());
                                tvTotalLendingPeriod.setText(scheduleDB.getsTotalLendingPeriod());
                                tvtTotalLendingPeriod.setText(scheduleDB.getsTotalLendingPeriod());
                                totLendingPeriod = Long.parseLong(tvtTotalLendingPeriod.getText().toString().trim());
                                tvDailyRental.setText(scheduleDB.getsDailyRental());
                                tvTotalRental.setText(scheduleDB.getsTotalRental());
                                tvTransactionDate.setText(scheduleDB.getsTransactionDate());
                                tvTransactionTime.setText(scheduleDB.getsTransactionTime());
                                tvGetTransactionLocation.setText(scheduleDB.getsTransactionLocation());

                                tvTransPeriod.setText(scheduleDB.getsTotalLendingPeriod());
                                tvTransCost.setText(scheduleDB.getsTotalRental());
                                tvTransDay.setText(scheduleDB.getsTransactionDate());
                                tvTransTime.setText(scheduleDB.getsTransactionTime());
                                tvTransPlace.setText(scheduleDB.getsTransactionLocation());

                            }
                        }
                    }
                });



        //뒤로가기 버튼 클릭 이벤트
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish();}
        });

        //대여기간 버튼 클릭 이벤트
        btnLendingPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLendingPeriodDialog();
            }
        });

        //대여비 버튼 클릭 이벤트
        btnRental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRentalDialog();
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
                AlertDialog.Builder dlg = new AlertDialog.Builder(ScheduleActivity.this);
                dlg.setTitle("DIY_거래정보");
                dlg.setMessage("거래정보 설정 하시겠습니까?");
                dlg.setIcon(R.mipmap.ic_launcher);

                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        scheduleDB.setsUserEmail(scheduleFirebaseAuth.getCurrentUser().getEmail().toString());

                        if (scheduleDB.getsStartDate() != null && scheduleDB.getsExpirationDate() != null && scheduleDB.getsTotalLendingPeriod() != null
                                && scheduleDB.getStartDate() != null && scheduleDB.getFinishDate() != null
                                && scheduleDB.getsDailyRental() != null && scheduleDB.getsTotalRental() != null && scheduleDB.getsTransactionDate() != null
                                && scheduleDB.getsTransactionTime() != null && scheduleDB.getsTransactionLocation() != null
                                && scheduleDB.getsCollectionId() != null && scheduleDB.getsChatNum() != null) {
                            scheduleProgressDialog.setTitle("DIY Schedule Uploading...");
                            scheduleProgressDialog.show();

                            //firestore DB에 저장
                            scheduleFirebaseFirestore.collection("DIY_Schedule").document().set(scheduleDB);
                            Toast.makeText(ScheduleActivity.this, "거래정보 설정 완료되었습니다!", Toast.LENGTH_SHORT).show();

                            // 시스템 채팅 등록
                            systemScheduleMsg(scheduleDB,"1");
                        } else {
                            Toast.makeText(ScheduleActivity.this, "거래정보 설정을 할 수 없습니다!", Toast.LENGTH_SHORT).show();
                            Toast.makeText(ScheduleActivity.this, "거래정보 설정 다시 입력하세요!", Toast.LENGTH_SHORT).show();
                        }
                        scheduleProgressDialog.dismiss();
                        finish();
                    }
                });

                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ScheduleActivity.this, "거래정보 설정 취소되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });

        //거래정보 수정 버튼 클릭 이벤트
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(ScheduleActivity.this);
                dlg.setTitle("DIY_거래정보");
                dlg.setMessage("거래정보 수정 하시겠습니까?");
                dlg.setIcon(R.mipmap.ic_launcher);

                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String userEmail = scheduleFirebaseAuth.getCurrentUser().getEmail().toString();   //사용자 이메일
                        Map<String, Object> scheduleUpdate = new HashMap<String, Object>();
                        scheduleUpdate.put("sStartDate", scheduleDB.getsStartDate());
                        scheduleUpdate.put("sExpirationDate", scheduleDB.getsExpirationDate());
                        scheduleUpdate.put("sTotalLendingPeriod", scheduleDB.getsTotalLendingPeriod());
                        scheduleUpdate.put("startDate", scheduleDB.getStartDate());
                        scheduleUpdate.put("finishDate", scheduleDB.getFinishDate());

                        scheduleUpdate.put("sDailyRental", scheduleDB.getsDailyRental());
                        scheduleUpdate.put("sTotalRental", scheduleDB.getsTotalRental());

                        scheduleUpdate.put("sTransactionDate", scheduleDB.getsTransactionDate());
                        scheduleUpdate.put("sTransactionTime", scheduleDB.getsTransactionTime());
                        scheduleUpdate.put("sTransactionLocation", scheduleDB.getsTransactionLocation());

                        if (scheduleDB.getsStartDate() != null && scheduleDB.getsExpirationDate() != null && scheduleDB.getsTotalLendingPeriod() != null
                                && scheduleDB.getStartDate() != null && scheduleDB.getFinishDate() != null
                                && scheduleDB.getsDailyRental() != null && scheduleDB.getsTotalRental() != null && scheduleDB.getsTransactionDate() != null
                                && scheduleDB.getsTransactionTime() != null && scheduleDB.getsTransactionLocation() != null) {
                            scheduleFirebaseFirestore.collection("DIY_Schedule")
                                    .document(getScheduleDBID)
                                    .update(scheduleUpdate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("거래정보 업데이트 성공!", "DocumentSnapshot successfully update!");
                                            systemScheduleMsg(scheduleDB,"2");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("거래정보 업데이트 실패", "Error updating document", e);
                                        }
                                    });
                            Toast.makeText(ScheduleActivity.this, "거래정보 수정 완료되었습니다!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ScheduleActivity.this, "거래정보 수정 취소되었습니다!", Toast.LENGTH_SHORT).show();

                        }
                        scheduleProgressDialog.dismiss();
                        finish();
                    }
                });

                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ScheduleActivity.this, "거래정보 수정 취소되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });

        //거래정보 삭제 버튼 클릭 이벤트
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(ScheduleActivity.this);
                dlg.setTitle("DIY_거래정보");
                dlg.setMessage("거래정보 삭제 하시겠습니까?");
                dlg.setIcon(R.mipmap.ic_launcher);

                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        scheduleFirebaseFirestore.collection("DIY_Schedule")
                                .document(getScheduleDBID)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("거래정보 삭제 성공!", "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("거래정보 삭제 실패", "Error deleting document", e);
                                    }
                                });
                        Toast.makeText(ScheduleActivity.this, "거래정보 삭제되었습니다!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ScheduleActivity.this, "거래정보 삭제 취소되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });
    }

    //대여기간 커스텀 다이얼로그 기능 메소드
    public void showLendingPeriodDialog() {
        //대여기간 대여일 클릭 이벤트
        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlDateFlag = true;
                controlFlag = true;
                new DatePickerDialog(ScheduleActivity.this, scheduleDatePicker, scheduleCalendar.get(Calendar.YEAR),
                        scheduleCalendar.get(Calendar.MONTH), scheduleCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //대여기간 반납일 클릭 이벤트
        tvExpirationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlDateFlag = true;
                controlFlag = false;
                new DatePickerDialog(ScheduleActivity.this, scheduleDatePicker, scheduleCalendar.get(Calendar.YEAR),
                        scheduleCalendar.get(Calendar.MONTH), scheduleCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        lendingPeriodDialog.getWindow().setGravity(Gravity.BOTTOM);
        lendingPeriodDialog.show();

        ImageButton imgBtnCancel = lendingPeriodDialog.findViewById(R.id.lendingPeriodWindow_imgBtn_cancel);
        imgBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lendingPeriodDialog.dismiss();
            }
        });
    }

    //대여일, 반납일, 대여일수 계산 메소드
    private void updateLabel(Boolean controlFlag) {
        String myFormat = "yyyy/MM/dd";    // 출력형식   2021/07/26
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

        if (controlFlag == true) {  //대여기간 시작일인 경우
            scheduleDB.setsStartDate(sdf.format(scheduleCalendar.getTime()));   //대여기간 시작일
            tvStartDate.setText(scheduleDB.getsStartDate());
            startDate = scheduleCalendar.getTimeInMillis() / (24*60*60*1000);
            scheduleDB.setStartDate(Long.toString(startDate));


            if (scheduleDB.getsExpirationDate() != null) {
                totLendingPeriod = Long.valueOf(scheduleDB.getFinishDate()) - Long.valueOf(scheduleDB.getStartDate());
                scheduleDB.setsTotalLendingPeriod(Long.toString(totLendingPeriod)); //대여기간 총 대여일수
                tvTotalLendingPeriod.setText(scheduleDB.getsTotalLendingPeriod()+"일");
                tvTransPeriod.setText(scheduleDB.getsTotalLendingPeriod()+"일");
            }
        } else {    //대여기간 반납일인 경우
            scheduleDB.setsExpirationDate(sdf.format(scheduleCalendar.getTime()));  //대여기간 종료일
            tvExpirationDate.setText(scheduleDB.getsExpirationDate());
            finishDate = scheduleCalendar.getTimeInMillis() / (24*60*60*1000);
            scheduleDB.setFinishDate(Long.toString(finishDate));

            if (scheduleDB.getsStartDate() != null) {
                totLendingPeriod = Long.valueOf(scheduleDB.getFinishDate()) - Long.valueOf(scheduleDB.getStartDate());
                scheduleDB.setsTotalLendingPeriod(Long.toString(totLendingPeriod)); //대여기간 총 대여일수
                tvTotalLendingPeriod.setText(scheduleDB.getsTotalLendingPeriod()+"일");
                tvTransPeriod.setText(scheduleDB.getsTotalLendingPeriod()+"일");
            } else {
                Toast.makeText(ScheduleActivity.this, "대여기간 시작일 먼저 선택해 주세요!", Toast.LENGTH_SHORT).show();
                tvExpirationDate.setText("");
            }
        }
    }

    //거래날짜 계산 메소드
    private void transactionDateLabel() {
        String myFormat = "yyyy/MM/dd";    // 출력형식   2021/07/26
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

        scheduleDB.setsTransactionDate(sdf.format(scheduleCalendar.getTime())); //거래일
        tvTransactionDate.setText(scheduleDB.getsTransactionDate());
        tvTransDay.setText(scheduleDB.getsTransactionDate());
    }

    //대여비 커스텀 다이얼로그 기능 메소드
    public void showRentalDialog() {
        //final String[] getRentalCost = new String[1];
        final int[] dailyRental = new int[1];
        final int[] totalRental = new int[1];

        if (scheduleDB.getsCollectionId() != null && scheduleDB.getsTotalLendingPeriod() != null) {
            scheduleFirebaseFirestore.collection("DIY_Equipment_Rental")
                    .document(scheduleDB.getsCollectionId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            getRentalCost[0] = task.getResult().getString("rentalCost");

                                if (getRentalCost[0].equals("무료")) {   //일일대여비가 무료인 경우
                                    tvDailyRental.setText(getRentalCost[0]);
                                    tvTotalRental.setText(getRentalCost[0]);
                                    scheduleDB.setsDailyRental(getRentalCost[0]);  //일일 대여비
                                    scheduleDB.setsTotalRental(getRentalCost[0]);  //총 대여비
                                    tvTransCost.setText(getRentalCost[0]);
                                } else {    //일일대여비가 유료인 경우
                                    dailyRental[0] = Integer.parseInt(getRentalCost[0]);
                                    totalRental[0] = dailyRental[0] * Integer.parseInt(scheduleDB.getsTotalLendingPeriod());
                                    scheduleDB.setsDailyRental(Integer.toString(dailyRental[0]));  //일일 대여비
                                    scheduleDB.setsTotalRental(Integer.toString(totalRental[0]));  //총 대여비
                                    tvDailyRental.setText(dailyRental[0] +"원");
                                    tvTotalRental.setText(totalRental[0] +"원");
                                    tvTransCost.setText(totalRental[0] +"원");
                                }
                                tvtTotalLendingPeriod.setText(scheduleDB.getsTotalLendingPeriod()+"일");
                                rentalDialog.getWindow().setGravity(Gravity.BOTTOM);
                                rentalDialog.show();
                            }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("getsCollectionId", "찾고자 하는 ID가 없습니다!");
                }
            });
        } else if (scheduleDB.getsCollectionId() == null) {
            Toast.makeText(ScheduleActivity.this, "장비 상세페이지에서 거래자 채팅 먼저 설정해주세요!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ScheduleActivity.this, "대여기간 먼저 설정 해주세요!", Toast.LENGTH_SHORT).show();
        }
/*
        if (scheduleDB.getsCollectionId() != null) {
            scheduleFirebaseFirestore.collection("DIY_Equipment_Rental")
                    .document(scheduleDB.getsCollectionId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            getRentalCost[0] = task.getResult().getString("rentalCost");

                            if (scheduleDB.getsTotalLendingPeriod() != null) {
                                if (getRentalCost[0].equals("무료")) {   //일일대여비가 무료인 경우
                                    tvDailyRental.setText(getRentalCost[0]);
                                    tvTotalRental.setText(getRentalCost[0]);
                                    scheduleDB.setsDailyRental(getRentalCost[0]);  //일일 대여비
                                    scheduleDB.setsTotalRental(getRentalCost[0]);  //총 대여비
                                } else {    //일일대여비가 유료인 경우
                                    dailyRental[0] = Integer.parseInt(getRentalCost[0]);
                                    totalRental[0] = dailyRental[0] * Integer.parseInt(scheduleDB.getsTotalLendingPeriod());
                                    scheduleDB.setsDailyRental(Integer.toString(dailyRental[0]));  //일일 대여비
                                    scheduleDB.setsTotalRental(Integer.toString(totalRental[0]));  //총 대여비
                                    tvDailyRental.setText(dailyRental[0] +"원");
                                    tvTotalRental.setText(totalRental[0] +"원");
                                }
                                tvtTotalLendingPeriod.setText(scheduleDB.getsTotalLendingPeriod()+"일");
                                rentalDialog.getWindow().setGravity(Gravity.BOTTOM);
                                rentalDialog.show();
                            } else {
                                Toast.makeText(ScheduleActivity.this, "대여기간 먼저 설정 해주세요!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("getsCollectionId", "찾고자 하는 ID가 없습니다!");
                }
            });

        } else {
            Toast.makeText(ScheduleActivity.this, "장비 상세페이지에서 거래자 채팅 먼저 설정해주세요!", Toast.LENGTH_SHORT).show();
        }

 */

        ImageButton imgBtnCancel = rentalDialog.findViewById(R.id.rentalWindow_imgBtn_cancel);  //취소버튼
        imgBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rentalDialog.dismiss();
            }
        });
    }

    //거래날짜 커스텀 다이얼로그 기능 메소드
    private void showTransactionDateDialog() {
        tvTransactionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlDateFlag = false;
                new DatePickerDialog(ScheduleActivity.this, scheduleDatePicker, scheduleCalendar.get(Calendar.YEAR),
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
                mTimePicker = new TimePickerDialog(ScheduleActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                        scheduleDB.setsTransactionTime(tvTransactionTime.getText().toString()); //거래시간
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
                Intent intent = new Intent(ScheduleActivity.this, LocationSearchActivity.class);
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
                        scheduleDB.setsTransactionLocation(data);
                    }
                }
            }
    );

    private void systemScheduleMsg(ScheduleDB scheduleDB, String tag){
        scheduleFirebaseDatabase = FirebaseDatabase.getInstance();
        scheduleRef = scheduleFirebaseDatabase.getReference().child("DIY_Chat");
        String result = null;

        Calendar calendar = Calendar.getInstance();
        String timestamp = calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
        if(tag.equals("1")){
            result = String.format("거래일정 안내입니다.\n 총 대여일: %s\n 총 비용: %s\n 거래일: %s\n 거래시간: %s\n 거래장소: %s",
                    scheduleDB.getsTotalLendingPeriod(),
                    scheduleDB.getsTotalRental(),
                    scheduleDB.getsTransactionDate(),
                    scheduleDB.getsTransactionTime(),
                    scheduleDB.getsTransactionLocation());
        } else if(tag.equals("2")){
            result = String.format("거래일정이 수정되었습니다.\n 총 대여일: %s\n 총 비용: %s\n 거래일: %s\n 거래시간: %s\n 거래장소: %s",
                    scheduleDB.getsTotalLendingPeriod(),
                    scheduleDB.getsTotalRental(),
                    scheduleDB.getsTransactionDate(),
                    scheduleDB.getsTransactionTime(),
                    scheduleDB.getsTransactionLocation());
        } else{
            return;
        }

        chatDTO = new ChatDTO(scheduleDB.getsChatNum(), "거래도우미", "-", result, timestamp);
        scheduleRef.child(scheduleDB.getsChatNum()).push().setValue(chatDTO);
//        chatDTO = new ChatDTO(CHAT_NUM, CHAT_USER_NICKNAME, CHAT_USER_EMAIL ,CHAT_USER_TEXT,timestamp);
//        chatRef.child(CHAT_NUM).push().setValue(chatDTO);
    }
}