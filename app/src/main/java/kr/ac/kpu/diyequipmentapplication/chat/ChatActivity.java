package kr.ac.kpu.diyequipmentapplication.chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import kr.ac.kpu.diyequipmentapplication.MainActivity;
import kr.ac.kpu.diyequipmentapplication.R;

public class ChatActivity extends AppCompatActivity {

    private String CHAT_NUM = null;
    private String CHAT_USER_EMAIL = null;
    private String CHAT_USER_NICKNAME = null;
    private String CHAT_USER_TEXT = null;

    private ArrayList<ChatModel> chatModels;
    private ChatModel chatModel;
    private ChatAdapter chatAdapter;

    private ListView lvChatList;
    private EditText etChatMsg;
    private Button btnChatSend;
    private TextView tvChatNum;

    private FirebaseAuth chatAuth = null;
    private FirebaseDatabase chatFDB = null;
    private DatabaseReference chatRef = null;
    private FirebaseFirestore userFS = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        // Firebase Auth, DB, Ref 참조
        chatAuth = FirebaseAuth.getInstance();
        chatFDB = FirebaseDatabase.getInstance();
        userFS = FirebaseFirestore.getInstance();
        chatRef = chatFDB.getReference().child("DIY_Chat");

        // 위젯, 어댑터 참조
        lvChatList = (ListView) findViewById(R.id.chat_lv_msg);
        etChatMsg = (EditText) findViewById(R.id.chat_et_msg_box);
        btnChatSend = (Button) findViewById(R.id.chat_btn_msg_send);
        tvChatNum = (TextView) findViewById(R.id.chat_tv_room_num);
        chatModels = new ArrayList<ChatModel>();
        chatAdapter = new ChatAdapter(chatModels, getLayoutInflater());
        lvChatList.setAdapter(chatAdapter);

        // 사용자 이메일 및 닉네임 가져오기
        CHAT_USER_EMAIL = chatAuth.getCurrentUser().getEmail().toString();

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

        if(CHAT_NUM == null){
            Random rand = new Random();
            Integer iValue = null;

            iValue = rand.nextInt(10000);  // 0 <= iValue < 10000
            CHAT_NUM = iValue.toString();
        }

        // 채팅방번호 입장
        tvChatNum.setText(" ROOM " + CHAT_NUM);
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
            chatModel = new ChatModel(CHAT_NUM, CHAT_USER_EMAIL, CHAT_USER_NICKNAME, CHAT_USER_TEXT,timestamp);
            chatRef.child(CHAT_NUM).push().setValue(chatModel);

            // 채팅알림 보내기
            chatAlarm(CHAT_USER_NICKNAME, CHAT_USER_TEXT);

            // 입력한 메세지 보냈으면 초기화
            etChatMsg.setText("");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);

        }
    });
    }

    private void chatWithUser(String chat_num) {
        // chat FDB 데이터 받아오기/추가/
        chatRef.child(chat_num).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatModel item = snapshot.getValue(ChatModel.class);
                chatModels.add(item);

                chatAdapter.notifyDataSetChanged();;
                lvChatList.setSelection(chatModels.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void chatAlarm(String chatTitle, String chatText){

        Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
        chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent chatPendIntent = PendingIntent.getActivity(this,0, chatIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ChatActivity.this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(chatTitle)
                .setContentText(chatText)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(chatPendIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);

        Log.e("LOG", "채팅보내기 완료 - " + chatText);

        NotificationManager chatAlarmManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        chatAlarmManager.notify(0,builder.build());
    }
}
