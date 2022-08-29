package kr.ac.kpu.diyequipmentapplication.chat;

import android.content.Context;
import android.content.Intent;
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

public class ChatStartAdapter extends BaseAdapter {
    ArrayList<ChatDTO> chattingStartList;
    LayoutInflater inflater;
    Context context;
    FirebaseAuth chatAuth = FirebaseAuth.getInstance();
    String chatEmail = null;
    String getProfileUrl = null;
    String profile = null;

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
        FirebaseFirestore chatStartFirestore = FirebaseFirestore.getInstance();
        View itemView = null;

        itemView = inflater.inflate(R.layout.activity_chatstart_form,viewGroup,false);

        ImageView ivProfile = itemView.findViewById(R.id.chatStartform_iv_profile);
        TextView tvChatnum = itemView.findViewById(R.id.chatStartform_tv_chatnum);
        TextView tvNickname = itemView.findViewById(R.id.chatStartform_tv_userNickname);
        TextView tvLastchat = itemView.findViewById(R.id.chatStartform_tv_lastchat);
        TextView tvPastime = itemView.findViewById(R.id.chatStartform_tv_lastchattime);

        tvNickname.setText(chat.getUserNickname());
        Log.e("chat",chat.getUserNickname());
        tvLastchat.setText(chat.getUserMsg());
        tvPastime.setText(chat.getTimestamp());
        tvChatnum.setText(chat.getChatNum());

        if(chatAuth.getCurrentUser().getEmail().equals(chat.getUserEmail())){
            profile = chat.getOtherEmail();
        } else if(chatAuth.getCurrentUser().getEmail().equals(chat.getOtherEmail())){
            profile = chat.getUserEmail();
        }

        chatStartFirestore.collection("DIY_Profile")
                .whereEqualTo("profileEmail", profile)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                                Log.e("profile-mine", chatAuth.getCurrentUser().getEmail().toString());
                                Log.e("profile-other",profile);
                                getProfileUrl = queryDocumentSnapshot.get("profileImage").toString().trim();
                                Picasso.get().load(getProfileUrl).into(ivProfile);
                            }
                        }
                    }
                });

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
