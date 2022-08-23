package kr.ac.kpu.diyequipmentapplication.equipment;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

import kr.ac.kpu.diyequipmentapplication.R;

//공급자가 입력한 데이터를 list_equipmentitem.xml파일에 등록하는 Adapter Class 구현
public class RegistrationAdapter extends RecyclerView.Adapter<RegistrationAdapter.ViewHolder> {

    Context context;
    List<RegistrationDTO> equipmentRegistrationList;
    FirebaseFirestore cartFS;
    FirebaseAuth cartAuth;

    public RegistrationAdapter(Context context, List<RegistrationDTO> equipmentRegistrationList) {
        this.context = context;
        this.equipmentRegistrationList = equipmentRegistrationList;
        cartFS = FirebaseFirestore.getInstance();
        cartAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public RegistrationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.registration_recyclerview_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistrationAdapter.ViewHolder holder, int position) {
        String myEmail = cartAuth.getCurrentUser().getEmail();

        RegistrationDTO registrationDTO = equipmentRegistrationList.get(position);
        holder.tvModelText.setText(registrationDTO.getModelInform());
        holder.tvModelName.setText(registrationDTO.getModelName());
        holder.tvLikeNum.setText(registrationDTO.getModelLikeNum());

        cartFS.collection("DIY_MyCart")
                .whereEqualTo("userEmail",myEmail.substring(0,myEmail.indexOf('@')))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot db: task.getResult()){
                        if(db.get("equipTitle")!=null&&db.get("equipTitle").equals(registrationDTO.getModelName())){
                            holder.imgBtnLike.setImageResource(R.drawable.ic_baseline_favorite_24);
                        }
                        else{
                            Log.e("DB","It is empty");
                        }
                    }
                }
            }
        });

        String imageUri = null;
        imageUri=registrationDTO.getRentalImage();
        Picasso.get().load(imageUri).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return equipmentRegistrationList.size();
    }

    //ViewHolder 클래스 구현
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton imgBtnLike;
        TextView tvModelName, tvModelText, tvLikeNum;

        //ViewHolder 클래스 생성자
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //list_equipmentitem.xml파일에 있는 뷰 객체 참조
            imageView = itemView.findViewById(R.id.registrationRecyclerviewItem_iv);
            imgBtnLike = itemView.findViewById(R.id.registrationRecyclerviewItem_btn_like);
            tvModelName = itemView.findViewById(R.id.registrationRecyclerviewItem_tv_title);
            tvModelText = itemView.findViewById(R.id.registrationRecyclerviewItem_tv_modelText);
            tvLikeNum = itemView.findViewById(R.id.registrationRecyclerviewItem_tv_likeNum);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        RegistrationDTO registrationDTO = equipmentRegistrationList.get(pos);
                        Intent intent = new Intent(context, EquipmentDetailActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("ModelName", registrationDTO.getModelName());
                        intent.putExtra("ModelInform", registrationDTO.getModelInform());
                        intent.putExtra("RentalImage",registrationDTO.getRentalImage());
                        intent.putExtra("RentalType", registrationDTO.getRentalType());
                        intent.putExtra("RentalCost", registrationDTO.getRentalCost());
                        intent.putExtra("RentalAddress", registrationDTO.getRentalAddress());
                        intent.putExtra("UserEmail", registrationDTO.getUserEmail());
                        intent.putExtra("RentalDate", registrationDTO.getRentalDate());
                        intent.putExtra("ModelCategory1",registrationDTO.getModelCategory1());
                        intent.putExtra("ModelCategory2",registrationDTO.getModelCategory2());
                        intent.putExtra("ModelCollectionId",registrationDTO.getModelCollectionId());
                        intent.putExtra("ModelLikeNum",registrationDTO.getModelLikeNum());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
