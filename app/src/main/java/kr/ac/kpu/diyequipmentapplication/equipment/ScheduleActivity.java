package kr.ac.kpu.diyequipmentapplication.equipment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import kr.ac.kpu.diyequipmentapplication.R;
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
    private Boolean controlFlag;            //대여기간 시작일(true), 종료일(false) 제어변수
    private Boolean controlDateFlag;        //대여기간(true), 거래날짜(false) 제어변수
    private Long startDate;                 //대여기간 시작일
    private Long finishDate;                //대여기간 종료일
    private Long totLendingPeriod;          //대여기간 총 대여일수
    private String getUserEmail;            //사용자 이메일 계정

    //커스텀 다이얼로그 참조위젯
    private Dialog rentalDialog;                //대여비 커스텀 다이얼로그
    private Dialog transactionDateDialog;       //거래날짜 커스텀 다이얼로그
    private Dialog transactionTimeDialog;       //거래시간 커스텀 다이얼로그
    private Dialog transactionLocationDialog;   //거래장소 커스텀 다이얼로그

    //Firebase 참조
    private FirebaseAuth scheduleFirebaseAuth;
    private FirebaseFirestore scheduleFirebaseFirestore;
    private ProgressDialog scheduleProgressDialog;

    private String tempStartDate;          //대여기간 시작일
    private String tempExpirationDate;        //대여기간 종료일
    private String tempTotalLendingPeriod;    //대여기간 총 대여일
    private String tempDailyRental;           //일일대여비
    private String tempTotalRental;           //총 대여비
    private String tempTransactionDate;       //거래일
    private String tempTransactionTime;       //거래시간
    private String tempTransactionLocation;   //거래장소


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

    TextView tvTransactionLocation;  //거래장소

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        imgBtnBack = (ImageButton) findViewById(R.id.schedule_btn_back);
        btnLendingPeriod = (Button) findViewById(R.id.schedule_btn_lendingPeriod);
        btnRental = (Button) findViewById(R.id.schedule_btn_rental);
        btnTransactionDate = (Button) findViewById(R.id.schedule_btn_transactionDate);
        btnTransactionTime = (Button) findViewById(R.id.schedule_btn_transactionTime);
        btnTransactionLocation = (Button) findViewById(R.id.schedule_btn_transactionLocation);
        btnSetup = (Button) findViewById(R.id.schedule_btn_setup);
        btnUpdate = (Button) findViewById(R.id.schedule_btn_update);

        //firebase 참조
        scheduleFirebaseAuth = FirebaseAuth.getInstance();
        scheduleFirebaseFirestore = FirebaseFirestore.getInstance();
        scheduleProgressDialog = new ProgressDialog(this);

        //대여기간 커스텀 다이얼로그 참조
        lendingPeriodDialog = new Dialog(ScheduleActivity.this);
        lendingPeriodDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        lendingPeriodDialog.setContentView(R.layout.schedule_lendingperiod_window_item);

        //대여비 커스텀 다이얼로그 참조
        rentalDialog = new Dialog(ScheduleActivity.this);
        rentalDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rentalDialog.setContentView(R.layout.schedule_rental_window_item);

        //거래날짜 커스텀 다이얼로그 참조
        transactionDateDialog = new Dialog(ScheduleActivity.this);
        transactionDateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        transactionDateDialog.setContentView(R.layout.schedule_transactiondate_window_item);

        //거래시간 커스텀 다이얼로그 참조
        transactionTimeDialog = new Dialog(ScheduleActivity.this);
        transactionTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        transactionTimeDialog.setContentView(R.layout.schedule_transactiontime_window_item);

        //거래장소 커스텀 다이얼로그 참조
        transactionLocationDialog = new Dialog(ScheduleActivity.this);
        transactionLocationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        transactionLocationDialog.setContentView(R.layout.schedule_transactionlocation_window_item);
        tvTransactionLocation= transactionLocationDialog.findViewById(R.id.transactionLocationWindow_tv_transactionLocation);

        //DIY_Signup DB에서 사용자 계정에 맞는 이메일 가져오는 기능 구현.
        //사용자 이메일 정보와 일치하는 데이터를 DIY_Signup DB에서 찾아서 getUserEmail 참조 변수에 이메일 값 참조.
        scheduleFirebaseFirestore.collection("DIY_Schedule")
                .whereEqualTo("sUserEmail", scheduleFirebaseAuth.getCurrentUser().getEmail().toString().trim())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                getUserEmail = queryDocumentSnapshot.get("sUserEmail").toString().trim();
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
                        final String userEmail = scheduleFirebaseAuth.getCurrentUser().getEmail().toString();   //사용자 이메일
                        final String startDate = tempStartDate;                         //대여기간 시작일
                        final String expirationDate = tempExpirationDate;               //대여기간 종료일
                        final String totalLendingPeriod = tempTotalLendingPeriod;       //대여기간 총 대여일
                        final String dailyRental = tempDailyRental;                     //일일대여비
                        final String totalRental = tempTotalRental;                     //총 대여비
                        final String transactionDate = tempTransactionDate;             //거래일
                        final String transactionTime = tempTransactionTime;             //거래시간
                        final String transactionLocation = tempTransactionLocation;     //거래장소

                        if (startDate != null && expirationDate != null && totalLendingPeriod != null && dailyRental != null &&
                        totalRental != null && transactionDate != null && transactionTime != null && transactionLocation != null) {
                            scheduleProgressDialog.setTitle("DIY Schedule Uploading...");
                            scheduleProgressDialog.show();

                            ScheduleDB scheduleDB = new ScheduleDB(userEmail, startDate, expirationDate, totalLendingPeriod, dailyRental,
                                    totalRental, transactionDate, transactionTime, transactionLocation);

                            //firestore DB에 저장
                            scheduleFirebaseFirestore.collection("DIY_Schedule").document().set(scheduleDB);
                        }
                        scheduleProgressDialog.dismiss();
                        Toast.makeText(ScheduleActivity.this, "거래정보 설정 완료되었습니다!", Toast.LENGTH_SHORT).show();
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

            }
        });
    }

    //대여기간 커스텀 다이얼로그 기능 메소드
    public void showLendingPeriodDialog() {
        TextView tvStartDate= lendingPeriodDialog.findViewById(R.id.lendingPeriodWindow_tv_startDate);
        TextView tvExpirationDate = lendingPeriodDialog.findViewById(R.id.lendingPeriodWindow_tv_expirationDate);
        TextView tvTotalLendingPeriod = lendingPeriodDialog.findViewById(R.id.lendingPeriodWindow_tv_totalLendingPeriod);

        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlDateFlag = true;
                controlFlag = true;
                new DatePickerDialog(ScheduleActivity.this, scheduleDatePicker, scheduleCalendar.get(Calendar.YEAR),
                        scheduleCalendar.get(Calendar.MONTH), scheduleCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

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
        String countNum;
        String myFormat = "yyyy/MM/dd";    // 출력형식   2021/07/26
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        TextView tvTotalLendingPeriod = lendingPeriodDialog.findViewById(R.id.lendingPeriodWindow_tv_totalLendingPeriod);

        if (controlFlag == true) {
            TextView tvStartDate= lendingPeriodDialog.findViewById(R.id.lendingPeriodWindow_tv_startDate);
            tempStartDate = sdf.format(scheduleCalendar.getTime()); //대여기간 시작일
            tvStartDate.setText(sdf.format(scheduleCalendar.getTime()));
            startDate = scheduleCalendar.getTimeInMillis() / (24*60*60*1000);
            tvTotalLendingPeriod.setText("반납일 선택해주세요!");
        } else {
            TextView tvExpirationDate= lendingPeriodDialog.findViewById(R.id.lendingPeriodWindow_tv_expirationDate);
            tempExpirationDate = sdf.format(scheduleCalendar.getTime());    //대여기간 종료일
            tvExpirationDate.setText(sdf.format(scheduleCalendar.getTime()));
            finishDate = scheduleCalendar.getTimeInMillis() / (24*60*60*1000);

            totLendingPeriod = finishDate - startDate;
            countNum = Long.toString(totLendingPeriod);
            tempTotalLendingPeriod = countNum;      //대여기간 총 대여일수
            tvTotalLendingPeriod.setText(countNum+"일");
        }
    }

    //거래날짜 계산 메소드
    private void transactionDateLabel() {
        String myFormat = "yyyy/MM/dd";    // 출력형식   2021/07/26
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

        TextView tvTransactionDate= transactionDateDialog.findViewById(R.id.transactionDateWindow_tv_transactionDate);  //거래날짜
        tempTransactionDate = sdf.format(scheduleCalendar.getTime());       //거래일
        tvTransactionDate.setText(sdf.format(scheduleCalendar.getTime()));
    }

    //대여비 커스텀 다이얼로그 기능 메소드
    public void showRentalDialog() {
        String countNum;
        int dailyRental = 5000;
        int totalRental;
        TextView tvTotalLendingPeriod= rentalDialog.findViewById(R.id.rentalWindow_tv_totalLendingPeriod);  //대여일수
        TextView tvDailyRental = rentalDialog.findViewById(R.id.rentalWindow_tv_dailyRental);               //일일대여비
        TextView tvTotalRental = rentalDialog.findViewById(R.id.rentalWindow_tv_totalRental);               //총 대여비

        countNum = Long.toString(totLendingPeriod);
        totalRental = dailyRental * Integer.parseInt(countNum);

        tempDailyRental = Integer.toString(dailyRental);    //일일대여비
        tempTotalRental = Integer.toString(totalRental);    //총 대여비

        tvTotalLendingPeriod.setText(countNum+"일");
        tvDailyRental.setText(dailyRental+"원");
        tvTotalRental.setText(totalRental+"원");

        rentalDialog.getWindow().setGravity(Gravity.BOTTOM);
        rentalDialog.show();

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
        TextView tvTransactionDate= transactionDateDialog.findViewById(R.id.transactionDateWindow_tv_transactionDate);  //거래날짜

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
        TextView tvTransactionTime= transactionTimeDialog.findViewById(R.id.transactionTimeWindow_tv_transactionTime);  //거래시간

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
                        tempTransactionTime = tvTransactionTime.getText().toString();   //거래시간
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
        tvTransactionLocation.setOnClickListener(new View.OnClickListener() {
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

    private final ActivityResultLauncher<Intent> getSearchResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                //LocationSearchActivity로부터 결과 값이 이곳으로 전달
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        String data = result.getData().getStringExtra("data");
                        tvTransactionLocation.setText(data);
                        tempTransactionLocation = data; //거래장소;
                    }
                }
            }
    );
}