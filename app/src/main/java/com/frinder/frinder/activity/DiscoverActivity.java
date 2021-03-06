package com.frinder.frinder.activity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.Profile;
import com.frinder.frinder.R;
import com.frinder.frinder.adapters.DiscoverUsersAdapter;
import com.frinder.frinder.adapters.InterestsAdapter;
import com.frinder.frinder.adapters.SpacesItemDecoration;
import com.frinder.frinder.dataaccess.DiscoverFirebaseDas;
import com.frinder.frinder.dataaccess.UserFirebaseDas;
import com.frinder.frinder.model.DiscoverUser;
import com.frinder.frinder.model.Interest;
import com.frinder.frinder.model.User;
import com.frinder.frinder.utils.LocationUtils;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import static com.frinder.frinder.activity.MainActivity.LOCATION_DENY_MSG;

public class DiscoverActivity extends BaseActivity {
    private static final String TAG = "DiscoverActivity";
    ArrayList<DiscoverUser> nearbyUsers;
    DiscoverUsersAdapter adapter;
    Profile profile;
    User currentUser;
    UserFirebaseDas userFirebaseDas;
    DiscoverFirebaseDas discoverFirebaseDas;
    private static final int REQUEST_FINE_LOCATION = 99;
    private LocationUtils locationUtilInstance;
    SwipeRefreshLayout srlDiscoverContainer;
    ImageView ivDiscoverUserIcon;
    RippleBackground rippleBackground;
    Handler handler;
    String filterInterest = "";
    ArrayList<Interest> interests;
    RecyclerView rvInterests;
    InterestsAdapter interestsAdapter;
    int previousInterestClickedPosition = -1;

    private static final long REPEAT_SEARCH_DELAY = 2000; // unit is milliseconds (2 sec delay)

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        locationUtilInstance = LocationUtils.getInstance();
        if(requestLocationPermissions()) {
            getCurrentLocation();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.ic_location_user_gray);

