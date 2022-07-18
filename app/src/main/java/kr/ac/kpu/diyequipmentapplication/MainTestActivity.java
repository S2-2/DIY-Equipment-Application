package kr.ac.kpu.diyequipmentapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import kr.ac.kpu.diyequipmentapplication.chat.ChatActivity;
import kr.ac.kpu.diyequipmentapplication.community.CommunityRecyclerview;
import kr.ac.kpu.diyequipmentapplication.equipment.RegistrationRecyclerview;
import kr.ac.kpu.diyequipmentapplication.menu.MenuSettingActivity;

public class MainTestActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private MainActivity fragmentHome = new MainActivity(); //홈
    private RegistrationRecyclerview fragmentEquipment = new RegistrationRecyclerview(); //장비거래목록
    private CommunityRecyclerview fragmentCommunity = new CommunityRecyclerview(); //커뮤니티목록
    private ChatActivity fragmentChatting = new ChatActivity(); //채팅
    private MenuSettingActivity fragmentMypage = new MenuSettingActivity(); //마이페이지

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(menuItem.getItemId())
            {
                case R.id.item_home:
                    transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();
                    break;
                case R.id.item_equipment:
                    transaction.replace(R.id.frameLayout, fragmentEquipment).commitAllowingStateLoss();
                    break;
                case R.id.item_community:
                    transaction.replace(R.id.frameLayout, fragmentCommunity).commitAllowingStateLoss();
                    break;
                case R.id.item_chatting:
                    transaction.replace(R.id.frameLayout, fragmentChatting).commitAllowingStateLoss();
                    break;
                case R.id.item_mypage:
                    transaction.replace(R.id.frameLayout, fragmentMypage).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }
}