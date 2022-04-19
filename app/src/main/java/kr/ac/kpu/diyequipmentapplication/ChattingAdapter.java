package kr.ac.kpu.diyequipmentapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ChattingAdapter extends BaseAdapter {
    ArrayList<ChattingFormDB> chattingFormDBList;
    LayoutInflater inflater;

    public ChattingAdapter(ArrayList<ChattingFormDB> chattingFormDBList, LayoutInflater inflater) {
        this.chattingFormDBList = chattingFormDBList;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return chattingFormDBList.size();
    }

    @Override
    public Object getItem(int position) {
        return chattingFormDBList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        // 현재 보여줄 n번째 데이터로 뷰 생성
        ChattingFormDB chat = chattingFormDBList.get(position);
        // 사용자에게 보여줄 채팅 뷰
        View itemView = null;

//        // 메세지 사용자와 상대방 구별
//        if(chat.getUserName().equals()){
//            itemView = inflater.inflate(R.layout.activity_chat_myform,viewGroup,false);
//        }
//        else{
//            itemView = inflater.inflate(R.layout.activity_chat_otherform,viewGroup,false)
//        }
//
//        // itemView 값들 설정 (이름, 메시지, 시간)
//        TextView tvName = itemView.findViewById(R.id.chat_tv_name);
//        TextView tvMsg = itemView.findViewById(R.id.chat_tv_msg);
//        TextView tvTime = itemView.findViewById(R.id.chat_tv_time);
//
//        tvName.setText(chat.getUserName());
//        tvMsg.setText(chat.getUserMsg());
//        tvTime.setText(chat.getChatTime());
//
        return itemView;
    }
}
