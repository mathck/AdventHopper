package at.gren.tuwien.weihnachtsmarkt.ui.detailed;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.DrawableCompat;

import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.inject.Inject;

import at.gren.tuwien.weihnachtsmarkt.R;
import at.gren.tuwien.weihnachtsmarkt.data.DataManager;
import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.ui.base.BaseActivity;
import at.gren.tuwien.weihnachtsmarkt.util.DialogFactory;
import at.gren.tuwien.weihnachtsmarkt.util.DistanceUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

import static at.gren.tuwien.weihnachtsmarkt.R.id.ratingBar;

public class DetailedActivity extends BaseActivity implements DetailedMvpView,OnMapReadyCallback {

    FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    @Inject DetailedPresenter mDetailedPresenter;
    @Inject DataManager mDataManager;

    @BindView(R.id.detailed_toolbar) Toolbar mdetailed_toolbar;
    @BindView(ratingBar) RatingBar mRatingBar;
    @BindView(R.id.title) TextView mTitle;
    @BindView(R.id.address) TextView mAddress;
    @BindView(R.id.openingHours) TextView mOpeningHours;
    @BindView(R.id.weblink) TextView mWeblink;
    @BindView(R.id.date) TextView mDate;
    @BindView(R.id.rating) TextView mRating;
    @BindView(R.id.distance) TextView mDistance;
    @BindView(R.id.floatingActionButton) FloatingActionButton mFloatingActionButton;
    @BindView(R.id.marketImage) ImageView mMarketImage;

    private Weihnachtsmarkt mMarkt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.detailed_view);

        mDetailedPresenter.attachView(this);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        mDetailedPresenter.loadMarkt(bundle.getString("key"));

        setSupportActionBar(mdetailed_toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable progress = mRatingBar.getProgressDrawable();
        DrawableCompat.setTint(progress, getResources().getColor(R.color.yellow_700));

        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl("gs://advent-hopper.appspot.com");
    }

    /***** MVP View methods implementation *****/

    @Override
    public void showAdventmarkt(final Weihnachtsmarkt markt) {
        this.mMarkt = markt;

        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(mStorageRef.child("images/" + markt.id() + ".jpg"))
                .placeholder(R.mipmap.snow_backdrop)
                .error(R.mipmap.snow_backdrop)
                .crossFade()
                .into(mMarketImage);

        mTitle.setText(markt.properties().BEZEICHNUNG());
        mAddress.setText(markt.properties().ADRESSE());

        mOpeningHours.setText(markt.properties().OEFFNUNGSZEIT().replace(",<br />", "\n"));
        mWeblink.setText(markt.properties().WEBLINK1());
        mDate.setText(markt.properties().DATUM());
        mRatingBar.setRating(Float.parseFloat(Double.toString(markt.properties().AVERAGERATING())));
        mRating.setText(Double.toString(markt.properties().AVERAGERATING()));

        mRatingBar.setOnTouchListener((View v, MotionEvent event)->{
            if (event.getAction() == MotionEvent.ACTION_UP) {
                String christmasMarktId = markt.id().substring(15);
                Dialog rankDialog = new Dialog(DetailedActivity.this, R.style.FullHeightDialog);
                rankDialog.setContentView(R.layout.rank_dialog);
                rankDialog.setCancelable(true);
                RatingBar dialogRatingBar = (RatingBar) rankDialog.findViewById(R.id.dialog_ratingbar);
                dialogRatingBar.setRating(2);
                mDataManager.getOwnRating(christmasMarktId, rankDialog);

                TextView text = (TextView) rankDialog.findViewById(R.id.rank_dialog_text);
                text.setText("Bewerte ".concat(markt.properties().BEZEICHNUNG()));

                Button updateButton = (Button) rankDialog.findViewById(R.id.rank_dialog_button);
                updateButton.setOnClickListener((View w) -> {
                    mDataManager.setRating(christmasMarktId, Math.round(dialogRatingBar.getRating()));
                    mDataManager.syncRatings();
                    mDataManager.syncMärkte();
                    showAdventmarkt(markt);
                    rankDialog.dismiss();
                });
                rankDialog.show();
            }
            return true;
        });

        if(mDataManager.getPreferencesHelper().hasLocation()) {

            String distance = DistanceUtil.getDistanceToMarket(
                mDataManager.getPreferencesHelper().getLocationLatitude(),
                mDataManager.getPreferencesHelper().getLocationLongitude(),
                markt);

            mDistance.setText(distance);
        }

        final Context context = this;
        mFloatingActionButton.setOnClickListener((View v) -> {
                Intent navigationIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" +
                                markt.geometry().coordinates().get(0) + "," +
                                markt.geometry().coordinates().get(1) +
                                "&mode=w"));

                context.startActivity(navigationIntent);
        });
    }

    @Override
    public void onDestroy() {
        Glide.get(this).clearMemory();
        super.onDestroy();

        mDetailedPresenter.detachView();
    }

    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(this, "Es gab ein Problem beim Laden des Adventmarkts").show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(mMarkt.geometry().coordinates().get(0), mMarkt.geometry().coordinates().get(1)))
                .title(mMarkt.properties().BEZEICHNUNG()));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 14));
    }
}
