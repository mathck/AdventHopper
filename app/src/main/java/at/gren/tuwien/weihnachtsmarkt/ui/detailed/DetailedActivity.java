package at.gren.tuwien.weihnachtsmarkt.ui.detailed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.DrawableCompat;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import javax.inject.Inject;

import at.gren.tuwien.weihnachtsmarkt.R;
import at.gren.tuwien.weihnachtsmarkt.data.DataManager;
import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.ui.base.BaseActivity;
import at.gren.tuwien.weihnachtsmarkt.util.DialogFactory;
import at.gren.tuwien.weihnachtsmarkt.util.DistanceUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailedActivity extends BaseActivity implements DetailedMvpView{

    @Inject DetailedPresenter mDetailedPresenter;
    @Inject DataManager mDataManager;

    @BindView(R.id.detailed_toolbar) Toolbar mdetailed_toolbar;
    @BindView(R.id.ratingBar) RatingBar mRatingBar;
    @BindView(R.id.title) TextView mTitle;
    @BindView(R.id.address) TextView mAddress;
    @BindView(R.id.openingHours) TextView mOpeningHours;
    @BindView(R.id.weblink) TextView mWeblink;
    @BindView(R.id.date) TextView mDate;
    @BindView(R.id.rating) TextView mRating;
    @BindView(R.id.distance) TextView mDistance;
    @BindView(R.id.floatingActionButton) FloatingActionButton mFloatingActionButton;

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
    }

    /***** MVP View methods implementation *****/

    @Override
    public void showAdventmarkt(final Weihnachtsmarkt markt) {
        mTitle.setText(markt.properties().BEZEICHNUNG());
        mAddress.setText(markt.properties().ADRESSE());

        mOpeningHours.setText(markt.properties().OEFFNUNGSZEIT().replace(",<br />", "\n"));
        mWeblink.setText(markt.properties().WEBLINK1());
        mDate.setText(markt.properties().DATUM());
        mRatingBar.setRating(Float.parseFloat(Double.toString(markt.properties().AVERAGERATING())));
        mRating.setText(Double.toString(markt.properties().AVERAGERATING()));

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
    public void showError() {
        DialogFactory.createGenericErrorDialog(this, "Es gab ein Problem beim Laden des Adventmarkts").show();
    }
}
