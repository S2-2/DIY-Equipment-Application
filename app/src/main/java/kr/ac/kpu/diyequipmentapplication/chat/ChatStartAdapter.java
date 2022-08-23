package kr.ac.kpu.diyequipmentapplication.chat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import kr.ac.kpu.diyequipmentapplication.R;

public class ChatStartAdapter extends BaseAdapter {
    ArrayList<ChatDTO> chattingStartList;
    LayoutInflater inflater;
    Context context;
    FirebaseAuth chatAuth = FirebaseAuth.getInstance();
    String chatEmail = null;

    public ChatStartAdapter(Context context, ArrayList<ChatDTO> chattingStartList, LayoutInflater inflater) {
        this.context = context;
        this.chattingStartList = chattingStartList;
        this.inflater = inflater;
        this.chatEmail = chatAuth.getCurrentUser().getEmail();
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
        ChatDTO chat = chattingStartList.get(position);
        View itemView = null;

        itemView = inflater.inflate(R.layout.activity_chatstart_form,viewGroup,false);

        TextView tvChatnum = itemView.findViewById(R.id.chatStartform_tv_chatnum);
        TextView tvNickname = itemView.findViewById(R.id.chatStartform_tv_userNickname);
        TextView tvLastchat = itemView.findViewById(R.id.chatStartform_tv_lastchat);
        TextView tvPastime = itemView.findViewById(R.id.chatStartform_tv_lastchattime);

        tvNickname.setText(chat.getUserNickname());
        Log.e("chat",chat.getUserNickname());
        tvLastchat.setText(chat.getUserMsg());
        tvPastime.setText(chat.getTimestamp());
        tvChatnum.setText(chat.getChatNum());

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), ChatActivity.class);
                intent.putExtra("chatNum", chat.getChatNum());
                if(!chatEmail.equals(chat.getOtherEmail())){
                    intent.putExtra("chatOtherEmail",chat.getOtherEmail());
                }
                else{
                    intent.putExtra("chatOtherEmail",chat.getUserEmail());
                }
                context.startActivity(intent);
            }
        });

        return itemView;
    }
}
