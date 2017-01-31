package at.gren.tuwien.weihnachtsmarkt.ui.main;

import android.Manifest;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import at.gren.tuwien.weihnachtsmarkt.R;
import at.gren.tuwien.weihnachtsmarkt.data.SyncService;
import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.ui.base.BaseActivity;
import at.gren.tuwien.weihnachtsmarkt.ui.navigation.NavigationDrawer;
import at.gren.tuwien.weihnachtsmarkt.util.DialogFactory;
import at.gren.tuwien.weihnachtsmarkt.util.events.LocationUpdatedEvent;
import at.gren.tuwien.weihnachtsmarkt.util.events.SyncCompletedEvent;
import at.gren.tuwien.weihnachtsmarkt.util.sort.CompareDistance;
import at.gren.tuwien.weihnachtsmarkt.util.sort.CompareRating;
import at.gren.tuwien.weihnachtsmarkt.widgets.EmptyRecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends BaseActivity implements MainMvpView, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, SearchView.OnQueryTextListener {

    private static final String EXTRA_TRIGGER_SYNC_FLAG = "at.gren.tuwien.weihnachtsmarkt.ui.main.MainActivity.EXTRA_TRIGGER_SYNC_FLAG";
    private static final long UPDATE_INTERVAL = 30;
    private static final long FASTEST_INTERVAL = 15;

    private GoogleApiClient mGoogleApiClient = null;

    @Inject MainPresenter mMainPresenter;
    @Inject MainAdapter mMainAdapter;

    @BindView(R.id.recycler_view) EmptyRecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    private LocationRequest mLocationRequest;
    private List<Weihnachtsmarkt> mMärkte = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        super.onCreate(savedInstanceState);

        activityComponent().inject(this);
        setTitle(R.string.title_cardView);

        mMainAdapter.setActivity(this);
        mRecyclerView.setAdapter(mMainAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setEmptyView(findViewById(R.id.emptyView));
        mMainPresenter.attachView(this);
        mMainPresenter.loadMärkte();

        if (getIntent().getBooleanExtra(EXTRA_TRIGGER_SYNC_FLAG, true)) {
            startService(SyncService.getStartIntent(this));
        }

        final Context context = this;
        mSwipeRefreshLayout.setOnRefreshListener(() -> startService(SyncService.getStartIntent(context)));

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

        new NavigationDrawer().build(this, mToolbar);
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
        mMainPresenter.loadMärkte();
        MainActivity.this.runOnUiThread(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            Snackbar.make(mSwipeRefreshLayout, "Weihnachtsmärkte wurden geladen!", Snackbar.LENGTH_LONG).show();
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
        mMärkte = märkte;

        mMärkte = new ArrayList<>();
        mMärkte.addAll(märkte);

        mMainAdapter.replaceAll(märkte);
    }

    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(this, getString(R.string.error_loading_list))
            .show();
    }

    @Override
    public void showAdventmaerkteEmpty() {
        mMainAdapter.replaceAll(Collections.emptyList());
        Toast.makeText(this, R.string.empty_list, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mMainPresenter.storeLocation(location);
            EventBus.getDefault().post(new LocationUpdatedEvent());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_distance:
                mMainAdapter.setComparator(new CompareDistance(mMainPresenter.dataManager.getPreferencesHelper().hasLocation(),
                        mMainPresenter.dataManager.getPreferencesHelper().getLocationLatitude(),
                        mMainPresenter.dataManager.getPreferencesHelper().getLocationLongitude()));
                mMainAdapter.notifyDataSetChanged();
                return true;
            case R.id.action_sort_rating:
                mMainAdapter.setComparator(new CompareRating());
                mMainAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchViewAction = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());

        searchViewAction.setSearchableInfo(searchableInfo);
        searchViewAction.setIconifiedByDefault(true);

        EditText searchEditText = (EditText)searchViewAction.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));

        ImageView searchMagIcon = (ImageView)searchViewAction.findViewById(android.support.v7.appcompat.R.id.search_button);
        searchMagIcon.setImageResource(R.drawable.ic_search_white_24dp);

        //TextView action_sort_distance = (TextView) menu.findItem(R.id.action_sort_distance).getActionView();
        //TextView action_sort_rating = (TextView) menu.findItem(R.id.action_sort_rating).getActionView();
        //action_sort_distance.setTextColor(Color.BLACK);
        //action_sort_rating.setTextColor(Color.BLACK);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<Weihnachtsmarkt> filteredModelList = filter(mMärkte, query);
        mMainAdapter.replaceAll(filteredModelList);
        mRecyclerView.scrollToPosition(0);
        return true;
    }

    private static List<Weihnachtsmarkt> filter(List<Weihnachtsmarkt> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<Weihnachtsmarkt> filteredModelList = new ArrayList<>();
        for (Weihnachtsmarkt model : models) {
            final String text = model.properties().BEZEICHNUNG().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}
