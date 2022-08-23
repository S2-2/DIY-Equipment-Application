package kr.ac.kpu.diyequipmentapplication.cart;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kr.ac.kpu.diyequipmentapplication.R;
import kr.ac.kpu.diyequipmentapplication.equipment.EquipmentDetailActivity;
import kr.ac.kpu.diyequipmentapplication.equipment.EquipmentRegistration;

//공급자가 입력한 데이터를 list_equipmentitem.xml파일에 등록하는 Adapter Class 구현
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    List<EquipmentRegistration> equipmentRegistrationList;

    public CartAdapter(Context context, List<EquipmentRegistration> equipmentRegistrationList) {
        this.context = context;
        this.equipmentRegistrationList = equipmentRegistrationList;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.registration_recyclerview_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {

        EquipmentRegistration equipmentRegistration = equipmentRegistrationList.get(position);
        holder.tvModelText.setText("Model Inform \n"+equipmentRegistration.getModelInform()+"\n");
        holder.tvModelName.setText("Model Name \n"+equipmentRegistration.getModelName());

        String imageUri = null;
        imageUri=equipmentRegistration.getRentalImage();
        Picasso.get().load(imageUri).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return equipmentRegistrationList.size();
    }

    //ViewHolder 클래스 구현
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvModelName, tvModelText;

        //ViewHolder 클래스 생성자
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //list_equipmentitem.xml파일에 있는 뷰 객체 참조
            imageView = itemView.findViewById(R.id.registrationRecyclerviewItem_iv);
            tvModelName = itemView.findViewById(R.id.registrationRecyclerviewItem_tv_title);
            tvModelText = itemView.findViewById(R.id.registrationRecyclerviewItem_tv_modelText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        EquipmentRegistration equipmentRegistration = equipmentRegistrationList.get(pos);
                        Intent intent = new Intent(context, EquipmentDetailActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("ModelName", equipmentRegistration.getModelName());
                        intent.putExtra("ModelInform", equipmentRegistration.getModelInform());
                        intent.putExtra("RentalImage",equipmentRegistration.getRentalImage());
                        intent.putExtra("RentalType", equipmentRegistration.getRentalType());
                        intent.putExtra("RentalCost", equipmentRegistration.getRentalCost());
                        intent.putExtra("RentalAddress", equipmentRegistration.getRentalAddress());
                        intent.putExtra("UserEmail", equipmentRegistration.getUserEmail());
                        intent.putExtra("RentalDate", equipmentRegistration.getRentalDate());
                        intent.putExtra("ModelCategory1",equipmentRegistration.getModelCategory1());
                        intent.putExtra("ModelCategory2",equipmentRegistration.getModelCategory2());
                        intent.putExtra("ModelLikeNum",equipmentRegistration.getModelLikeNum());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
