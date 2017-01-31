package at.gren.tuwien.weihnachtsmarkt.ui.map;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import at.gren.tuwien.weihnachtsmarkt.R;
import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.ui.base.BaseActivity;
import at.gren.tuwien.weihnachtsmarkt.ui.navigation.NavigationDrawer;
import at.gren.tuwien.weihnachtsmarkt.util.DialogFactory;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends BaseActivity implements MapMvpView, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    @Inject MapPresenter mMapPresenter;

    @BindView(R.id.map_toolbar) Toolbar mToolbar;
    @BindView(R.id.bottom_sheet) NestedScrollView mBottomSheet;
    @BindView(R.id.bottom_sheet_title) TextView mBottom_sheet_title;
    @BindView(R.id.bottom_sheet_ratingBar) RatingBar mBottom_sheet_ratingBar;
    @BindView(R.id.bottom_sheet_rating) TextView mBottom_sheet_rating;
    @BindView(R.id.bottom_sheet_address) TextView mBottom_sheet_address;
    @BindView(R.id.bottom_sheet_date) TextView mBottom_sheet_date;
    @BindView(R.id.bottom_sheet_openingHours) TextView mBottom_sheet_openingHours;
    @BindView(R.id.bottom_sheet_weblink) TextView mBottom_sheet_weblink;

    private GoogleMap mGoogleMap;
    private BottomSheetBehavior mBottomSheetBehavior;
    private HashMap<Marker, Weihnachtsmarkt> mMarkerMap;
    private Marker mCurrentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.map_view);
        ButterKnife.bind(this);

        mMapPresenter.attachView(this);
        setSupportActionBar(mToolbar);

        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

        Drawer drawer = new NavigationDrawer().build(this, mToolbar);
        drawer.deselect();
        drawer.getDrawerItem(3).withSetSelected(true);

        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior.setPeekHeight(450);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        LatLng latLng = new LatLng(48.209206, 16.372778);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        mMapPresenter.loadMärkte();
        mGoogleMap.setOnMarkerClickListener(this);
    }

    /***** MVP View methods implementation *****/

    @Override
    public void showAdventmaerkteOnMap(List<Weihnachtsmarkt> märkte) {
        mMarkerMap = new HashMap<>();
        for(Weihnachtsmarkt weihnachtsmarkt : märkte) {
            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(weihnachtsmarkt.geometry().coordinates().get(0), weihnachtsmarkt.geometry().coordinates().get(1)))
                    .title(weihnachtsmarkt.properties().BEZEICHNUNG()));
            mMarkerMap.put(marker,weihnachtsmarkt);
        }
    }

    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(this, "Es gab ein Problem beim Laden der Karte").show();
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

    @Override
    public boolean onMarkerClick(Marker newMarker) {
        if(mCurrentMarker != null){
            mCurrentMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
        mCurrentMarker = newMarker;
        mCurrentMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottom_sheet_title.setText(mMarkerMap.get(mCurrentMarker).properties().BEZEICHNUNG());
        mBottom_sheet_ratingBar.setRating(Float.parseFloat(Double.toString(mMarkerMap.get(mCurrentMarker).properties().AVERAGERATING())));
        mBottom_sheet_rating.setText(Double.toString(mMarkerMap.get(mCurrentMarker).properties().AVERAGERATING()));
        mBottom_sheet_address.setText(mMarkerMap.get(mCurrentMarker).properties().ADRESSE());
        mBottom_sheet_date.setText(mMarkerMap.get(mCurrentMarker).properties().DATUM());
        mBottom_sheet_openingHours.setText(mMarkerMap.get(mCurrentMarker).properties().OEFFNUNGSZEIT());
        mBottom_sheet_weblink.setText(mMarkerMap.get(mCurrentMarker).properties().WEBLINK1());
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED || mBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_COLLAPSED) {

                Rect outRect = new Rect();
                mBottomSheet.getGlobalVisibleRect(outRect);

                if(!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    mCurrentMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }
}
