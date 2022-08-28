package kr.ac.kpu.diyequipmentapplication.chat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import kr.ac.kpu.diyequipmentapplication.R;
import kr.ac.kpu.diyequipmentapplication.RentalHistoryRecyclerviewActivity;
import kr.ac.kpu.diyequipmentapplication.equipment.ScheduleActivity;

public class ChatActivity extends AppCompatActivity {

    private static final String FCM_MSG_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAARN3XDr8:APA91bFLL-YooOesFN3cFqv4I0beW03y8gv8g_okhVUbhBrEx492aGvAwsPOBEng-d2NznsTX71FcY8GErT3NVG9wQ_NJIV_VCCKy8ET8haY3G6AJhUXz5q9rMKKRPq7mHZeIEVy3sJT";

    private String CHAT_NUM = null;
    private String CHAT_USER_EMAIL = null;
    private String CHAT_OTHER_EMAIL = null;
    private String CHAT_USER_NICKNAME = null;
    private String CHAT_USER_TEXT = null;

    private ArrayList<ChatDTO> chatDTOS;
    private ChatDTO chatDTO;
    private ChatAdapter chatAdapter;

    private ListView lvChatList;
    private EditText etChatMsg;
    private Button btnChatSend;
    private TextView tvChatNum;

    private FirebaseAuth chatAuth = null;
    private FirebaseDatabase chatFDB = null;
    private DatabaseReference chatRef = null;
    private DatabaseReference fcmRef = null;
    private FirebaseFirestore userFS = null;

    private ImageButton imgBtn_back = null;

    private Button btnTransactionSchedule;      //거래일정 버튼

    private String getModelCollectionId;    //해당 장비 컬렉션 Id
    private FirebaseFirestore chattingFirebaseFirestore;
    private Button btnTransaction;      //거래 버튼

    //거래 기능 관련 위젯 참조 변수
    private FirebaseAuth transactionFirebaseAuth;
    private FirebaseFirestore transactionFirebaseFirestore;
    private ProgressDialog transactionProgressDialog;
    private Dialog transactionDialog;                //거래 상세페이지 커스텀 다이얼로그
    private ImageView imgViewT;
    private TextView tvTcategory, tvTmodelName, tvTuserName, tvTrentalType, tvTrentalDate, tvTrentalCost, tvTstartDate,
            tvTexpirationDate, tvTtotalLendingPeriod, tvTtotalRental, tvTtransactionDate, tvTtransactionTime, tvTtransactionLocation;

    private Button btnTok, btnTcancel;
    private String getTransactionDBId;
    private TransactionDTO transactionDTO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        // Firebase Auth, DB, Ref 참조
        chatAuth = FirebaseAuth.getInstance();
        chatFDB = FirebaseDatabase.getInstance();
        userFS = FirebaseFirestore.getInstance();
        chatRef = chatFDB.getReference().child("DIY_Chat");
        fcmRef = chatFDB.getReference().child("DIY_FcmUserData");

        // 위젯, 어댑터 참조
        imgBtn_back = (ImageButton) findViewById(R.id.chat_btn_back);
        imgBtn_home = (ImageButton) findViewById(R.id.chatting_btn_home);
        lvChatList = (ListView) findViewById(R.id.chat_lv_msg);
        etChatMsg = (EditText) findViewById(R.id.chat_et_msg_box);
        btnChatSend = (Button) findViewById(R.id.chat_btn_msg_send);
        tvChatNum = (TextView) findViewById(R.id.chat_tv_room_num);
        chatDTOS = new ArrayList<ChatDTO>();
        chatAdapter = new ChatAdapter(chatDTOS, getLayoutInflater());
        lvChatList.setAdapter(chatAdapter);

        // 사용자 이메일 및 닉네임 가져오기
        CHAT_USER_EMAIL = chatAuth.getCurrentUser().getEmail().toString();

