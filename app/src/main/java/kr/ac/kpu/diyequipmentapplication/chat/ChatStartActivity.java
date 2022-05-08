package kr.ac.kpu.diyequipmentapplication.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kr.ac.kpu.diyequipmentapplication.R;

public class ChatStartActivity extends AppCompatActivity {

    private EditText edtChatNum;
    private Button btnGo;
    private ListView lvChatList;

    private FirebaseAuth chatAuth = null;
    private FirebaseDatabase chatFDB = FirebaseDatabase.getInstance();
    private DatabaseReference chatRef = chatFDB.getReference().child("DIY_Chat");

    // chat 리스트에 입력될 변수들
    private String CHAT_USER_EMAIL = null;
    private String CHAT_OTHER_NICKNAME = null;
    private String CHAT_LASTTIME = null;
    private String CHAT_LASTCHAT = null;

    private ArrayList<ChatModel> chatStartLists;
    private ChatModel chatModel;
    private ChatStartAdapter chatStartAdapter;
    private ListView lvChatStartList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_chat);

        chatAuth = FirebaseAuth.getInstance();
        // 채팅 방번호, 버튼, 채팅방 리스트
        edtChatNum = (EditText) findViewById(R.id.start_chat_et_num);
        btnGo = (Button) findViewById(R.id.start_chat_btn_go);
        lvChatList = (ListView) findViewById(R.id.start_lv_clist);

        CHAT_USER_EMAIL = chatAuth.getCurrentUser().getEmail().toString();

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 선택한 방이 있는지 없는지 확인
                if (edtChatNum.getText().toString().equals(""))
                    return;

                Intent intent = new Intent(ChatStartActivity.this, ChatActivity.class);
                intent.putExtra("chatNum", edtChatNum.getText().toString());
                startActivity(intent);
            }
        });
        showChatList(CHAT_USER_EMAIL);
    }

    private void showChatList(String email){
        // 리스트 어댑터 생성
//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,android.R.id.text1);
        chatStartLists = new ArrayList<ChatModel>();
        chatStartAdapter = new ChatStartAdapter(chatStartLists, getLayoutInflater());
        lvChatList.setAdapter(chatStartAdapter);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    try{
                        String key = snapshot.getKey();
                        User value = snapshot.getValue(User.class);
                        Log.e("LOG", "snapshot : " + key);
                        Log.e("LOG", "snapshot :" + value);

                    }catch (ClassCastException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // 현재 자신에게 등록되어 있는 채팅 데이터 받아오기 및 리스너 관리
//        chatRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                String ChatNum = snapshot.getKey();
//                ChatModel item = snapshot.child(ChatNum).getValue(ChatModel.class);
//                chatStartLists.add(item);
//
//                Log.e("LOG", "C - snapshot: " + snapshot);
//                Log.e("LOG", "C - snapshot.getValue: " + snapshot.getValue());
//                Log.e("LOG", "C - snapshot.getChildren: " + snapshot.getChildren().forEach(););
//                Log.e("LOG", "C - snapshot.getKey: " + snapshot.getKey());
//                Log.e("LOG", "C - snapshot.getPriority: " + snapshot.getPriority());
//
//
//                chatStartAdapter.notifyDataSetChanged();
//                lvChatStartList.setSelection(chatStartLists.size()-1);
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }
}
