package at.gren.tuwien.weihnachtsmarkt.ui.detailed;

import android.os.Bundle;

import at.gren.tuwien.weihnachtsmarkt.R;
import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.ui.base.BaseActivity;
import butterknife.ButterKnife;

public class DetailedActivity extends BaseActivity implements DetailedMvpView{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.detailed_view);
        ButterKnife.bind(this);

    }

    /***** MVP View methods implementation *****/

    @Override
    public void showAdventmarkt(Weihnachtsmarkt markt) {

    }

    @Override
    public void showError() {

    }
}
