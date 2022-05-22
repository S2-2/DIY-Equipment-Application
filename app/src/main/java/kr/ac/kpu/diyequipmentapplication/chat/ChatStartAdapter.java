package kr.ac.kpu.diyequipmentapplication.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import kr.ac.kpu.diyequipmentapplication.R;

public class ChatStartAdapter extends BaseAdapter {
    ArrayList<ChatModel> chattingStartList;
    LayoutInflater inflater;
    Context context;

    public ChatStartAdapter(Context context, ArrayList<ChatModel> chattingStartList, LayoutInflater inflater) {
        this.context = context;
        this.chattingStartList = chattingStartList;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return chattingStartList.size();
    }

    @Override
    public Object getItem(int position) {
        return chattingStartList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ChatModel chat = chattingStartList.get(position);
        View itemView = null;

        itemView = inflater.inflate(R.layout.activity_chatstart_form,viewGroup,false);

        TextView tvChatnum = itemView.findViewById(R.id.chatStartform_tv_chatnum);
        TextView tvNickname = itemView.findViewById(R.id.chatStartform_tv_userNickname);
        TextView tvLastchat = itemView.findViewById(R.id.chatStartform_tv_lastchat);
        TextView tvPastime = itemView.findViewById(R.id.chatStartform_tv_lastchattime);

        tvNickname.setText(chat.getUserNickname());
        tvLastchat.setText(chat.getUserMsg());
        tvPastime.setText(chat.getTimestamp());
        tvChatnum.setText(chat.getChatNum());

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), ChatActivity.class);
                intent.putExtra("chatNum", chat.getChatNum());
                context.startActivity(intent);
            }
        });

        return itemView;
    }
}
