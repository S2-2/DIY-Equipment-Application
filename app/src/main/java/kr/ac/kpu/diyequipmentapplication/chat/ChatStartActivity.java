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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import kr.ac.kpu.diyequipmentapplication.R;

public class ChatStartActivity extends AppCompatActivity {

    private EditText edtChatNum;
    private Button btnGo;
    private ListView lvChatList;

    private FirebaseDatabase chatFDB = FirebaseDatabase.getInstance();
    private DatabaseReference chatRef = chatFDB.getReference().child("DIY_Chat");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_chat);

        // 채팅 방번호, 버튼, 채팅방 리스트
        edtChatNum = (EditText) findViewById(R.id.start_chat_et_num);
        btnGo = (Button) findViewById(R.id.start_chat_btn_go);
        lvChatList = (ListView) findViewById(R.id.start_lv_clist);

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

        showChatList();
    }

    private void showChatList(){
        // 리스트 어댑터 생성
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,android.R.id.text1);
        lvChatList.setAdapter(adapter);



        // 현재 자신에게 등록되어 있는 채팅 데이터 받아오기 및 리스너 관리
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.e("LOG","snapshot.getKey() : "+snapshot.getKey());
                adapter.add(snapshot.getKey());
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
}
