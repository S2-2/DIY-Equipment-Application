package kr.ac.kpu.diyequipmentapplication.menu;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.kpu.diyequipmentapplication.R;
import kr.ac.kpu.diyequipmentapplication.chat.ChatActivity;
import kr.ac.kpu.diyequipmentapplication.chat.TransactionDTO;

public class RentalHistoryAdapter extends RecyclerView.Adapter<RentalHistoryAdapter.ViewHolder> {
    Context context;
    List<TransactionDTO> transactionDTOList;

    public RentalHistoryAdapter(Context context, List<TransactionDTO> transactionDTOList) {
        this.context = context;
        this.transactionDTOList = transactionDTOList;
    }

    @NonNull
    @Override
    public RentalHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rental_history_recyclerview_item,parent,false);
        return new RentalHistoryAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RentalHistoryAdapter.ViewHolder holder, int position) {

        TransactionDTO transactionDTO = transactionDTOList.get(position);
        holder.tvCategory.setText(transactionDTO.gettCategory());
        holder.tvModelName.setText(transactionDTO.gettModelName());
        holder.tvRentalType.setText(transactionDTO.gettRentalType());
        holder.tvRentalDate.setText(transactionDTO.gettRentalDate());
        holder.tvRentalCost.setText(transactionDTO.gettRentalCost());
        holder.tvCondition.setText(transactionDTO.gettTransactionCondition());

        String imageUri = null;
        imageUri=transactionDTO.gettImgView();
        Picasso.get().load(imageUri).into(holder.imgViewRental);
    }

    @Override
    public int getItemCount() {
        return transactionDTOList.size();
    }

    //ViewHolder 클래스 구현
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgViewRental;
        TextView tvCategory, tvModelName, tvRentalType, tvRentalDate, tvRentalCost, tvCondition;

        FirebaseAuth transactionFirebaseAuth;
        FirebaseFirestore transactionFirebaseFirestore;
        ProgressDialog transactionProgressDialog;
        Dialog transactionDialog;                //거래 상세페이지 커스텀 다이얼로그
        ImageView imgViewT;
        TextView tvTcategory, tvTmodelName, tvTuserName, tvTrentalType, tvTrentalDate, tvTrentalCost, tvTstartDate,
                tvTexpirationDate, tvTtotalLendingPeriod, tvTtotalRental, tvTtransactionDate, tvTtransactionTime, tvTtransactionLocation;

        Button btnTchatting, btnTreturn;
        String getTransactionDBId;

        //ViewHolder 클래스 생성자
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //rental_history_recyclerview_item.xml파일에 있는 뷰 객체 참조
            imgViewRental = itemView.findViewById(R.id.rentalHistoryRecyclerview_iv_equip);
            tvCategory = itemView.findViewById(R.id.rentalHistoryRecyclerview_tv_category);
            tvModelName = itemView.findViewById(R.id.rentalHistoryRecyclerview_tv_modelName);
            tvRentalType = itemView.findViewById(R.id.rentalHistoryRecyclerview_tv_rentalType);
            tvRentalDate = itemView.findViewById(R.id.rentalHistoryRecyclerview_tv_rentalDate);
            tvRentalCost = itemView.findViewById(R.id.rentalHistoryRecyclerview_tv_rentalCost);
            tvCondition = itemView.findViewById(R.id.rentalHistoryRecyclerview_tv_condition);

            //거래 기능 관련 참조
            transactionFirebaseAuth = FirebaseAuth.getInstance();
            transactionFirebaseFirestore = FirebaseFirestore.getInstance();

            transactionDialog = new Dialog(context);
            transactionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            transactionDialog.setContentView(R.layout.transactiondetail_window_item);

            imgViewT = transactionDialog.findViewById(R.id.transaction_imgView);
            tvTcategory = transactionDialog.findViewById(R.id.transaction_tv_category);
            tvTmodelName = transactionDialog.findViewById(R.id.transaction_tv_modelName);
            tvTuserName = transactionDialog.findViewById(R.id.transaction_tv_userName);
            tvTrentalType = transactionDialog.findViewById(R.id.transaction_tv_rentalType);
            tvTrentalDate = transactionDialog.findViewById(R.id.transaction_tv_rentalDate);
            tvTrentalCost = transactionDialog.findViewById(R.id.transaction_tv_rentalCost);

            tvTstartDate = transactionDialog.findViewById(R.id.transaction_tv_startDate);
            tvTexpirationDate = transactionDialog.findViewById(R.id.transaction_tv_expirationDate);
            tvTtotalLendingPeriod = transactionDialog.findViewById(R.id.transaction_tv_totalLendingPeriod);
            tvTtotalRental = transactionDialog.findViewById(R.id.transaction_tv_totalRental);
            tvTtransactionDate = transactionDialog.findViewById(R.id.transaction_tv_transactionDate);
            tvTtransactionTime = transactionDialog.findViewById(R.id.transaction_tv_transactionTime);
            tvTtransactionLocation = transactionDialog.findViewById(R.id.transaction_tv_transactionLocation);
            btnTchatting = transactionDialog.findViewById(R.id.transaction_btn_chatting);
            btnTreturn = transactionDialog.findViewById(R.id.transaction_btn_return);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        TransactionDTO transactionDTO = transactionDTOList.get(pos);

                        transactionFirebaseFirestore.collection("DIY_Transaction")
                                .whereEqualTo("tScheduleId", transactionDTO.gettScheduleId())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                getTransactionDBId = queryDocumentSnapshot.getId();
                                            }
                                        }
                                    }
                                });

                        Picasso.get().load(transactionDTO.gettImgView()).into(imgViewT);
                        tvTcategory.setText(transactionDTO.gettCategory());
                        tvTmodelName.setText(transactionDTO.gettModelName());
                        tvTuserName.setText(transactionDTO.gettUserName());
                        tvTrentalType.setText(transactionDTO.gettRentalType());
                        tvTrentalDate.setText(transactionDTO.gettRentalDate());
                        tvTrentalCost.setText(transactionDTO.gettRentalCost());

                        tvTstartDate.setText(transactionDTO.gettStartDate());
                        tvTexpirationDate.setText(transactionDTO.gettExpirationDate());
                        tvTtotalLendingPeriod.setText(transactionDTO.gettTotalLendingPeriod());
                        tvTtotalRental.setText(transactionDTO.gettTotalRental());
                        tvTtransactionDate.setText(transactionDTO.gettTransactionDate());
                        tvTtransactionTime.setText(transactionDTO.gettTransactionTime());
                        tvTtransactionLocation.setText(transactionDTO.gettTransactionLocation());
                        transactionDialog.show();

                        //거래 상세 페이지에서 거래자와 채팅 액티비티로 이동!
                        btnTchatting.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(context, ChatActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                //intent.putExtra("ModelCollectionId", transactionDTO.getid.getCommunityContent());
                                context.startActivity(intent);
                            }
                        });

                        //거래 상세 페이지 확인 버튼 클릭 이벤트
                        btnTreturn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder dlg = new AlertDialog.Builder(context);
                                dlg.setTitle("DIY_반납");
                                dlg.setMessage("공구 반납 진행을 하시겠습니까?");
                                dlg.setIcon(R.mipmap.ic_launcher);

                                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        final String transactionCondition = "반납";
                                        transactionDTO.settTransactionCondition(transactionCondition);

                                        Map<String, Object> rentalHistoryUpdate = new HashMap<String, Object>();
                                        rentalHistoryUpdate.put("tTransactionCondition", transactionDTO.gettTransactionCondition());

                                        if (transactionDTO.gettTransactionCondition() != null) {
                                            transactionFirebaseFirestore.collection("DIY_Transaction")
                                                    .document(getTransactionDBId)
                                                    .update(rentalHistoryUpdate)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("공구 반납 업데이트 성공!", "DocumentSnapshot successfully update!");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("공구 반납 업데이트 실패", "Error updating document", e);
                                                        }
                                                    });

                                            Toast.makeText(context, "공구 반납 완료 되었습니다!", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(context, "반납 정보 설정을 할 수 없습니다!", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(context, "반납 설정 다시 입력하세요!", Toast.LENGTH_SHORT).show();
                                        }
                                        transactionDialog.dismiss();
                                    }
                                });

                                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(context, "공구 반납 취소 되었습니다!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dlg.show();
                            }
                        });
                    }
                }
            });
        }
    }
}
