package com.cr4zyrocket.foodappclient.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cr4zyrocket.foodappclient.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyLocationFragment extends Fragment implements OnMapReadyCallback {
    View view;
    GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_my_location,container,false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapMyLocation);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Add a marker in Sydney and move the camera
        mMap = googleMap;
        LatLng myLocation = new LatLng(21.028395, 105.803549);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("Trường đại học Giao Thông Vận Tải").snippet("Địa chỉ: Số 3 phố Cầu Giấy - phường Láng Thượng - quận Đống Đa - TP.Hà Nội").icon(BitmapDescriptorFactory.defaultMarker()));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(15).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