        //뒤로가기 버튼 클릭시 채팅 목록 페이지에서 전 페이지로 이동
        imgBtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        userFS.collection("DIY_Signup")
                .whereEqualTo("userEmail", chatAuth.getCurrentUser().getEmail().toString().trim())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                Log.d("main SignupDB", queryDocumentSnapshot.get("userNickname").toString().trim());
                                CHAT_USER_NICKNAME = queryDocumentSnapshot.get("userNickname").toString();
                            }
                        }
                    }
                });

        // 채팅 시작방에서 받아온 채팅방 번호, 상대유저 이름 저장
        Intent intent = getIntent();
        CHAT_NUM = intent.getStringExtra("chatNum");
        if(intent.getStringExtra("ModelOwnerEmail") != null){
            CHAT_OTHER_EMAIL = intent.getStringExtra("ModelOwnerEmail");
        }
        if(intent.getStringExtra("chatOtherEmail")!= null){
            CHAT_OTHER_EMAIL = intent.getStringExtra("chatOtherEmail");
        }


        if(CHAT_NUM == null){
            Random rand = new Random();
            Integer iValue = null;

            iValue = rand.nextInt(10000);  // 0 <= iValue < 10000
            CHAT_NUM = iValue.toString();
        }

        // 채팅방번호 입장
        tvChatNum.setText("ROOM" + "-" + CHAT_NUM);
        Log.e("LOG", "chatnum:"+CHAT_NUM);
        chatWithUser(CHAT_NUM);

        btnChatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이름 비어있을시 리턴
                if(etChatMsg.getText().toString().equals(""))
                    return;
                else{
                    CHAT_USER_TEXT = etChatMsg.getText().toString();
                }

                // 캘랜더 시간 가져오기
                Calendar calendar = Calendar.getInstance();
                String timestamp = calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);

                // firebaseDB에 데이터 저장
                chatDTO = new ChatDTO(CHAT_NUM, CHAT_USER_NICKNAME, CHAT_USER_EMAIL, CHAT_OTHER_EMAIL ,CHAT_USER_TEXT,timestamp);
                chatRef.child(CHAT_NUM).push().setValue(chatDTO);

                // 채팅알림 보내기
                sendNotification(CHAT_USER_NICKNAME, CHAT_USER_EMAIL ,CHAT_USER_TEXT);

                // 입력한 메세지 보냈으면 초기화
                etChatMsg.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
            }
        });

