package kr.ac.kpu.diyequipmentapplication;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//현재 작업중인 액티비티 클래스입니다.
//등록된 전체 장비 대여 주소를 구글맵에 마커로 표기하는 클래스 구현
public class AllRentalGoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    ArrayList<EquipmentRegistration> equipmentRegistrationArrayList;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseStorage mStorage;
    GoogleMap gMap;
    MapFragment mapFrag;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_rental_google_map);


        mDatabase = FirebaseDatabase.getInstance();     //Firebase RealTime DB root 객체 참조
        mRef = mDatabase.getReference().child("DIY_Equipment_Rental");  //Firebase RealTime DB의 root객체의 자식인 DIY_Equipment_Rental 객체 참조
        mStorage = FirebaseStorage.getInstance();       //Firebase Storage 객체 참조
        equipmentRegistrationArrayList = new ArrayList<EquipmentRegistration>();     //ArrayList 객체 생성 및 초기화

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                EquipmentRegistration equipmentRegistration = snapshot.getValue(EquipmentRegistration.class);
                equipmentRegistrationArrayList.add(equipmentRegistration);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setTitle("DIY ALL Rental GoogleMap");
        mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.fg_allRentalGoogleMap);
        mapFrag.getMapAsync(this);
    }

    //버튼 클릭시 처음 나오는 GoogleMap 화면
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        /*
        Location rentalLocation = addrToPoint(context, userRentalAddress);
        final LatLng rentalLatlng = new LatLng(rentalLocation.getLatitude(), rentalLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(rentalLatlng);
        markerOptions.title(userRentalAddress);
        gMap.addMarker(markerOptions);

         */

        //Firebase RealTime DB에 저장된 RentalAddress 주소 모두 읽기
        for (int i = 0; i < equipmentRegistrationArrayList.size(); i++)
        {
            Location location = addrToPoint(context, equipmentRegistrationArrayList.get(i).getRentalAddress());
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions2 = new MarkerOptions();
            markerOptions2.position(latLng);
            gMap.addMarker(markerOptions2);
        }

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLng latLng = new LatLng(addrToPoint(context, "서울특별시").getLatitude(), addrToPoint(context, "경기도").getLongitude());
                gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                gMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                //gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);   //Google Map 위성지도
            }
        });



    }

    //입력 주소의 Lat, Long구하는 메소드
    public static Location addrToPoint(Context context, String getAddress) {
        Location location = new Location("");
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName(getAddress, 3);    //GoogleMap에 표기할 주소명 입력
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0,1,0,"위성 지도");
        menu.add(0,2,0,"일반 지도");
        menu.add(0,3,0,"이전 페이지");
        //menu.add(0,3,0,"월드컵경기장 바로가기");
        //menu.add(0,4,0,"대여 장비 지도");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case 2:
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case 3:
                //gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.568256, 126.897240), 15)); //월드컵경기장 위치

                //Intent intent = new Intent(RentalGoogleMap.this, EquipmentDetailActivity.class);
                //startActivity(intent);
                finish();
                return true;
        }
        return false;
    }
}