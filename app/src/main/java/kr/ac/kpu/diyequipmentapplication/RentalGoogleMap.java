package kr.ac.kpu.diyequipmentapplication;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import kr.ac.kpu.diyequipmentapplication.R;

//등록된 대여 장비 주소를 이용해 구글맵 마커 표기하는 액티비티 클래스 구현
public class RentalGoogleMap extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap gMap;             //구글맵 객체 참조하는 변수
    MapFragment mapFrag;        //구글맵 프레그먼트 객체 참조하는 변수
    Context context = this;     //RentalGoogleMap 참조하는 변수
    private String userRentalAddress;   //상세 페이지에 등록된 장비 대여 주소를 참조하는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_google_map);

        Intent intent = getIntent();    //상세 페이지에서 넘어온 데이터 참조
        userRentalAddress = intent.getStringExtra("GetRentalAddress");  //상세 페이지에서 넘어온 GetRentalAddress 참조

        setTitle("DIY Rental GoogleMap");
        mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.fg_rentalGoogleMap);
        mapFrag.getMapAsync(this);
    }

    //버튼 클릭시 처음 나오는 GoogleMap 화면
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;       //구글맵 객체 참조

        //상세 페이지에 넘어온 장비 대여 주소 데이터를 이용해 구글맵에 표기하는 기능 구현
        Location rentalLocation = addrToPoint(context, userRentalAddress);
        final LatLng rentalLatlng = new LatLng(rentalLocation.getLatitude(), rentalLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(rentalLatlng);
        markerOptions.title(userRentalAddress);
        gMap.addMarker(markerOptions);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                gMap.moveCamera(CameraUpdateFactory.newLatLng(rentalLatlng));
                gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
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

    //구글맵 옵션 메뉴 기능 구현
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0,1,0,"위성 지도");
        menu.add(0,2,0,"일반 지도");
        menu.add(0,3,0,"Rental Detail");
        //menu.add(0,3,0,"월드컵경기장 바로가기");
        //menu.add(0,4,0,"대여 장비 지도");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 1:     //위성 지도인 경우
                gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case 2:     //일반 지도인 경우
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case 3:     //구글맵 페이지에서 상세 페이지로 이동
                finish();
                return true;
        }
        return false;
    }
}