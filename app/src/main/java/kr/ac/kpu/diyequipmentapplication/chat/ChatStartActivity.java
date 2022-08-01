package kr.ac.kpu.diyequipmentapplication.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import kr.ac.kpu.diyequipmentapplication.R;

public class ChatStartActivity extends AppCompatActivity  {

    private static final String TAG = "ChatStartAct";

    private ListView lvChatList;

    private FirebaseAuth chatAuth = null;
    private FirebaseDatabase chatFDB = FirebaseDatabase.getInstance();
    private DatabaseReference chatRef = chatFDB.getReference().child("DIY_Chat");
    private FirebaseFirestore chatFS = FirebaseFirestore.getInstance();

    private ImageButton imgBtn_back = null;
    // chat 리스트에 입력될 변수들
    private String CHAT_USER_EMAIL = null;
    private String CHAT_OTHER_NICKNAME = null;

    private Boolean myRoom = false;

    private ArrayList<ChatDTO> chatStartLists;
    private ChatDTO chatDTO;
    private ChatStartAdapter chatStartAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_chat);

        imgBtn_back = (ImageButton) findViewById(R.id.chatStart_btn_back);
        chatAuth = FirebaseAuth.getInstance();
        lvChatList = (ListView) findViewById(R.id.start_lv_clist);

        CHAT_USER_EMAIL = chatAuth.getCurrentUser().getEmail().toString().trim();
        Log.e("ChatList",CHAT_USER_EMAIL);

        showChatList(CHAT_USER_EMAIL);

        imgBtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void showChatList(String email){
        // 리스트 어댑터 생성
        chatStartLists = new ArrayList<ChatDTO>();
        chatStartAdapter = new ChatStartAdapter(ChatStartActivity.this,chatStartLists, getLayoutInflater());
        lvChatList.setAdapter(chatStartAdapter);
        CHAT_OTHER_NICKNAME = "(응답대기중)";


        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatStartLists.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    for(DataSnapshot ds : snapshot.getChildren() ){
                        chatDTO = ds.getValue(ChatDTO.class);
                        myRoom = false;
                        // 내가 포함된 채팅만 필터링
                        if(chatDTO.getUserEmail().equals(CHAT_USER_EMAIL)||chatDTO.getOtherEmail().equals(CHAT_USER_EMAIL)){
                            myRoom = true;
                        }
                        // 상대방 닉네임 가져오기
                        if(!chatDTO.getOtherEmail().equals(CHAT_USER_EMAIL)&&!chatDTO.getOtherEmail().equals("-")){
                            chatFS.collection("DIY_Signup")
                                    .whereEqualTo("userEmail",chatDTO.getOtherEmail())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                for(QueryDocumentSnapshot db : task.getResult()){
                                                    CHAT_OTHER_NICKNAME = db.get("userNickname").toString().trim();
                                                    chatDTO.setUserNickname(CHAT_OTHER_NICKNAME);
                                                }
                                            }
                                        }
                                    });
                        }
                        else{
                            chatFS.collection("DIY_Signup")
                                    .whereEqualTo("userEmail",chatDTO.getUserEmail())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                for(QueryDocumentSnapshot db : task.getResult()){
                                                    CHAT_OTHER_NICKNAME = db.get("userNickname").toString().trim();
                                                    chatDTO.setUserNickname(CHAT_OTHER_NICKNAME);
                                                }
                                            }
                                        }
                                    });
                        }
                    }

//                    chatStartLists.add(chatDTO);
                    if(myRoom){
                        chatStartLists.add(chatDTO);
                    }
                }

                chatStartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}

