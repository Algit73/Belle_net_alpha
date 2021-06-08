package com.soluk.belle_net_alpha.selected_event;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.ui.route.NavigationMapRoute;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.main_fragments.map_fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Selected_Event_Map_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Selected_Event_Map_Fragment extends Fragment implements OnMapReadyCallback
{



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Selected_Event_Map_Fragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Selected_Event_Map_Fragment.
     */

    private MapView mapView;
    private MapboxMap mapboxMap;
    private SymbolManager symbol_manager;
    private NavigationOptions navigation_options;
    private MapboxNavigation mapbox_navigation;
    private LocationComponent location_component;
    private NavigationMapRoute navigation_mapRoute;


    // TODO: Rename and change types and number of parameters
    public static Selected_Event_Map_Fragment newInstance(String param1, String param2)
    {
        Selected_Event_Map_Fragment fragment = new Selected_Event_Map_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_selected_event_map, container, false);

        mapView = v.findViewById(R.id.mapview_event);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        return v;
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap)
    {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, style ->
        {
            mapbox_navigation_configuration(style);
            symbol_manager = new SymbolManager(mapView, mapboxMap, style);
            symbol_manager.setIconAllowOverlap(true);
        });

    }

    void mapbox_navigation_configuration(Style style)
    {
        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(getActivity());
        navigation_options = MapboxNavigation.defaultNavigationOptionsBuilder(getActivity(),
                getString(R.string.mapbox_access_token))
                .locationEngine(locationEngine)
                .build();
        mapbox_navigation = new MapboxNavigation(navigation_options);
        symbol_manager = new SymbolManager(mapView, mapboxMap, style);
        symbol_manager.setIconAllowOverlap(true);
        style.addImage("marker_guide_pin", BitmapFactory.decodeResource(getResources(),R.drawable.blue_marker));
        style.addImage("marker_start_flag", BitmapFactory.decodeResource(getResources(),R.drawable.start_64));
    }

    private void set_camera_position(int tilt, LatLng point)
    {
        CameraPosition position = null;
            position = new CameraPosition.Builder()
                    .target(point) // Sets the new camera position
                    .zoom(12) // Sets the zoom
                    //.bearing(degree) // Rotate the camera
                    .tilt(tilt) // Set the camera tilt
                    .build();

        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 2000);
    }
}