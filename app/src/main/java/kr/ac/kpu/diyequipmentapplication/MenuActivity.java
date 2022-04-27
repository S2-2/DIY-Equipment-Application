package kr.ac.kpu.diyequipmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    Button btn_myDeal, btn_chatting, btn_myCommunity, btn_deal, btn_community, btn_location, btn_logout, btn_withdraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btn_myDeal = findViewById(R.id.menu_btn_myDeal);
        btn_chatting = findViewById(R.id.menu_btn_chatting);
        btn_myCommunity = findViewById(R.id.menu_btn_myCommunity);
        btn_deal = findViewById(R.id.menu_btn_deal);
        btn_community = findViewById(R.id.menu_btn_community);
        btn_location = findViewById(R.id.menu_btn_location);
        btn_logout = findViewById(R.id.menu_btn_logout);
        btn_withdraw = findViewById(R.id.menu_btn_withdraw);

        /* 거래내역
        btn_chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ChattingAdapter.class);
                startActivity(intent);
            }
        }); */

        /* 채팅 */
        btn_chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ChattingAdapter.class);
                startActivity(intent);
            }
        });

        /* 내가 쓴 커뮤니티
        btn_myCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ChattingAdapter.class);
                startActivity(intent);
            }
        }); */

        /* 거래 목록 */
        btn_deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ChattingAdapter.class);
                startActivity(intent);
            }
        });

        /* 커뮤니티 목록 */
        btn_community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),CommunityActivity.class);
                startActivity(intent);
            }
        });

        /* 위치설정
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ChattingAdapter.class);
                startActivity(intent);
            }
        }); */

        /* 로그아웃
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ChattingAdapter.class);
                startActivity(intent);
            }
        }); */

        /* 탈퇴하기
        btn_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ChattingAdapter.class);
                startActivity(intent);
            }
        }); */


    }
}