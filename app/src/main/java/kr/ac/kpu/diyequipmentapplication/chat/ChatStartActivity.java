package kr.ac.kpu.diyequipmentapplication.chat;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import kr.ac.kpu.diyequipmentapplication.R;

public class ChatStartActivity extends AppCompatActivity  {

    private static final String TAG = "ChatStartAct";

    private EditText edtChatNum;
    private Button btnGo;
    private ListView lvChatList;

    private FirebaseAuth chatAuth = null;
    private FirebaseDatabase chatFDB = FirebaseDatabase.getInstance();
    private DatabaseReference chatRef = chatFDB.getReference().child("DIY_Chat");
    private FirebaseFirestore signFDB = FirebaseFirestore.getInstance();

    // chat 리스트에 입력될 변수들
    private String CHAT_USER_EMAIL = null;
    private String CHAT_OTHER_NICKNAME = null;

    private ArrayList<ChatDTO> chatStartLists;
    private ChatDTO chatDTO;
    private ChatStartAdapter chatStartAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_chat);

        chatAuth = FirebaseAuth.getInstance();
        lvChatList = (ListView) findViewById(R.id.start_lv_clist);

        CHAT_USER_EMAIL = chatAuth.getCurrentUser().getEmail().toString().trim();

        showChatList(CHAT_USER_EMAIL);
    }

    private void showChatList(String email){
        // 리스트 어댑터 생성
        chatStartLists = new ArrayList<ChatDTO>();
        chatStartAdapter = new ChatStartAdapter(ChatStartActivity.this,chatStartLists, getLayoutInflater());
        lvChatList.setAdapter(chatStartAdapter);


        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatStartLists.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    for(DataSnapshot ds : snapshot.getChildren() ){
                        chatDTO = ds.getValue(ChatDTO.class);
                        if(!chatDTO.getUserEmail().equals(CHAT_USER_EMAIL)){
                            CHAT_OTHER_NICKNAME = chatDTO.getUserNickname();
                        }

                    }
                    chatDTO.setUserNickname(CHAT_OTHER_NICKNAME);
                    chatStartLists.add(chatDTO);
                }
                chatStartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}

