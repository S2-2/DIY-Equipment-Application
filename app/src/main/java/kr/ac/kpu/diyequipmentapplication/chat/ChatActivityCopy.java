//package kr.ac.kpu.diyequipmentapplication.chat;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//
//import kr.ac.kpu.diyequipmentapplication.R;
//
//public class ChatActivity extends AppCompatActivity {
//
//    // Firebase Database 관리 객체참조변수
//    private FirebaseAuth firebaseAuth = null;     //FirebaseAuth 참조 변수 선언
//    private FirebaseFirestore userFirestore = null;     //파이어스토어 참조 변수 선언
//    private  FirebaseDatabase chatDatabase = null;
//    private DatabaseReference chatRef = null;
//
//    private EditText etChat;
//    private ListView lvChat;
//    private Button btnChatSend;
//    private ArrayList<ChattingModel> chattingList = new ArrayList<>();
//    private ChattingModel myChat;
//    private ChattingAdapter chatAdapter;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chatting);
//
//        // activity_chatting과 연결
//        etChat = findViewById(R.id.chat_et_box);
//        lvChat = findViewById(R.id.chat_lv_room);
//        btnChatSend = findViewById(R.id.chat_btn_send);
//        lvChat.setAdapter(chatAdapter);
//
//        firebaseAuth = FirebaseAuth.getInstance();
//        // Firestore user DB관리 객체와 user 참고 객체 가져오기
//        userFirestore = FirebaseFirestore.getInstance();
//        // 사용자 계정에 맞는지 확인하고 정보 Nickname, Name가져오기
//        userFirestore.collection("DIY_Signup")
//                .whereEqualTo("userEmail", firebaseAuth.getCurrentUser().getEmail().toString().trim())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
//                                Log.d("main SignupDB", queryDocumentSnapshot.get("userNickname").toString().trim());
//                                Log.d("main SignupDB", queryDocumentSnapshot.get("userEmail").toString().trim());
//                                myChat.setUserNickname(queryDocumentSnapshot.get("userNickname").toString().trim());
//                                myChat.setUserEmail(queryDocumentSnapshot.get("userEmail").toString().trim());
//                            }
//                        }
//                    }
//                });
//        chatAdapter = new ChattingAdapter(chattingList, getLayoutInflater());
//        getSupportActionBar().setTitle(myChat.getUserNickname());
//
//        // Firebase chat DB관리 객체와 chat노드 참고객체 가져오기
//        chatDatabase = FirebaseDatabase.getInstance();
//        chatRef = chatDatabase.getReference("DIY_Chat");
//
//        // FirebaseDB에서 채팅 메세지들 실시간으로 읽어오기
//        // chat노드에 저장되어 있는 데이터들 읽어오기
//        chatRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                // 새로 추가된 데이터 가져오기
//                ChattingModel chattingModel = snapshot.getValue(ChattingModel.class);
//
//                // 새로운 메세지를 리스트뷰에 추가하기 위해 ArrayList에 추가
//                chattingList.add(chattingModel);
//
//                // 리스트뷰를 갱신
//                chatAdapter.notifyDataSetChanged();
//                lvChat.setSelection(chattingList.size()-1);
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
//
//        btnChatSend.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                // FirebaeDB에 저장될 값 (닉네임, ID, 메세지, 시간)
//                String userEmail = myChat.getUserEmail();
//                String userNickname = myChat.getUserNickname();
//                String userMsg =  etChat.getText().toString();
//
//                // 캘랜더 시간 가져오기
//                Calendar calendar = Calendar.getInstance();
//                String timestamp = calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
//
//                // firebaseDB에 저장할 값 설정
//                ChattingModel chattingModel = new ChattingModel(userEmail, userNickname, userMsg, timestamp);
//                chatRef.push().setValue(chattingModel);
//
//                // EditText 글씨 지우기
//                etChat.setText("");
//
//                // 키패드 없애기
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
//            }
//        });
//    }
//}