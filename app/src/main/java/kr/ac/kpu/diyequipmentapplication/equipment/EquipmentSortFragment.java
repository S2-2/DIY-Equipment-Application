package kr.ac.kpu.diyequipmentapplication.equipment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import kr.ac.kpu.diyequipmentapplication.R;

public class EquipmentSortFragment extends Fragment {

    private Spinner registrationSortSpr;
    ArrayAdapter<CharSequence> registrationSprAdapter;

    public EquipmentSortFragment(){ };

    public static EquipmentSortFragment newInstance(){
        EquipmentSortFragment fragment = new EquipmentSortFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sort_spr,container,false);

        // 선택에 의해 필터링 될 스피너
        registrationSortSpr = (Spinner) view.findViewById(R.id.equipSort_spr_search);
        registrationSprAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.sort_item, android.R.layout.simple_spinner_item);
        registrationSprAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        registrationSortSpr.setAdapter(registrationSprAdapter);

        registrationSortSpr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = registrationSortSpr.getItemAtPosition(i).toString();
                ((RegistrationRecyclerview)getActivity()).sprFiltering(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
