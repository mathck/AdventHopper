package at.gren.tuwien.weihnachtsmarkt.ui.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import at.gren.tuwien.weihnachtsmarkt.R;
import at.gren.tuwien.weihnachtsmarkt.data.SyncService;
import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.ui.base.BaseActivity;
import at.gren.tuwien.weihnachtsmarkt.util.DialogFactory;
import at.gren.tuwien.weihnachtsmarkt.util.events.LocationUpdatedEvent;
import at.gren.tuwien.weihnachtsmarkt.util.events.SyncCompletedEvent;
import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends BaseActivity implements MainMvpView, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String EXTRA_TRIGGER_SYNC_FLAG = "at.gren.tuwien.weihnachtsmarkt.ui.main.MainActivity.EXTRA_TRIGGER_SYNC_FLAG";
    private static final long UPDATE_INTERVAL = 30;
    private static final long FASTEST_INTERVAL = 15;

    private GoogleApiClient mGoogleApiClient = null;

    @Inject MainPresenter mMainPresenter;
    @Inject MainAdapter mMainAdapter;

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;

    private LocationRequest mLocationRequest;

    /**
     * Return an Intent to start this Activity.
     * triggerDataSyncOnCreate allows disabling the background sync service onCreate. Should
     * only be set to false during testing.
     */
    public static Intent getStartIntent(Context context, boolean triggerDataSyncOnCreate) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_TRIGGER_SYNC_FLAG, triggerDataSyncOnCreate);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_main);
        setTitle(getResources().getString(R.string.title_cardView));
        ButterKnife.bind(this);

        mRecyclerView.setAdapter(mMainAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMainPresenter.attachView(this);
        mMainPresenter.loadMärkte();

        if (getIntent().getBooleanExtra(EXTRA_TRIGGER_SYNC_FLAG, true)) {
            startService(SyncService.getStartIntent(this));
        }

        final Context context = this;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startService(SyncService.getStartIntent(context));
            }
        });

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void syncCompleted(SyncCompletedEvent event) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                Snackbar.make(mSwipeRefreshLayout, "Weihnachtsmärkte wurden neu geladen", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        MainActivityPermissionsDispatcher.startLocationListenerWithCheck(this);
    }

    @SuppressWarnings("MissingPermission")
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void startLocationListener() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mMainPresenter.detachView();
    }

    /***** MVP View methods implementation *****/

    @Override
    public void showAdventmaerkte(List<Weihnachtsmarkt> märkte) {
        mMainAdapter.setWeihnachtsmärkte(märkte);
        mMainAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(this, getString(R.string.error_loading_list))
                .show();
    }

    @Override
    public void showAdventmaerkteEmpty() {
        mMainAdapter.setWeihnachtsmärkte(Collections.<Weihnachtsmarkt>emptyList());
        mMainAdapter.notifyDataSetChanged();
        Toast.makeText(this, R.string.empty_list, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mMainPresenter.storeLocation(location);
            EventBus.getDefault().post(new LocationUpdatedEvent());
        }
    }
}