//        private void systemMsg(){
//            String sysMessage = null;
//            // 캘랜더 시간 가져오기
//            Calendar calendar = Calendar.getInstance();
//            String timestamp = calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
//
//            // firebaseDB에 데이터 저장
//            chatDTO = new ChatDTO(CHAT_NUM, "거래도우미", "-" ,sysMessage,timestamp);
//            chatRef.child(CHAT_NUM).push().setValue(chatDTO);
//
//        };

        //거래일정 버튼 클릭 이벤트
        btnTransactionSchedule = (Button) findViewById(R.id.chatting_btn_transactionSchedule);
        btnTransactionSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //채팅 액티비티에서 거래설정 페이지로 이동!
                Intent getIntent = getIntent();
                Intent transactionScheduleIntent = new Intent(ChatActivity.this, ScheduleActivity.class);
                transactionScheduleIntent.putExtra("ModelCollectionId", getIntent.getStringExtra("ModelCollectionId"));
                transactionScheduleIntent.putExtra("CHAT_NUM", CHAT_NUM);
                transactionScheduleIntent.putExtra("USER_EMAIL", CHAT_USER_EMAIL);
                transactionScheduleIntent.putExtra("OTHER_EMAIL", CHAT_OTHER_EMAIL);
                startActivity(transactionScheduleIntent);
            }
        });

        //거래 기능 관련 참조
        transactionFirebaseAuth = FirebaseAuth.getInstance();
        transactionFirebaseFirestore = FirebaseFirestore.getInstance();
        transactionProgressDialog = new ProgressDialog(this);

        transactionDialog = new Dialog(ChatActivity.this);
        transactionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        transactionDialog.setContentView(R.layout.transaction_window_item);

        imgViewT = transactionDialog.findViewById(R.id.transaction_imgView);
        tvTcategory = transactionDialog.findViewById(R.id.transaction_tv_category);
        tvTmodelName = transactionDialog.findViewById(R.id.transaction_tv_modelName);
        tvTuserName = transactionDialog.findViewById(R.id.transaction_tv_userName);
        tvTrentalType = transactionDialog.findViewById(R.id.transaction_tv_rentalType);
        tvTrentalDate = transactionDialog.findViewById(R.id.transaction_tv_rentalDate);
        tvTrentalCost = transactionDialog.findViewById(R.id.transaction_tv_rentalCost);

        tvTstartDate = transactionDialog.findViewById(R.id.transaction_tv_startDate);
        tvTexpirationDate = transactionDialog.findViewById(R.id.transaction_tv_expirationDate);
        tvTtotalLendingPeriod = transactionDialog.findViewById(R.id.transaction_tv_totalLendingPeriod);
        tvTtotalRental = transactionDialog.findViewById(R.id.transaction_tv_totalRental);
        tvTtransactionDate = transactionDialog.findViewById(R.id.transaction_tv_transactionDate);
        tvTtransactionTime = transactionDialog.findViewById(R.id.transaction_tv_transactionTime);
        tvTtransactionLocation = transactionDialog.findViewById(R.id.transaction_tv_transactionLocation);
        btnTok = transactionDialog.findViewById(R.id.transaction_btn_ok);
        btnTcancel = transactionDialog.findViewById(R.id.transaction_btn_cancel);
        transactionDTO = new TransactionDTO();

        //거래 상세 페이지 취소 버튼 클릭 이벤트
        btnTcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transactionDialog.dismiss();
            }
        });

        //거래 상세 페이지 확인 버튼 클릭 이벤트
        btnTok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(ChatActivity.this);
                dlg.setTitle("DIY_거래");
                dlg.setMessage("거래 진행을 하시겠습니까?");
                dlg.setIcon(R.mipmap.ic_launcher);

                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String transactionCondition = "대여";
                        transactionDTO.settTransactionCondition(transactionCondition);
                        transactionDTO.settUserEmail(transactionFirebaseAuth.getCurrentUser().getEmail());
                        transactionDTO.settOtherEmail(CHAT_OTHER_EMAIL);

                        if (transactionDTO.gettScheduleId() != null && transactionDTO.gettImgView() != null
                                && transactionDTO.gettCategory() != null && transactionDTO.gettModelName() != null
                                && transactionDTO.gettUserName() != null && transactionDTO.gettRentalType() != null
                                && transactionDTO.gettRentalDate() != null && transactionDTO.gettRentalCost() != null
                                && transactionDTO.gettStartDate() != null && transactionDTO.gettExpirationDate() != null
                                && transactionDTO.gettTotalLendingPeriod() != null && transactionDTO.gettTotalRental() != null
                                && transactionDTO.gettTransactionDate() != null && transactionDTO.gettTransactionTime() != null
                                && transactionDTO.gettTransactionLocation() != null && transactionDTO.gettUserEmail() != null
                                && transactionDTO.gettOtherEmail() != null) {
                            transactionProgressDialog.setTitle("DIY Transaction Uploading...");
                            transactionProgressDialog.show();

                            //firestore DB에 저장
                            transactionFirebaseFirestore.collection("DIY_Transaction").document().set(transactionDTO);
                            Toast.makeText(ChatActivity.this, "거래가 완료 되었습니다!", Toast.LENGTH_SHORT).show();

                            // 시스템메시지 출력
                            systemTransMsg(transactionDTO, CHAT_NUM);

                            Intent transactionIntent = new Intent(ChatActivity.this, RentalHistoryRecyclerviewActivity.class);
                            startActivity(transactionIntent);

                        } else {
                            Toast.makeText(ChatActivity.this, "거래정보 설정을 할 수 없습니다!", Toast.LENGTH_SHORT).show();
                            Toast.makeText(ChatActivity.this, "거래정보 설정 다시 입력하세요!", Toast.LENGTH_SHORT).show();
                        }
                        transactionProgressDialog.dismiss();
                        finish();
                    }
                });

                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ChatActivity.this, "거래가 취소 되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });



        // 뒤로가기 버튼
        imgBtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //거래 버튼 클릭 이벤트
        btnTransaction = (Button) findViewById(R.id.chatting_btn_transaction);
        btnTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //DIY_Schedule DB에 sChatNum에 일치하는 CHAT_NUM이 있는지 확인하는 메소드
                transactionFirebaseFirestore.collection("DIY_Schedule")
                        .whereEqualTo("sChatNum", CHAT_NUM)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        getTransactionDBId = queryDocumentSnapshot.getId(); //DIY_Schedule DB Id 참조

                                        if (getTransactionDBId != null) {
                                            //DIY_Schedule에 sChatNum이 있는 경우 DIY_Transaction에 저장 후 출력
                                            showTransactionDialog();

                                        } else {
                                            //DIY_Schedule에 sChatNum이 없는 경우 상대방에게 토스트 메시지 출력
                                            Toast.makeText(ChatActivity.this, "해당 공구에 대한 거래일정 정보가 없습니다!", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(ChatActivity.this, "거래일정 먼저 작업해 주세요!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        });


            }
        });

        //채팅 액티비티에서 공구정보 보여주는 기능
        getModelCollectionId = intent.getStringExtra("ModelCollectionId");
        final String[] getImgaeUrl = new String[1];
        final String[] getEmail = new String[1];
        chattingFirebaseFirestore = FirebaseFirestore.getInstance();
        ImageView imgView = findViewById(R.id.chatting_imgView);
        TextView tvCategory = findViewById(R.id.chatting_tv_category);
        TextView tvModelName = findViewById(R.id.chatting_tv_modelName);
        TextView tvUserName = findViewById(R.id.chatting_tv_userName);
        TextView tvRentalType = findViewById(R.id.chatting_tv_rentalType);
        TextView tvRentalDate = findViewById(R.id.chatting_tv_rentalDate);
        TextView tvRentalCost = findViewById(R.id.chatting_tv_rentalCost);

        if (getModelCollectionId != null) {
            chattingFirebaseFirestore.collection("DIY_Equipment_Rental")
                    .document(getModelCollectionId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            getImgaeUrl[0] = task.getResult().getString("rentalImage");
                            Picasso.get().load(getImgaeUrl[0]).into(imgView);
                            getEmail[0] = task.getResult().getString("userEmail");
                            tvCategory.setText(task.getResult().getString("modelCategory1")+",\n"+
                                    task.getResult().getString("modelCategory2").toString().trim());
                            tvModelName.setText(task.getResult().getString("modelName"));
                            tvRentalType.setText(task.getResult().getString("rentalType"));
                            tvRentalDate.setText(task.getResult().getString("rentalDate"));
                            tvRentalCost.setText(task.getResult().getString("rentalCost"));

                            transactionDTO.settImgView(task.getResult().getString("rentalImage"));
                            transactionDTO.settCategory(task.getResult().getString("modelCategory1")+",\n"+
                                    task.getResult().getString("modelCategory2").toString().trim());
                            transactionDTO.settModelName(task.getResult().getString("modelName"));
                            transactionDTO.settRentalType(task.getResult().getString("rentalType"));
                            transactionDTO.settRentalDate(task.getResult().getString("rentalDate"));
                            transactionDTO.settRentalCost(task.getResult().getString("rentalCost"));

                            chattingFirebaseFirestore.collection("DIY_Signup")
                                    .whereEqualTo("userEmail",getEmail[0])
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                    tvUserName.setText(queryDocumentSnapshot.get("userName").toString().trim());
                                                    transactionDTO.settUserName(queryDocumentSnapshot.get("userName").toString().trim());
                                                }
                                            }
                                        }
                                    });
                        }
                    });

        } else {
            //Toast.makeText(ChatActivity.this, "장비 상세페이지에서 거래자 채팅 먼저 설정해주세요!", Toast.LENGTH_SHORT).show();
            chattingFirebaseFirestore.collection("DIY_Schedule")
                    .whereEqualTo("sChatNum", CHAT_NUM)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                    chattingFirebaseFirestore.collection("DIY_Equipment_Rental")
                                            .document(queryDocumentSnapshot.get("sCollectionId").toString().trim())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    getImgaeUrl[0] = task.getResult().getString("rentalImage");
                                                    Picasso.get().load(getImgaeUrl[0]).into(imgView);
                                                    getEmail[0] = task.getResult().getString("userEmail");
                                                    tvCategory.setText(task.getResult().getString("modelCategory1")+",\n"+
                                                            task.getResult().getString("modelCategory2").toString().trim());
                                                    tvModelName.setText(task.getResult().getString("modelName"));
                                                    tvRentalType.setText(task.getResult().getString("rentalType"));
                                                    tvRentalDate.setText(task.getResult().getString("rentalDate"));
                                                    tvRentalCost.setText(task.getResult().getString("rentalCost"));

                                                    transactionDTO.settImgView(task.getResult().getString("rentalImage"));
                                                    transactionDTO.settCategory(task.getResult().getString("modelCategory1")+",\n"+
                                                            task.getResult().getString("modelCategory2").toString().trim());
                                                    transactionDTO.settModelName(task.getResult().getString("modelName"));
                                                    transactionDTO.settRentalType(task.getResult().getString("rentalType"));
                                                    transactionDTO.settRentalDate(task.getResult().getString("rentalDate"));
                                                    transactionDTO.settRentalCost(task.getResult().getString("rentalCost"));

                                                    chattingFirebaseFirestore.collection("DIY_Signup")
                                                            .whereEqualTo("userEmail",getEmail[0])
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                                            tvUserName.setText(queryDocumentSnapshot.get("userName").toString().trim());
                                                                            transactionDTO.settUserName(queryDocumentSnapshot.get("userName").toString().trim());
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                }
                                            });
                                }
                            }
                        }
                    });
        }
    }


    private void chatWithUser(String chat_num) {
        // chat FDB 데이터 받아오기/추가/
        chatRef.child(chat_num).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatDTO item = snapshot.getValue(ChatDTO.class);
                chatDTOS.add(item);
                chatAdapter.notifyDataSetChanged();;
                lvChatList.setSelection(chatDTOS.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void sendNotification(String nickname, String userEmail, String message){
        fcmRef.child(userEmail.substring(0, userEmail.indexOf('@')))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final FcmDTO userData = dataSnapshot.getValue(FcmDTO.class);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // FMC 메시지 생성 start
                                    JSONObject root = new JSONObject();
                                    JSONObject notification = new JSONObject();
                                    notification.put("body", message);
                                    notification.put("title", getString(R.string.app_name));
                                    root.put("notification", notification);
                                    root.put("to", userData.fcmToken);
                                    // FMC 메시지 생성 end

                                    URL Url = new URL(FCM_MSG_URL);
                                    HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                                    conn.setRequestMethod("POST");
                                    conn.setDoOutput(true);
                                    conn.setDoInput(true);
                                    conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                                    conn.setRequestProperty("Accept", "application/json");
                                    conn.setRequestProperty("Content-type", "application/json");
                                    OutputStream os = conn.getOutputStream();
                                    os.write(root.toString().getBytes("utf-8"));
                                    os.flush();
                                    conn.getResponseCode();
                                    Log.e("Chat","sendNotification clear()");
                                    Log.e("Chat","sendNotification userData: "+userData.getUserEmail());
                                    Log.e("Chat","sendNotification userData: "+message);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //거래 상세 페이지 커스텀 다이얼로그 기능 메소드
    private void showTransactionDialog() {
        transactionFirebaseFirestore.collection("DIY_Schedule")
                .document(getTransactionDBId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        transactionDTO.settScheduleId(getTransactionDBId);
                        transactionDTO.settStartDate(task.getResult().getString("sStartDate"));
                        transactionDTO.settExpirationDate(task.getResult().getString("sExpirationDate"));
                        transactionDTO.settTotalLendingPeriod(task.getResult().getString("sTotalLendingPeriod"));
                        transactionDTO.settTotalRental(task.getResult().getString("sTotalRental"));
                        transactionDTO.settTransactionDate(task.getResult().getString("sTransactionDate"));
                        transactionDTO.settTransactionTime(task.getResult().getString("sTransactionTime"));
                        transactionDTO.settTransactionLocation(task.getResult().getString("sTransactionLocation"));
                        transactionDTO.settOtherEmail(task.getResult().getString("sOtherEmail"));

                        Picasso.get().load(transactionDTO.gettImgView()).into(imgViewT);
                        tvTcategory.setText(transactionDTO.gettCategory());
                        tvTmodelName.setText(transactionDTO.gettModelName());
                        tvTuserName.setText(transactionDTO.gettUserName());
                        tvTrentalType.setText(transactionDTO.gettRentalType());
                        tvTrentalDate.setText(transactionDTO.gettRentalDate());
                        tvTrentalCost.setText(transactionDTO.gettRentalCost());

                        tvTstartDate.setText(transactionDTO.gettStartDate());
                        tvTexpirationDate.setText(transactionDTO.gettExpirationDate());
                        tvTtotalLendingPeriod.setText(transactionDTO.gettTotalLendingPeriod());
                        tvTtotalRental.setText(transactionDTO.gettTotalRental());
                        tvTtransactionDate.setText(transactionDTO.gettTransactionDate());
                        tvTtransactionTime.setText(transactionDTO.gettTransactionTime());
                        tvTtransactionLocation.setText(transactionDTO.gettTransactionLocation());

                    }
                });
        transactionDialog.show();
    }

    private void systemTransMsg(TransactionDTO transactionDTO, String chatNum) {
         chatFDB= FirebaseDatabase.getInstance();
         chatRef = chatFDB.getReference().child("DIY_Chat");
        String result = null;

        Calendar calendar = Calendar.getInstance();
        String timestamp = calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);

//            result = String.format("거래일정이 수정되었습니다.\n 총 대여일: %s\n 총 비용: %s\n 거래일: %s\n 거래시간: %s\n 거래장소: %s",
//                    transactionDTO.getsTotalLendingPeriod(),
//                    transactionDTO.getsTotalRental(),
//                    transactionDTO.getsTransactionDate(),
//                    transactionDTO.getsTransactionTime(),
//                    transactionDTO.getsTransactionLocation());
        result = String.format("거래가 성립되었습니다. \n 서로가 서로를 존중하는 아름다운 거래되시길 바랍니다..");

        chatDTO = new ChatDTO(chatNum, "거래도우미", transactionDTO.gettUserEmail(), transactionDTO.gettOtherEmail(), result, timestamp);
        chatRef.child(chatNum).push().setValue(chatDTO);
    }
}
