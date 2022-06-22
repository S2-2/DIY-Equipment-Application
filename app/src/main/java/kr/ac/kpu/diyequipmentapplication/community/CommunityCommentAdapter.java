package kr.ac.kpu.diyequipmentapplication.community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.ac.kpu.diyequipmentapplication.R;

public class CommunityCommentAdapter extends RecyclerView.Adapter<CommunityCommentAdapter.ViewHolder>{
    Context context;
    List<CommunityComment> communityCommentList;

    public CommunityCommentAdapter(Context context, List<CommunityComment> communityCommentList) {
        this.context = context;
        this.communityCommentList = communityCommentList;
    }

    @NonNull
    @Override
    public CommunityCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_recyclerview_comment_item,parent,false);
        return new CommunityCommentAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityCommentAdapter.ViewHolder holder, int position) {
        Boolean tempLike;

        CommunityComment communityComment = communityCommentList.get(position);
        tempLike = communityComment.getCommentLike();
        holder.tvNickname.setText(communityComment.getCommentNickname());
        holder.tvDate.setText(communityComment.getCommentDate());
        holder.tvComment.setText(communityComment.getComment());

        if (tempLike == true)
            holder.imgBtnLike.setImageResource(R.drawable.ic_baseline_favorite_border_red_24);
        else
            holder.imgBtnLike.setImageResource(R.drawable.ic_baseline_favorite_border_dark_24);

    }

    @Override
    public int getItemCount() {
        return communityCommentList.size();
    }

    //ViewHolder 클래스 구현
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvComment, tvNickname, tvDate;
        ImageButton imgBtnLike;

        //ViewHolder 클래스 생성자
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //community_recyclerview_comment_item.xml파일에 있는 뷰 객체 참조
            tvComment = itemView.findViewById(R.id.communityRecyclerviewCommentItem_tv_comment);
            tvNickname = itemView.findViewById(R.id.communityRecyclerviewCommentItem_tv_nickname);
            tvDate = itemView.findViewById(R.id.communityRecyclerviewCommentItem_tv_date);
        }
    }
}
