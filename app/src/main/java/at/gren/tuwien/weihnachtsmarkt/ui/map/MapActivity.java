package at.gren.tuwien.weihnachtsmarkt.ui.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import java.util.List;

import javax.inject.Inject;

import at.gren.tuwien.weihnachtsmarkt.R;
import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.ui.base.BaseActivity;
import at.gren.tuwien.weihnachtsmarkt.ui.main.MainActivity;
import at.gren.tuwien.weihnachtsmarkt.util.DialogFactory;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Michael on 30.01.2017.
 */
public class MapActivity extends BaseActivity implements MapMvpView, OnMapReadyCallback {

    @Inject MapPresenter mMapPresenter;

    @BindView(R.id.map_toolbar) Toolbar mToolbar;

    private GoogleMap mGoogleMap;

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

        final Context context = this;

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
                            intent = new Intent(context, MapActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
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
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        LatLng latLng = new LatLng(48.209206,16.372778);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        mMapPresenter.loadMärkte();
    }

    /***** MVP View methods implementation *****/

    @Override
    public void showAdventmaerkteOnMap(List<Weihnachtsmarkt> märkte) {
        for(Weihnachtsmarkt weihnachtsmarkt : märkte) {
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(weihnachtsmarkt.geometry().coordinates().get(0), weihnachtsmarkt.geometry().coordinates().get(1)))
                    .title(weihnachtsmarkt.properties().BEZEICHNUNG()));
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
}
