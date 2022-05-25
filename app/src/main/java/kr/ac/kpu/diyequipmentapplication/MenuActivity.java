package kr.ac.kpu.diyequipmentapplication;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import kr.ac.kpu.diyequipmentapplication.chat.ChatStartActivity;

public class MenuActivity extends AppCompatActivity {
    private FirebaseFirestore menuFirebaseFirestore = null;     //파이어스토어 참조 변수 선언
    private TextView tvNickname = null;
    private TextView tvuserLocation = null;

    private ImageButton imgBtn_back = null;
    private ImageButton imgBtn_home = null;
    private Button btn_logout = null;
    private Button btn_withdraw = null;
    private Button btn_startChat = null;

    private FirebaseAuth menuFirebaseAuth = null;

    Button btn_myDeal, btn_chatting, btn_myCommunity, btn_deal, btn_community, btn_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        imgBtn_back = (ImageButton) findViewById(R.id.menu_btn_back);
        imgBtn_home = (ImageButton) findViewById(R.id.menu_home);
        btn_logout = (Button) findViewById(R.id.menu_btn_logout);
        btn_withdraw = (Button) findViewById(R.id.menu_btn_withdraw);
        btn_startChat = (Button) findViewById(R.id.menu_btn_chatting);

        tvNickname = (TextView) findViewById(R.id.menu_tv_nickname);    //사용자 닉네임 참조
        tvuserLocation = (TextView) findViewById(R.id.menu_tv_userlocation);    //사용자 닉네임 참조
        menuFirebaseFirestore = FirebaseFirestore.getInstance();        //파이어스토어 초기화 및 객체 참조

        menuFirebaseAuth = FirebaseAuth.getInstance();

        //DIY_Signup DB에서 사용자 계정에 맞는 닉네임 가져오는 기능 구현.
        //사용자 이메일 정보와 일치하는 데이터를 DIY_Signup DB에서 찾아서 etNickname 참조 변수에 닉네임 값 참조.
        menuFirebaseFirestore.collection("DIY_Signup")
                .whereEqualTo("userEmail", menuFirebaseAuth.getCurrentUser().getEmail().toString().trim())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                Log.d("menu SignupDB", queryDocumentSnapshot.get("userNickname").toString().trim());
                                tvNickname.setText(queryDocumentSnapshot.get("userNickname").toString().trim());
                            }
                        }
                    }
                });

        imgBtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imgBtn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //로그아웃 버튼 클릭 이벤트
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MenuActivity.this);
                dlg.setTitle("로그아웃");
                dlg.setMessage("로그아웃 하시겠습니까?");
                dlg.setIcon(R.mipmap.ic_launcher);

                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        menuFirebaseAuth.signOut();
                        Toast.makeText(MenuActivity.this, "로그아웃 되었습니다!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MenuActivity.this, "로그아웃 취소되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });

        //회원탈퇴 버튼 클릭 이벤트
        btn_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MenuActivity.this);
                dlg.setTitle("회원 탈퇴");
                dlg.setMessage("회원 탈퇴하시겠습니까?");
                dlg.setIcon(R.mipmap.ic_launcher);

                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        menuFirebaseAuth.getCurrentUser().delete();
                        Toast.makeText(MenuActivity.this, "회원 탈퇴되었습니다!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MenuActivity.this, "회원 탈퇴 취소되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });

        //채팅 버튼 클릭 이벤트
        btn_startChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MenuActivity.this);
                dlg.setTitle("DIY_채팅");
                dlg.setMessage("채팅창으로 접속하시겠습니까?");
                dlg.setIcon(R.mipmap.ic_launcher);

                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MenuActivity.this, "채팅창으로 접속되었습니다!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MenuActivity.this, ChatStartActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MenuActivity.this, "채팅창 접속이 취소되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });
    }
}