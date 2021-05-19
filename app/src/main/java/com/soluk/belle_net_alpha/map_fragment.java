package com.soluk.belle_net_alpha;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.navigation.base.internal.route.RouteUrl;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.directions.session.RoutesRequestCallback;
import com.mapbox.navigation.ui.route.NavigationMapRoute;
import com.soluk.belle_net_alpha.event_data_maker.file_maker;
import com.soluk.belle_net_alpha.model.event_db_vm;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.os.Looper.getMainLooper;
import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link map_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class map_fragment extends Fragment implements
        OnMapReadyCallback, MapboxMap.OnMapClickListener,MapboxMap.OnMapLongClickListener
{

    private static final String TAG = map_fragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String MARKER_LAYER_ID = "MARKER_LAYER_ID";
    private static final String PROPERTY_SELECTED = "selected";
    private static final String GEOJSON_SOURCE_ID = "GEOJSON_SOURCE_ID";
    private static final String MARKER_IMAGE_ID = "MARKER_IMAGE_ID";
    private static final String CALLOUT_LAYER_ID = "CALLOUT_LAYER_ID";

    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_DATE_CREATED = "date_created";
    private static final String PROPERTY_EVENT_DATE = "event_date";
    private static final String PROPERTY_EVENT_ID = "event_id";
    private static final String PROPERTY_IS_USER_JOINED = "is_user_joined";
    private static final String PROPERTY_PIC = "profile_pic";
    private static final String PROPERTY_NUM_OF_JOINED = "count";

    private static final String USER_NAME = "Alireza";
    private static final String USER_FAMILY = "Alikhani";
    private static final String USER_PIC = "#loncle";
    private static final String USER_ID = "#ahdx98!s5kjxsp";
    private static final String USER_PASSWORD = "alirezalovesbellenet";

    private static String file_directory_static = "";
    private static final String FILE_NAME = "geo_json_bellenet";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FeatureCollection featureCollection;
    private int number_of_selected_cards;
    private String  last_event_selected;
    private boolean join_status = false;
    private GeoJsonSource source;
    private event_db_vm db_model;

    Button join_btn;
    Button cancel_join_event_btn;
    TextView hint_user_tv;
    TextView selected_user_name;
    CircleImageView selected_user_profile_image;
    TextView selected_event_date;
    TextView selected_event_type;
    TextView selected_event_status;


    /// Mapbox Variables
    private MapView mapView;
    private MapboxMap mapboxMap;
    private SymbolManager symbol_manager;
    private NavigationOptions navigation_options;
    private MapboxNavigation mapbox_navigation;
    private LocationComponent location_component;
    private NavigationMapRoute navigation_mapRoute;

    private BottomSheetBehavior bottom_sheet_click_join;
    private BottomSheetBehavior bottom_sheet_Choose_challenge;
    private FloatingActionButton set_cam_on_location_fab;
    private SpeedDialView pin_event_sd;

    private boolean add_postition_mode = false;
    private boolean is_marker_added = false;
    private Symbol user_marker_pinned;

    private TextView event_date_tv;
    private TextView event_time_tv;
    private TextView hint_1;
    private TextView hint_2;

    private final Calendar calendar_time_picker = Calendar.getInstance();
    private ImageButton add_marker_on_map_ib;
    private ImageButton accept_marker_on_map_ib;
    private ImageButton reject_marker_on_map_ib;

    private ImageView loading_screen;
    private ProgressBar loading_wheel;


    private final int EVENT_TYPE_ENSEMBLE = 0;
    private final int EVENT_TYPE_CHALLENGE = 1;
    private final int EVENT_TYPE_EXPERIENCE = 2;
    private int event_type;

    private List<Point> list_of_added_points;
    private List<Symbol> list_of_of_added_points_symbol;

    public map_fragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment maps_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static map_fragment newInstance(String param1, String param2)
    {
        map_fragment fragment = new map_fragment();
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
        /*
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

         */

        file_directory_static = getActivity().getFilesDir().toString();
        Log.d(TAG,"map Fragment dir: "+file_directory_static);
        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
        db_model = new ViewModelProvider(getActivity()).get(event_db_vm.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        loading_screen = v.findViewById(R.id.loading_screen);
        loading_wheel = v.findViewById(R.id.loading_wheel);


        //model.set_db_handler(()->{Log.d("main_activity","Finally There Fragment");});


        mapView = v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        View bottom_sheet_click_join_view = v.findViewById(R.id.bottom_sheet_click_to_join);
        bottom_sheet_click_join = BottomSheetBehavior.from(bottom_sheet_click_join_view);
        bottom_sheet_click_join.setState(BottomSheetBehavior.STATE_HIDDEN);



        /// Configuring BottomSheet_Choose_Challenge in the BelleNet
        View bottom_sheet_challenges_veiw = v.findViewById(R.id.bottom_sheet_challenges);
        bottom_sheet_Choose_challenge = BottomSheetBehavior.from(bottom_sheet_challenges_veiw);
        bottom_sheet_Choose_challenge.setState(BottomSheetBehavior.STATE_HIDDEN);

        /// Set camera on the user location
        set_cam_on_location_fab = v.findViewById(R.id.fab_pin_on_maps);
        set_cam_on_location_fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                LatLng current_position = new LatLng(location_component.getLastKnownLocation().getLatitude()
                        ,location_component.getLastKnownLocation().getLongitude()) ;
                set_camera_position(0,current_position);

            }
        });

        hint_1 = v.findViewById(R.id.marker_explanation_text);
        hint_2 = v.findViewById(R.id.tap_to_add_marker_text);
        button_sheet_add_event_init(v);
        bottom_sheet_join_event_init(v);
        pin_event_sd_init(v);


        return v;
    }

    void button_sheet_add_event_init(View v)
    {

        // Configuring Buttons in the BottomSheet of BelleNet
        Button save_challenge_btn = v.findViewById(R.id.save_challenge);
        Button cancel_challenge_btn = v.findViewById(R.id.cancel_challenge);

        cancel_challenge_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                is_marker_added = false;
                add_postition_mode = false;
                change_add_marker_on_map_ib_mode(true);
                remove_points_routes();
                bottom_sheet_Choose_challenge.setState(BottomSheetBehavior.STATE_HIDDEN);
                ((main_activity)getActivity()).bottom_navigation_visibility(true);
            }
        });

        save_challenge_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(list_of_added_points==null)
                {
                    Toast.makeText(getActivity(),
                            "Please add your spots on the map",Toast.LENGTH_SHORT).show();
                    return;
                }

                String date_value = event_date_tv.getText().toString();

                if(!date_value.matches(".*\\d.*"))
                {
                    Toast.makeText(getActivity(),
                            "Please input the event date",Toast.LENGTH_SHORT).show();
                    return;
                }

                String time_value = event_time_tv.getText().toString();

                if(!time_value.matches(".*\\d.*"))
                {
                    Toast.makeText(getActivity(),
                            "Please input the event time",Toast.LENGTH_SHORT).show();
                    return;
                }

                send_user_created_event();
                ((main_activity)getActivity()).bottom_navigation_visibility(true);
                bottom_sheet_Choose_challenge.setState(BottomSheetBehavior.STATE_HIDDEN);
                remove_points_routes();
                change_add_marker_on_map_ib_mode(true);
            }
        });

        /// Configuring DatePicker
        event_date_tv = v.findViewById(R.id.input_date_tv);
        event_date_tv.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                Date_Time_Provider date_provider = new Date_Time_Provider(getActivity())
                        .set_date_tv(event_date_tv)
                        .set_date_format(Date_Time_Provider.US);

                date_provider.show_date_dialog();
            }
        });

        /// Configuring TimePicker
        event_time_tv = v.findViewById(R.id.input_time_tv);

        event_time_tv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Date_Time_Provider time_provider = new Date_Time_Provider(getActivity())
                        .set_time_tv(event_time_tv)
                        .set_time_format(Date_Time_Provider.H24);

                time_provider.show_time_dialog();
            }
        });

        /// Configuring pinning on map
        add_marker_on_map_ib = v.findViewById(R.id.add_marker_ib);
        accept_marker_on_map_ib = v.findViewById(R.id.accept_pinned_markers_ib);
        reject_marker_on_map_ib = v.findViewById(R.id.reject_pinned_markers_ib);

        // Configuring Add Marker: 1- Adding points and route 2- Removing points and route
        add_marker_on_map_ib.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Check if any point has been added previously: null if not
                if(list_of_added_points!=null)
                {
                    remove_points_routes();
                    change_add_marker_on_map_ib_mode(true);
                }
                else
                {
                    bottom_sheet_Choose_challenge.setState(BottomSheetBehavior.STATE_HIDDEN);

                    accept_marker_on_map_ib.setVisibility(View.VISIBLE);
                    reject_marker_on_map_ib.setVisibility(View.VISIBLE);

                    set_cam_on_location_fab.setVisibility(View.INVISIBLE);
                    pin_event_sd.setVisibility(View.INVISIBLE);

                    add_postition_mode = true;
                }

            }
        });

        // Configuring accept button: accepts the points if user choses at least one
        accept_marker_on_map_ib.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /// Check if any point has been added, if not toasts a message
                if(list_of_of_added_points_symbol!=null)
                {
                    // Only "Share Experience" accepts one point
                    //if(event_type!=event_types.EVENT_TYPE_ENSEMBLE
                    if(event_type!=EVENT_TYPE_ENSEMBLE
                            ||list_of_of_added_points_symbol.size()>1)
                    {
                        open_bottom_sheet_add_event();
                        ////change_add_marker_on_map_ib_mode(false);
                        change_add_marker_on_map_ib_mode(false);
                    }
                    else
                        Toast.makeText(getActivity(),R.string.user_has_not_pinned_destination,
                                Toast.LENGTH_LONG).show();

                }
                else
                {
                    Toast.makeText(getActivity(),R.string.user_has_not_pinned_location,
                            Toast.LENGTH_LONG).show();
                }


            }
        });

        // Configuring reject button: removes points and route, empties the list of points
        reject_marker_on_map_ib.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                open_bottom_sheet_add_event();
                remove_points_routes();
                change_add_marker_on_map_ib_mode(true);
            }
        });
    }

    void send_user_created_event()
    {
        wait_on_loading_data(false);

        Date current_date = Calendar.getInstance().getTime();
        SimpleDateFormat standard_date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);    // Date Styles may should change to the getDefault()
        SimpleDateFormat time_24_format = new SimpleDateFormat("H:mm", Locale.US);

        //symbol_manager.delete(user_marker_pinned);
        Map<String, String> params = new HashMap<>();
        params.put("user_name",USER_NAME);
        params.put("user_family",USER_FAMILY);
        params.put("user_id",USER_ID);
        params.put("user_password",USER_PASSWORD);
        params.put("num_points",String.valueOf(list_of_added_points.size()));
        params.put("event_type",String.valueOf(event_type));
        params.put("date_created",standard_date_format.format(current_date));
        params.put("date_of_event",standard_date_format.format(current_date));
        params.put("event_time",time_24_format.format(calendar_time_picker.getTime()));
        params.put("user_type",String.valueOf(1));
        params.put("user_picture",USER_PIC);

        JSONObject json = new JSONObject();
        try
        {

            json.put("user_name",USER_NAME);
            json.put("user_family",USER_FAMILY);
            json.put("user_id",USER_ID);
            json.put("user_password",USER_PASSWORD);
            json.put("num_points",String.valueOf(list_of_added_points.size()));
            json.put("event_type",String.valueOf(event_type));
            json.put("date_created",standard_date_format.format(current_date));
            json.put("date_of_event",standard_date_format.format(current_date));
            json.put("event_time",time_24_format.format(calendar_time_picker.getTime()));
            json.put("user_type",String.valueOf(1));
            json.put("user_picture",USER_PIC);


            for(int i=0;i<list_of_added_points.size();i++)
            {
                String longitude_x = "longitude_"+ i;
                String latitude_x = "latitude_"+ i;
                params.put(longitude_x,String.valueOf(list_of_added_points.get(i).longitude()));
                params.put(latitude_x,String.valueOf(list_of_added_points.get(i).latitude()));
                json.put(longitude_x,String.valueOf(list_of_added_points.get(i).longitude()));
                json.put(latitude_x,String.valueOf(list_of_added_points.get(i).latitude()));
            }
        }
        catch (Exception e) {}

        Callback callback = new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {

                Log.d(TAG,"Post Test Code: "+response.code());
                Log.d(TAG,"Post Test Body: "+response.body().string());
                db_model.refresh_db();
            }
        };

        HTTP_Provider.post_json("belle_net_users_info/user_register_new_event.php",json,callback);

    }

    /// Sends Join, Opt out and Remove commands
    //void send_join_command(Map<String, String> params, Feature feature, JSONObject json)
    void send_join_command(Feature feature, JSONObject json)
    {
        //Log.d(TAG,"Send join: "+params);
        Log.d(TAG,"Send join JSON: "+json);
        Callback callback = new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {

                Log.d(TAG, "Post Body: "+response.body().string());
                send_join_command_response(feature);

            }
        };
        wait_on_loading_data(false);

        HTTP_Provider.post_json("belle_net_users_info/send_join_event_status.php",json,callback);
        db_model.refresh_db();
        remove_points_routes();
    }

    void send_join_command_response(Feature feature)
    {
        number_of_selected_cards--;

        Handler mainHandler = new Handler(getMainLooper());
        Runnable myRunnable = () ->
        {
            setFeatureSelectState(feature, false);
            if(number_of_selected_cards==0)
                set_camera_position(0,null);
            ((main_activity)getActivity()).bottom_navigation_visibility(true);
            bottom_sheet_click_join.setState(BottomSheetBehavior.STATE_HIDDEN);
        };
        mainHandler.post(myRunnable);
    }

    /// Opens bottom_sheet_add_event
    void open_bottom_sheet_add_event()
    {
        bottom_sheet_Choose_challenge.setState(BottomSheetBehavior.STATE_EXPANDED);
        accept_marker_on_map_ib.setVisibility(View.INVISIBLE);
        reject_marker_on_map_ib.setVisibility(View.INVISIBLE);

        set_cam_on_location_fab.setVisibility(View.VISIBLE);
        pin_event_sd.setVisibility(View.VISIBLE);
        add_postition_mode = false;
    }

    /// Removing route and pinned points on the map on demand
    void remove_points_routes()
    {
        if(list_of_of_added_points_symbol!=null)
        {
            symbol_manager.delete(list_of_of_added_points_symbol);
            list_of_of_added_points_symbol = null;
            list_of_added_points = null;

            navigation_mapRoute.updateRouteVisibilityTo(false);
        }

    }

    /// Initializing Pin_event_Speed_Dial
    void pin_event_sd_init(View v)
    {
        pin_event_sd = v.findViewById(R.id.pin_event_sd);
        pin_event_sd.addActionItem(new SpeedDialActionItem.Builder(R.id.sd_ensemble_cycling,
                R.drawable.bike)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources()
                        ,R.color.teal_500,getActivity().getTheme()))
                .setFabImageTintColor(Color.WHITE)
                .setLabelBackgroundColor(Color.WHITE)
                .setLabel("Ensemble Cycling")
                .create());
        pin_event_sd.addActionItem(new SpeedDialActionItem.Builder(R.id.sd_offer_challenge,
                R.drawable.map)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources()
                        ,R.color.light_blue_500,getActivity().getTheme()))
                .setFabImageTintColor(Color.WHITE)
                .setLabelBackgroundColor(Color.WHITE)
                .setLabel("Offer a Challenge")
                .create());
        pin_event_sd.addActionItem(new SpeedDialActionItem.Builder(R.id.sd_share_experience,
                R.drawable.image)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources()
                        ,R.color.blue_500,getActivity().getTheme()))
                .setFabImageTintColor(Color.WHITE)
                .setLabelBackgroundColor(Color.WHITE)
                .setLabel("Share Experience")
                .create());

        pin_event_sd.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener()
        {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem)
            {
                ((main_activity)getActivity()).bottom_navigation_visibility(false);
                switch (actionItem.getId())
                {
                    case R.id.sd_ensemble_cycling:
                        bottom_sheet_Choose_challenge.setState(BottomSheetBehavior.STATE_EXPANDED);
                        //event_type = event_types.EVENT_TYPE_ENSEMBLE;
                        event_type = EVENT_TYPE_ENSEMBLE;
                        break;
                    case R.id.sd_offer_challenge:  break;
                    case R.id.sd_share_experience:  break;
                }
                return false;
            }
        });
    }

    void change_add_marker_on_map_ib_mode (boolean add_mode)
    {
        //hint_1 = v.findViewById(R.id.marker_explanation_text);
        //hint_2 = v.findViewById(R.id.tap_to_add_marker_text);

        if(add_mode)
        {
            add_marker_on_map_ib.setImageResource(R.drawable.plus);
            add_marker_on_map_ib.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources()
                    ,R.color.teal_200,getActivity().getTheme()));

            hint_1.setText(R.string.hint_user_to_mark_spots_on_map);
            hint_2.setText(R.string.hint_user_to_tap_on_add_icon);
        }
        else
        {
            add_marker_on_map_ib.setImageResource(R.drawable.close);
            add_marker_on_map_ib.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources()
                    , R.color.red_500, getActivity().getTheme()));

            hint_1.setText(R.string.hint_user_the_marks_added);
            hint_2.setText(R.string.hint_user_to_remove_the_marks);
        }
    }

    private void getRoute(List<Point> list)
    {

        Log.d(TAG, "Get Route");
        //navigation_mapRoute.updateRouteVisibilityTo(false);
        mapbox_navigation.requestRoutes(RouteOptions.builder()
                .baseUrl("https://api.mapbox.com")
                .user("mapbox")
                .requestUuid("ck7dtdd2z00yx75plynvtan26")
                .accessToken(getString(R.string.mapbox_access_token))
                .coordinates(list)
                .geometries(RouteUrl.GEOMETRY_POLYLINE6)
                .voiceUnits(RouteUrl.METRIC)
                .profile(RouteUrl.PROFILE_DRIVING)
                .build(), new RoutesRequestCallback()
        {
            @Override
            public void onRoutesReady(@NotNull List<? extends DirectionsRoute> list)
            {
                Log.d("TAG","Received");
                DirectionsRoute currentRoute;
                currentRoute = list.get(0);

                if (navigation_mapRoute == null)
                {
                    navigation_mapRoute = new NavigationMapRoute.Builder(mapView, mapboxMap, getActivity())
                            .withVanishRouteLineEnabled(true)
                            .withMapboxNavigation(mapbox_navigation)
                            .build();
                }
                navigation_mapRoute.addRoute(currentRoute);


            }

            @Override
            public void onRoutesRequestFailure(@NotNull Throwable throwable, @NotNull RouteOptions routeOptions)
            {
                Log.d("TAG","Failure");
            }

            @Override
            public void onRoutesRequestCanceled(@NotNull RouteOptions routeOptions)
            {
                Log.d("TAG","Canceled");
            }
        });

    }

    private void bottom_sheet_join_event_init(View v)
    {
        join_btn = v.findViewById(R.id.join_event);
        cancel_join_event_btn = v.findViewById(R.id.cancel_join_event);
        hint_user_tv = v.findViewById(R.id.selected_event_user_join_hint);
        selected_user_name = v.findViewById(R.id.selected_user_name);
        selected_user_profile_image = v.findViewById(R.id.selected_user_profile_image);
        selected_event_date = v.findViewById(R.id.selected_event_date);
        selected_event_type = v.findViewById(R.id.selected_event_type);
        selected_event_status = v.findViewById(R.id.selected_event_user_join_status);
    }

    /// Each time a pin selected, the appropriate bottom_sheet appears
    private void update_bottom_sheet_click(Feature feature) throws JSONException
    {
        JSONObject info = new JSONObject(feature.properties().toString());
        ((main_activity)getActivity()).bottom_navigation_visibility(false);
        bottom_sheet_click_join.setState(BottomSheetBehavior.STATE_EXPANDED);

        List<Point> list_received_points  = null;
        list_received_points = new ArrayList<>();

        int num_points = Integer.parseInt(info.get("num_points").toString());

        for(int i=0; i<num_points;i++)
        {
            Point point = Point.fromLngLat(Double.parseDouble(info.get("longitude_"+i).toString())
                    ,Double.parseDouble(info.get("latitude_"+i).toString()));
            list_received_points.add(point);
        }
        if(num_points>1)
            getRoute(list_received_points);



        /// Setting name of the user
        String name_family = (String) (info.get("name")+ " " + info.get("family"));
        selected_user_name.setText(name_family);

        /// Setting the profile image of the selected_user
        Bitmap selected_profile_image_bmp = get_profile_bmp(info.get("profile_pic").toString().substring(1));
        if(selected_profile_image_bmp!=null)
            selected_user_profile_image.setImageBitmap(selected_profile_image_bmp);

        /// Setting event date
        selected_event_date.setText(date_reformat(info.get("event_date").toString()));

        /// Setting event type

        String event_type = info.get("event_type").toString();
        if(event_type.equals("1"))
            selected_event_type.setText("Ensemble Riding");
        else if(event_type.equals("2"))
            selected_event_type.setText("Offering Challenge");
        if(event_type.equals("3"))
            selected_event_type.setText("Sharing Experience");



        boolean user_owns_event = info.get("user_id").toString().equals(USER_ID);
        if(!user_owns_event)
        {
            join_btn.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources()
                    ,R.color.save_button,getActivity().getTheme()));
            if (info.get("is_user_joined").toString().equals("true"))
            {
                selected_event_status.setText("You have joined this event");
                hint_user_tv.setText("*Click opt out if you don't want to participate");
                join_btn.setText("Opt out");
                join_status = true;

            }
            else
            {
                selected_event_status.setText("You have not joined this event");
                hint_user_tv.setText("*Click join to join this event");
                join_btn.setText("Join");
                join_status = false;
            }
        }
        else
        {
            selected_event_status.setText("You own this event");
            hint_user_tv.setText("*Click remove to revoke the event");
            join_btn.setText("Remove");
            join_btn.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources()
                    ,R.color.red_500,getActivity().getTheme()));
        }

        join_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Date current_date = Calendar.getInstance().getTime();
                SimpleDateFormat standard_date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                Log.d(TAG, "Button Pressed");
                //Map<String, String> params = new HashMap<>();
                JSONObject json = new JSONObject();

                try
                {   json.put("joined_user_id",USER_ID);
                    json.put("event_unique_id",info.get("event_id").toString());}
                catch (Exception e) {Log.d(TAG, "event_unique_id catch " + e);}



                if(!user_owns_event)
                {
                    if (join_status)
                    {
                        //params.put("request", "out");
                        try {json.put("request","out");}
                        catch (Exception ignored) {}
                    }
                    else
                    {

                        try {
                            json.put("request","join");
                            json.put("user_date_of_join",standard_date_format.format(current_date));}
                        catch (Exception ignored) {}
                    }
                    send_join_command(feature,json);
                }
                else
                {
                    try {json.put("request","remove");}
                    catch (Exception ignored) {}
                    AlertDialog.Builder alert_dialog_bl = new AlertDialog.Builder(getActivity());
                    alert_dialog_bl.setMessage("Do you really want to remove this event?")
                            .setCancelable(true)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener()
                            {public void onClick(DialogInterface dialog, int id) {send_join_command(feature,json);}})
                            .setNegativeButton("CANCEL",new DialogInterface.OnClickListener()
                            {public void onClick(DialogInterface dialog, int id) {dialog.cancel();}});


                    AlertDialog alertDialog = alert_dialog_bl.create();

                    alertDialog.show();
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ResourcesCompat.getColor(getResources()
                            ,R.color.gray_800,getActivity().getTheme()));
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ResourcesCompat.getColor(getResources()
                            ,R.color.gray_800,getActivity().getTheme()));

                }

            }
        });

        /// Closing the bottom_sheet_click
        cancel_join_event_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                number_of_selected_cards--;
                ((main_activity)getActivity()).bottom_navigation_visibility(true);
                bottom_sheet_click_join.setState(BottomSheetBehavior.STATE_HIDDEN);
                if(number_of_selected_cards==0)
                    set_camera_position(0,null);


                setFeatureSelectState(feature, false);

                //symbol_manager.delete(li);
                navigation_mapRoute.updateRouteVisibilityTo(false);
            }
        });


        Log.d(TAG,"Feature selected data: "+info.toString());

    }

    private String date_reformat(String time)
    {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MMMM,dd,yy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }


    private Bitmap get_profile_bmp(String name)
    {
        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir("Profile_Pictures", Context.MODE_PRIVATE);
        try
        {
            File file =new File(directory, name);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            return bitmap;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    void wait_on_loading_data(boolean is_loaded)
    {
        if(is_loaded)
        {
            loading_wheel.setVisibility(View.GONE);
            loading_screen.setVisibility(View.GONE);
        }
        else
        {
            loading_wheel.setVisibility(View.VISIBLE);
            loading_screen.setVisibility(View.VISIBLE);
        }
    }


    public void update_map()
    {
        //Log.d(TAG,"Fragment Called");


        new Load_GeoJson_Data_Task(map_fragment.this).execute();
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded()
        {
            @Override
            public void onStyleLoaded(@NonNull Style style)
            {

                //mapboxMap.addOnMapClickListener(main_activity.this);
                //mapboxMap.addOnMapLongClickListener(main_activity.this);
                symbol_manager = new SymbolManager(mapView, mapboxMap, style);
                symbol_manager.setIconAllowOverlap(true);
                style.addImage("marker_guide_pin", BitmapFactory.decodeResource(getResources(),R.drawable.blue_marker));
                style.addImage("marker_start_flag", BitmapFactory.decodeResource(getResources(),R.drawable.start_64));
                if (navigation_mapRoute!=null)
                    navigation_mapRoute.updateRouteVisibilityTo(false);
                wait_on_loading_data(true);


            }
        });


    }


    /// Configuring Map Interactions

    @Override
    public boolean onMapClick(@NonNull LatLng point)
    {
        return handleClickIcon(mapboxMap.getProjection().toScreenLocation(point));
    }

    @Override
    public boolean onMapLongClick(@NonNull LatLng point)
    {
        Log.d(TAG, "onMapLongClick: Entered");

        if (add_postition_mode)  // Prohibit adding marker when the mode was not initiated
        {
            if(list_of_of_added_points_symbol==null)
                list_of_of_added_points_symbol = new ArrayList<>();

            if (event_type != EVENT_TYPE_ENSEMBLE)
            {
                if (!is_marker_added)   // Check if any marker has been added by the user
                {
                    //selected_postion = point;
                    user_marker_pinned = symbol_manager.create(new SymbolOptions()
                            .withLatLng(new LatLng(point.getLatitude(), point.getLongitude()))
                            .withIconImage("marker_start_flag")
                            .withTextAnchor(Property.TEXT_ANCHOR_BOTTOM)
                            .withIconSize(1.0f));

                    is_marker_added = true;
                    //add_postition_mode = false;
                }
                else
                {
                    symbol_manager.delete(user_marker_pinned);
                    //selected_postion = point;
                    user_marker_pinned = symbol_manager.create(new SymbolOptions()
                            .withLatLng(new LatLng(point.getLatitude(), point.getLongitude()))
                            .withIconImage("marker_start_flag")
                            .withTextAnchor(Property.TEXT_ANCHOR_BOTTOM)
                            .withIconSize(1.0f));
                }
            }

            else
            {
                Log.d(TAG, "Ensemble Riding: Entered");
                if (list_of_added_points == null)
                {
                    list_of_added_points = new ArrayList<>();
                    user_marker_pinned = symbol_manager.create(new SymbolOptions()
                            .withLatLng(new LatLng(point.getLatitude(), point.getLongitude()))
                            .withIconImage("marker_start_flag")
                            .withTextAnchor(Property.TEXT_ANCHOR_BOTTOM)
                            .withIconSize(1.0f));
                }
                else
                {
                    user_marker_pinned = symbol_manager.create(new SymbolOptions()
                            .withLatLng(new LatLng(point.getLatitude(), point.getLongitude()))
                            .withIconImage("marker_guide_pin")
                            .withTextAnchor(Property.TEXT_ANCHOR_BOTTOM)
                            .withIconSize(1.0f));
                }

                list_of_of_added_points_symbol.add(user_marker_pinned);

                Point marked_point = Point.fromLngLat(point.getLongitude(), point.getLatitude());
                list_of_added_points.add(marked_point);
                Log.d(TAG, "list Size: "+list_of_added_points.size());
                if(list_of_added_points.size()>1)
                    getRoute(list_of_added_points);

            }
        }
        return false;
    }


    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap)
    {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded()
        {
            @Override
            public void onStyleLoaded(@NonNull Style style)
            {
                //if(data_received_correctly)
                  //  new main_activity.LoadGeoJsonDataTask(main_activity.this).execute();
                mapboxMap.addOnMapClickListener(map_fragment.this);
                mapboxMap.addOnMapLongClickListener(map_fragment.this);
                enable_location_component(style);
                mapbox_navigation_configuration(style);
                symbol_manager = new SymbolManager(mapView, mapboxMap, style);
                symbol_manager.setIconAllowOverlap(true);
            }
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
        //mapbox_navigation.registerRouteProgressObserver(main_activity.this);
        mapboxMap.addOnMapClickListener(map_fragment.this);
        symbol_manager = new SymbolManager(mapView, mapboxMap, style);
        symbol_manager.setIconAllowOverlap(true);
        style.addImage("marker_guide_pin", BitmapFactory.decodeResource(getResources(),R.drawable.blue_marker));
        style.addImage("marker_start_flag", BitmapFactory.decodeResource(getResources(),R.drawable.start_64));
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enable_location_component(@NonNull Style loadedMapStyle)
    {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getActivity()))
        {

            LocationComponentOptions locationComponentOptions =
                    LocationComponentOptions.builder(getActivity())
                            .pulseEnabled(true)
                            .pulseColor(Color.CYAN)
                            .pulseAlpha(.4f)
                            .pulseInterpolator(new BounceInterpolator())
                            .build();

            LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
                    .builder(getActivity(), loadedMapStyle)
                    .locationComponentOptions(locationComponentOptions)
                    .build();

            location_component = mapboxMap.getLocationComponent();
            location_component.activateLocationComponent(locationComponentActivationOptions);
            location_component.setLocationComponentEnabled(true);
            location_component.setCameraMode(CameraMode.TRACKING);
            location_component.setRenderMode(RenderMode.COMPASS);
        }
        else
        {
            //permissionsManager = new PermissionsManager(this);
            //permissionsManager.requestLocationPermissions(this);
        }
    }

    private void set_camera_position(int tilt,LatLng point)
    {
        CameraPosition position = null;
        if (point != null)
            position = new CameraPosition.Builder()
                    .target(point) // Sets the new camera position
                    //.zoom(12) // Sets the zoom
                    //.bearing(degree) // Rotate the camera
                    .tilt(tilt) // Set the camera tilt
                    .build();
        else
            position = new CameraPosition.Builder()
                    .tilt(tilt) // Set the camera tilt
                    .build();

        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 2000);
    }

    private boolean handleClickIcon(PointF screenPoint)
    {
        List<Feature> features = mapboxMap.queryRenderedFeatures(screenPoint, MARKER_LAYER_ID);
        if (!features.isEmpty())
        {
            String event_id = features.get(0).getStringProperty(PROPERTY_EVENT_ID);
            Log.v(TAG,"Feature Name: "+event_id);
            List<Feature> featureList = featureCollection.features();
            if (featureList != null)
            {
                Log.v(TAG,"Feature Size: "+featureList.size());
                for (int i = 0; i < featureList.size(); i++)
                {
                    if (featureList.get(i).getStringProperty(PROPERTY_EVENT_ID).equals(event_id))
                    {
                        Log.v(TAG,"featureList.get(i): "+featureList.get(i));
                        Log.d(TAG,"number_of_selected_cards: "+number_of_selected_cards);

                        if (featureSelectStatus(i))
                        {

                            if(featureList.get(i).getStringProperty(PROPERTY_EVENT_ID).equals(last_event_selected))
                            {
                                number_of_selected_cards--;
                                if(number_of_selected_cards==0)
                                {
                                    set_camera_position(0, null);
                                    ((main_activity)getActivity()).bottom_navigation_visibility(true);
                                    bottom_sheet_click_join.setState(BottomSheetBehavior.STATE_HIDDEN);
                                }


                                setFeatureSelectState(featureList.get(i), false);
                                Log.v(TAG,"Item Deselected");
                            }
                            else
                            {
                                last_event_selected = featureList.get(i).getStringProperty(PROPERTY_EVENT_ID);
                                try {update_bottom_sheet_click(featureList.get(i));}
                                catch (Exception e){Log.d(TAG,"Closing Bottom: "+e.getMessage());}
                            }
                        }
                        else
                        {
                            last_event_selected = featureList.get(i).getStringProperty(PROPERTY_EVENT_ID);
                            number_of_selected_cards++;
                            if(number_of_selected_cards>0)
                            {

                                try
                                {
                                    JSONObject geometry = new JSONObject(featureList.get(i).geometry().toJson());
                                    update_bottom_sheet_click(featureList.get(i));
                                    JSONArray point = new JSONArray(geometry.get("coordinates").toString());

                                    set_camera_position(50,new LatLng(Double.parseDouble(point.get(1).toString())
                                            ,Double.parseDouble(point.get(0).toString())));
                                }
                                catch (Exception e)
                                {
                                    Log.d(TAG,"Feature selected Exception: "+e.getMessage());
                                }

                            }
                            setSelected(i);
                        }
                    }
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    private void setSelected(int index)
    {
        if (featureCollection.features() != null)
        {
            Feature feature = featureCollection.features().get(index);
            setFeatureSelectState(feature, true);
            refreshSource();
        }
    }

    private boolean featureSelectStatus(int index)
    {
        if (featureCollection == null)
        {
            return false;
        }
        return featureCollection.features().get(index).getBooleanProperty(PROPERTY_SELECTED);
    }

    private void setFeatureSelectState(Feature feature, boolean selectedState)
    {
        if (feature.properties() != null)
        {
            feature.properties().addProperty(PROPERTY_SELECTED, selectedState);
            refreshSource();
        }
    }

    /**
     * Updates the display of data on the map after the FeatureCollection has been modified
     */
    private void refreshSource()
    {
        if (source != null && featureCollection != null)
        {
            source.setGeoJson(featureCollection);
        }
    }

    /**
     * Sets up all of the sources and layers needed for this example
     *
     * @param collection the FeatureCollection to set equal to the globally-declared FeatureCollection
     */
    public void setUpData(final FeatureCollection collection)
    {
        featureCollection = collection;
        if (mapboxMap != null)
        {
            mapboxMap.getStyle(style ->
            {
                style.removeSource(GEOJSON_SOURCE_ID);
                style.removeLayer(GEOJSON_SOURCE_ID);
                setupSource(style);
                setUpImage(style);
                setUpMarkerLayer(style);
                setUpInfoWindowLayer(style);
            });

        }
    }

    /**
     * Adds the GeoJSON source to the map
     */
    private void setupSource(@NonNull Style loadedStyle)
    {
        Log.v(TAG,"source was not null");
        source = new GeoJsonSource(GEOJSON_SOURCE_ID, featureCollection);
        loadedStyle.addSource(source);

    }

    /**
     * Adds the marker image to the map for use as a SymbolLayer icon
     */
    private void setUpImage(@NonNull Style loadedStyle)
    {
        loadedStyle.addImage(MARKER_IMAGE_ID, BitmapFactory.decodeResource(
                this.getResources(), R.drawable.red_marker));
    }

    /**
     * Setup a layer with maki icons, eg. west coast city.
     */
    private void setUpMarkerLayer(@NonNull Style loadedStyle)
    {
        loadedStyle.addLayer(new SymbolLayer(MARKER_LAYER_ID, GEOJSON_SOURCE_ID)
                .withProperties(
                        iconImage(MARKER_IMAGE_ID),
                        iconAllowOverlap(true),
                        iconOffset(new Float[] {0f, -8f})
                ));
    }

    /**
     * Invoked when the bitmaps have been generated from a view.
     */
    public void setImageGenResults(HashMap<String, Bitmap> imageMap)
    {
        if (mapboxMap != null)
        {
            mapboxMap.getStyle(style ->
            {
                // calling addImages is faster as separate addImage calls for each bitmap.
                style.addImages(imageMap);
            });
        }
    }

    /**
     * Setup a layer with Android SDK call-outs
     * <p>
     * name of the feature is used as key for the iconImage
     * </p>
     */
    private void setUpInfoWindowLayer(@NonNull Style loadedStyle)
    {
        loadedStyle.addLayer(new SymbolLayer(CALLOUT_LAYER_ID, GEOJSON_SOURCE_ID)
                .withProperties(
                        /* show image with id title based on the value of the name feature property */
                        iconImage("{name}"),

                        /* set anchor of icon to bottom-left */
                        iconAnchor(ICON_ANCHOR_BOTTOM),

                        /* all info window and marker image to appear at the same time*/
                        iconAllowOverlap(true),

                        /* offset the info window to be above the marker */
                        iconOffset(new Float[] {-2f, -28f})
                )
                /* add a filter to show only when selected feature property is true */
                .withFilter(eq((get(PROPERTY_SELECTED)), literal(true))));
    }


    private static class Load_GeoJson_Data_Task extends AsyncTask<Void, Void, FeatureCollection>
    {

        private final WeakReference<map_fragment> activityRef;

        Load_GeoJson_Data_Task(map_fragment activity)
        {
            this.activityRef = new WeakReference<>(activity);
            Log.v(TAG,"LoadGeoJsonDataTask: constructor");

        }

        @Override
        protected FeatureCollection doInBackground(Void... params)
        {
            map_fragment activity = activityRef.get();

            if (activity == null)
            {
                return null;
            }

            Log.v(TAG,"Feature Collection Returned");

            file_maker geo_json_holder = new file_maker(file_directory_static,FILE_NAME);
            geo_json_holder.read_json();

            Log.v(TAG,"LoadGeoJsonDataTask: doInBackground");
            return FeatureCollection.fromJson(geo_json_holder.read_json().toString());
        }

        @Override
        protected void onPostExecute(FeatureCollection featureCollection)
        {
            super.onPostExecute(featureCollection);
            Log.v(TAG,"On post execute");
            map_fragment activity = activityRef.get();
            if (featureCollection == null || activity == null)
            {
                return;
            }

            // This example runs on the premise that each GeoJSON Feature has a "selected" property,
            // with a boolean value. If your data's Features don't have this boolean property,
            // add it to the FeatureCollection 's features with the following code:
            for (Feature singleFeature : featureCollection.features())
            {
                singleFeature.addBooleanProperty(PROPERTY_SELECTED, false);
                Log.v(TAG,"PROPERTY_SELECTED, False");
            }

            activity.setUpData(featureCollection);
            Generate_View_Icon_Task generateViewIconTask=null;
            Log.v(TAG,"LoadGeoJsonDataTask: onPostExecute");
            new Generate_View_Icon_Task(activity).execute(featureCollection);

        }

    }

    /**
     * This method handles click events for SymbolLayer symbols.
     * <p>
     * When a SymbolLayer icon is clicked, we moved that feature to the selected state.
     * </p>
     *
     * @param screenPoint the point on screen clicked
     */

    /**
     * AsyncTask to generate Bitmap from Views to be used as iconImage in a SymbolLayer.
     * <p>
     * Call be optionally be called to update the underlying data source after execution.
     * </p>
     * <p>
     * Generating Views on background thread since we are not going to be adding them to the view hierarchy.
     * </p>
     */
    private static class Generate_View_Icon_Task extends AsyncTask<FeatureCollection, Void, HashMap<String, Bitmap>>
    {

        private final HashMap<String, View> viewMap = new HashMap<>();
        private final WeakReference<map_fragment> activityRef;
        private final boolean refreshSource;
        @SuppressLint("StaticFieldLeak")
        private final Context context;
        private boolean is_user_joined;

        Generate_View_Icon_Task(map_fragment activity, boolean refreshSource)
        {
            this.activityRef = new WeakReference<>(activity);
            this.refreshSource = refreshSource;
            this.context = activity.getContext();
            Log.v(TAG,"GenerateViewIconTask: Constructor_1");
        }

        Generate_View_Icon_Task(map_fragment activity)
        {
            this(activity, false);
            Log.v(TAG,"GenerateViewIconTask: Constructor_2");
        }

        private String date_reformat(String time)
        {
            String inputPattern = "yyyy-MM-dd";
            String outputPattern = "MMMM,dd,yy";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

            Date date = null;
            String str = null;

            try {
                date = inputFormat.parse(time);
                str = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return str;
        }


        private Bitmap get_profile_bmp(String name)
        {
            ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir("Profile_Pictures", Context.MODE_PRIVATE);
            try
            {
                File file =new File(directory, name);
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                return bitmap;
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
                return null;
            }
        }



        @SuppressWarnings("WrongThread")
        @Override
        protected HashMap<String, Bitmap> doInBackground(FeatureCollection... params)
        {
            map_fragment activity = activityRef.get();
            if (activity != null)
            {
                HashMap<String, Bitmap> imagesMap = new HashMap<>();
                LayoutInflater inflater = LayoutInflater.from(activity.getActivity()); //////

                FeatureCollection featureCollection = params[0];

                for (Feature feature : featureCollection.features())
                {

                    @SuppressLint("InflateParams") ConstraintLayout constraint_layout =
                            (ConstraintLayout) inflater.inflate(R.layout.event_id_card, null);

                    /// Name Section
                    String name = feature.getStringProperty(PROPERTY_NAME);
                    TextView titleTextView = constraint_layout.findViewById(R.id.invitor_name);
                    titleTextView.setText(name);

                    /// Created Date Section
                    String date_creation = feature.getStringProperty(PROPERTY_DATE_CREATED);
                    TextView created_date = constraint_layout.findViewById(R.id.created_date);
                    created_date.setText(date_reformat(date_creation));

                    /// Event Date Section
                    String date_of_event = feature.getStringProperty(PROPERTY_EVENT_DATE);
                    TextView event_date = constraint_layout.findViewById(R.id.event_date);
                    event_date.setText(date_reformat(date_of_event)); //

                    /// Profile Pix Section
                    String profile_pic_name = feature.getStringProperty(PROPERTY_PIC).substring(1);
                    CircleImageView profile_image = constraint_layout.findViewById(R.id.profile_image);
                    Bitmap profile_pic = get_profile_bmp(profile_pic_name);
                    if(profile_pic!=null)
                        profile_image.setImageBitmap(profile_pic);

                    /// Number of Joined User Section
                    try
                    {
                        String num_of_joined = feature.getStringProperty(PROPERTY_NUM_OF_JOINED);
                        TextView number_of_joined_users_tv = constraint_layout.findViewById(R.id.num_of_joined);
                        number_of_joined_users_tv.setText(num_of_joined);
                    }
                    catch (Exception e)
                    {

                    }


                    /// Is User Joined Section
                    String is_user_joined = feature.getStringProperty(PROPERTY_IS_USER_JOINED);


                    int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    constraint_layout.measure(measureSpec, measureSpec);


                    Bitmap bitmap = map_fragment.SymbolGenerator.generate(constraint_layout);
                    imagesMap.put(name, bitmap);
                    viewMap.put(name, constraint_layout);
                }
                Log.v(TAG,"GenerateViewIconTask: doInBackground");
                return imagesMap;
            }
            else
            {
                Log.v(TAG,"GenerateViewIconTask: doInBackground Null");
                return null;
            }
        }

        @Override
        protected void onPostExecute(HashMap<String, Bitmap> bitmapHashMap)
        {
            super.onPostExecute(bitmapHashMap);
            map_fragment activity = activityRef.get();
            if (activity != null && bitmapHashMap != null)
            {
                activity.setImageGenResults(bitmapHashMap);
                if (refreshSource)
                {
                    activity.refreshSource();
                }
            }
            Log.v(TAG,"GenerateViewIconTask: onPostExecute");
            //Toast.makeText(activity, R.string.tap_on_marker_instruction, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Utility class to generate Bitmaps for Symbol.
     */
    private static class SymbolGenerator
    {

        /**
         * Generate a Bitmap from an Android SDK View.
         *
         * @param view the View to be drawn to a Bitmap
         * @return the generated bitmap
         */
        static Bitmap generate(@NonNull View view)
        {
            int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(measureSpec, measureSpec);

            int measuredWidth = view.getMeasuredWidth();
            int measuredHeight = view.getMeasuredHeight();

            view.layout(0, 0, measuredWidth, measuredHeight);
            Bitmap bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            return bitmap;
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (mapboxMap != null)
        {
            mapboxMap.removeOnMapClickListener(this);
        }
        mapView.onDestroy();
        mapbox_navigation.onDestroy();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }



}