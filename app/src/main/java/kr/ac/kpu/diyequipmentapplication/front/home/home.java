package kr.ac.kpu.diyequipmentapplication.front.home;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.ac.kpu.diyequipmentapplication.R;

public class home extends AppCompatActivity {

    TextView eq_name;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setContentView(R.layout.home);

        eq_name = findViewById(R.id.textView12);
        eq_name.setText("망치");
    }
}
