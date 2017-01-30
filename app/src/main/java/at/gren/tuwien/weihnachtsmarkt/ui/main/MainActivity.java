package at.gren.tuwien.weihnachtsmarkt.ui.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

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
import at.gren.tuwien.weihnachtsmarkt.widgets.EmptyRecyclerView;
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

    @BindView(R.id.recycler_view) EmptyRecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;

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
        setTitle(R.string.title_cardView);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

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

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                    createNavbarItem(R.string.title_cardView, new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_place)),
                    createNavbarItem(R.string.title_bestRated, new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_star)),
                    createNavbarItem(R.string.title_map, new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_map)),
                    new DividerDrawerItem(),
                    new SecondaryDrawerItem().withName(R.string.others),
                    createNavbarItem(R.string.settings, new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_settings)).withEnabled(false).withBadge("bald verfügbar!"),
                    createNavbarItem(R.string.opensource, new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_code)),
                    createNavbarItem(R.string.contact, new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_mail))
                )
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {

                    Intent intent;

                    switch ((int) drawerItem.getIdentifier()) {

                        case 1:
                            intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                            break;
                        case 3:
                            intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                            //TODO: Navigation to MapView
                            break;
                        case 4:
                            break;
                        case 5:
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mathck/AdventHopper"));
                            startActivity(intent);
                            break;
                        case 6:
                            ShareCompat.IntentBuilder.from(this)
                                .setType("message/rfc822")
                                .addEmailTo("mateusz@gren.at")
                                .setSubject("Weihnachtsmarkt App")
                                .setChooserTitle("E-Mail versenden mit ...")
                                .startChooser();
                            break;
                        default:
                            intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);

                            break;
                    }

                    return true;
                })
                .build();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
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
        MainActivity.this.runOnUiThread(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            Snackbar.make(mSwipeRefreshLayout, "Lade Weihnachtsmärkte...", Snackbar.LENGTH_LONG).show();
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
        mMainAdapter.setWeihnachtsmärkte(Collections.emptyList());
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

    private int mNavbarItemId = 1;
    private PrimaryDrawerItem createNavbarItem(@StringRes int stringId, @DrawableRes int iconId) {
        return new PrimaryDrawerItem()
                .withIdentifier(mNavbarItemId++)
                .withName(stringId)
                .withIcon(iconId)
                .withIconTintingEnabled(true)
                .withIconColor(getResources().getColor(R.color.grey_600))
                .withSelectedIconColor(getResources().getColor(R.color.blue_500));
    }

    private PrimaryDrawerItem createNavbarItem(@StringRes int stringId, Drawable drawable) {
        return new PrimaryDrawerItem()
                .withIdentifier(mNavbarItemId++)
                .withName(stringId)
                .withIcon(drawable)
                .withIconTintingEnabled(true)
                .withIconColor(getResources().getColor(R.color.grey_600))
                .withSelectedIconColor(getResources().getColor(R.color.blue_500));
    }
}
