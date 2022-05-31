package kr.ac.kpu.diyequipmentapplication.community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.tvNickname.setText("NickName : "+ communityRegistration.getCommunityNickname());
        holder.tvCategory.setText("Category : "+ communityRegistration.getCommunityCategory());
        holder.tvDateAndTime.setText("Date : "+ communityRegistration.getCommunityDateAndTime());
        holder.tvContent.setText("Contents : "+ communityRegistration.getCommunityContent());
    }

    @Override
    public int getItemCount() {
        return communityRegistrationList.size();
    }

    //ViewHolder 클래스 구현
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNickname, tvCategory, tvDateAndTime, tvContent;
        Button btnComments;
        ImageButton imgBtnLike;

        //ViewHolder 클래스 생성자
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //list_equipmentitem.xml파일에 있는 뷰 객체 참조
            tvNickname = itemView.findViewById(R.id.communityRecyclerviewItem_tv_nickname);
            tvCategory = itemView.findViewById(R.id.communityRecyclerviewItem_tv_category);
            tvDateAndTime = itemView.findViewById(R.id.communityRecyclerviewItem_dateAndTime);
            tvContent = itemView.findViewById(R.id.communityRecyclerviewItem_tv_contents);
            btnComments = itemView.findViewById(R.id.communityRecyclerviewItem_btn_comments);
            imgBtnLike = itemView.findViewById(R.id.communityRecyclerviewItem_btn_like);
        }
    }
}
