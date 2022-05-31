package kr.ac.kpu.diyequipmentapplication.equipment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kr.ac.kpu.diyequipmentapplication.R;


//등록된 대여 장비 주소를 이용해 구글맵 마커 표기하는 액티비티 클래스 구현
public class RentalGoogleMap extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    GoogleMap gMap;             //구글맵 객체 참조하는 변수
    MapFragment mapFrag;        //구글맵 프레그먼트 객체 참조하는 변수
    Context context = this;     //RentalGoogleMap 참조하는 변수
    private LatLng rentalLatlng = null;
    private FirebaseFirestore rentalMapFirebaseFirestore = null;
    private ArrayList<String> getAddressArrayList = null;

    private FirebaseAuth mainFirebaseAuth;     //FirebaseAuth 참조 변수 선언
    private String tempAddress;

    Dialog rentalGoogleMapDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_google_map);

        rentalMapFirebaseFirestore = FirebaseFirestore.getInstance();
        getAddressArrayList = new ArrayList<String>();

        setTitle("DIY Rental GoogleMap");
        mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.fg_rentalGoogleMap);
        mapFrag.getMapAsync(this);


        rentalGoogleMapDialog = new Dialog(RentalGoogleMap.this);
        rentalGoogleMapDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rentalGoogleMapDialog.setContentView(R.layout.rental_google_map_window_item);
    }

    public void showRentalGoogleMapDialog(String tempAdd) {
        final String[] getImgaeUrl = new String[1];
        final String[] getEmail = new String[1];
        ImageView imgView = rentalGoogleMapDialog.findViewById(R.id.rentalGoogleMapWindow_imgView);
        TextView tvCatecory = rentalGoogleMapDialog.findViewById(R.id.rentalGoogleMapWindow_tv_category);
        TextView tvModelName = rentalGoogleMapDialog.findViewById(R.id.rentalGoogleMapWindow_tv_modelName);
        TextView tvUserName = rentalGoogleMapDialog.findViewById(R.id.rentalGoogleMapWindow_tv_userName);
        TextView tvRentalType = rentalGoogleMapDialog.findViewById(R.id.rentalGoogleMapWindow_tv_rentalType);
        TextView tvRentalDate = rentalGoogleMapDialog.findViewById(R.id.rentalGoogleMapWindow_tv_rentalDate);
        TextView tvRentalCost = rentalGoogleMapDialog.findViewById(R.id.rentalGoogleMapWindow_tv_rentalCost);


        rentalMapFirebaseFirestore.collection("DIY_Equipment_Rental")
                .whereEqualTo("rentalAddress",tempAdd)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                getImgaeUrl[0] = queryDocumentSnapshot.get("rentalImage").toString().trim();
                                Picasso.get().load(getImgaeUrl[0]).into(imgView);
                                getEmail[0] = queryDocumentSnapshot.get("userEmail").toString().trim();
                                tvCatecory.setText(queryDocumentSnapshot.get("modelCategory1").toString().trim()+",\n"+
                                        queryDocumentSnapshot.get("modelCategory2").toString().trim());
                                tvModelName.setText(queryDocumentSnapshot.get("modelName").toString().trim());
                                tvRentalType.setText(queryDocumentSnapshot.get("rentalType").toString().trim());
                                tvRentalDate.setText(queryDocumentSnapshot.get("rentalDate").toString().trim());
                                tvRentalCost.setText(queryDocumentSnapshot.get("rentalCost").toString().trim());
                                //tvUserName.setText(queryDocumentSnapshot.get("userEmail").toString().trim());

                                rentalMapFirebaseFirestore.collection("DIY_Signup")
                                        .whereEqualTo("userEmail",getEmail[0])
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                        tvUserName.setText(queryDocumentSnapshot.get("userName").toString().trim());
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });



        rentalGoogleMapDialog.getWindow().setGravity(Gravity.BOTTOM);
        rentalGoogleMapDialog.show();

        ImageButton imgBtnCancel = rentalGoogleMapDialog.findViewById(R.id.rentalGoogleMapWindow_imgBtn_cancel);
        imgBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rentalGoogleMapDialog.dismiss();
            }
        });

        Button btnRentalDetail = rentalGoogleMapDialog.findViewById(R.id.rentalGoogleMapWindow_btn_rentalDetail);
        btnRentalDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rentalMapFirebaseFirestore.collection("DIY_Equipment_Rental")
                        .whereEqualTo("rentalAddress",tempAdd)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        Intent intent = new Intent(RentalGoogleMap.this, EquipmentDetailActivity.class);
                                        intent.putExtra("ModelName", queryDocumentSnapshot.get("modelName").toString().trim());
                                        intent.putExtra("ModelInform", queryDocumentSnapshot.get("modelInform").toString().trim());
                                        intent.putExtra("RentalImage",queryDocumentSnapshot.get("rentalImage").toString().trim());
                                        intent.putExtra("RentalType", queryDocumentSnapshot.get("rentalType").toString().trim());
                                        intent.putExtra("RentalCost", queryDocumentSnapshot.get("rentalCost").toString().trim());
                                        intent.putExtra("RentalAddress", queryDocumentSnapshot.get("rentalAddress").toString().trim());
                                        intent.putExtra("UserEmail", queryDocumentSnapshot.get("userEmail").toString().trim());
                                        intent.putExtra("RentalDate", queryDocumentSnapshot.get("rentalDate").toString().trim());
                                        intent.putExtra("ModelCategory1",queryDocumentSnapshot.get("modelCategory1").toString().trim());
                                        intent.putExtra("ModelCategory2",queryDocumentSnapshot.get("modelCategory1").toString().trim());
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
            }
        });
    }

    //버튼 클릭시 처음 나오는 GoogleMap 화면
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;       //구글맵 객체 참조

        rentalMapFirebaseFirestore.collection("DIY_Equipment_Rental")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                getAddressArrayList.add(queryDocumentSnapshot.get("rentalAddress").toString().trim());
                                Location rentalLocation = addrToPoint(context, getAddressArrayList.get(i));
                                rentalLatlng = new LatLng(rentalLocation.getLatitude(), rentalLocation.getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(rentalLatlng);
                                markerOptions.title(queryDocumentSnapshot.get("rentalAddress").toString().trim());
                                Log.d("getID->", queryDocumentSnapshot.getId().toString().trim());
                                gMap.addMarker(markerOptions);
                                i++;
                            }
                        }
                    }
                });

        //final LatLng lastLatLng = new LatLng(38.300603, 126.262021);
        final LatLng lastLatLng = new LatLng(37.541, 126.986);
        googleMap.setOnMarkerClickListener(this);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //gMap.moveCamera(CameraUpdateFactory.newLatLng(rentalLatlng));
                gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                gMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng));
            }
        });
    }

    //입력 주소의 Lat, Long구하는 메소드
    public static Location addrToPoint(Context context, String getAddress) {
        Location location = new Location("");
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName(getAddress, 5);    //GoogleMap에 표기할 주소명 입력
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null) {
            for (int i = 0; i < addresses.size(); i++) {
                Address lating = addresses.get(i);
                location.setLatitude(lating.getLatitude());
                location.setLongitude(lating.getLongitude());
            }
        }
        return location;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        tempAddress = marker.getTitle().toString().trim();
        Log.d("click", tempAddress);

        showRentalGoogleMapDialog(tempAddress);
        return true;
    }
}