        // Lookup the recyclerview in activity layout
        rvInterests = (RecyclerView) findViewById(R.id.rvInterests);
        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvInterests.setLayoutManager(horizontalLayoutManagaer);
        interests = Interest.createFilterInterestList(getResources().getStringArray(R.array.filter_interest_label),
                getResources().obtainTypedArray(R.array.filter_interest_icon),
                getResources().obtainTypedArray(R.array.filter_interest_pics),
                getResources().getIntArray(R.array.filter_interest_color),
                getResources().getStringArray(R.array.filter_interest_forDB));
        interestsAdapter = new InterestsAdapter(this, interests);
        rvInterests.setAdapter(interestsAdapter);
        interestsAdapter.setOnItemClickListener(new InterestsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                getSelectedInterest(position);
            }
        });

        srlDiscoverContainer = (SwipeRefreshLayout) findViewById(R.id.srlDiscoverContainer);
        RecyclerView rvDiscoverusers = (RecyclerView) findViewById(R.id.rvDiscoverUsers);
        nearbyUsers = new ArrayList<>();
        adapter = new DiscoverUsersAdapter(this, nearbyUsers);
        rvDiscoverusers.setAdapter(adapter);
        rvDiscoverusers.setLayoutManager(new LinearLayoutManager(this));
        SpacesItemDecoration decoration = new SpacesItemDecoration(24);
        rvDiscoverusers.addItemDecoration(decoration);

        profile = Profile.getCurrentProfile();

        userFirebaseDas = new UserFirebaseDas(DiscoverActivity.this);
        userFirebaseDas.getUser("123", new UserFirebaseDas.OnCompletionListener() {
            @Override
            public void onUserReceived(User user) {
                currentUser = user;
                user.getLocation();
                if(currentUser.getLocation()!=null && currentUser.getLocation().size() > 0) {
                    getdiscoverUsers();
                }
                else {
                    repeatGetDiscoverUsers();
                }
            }
        });

        discoverFirebaseDas = new DiscoverFirebaseDas(DiscoverActivity.this);

        srlDiscoverContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(currentUser.getLocation()!=null && currentUser.getLocation().size() > 0) {
                    getdiscoverUsers();
                }
                else {
                    repeatGetDiscoverUsers();
                }
            }
        });

        rippleBackground=(RippleBackground)findViewById(R.id.pbDiscoverUser);
        ivDiscoverUserIcon = (ImageView) findViewById(R.id.ivDiscoverUserIcon);
        ivDiscoverUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rippleBackground.startRippleAnimation();
                ivDiscoverUserIcon.setVisibility(View.VISIBLE);
            }
        });
        handler = new Handler();
    }

    private void getSelectedInterest(int position) {
        Interest interestClicked = interests.get(position);
        String interestClickedDBValue = interestClicked.getDBValue();

        if (filterInterest.isEmpty()) {
            interestClicked.setSelected(true);
            filterInterest = interestClickedDBValue;
            previousInterestClickedPosition = position;
            interestsAdapter.notifyItemChanged(position);
        }
        else {
            if (interestClickedDBValue.contentEquals(filterInterest)) {
                interestClicked.setSelected(false);
                filterInterest = "";
                previousInterestClickedPosition = -1;
                interestsAdapter.notifyItemChanged(position);
            } else {
                interests.get(previousInterestClickedPosition).setSelected(false);
                interestsAdapter.notifyItemChanged(previousInterestClickedPosition);

                interestClicked.setSelected(true);
                filterInterest = interestClickedDBValue;
                previousInterestClickedPosition = position;
                interestsAdapter.notifyItemChanged(position);
            }
        }

        //ToDo Call method to refresh Discover UI
        if(currentUser.getLocation()!=null && currentUser.getLocation().size() > 0) {
            getdiscoverUsers();
        }
        else {
            repeatGetDiscoverUsers();
        }
    }

    private void repeatGetDiscoverUsers() {
        handler.removeCallbacksAndMessages(null);
        rippleBackground.startRippleAnimation();
        ivDiscoverUserIcon.setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Search for nearby users again after 2sec delay
                if(currentUser.getLocation()!=null && currentUser.getLocation().size() > 0) {
                    getdiscoverUsers();
                }
                else {
                    repeatGetDiscoverUsers();
                }
            }
        }, REPEAT_SEARCH_DELAY);
    }

    private void getCurrentLocation() {
        locationUtilInstance.startLocationUpdates(this,locationUtilsCallback);
    }

    public void getdiscoverUsers() {
        rippleBackground.startRippleAnimation();
        ivDiscoverUserIcon.setVisibility(View.VISIBLE);
        //Clear the list each time discover menu button is clicked as user list will change based on who is nearby
        if (!nearbyUsers.isEmpty()) {
            nearbyUsers.clear();
            adapter.notifyDataSetChanged();
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        double searchRadius = pref.getInt("radius", 200);

        Log.d(TAG, "Interest Filter? " + filterInterest);

        discoverFirebaseDas.getNearbyUsers(currentUser, searchRadius, filterInterest, new DiscoverFirebaseDas.OnCompletionListener() {
            @Override
            public void onNearbyUserListReceived(ArrayList<DiscoverUser> nearbyUsersList) {
                if (nearbyUsersList != null) {
                    nearbyUsers.clear();
                    nearbyUsers.addAll(new HashSet<DiscoverUser>(nearbyUsersList));
                    updateUsersDisplayed();
                }
                else {
                    displayMsgRepeatDiscover();
                }
            }
        });
    }

    private void updateUsersDisplayed() {
        sortNearbyUsers();
        adapter.notifyDataSetChanged();
        srlDiscoverContainer.setRefreshing(false);
        rippleBackground.stopRippleAnimation();
        ivDiscoverUserIcon.setVisibility(View.GONE);
    }

    private void sortNearbyUsers() {
        Collections.sort(nearbyUsers, new Comparator<DiscoverUser>() {
            public int compare(DiscoverUser dUser1, DiscoverUser dUser2) {
                return dUser1.getDistanceFromAppUser().compareTo(dUser2.getDistanceFromAppUser());
            }
        });
    }

    private void displayMsgRepeatDiscover() {
        //TODO Show message in snackbar
        Toast.makeText(DiscoverActivity.this, "Found nobody. Trying increasing search radius.", Toast.LENGTH_SHORT).show();
        srlDiscoverContainer.setRefreshing(false);

        //TODO remove next 2 lines after debugging discover lag
        //rippleBackground.stopRippleAnimation();
        //ivDiscoverUserIcon.setVisibility(View.GONE);

        repeatGetDiscoverUsers();
    }

    private boolean requestLocationPermissions() {
        int hasFineLocation = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocation = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if(hasCoarseLocation != PackageManager.PERMISSION_GRANTED || hasFineLocation != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_action_discover).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    getCurrentLocation();
                } else {
                    // Permission Denied
                    Toast.makeText(this, LOCATION_DENY_MSG, Toast.LENGTH_LONG)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onPause() {
        handler.removeCallbacksAndMessages(null);
        locationUtilInstance.stopLocationUpdates(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        locationUtilInstance.stopLocationUpdates(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        locationUtilInstance.startLocationUpdates(this,locationUtilsCallback);
        super.onResume();
    }

    LocationUtils.LocationUpdate locationUtilsCallback = new LocationUtils.LocationUpdate(){
        @Override
        public void onLocationChanged(Context context, Location location) {
            // New location has now been determined
            String msg = "Updated Location: " +
                    Double.toString(location.getLatitude()) + "," +
                    Double.toString(location.getLongitude());
            //Log.d(TAG, msg);

            if(location!=null) {
                ArrayList<Double> locationList = new ArrayList<>();
                locationList.add(location.getLatitude());
                locationList.add(location.getLongitude());
                //Log.d(TAG, "Updating user " + Profile.getCurrentProfile().getId() + " location with " + locationList.toString());
                userFirebaseDas.updateUserLocation(profile.getId(), locationList);

                if(currentUser!=null && (currentUser.getLocation()==null || currentUser.getLocation().size() == 0)) {
                    userFirebaseDas.getUser(profile.getId(), new UserFirebaseDas.OnCompletionListener() {
                        @Override
                        public void onUserReceived(User user) {
                            currentUser = user;
                            //Log.d(TAG, "Location Update onUserReceived: user = " + currentUser.toString());
                            if (currentUser.getLocation() != null && currentUser.getLocation().size() > 0) {
                                getdiscoverUsers();
                            } else {
                                repeatGetDiscoverUsers();
                            }
                        }
                    });
                }
            }
        }
    };
}
