package kr.ac.kpu.diyequipmentapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_google_map);

        rentalMapFirebaseFirestore = FirebaseFirestore.getInstance();
        getAddressArrayList = new ArrayList<String>();

        setTitle("DIY Rental GoogleMap");
        mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.fg_rentalGoogleMap);
        mapFrag.getMapAsync(this);
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
                                /*
                                markerOptions.title("사용자 메일 : " + queryDocumentSnapshot.get("userEmail").toString().trim() + "\n"
                                        + "장비 모델명 : " + queryDocumentSnapshot.get("modelName").toString().trim() + "\n"
                                        + "장비 정보  : " + queryDocumentSnapshot.get("modelInform").toString().trim() + "\n"
                                        + "대여 주소  : " + queryDocumentSnapshot.get("rentalAddress").toString().trim());

                                 */

                                markerOptions.title(queryDocumentSnapshot.get("rentalAddress").toString().trim());
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

    //구글맵 옵션 메뉴 기능 구현
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, 1, 0, "위성 지도");
        menu.add(0, 2, 0, "일반 지도");
        menu.add(0, 3, 0, "Rental Detail");
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Toast.makeText(RentalGoogleMap.this, marker.getTitle(), Toast.LENGTH_SHORT).show();

        AlertDialog.Builder dlg = new AlertDialog.Builder(RentalGoogleMap.this);
        dlg.setTitle("장비대여 상세페이지");
        dlg.setMessage("장비대여 상세페이지로 이동하시겠습니까?");
        dlg.setIcon(R.mipmap.ic_launcher);

        dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(RentalGoogleMap.this, "장비대여 상세페이지로 이동되었습니다!", Toast.LENGTH_SHORT).show();

                tempAddress = marker.getTitle().toString().trim();
                Log.d("click", tempAddress);

                rentalMapFirebaseFirestore.collection("DIY_Equipment_Rental")
                        .whereEqualTo("rentalAddress",tempAddress)
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
                finish();
            }
        });

        dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(RentalGoogleMap.this, "장비대여 상세페이지 이동 취소되었습니다!", Toast.LENGTH_SHORT).show();
            }
        });
        dlg.show();
        return true;
    }
}