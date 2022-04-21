package kr.ac.kpu.diyequipmentapplication.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import kr.ac.kpu.diyequipmentapplication.R;

public class ChatAdapter extends BaseAdapter {

    ArrayList<ChatModel> chattingModelList;
    LayoutInflater inflater;
    FirebaseAuth userAuth;

    public ChatAdapter(ArrayList<ChatModel> chattingModelList, LayoutInflater inflater) {
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
        ChatModel chat = chattingModelList.get(position);
        // 사용자에게 보여줄 채팅 뷰
        View itemView = null;

        // 메세지 사용자와 상대방 구별
        if(chat.getUserEmail().equals(userAuth.getCurrentUser().getEmail().toString())){
            itemView = inflater.inflate(R.layout.activity_chat_myform,viewGroup,false);
        }
        else{
            itemView = inflater.inflate(R.layout.activity_chat_otherform,viewGroup,false);
        }

        // itemView 값들 설정 (이름, 메시지, 시간)
        TextView tvName = itemView.findViewById(R.id.chat_tv_name);
        TextView tvMsg = itemView.findViewById(R.id.chat_tv_msg);
        TextView tvTime = itemView.findViewById(R.id.chat_tv_time);

        tvName.setText(chat.getUserNickname());
        tvMsg.setText(chat.getUserMsg());
        tvTime.setText(chat.getTimestamp());

        return itemView;
    }
}
