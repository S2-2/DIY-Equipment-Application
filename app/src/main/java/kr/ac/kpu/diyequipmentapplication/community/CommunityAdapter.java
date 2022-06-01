package kr.ac.kpu.diyequipmentapplication.community;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kr.ac.kpu.diyequipmentapplication.R;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    Context context;
    List<CommunityRegistration> communityRegistrationList;

    public CommunityAdapter(Context context, List<CommunityRegistration> communityRegistrationList) {
        this.context = context;
        this.communityRegistrationList = communityRegistrationList;
    }

    @NonNull
    @Override
    public CommunityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_recyclerview_item,parent,false);
        return new CommunityAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityAdapter.ViewHolder holder, int position) {

        CommunityRegistration communityRegistration = communityRegistrationList.get(position);
        holder.tvNickname.setText(communityRegistration.getCommunityNickname());
        holder.tvDateAndTime.setText(communityRegistration.getCommunityDateAndTime());
        holder.tvContent.setText(communityRegistration.getCommunityContent());

    }

    @Override
    public int getItemCount() {
        return communityRegistrationList.size();
    }

    //ViewHolder 클래스 구현
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNickname, tvDateAndTime, tvContent;

        //ViewHolder 클래스 생성자
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //list_equipmentitem.xml파일에 있는 뷰 객체 참조
            tvNickname = itemView.findViewById(R.id.communityRecyclerviewItem_tv_nickname);
            tvDateAndTime = itemView.findViewById(R.id.communityRecyclerviewItem_dateAndTime);
            tvContent = itemView.findViewById(R.id.communityRecyclerviewItem_tv_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        CommunityRegistration communityRegistration = communityRegistrationList.get(pos);
                        Intent intent = new Intent(context, CommunityDetailActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("CommunityContents", communityRegistration.getCommunityContent());
                        intent.putExtra("CommunityImage", communityRegistration.getCommunityImage());
                        intent.putExtra("CommunityNickname",communityRegistration.getCommunityNickname());
                        intent.putExtra("CommunityDateAndTime", communityRegistration.getCommunityDateAndTime());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
