package kr.ac.kpu.diyequipmentapplication.chat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kr.ac.kpu.diyequipmentapplication.R;

public class ChatAdapter extends BaseAdapter {

    ArrayList<ChatDTO> chattingModelList;
    LayoutInflater inflater;
    FirebaseAuth userAuth;
    FirebaseFirestore chatFirestore = null;
    String getProfileUrl = null;
    String profile = null;

    public ChatAdapter(ArrayList<ChatDTO> chattingModelList, LayoutInflater inflater) {
        this.chattingModelList = chattingModelList;
        this.inflater = inflater;
        userAuth = FirebaseAuth.getInstance();
    }

    @Override
    public int getCount() {
        return chattingModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return chattingModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        // 현재 보여줄 n번째 데이터로 뷰 생성
        ChatDTO chat = chattingModelList.get(position);
        // 사용자에게 보여줄 채팅 뷰
        View itemView = null;

        // 메세지 사용자와 상대방 구별
        if(chat.getUserEmail().equals(userAuth.getCurrentUser().getEmail().toString())){
            itemView = inflater.inflate(R.layout.activity_chat_myform,viewGroup,false);
        }
        else{
            itemView = inflater.inflate(R.layout.activity_chat_otherform,viewGroup,false);
        }

        // 프로필 값 설정
        ImageView ivProfile = itemView.findViewById(R.id.chat_iv);

        // itemView 값들 설정 (이름, 메시지, 시간)
        TextView tvName = itemView.findViewById(R.id.chat_tv_name);
        TextView tvMsg = itemView.findViewById(R.id.chat_tv_msg);
        TextView tvTime = itemView.findViewById(R.id.chat_tv_time);

        tvName.setText(chat.getUserNickname());
        tvMsg.setText(chat.getUserMsg());
        tvTime.setText(chat.getTimestamp());

        chatFirestore = FirebaseFirestore.getInstance();

        if(userAuth.getCurrentUser().getEmail().equals(chat.getUserEmail())){
            profile = chat.getUserEmail();
        } else if(userAuth.getCurrentUser().getEmail().equals(chat.getOtherEmail())){
            profile = chat.getUserEmail();
        }

        chatFirestore.collection("DIY_Profile")
                .whereEqualTo("profileEmail", profile)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                                Log.e("chatprofile-mine", userAuth.getCurrentUser().getEmail().toString());
                                Log.e("chatprofile-other",profile);
                                getProfileUrl = queryDocumentSnapshot.get("profileImage").toString().trim();
                                Picasso.get().load(getProfileUrl).into(ivProfile);
                            }
                        }
                    }
                });

        return itemView;
    }
}
