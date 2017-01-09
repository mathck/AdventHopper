package at.gren.tuwien.weihnachtsmarkt.ui.detailed;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;

import android.support.v7.widget.Toolbar;
import android.widget.RatingBar;

import at.gren.tuwien.weihnachtsmarkt.R;
import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.ui.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailedActivity extends BaseActivity implements DetailedMvpView{

    @BindView(R.id.detailed_toolbar)
    Toolbar mdetailed_toolbar;
    @BindView(R.id.ratingBar) RatingBar mratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.detailed_view);
        ButterKnife.bind(this);

        setSupportActionBar(mdetailed_toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable progress = mratingBar.getProgressDrawable();
        DrawableCompat.setTint(progress, Color.YELLOW);
    }

    /***** MVP View methods implementation *****/

    @Override
    public void showAdventmarkt(Weihnachtsmarkt markt) {

    }

    @Override
    public void showError() {

    }
}
