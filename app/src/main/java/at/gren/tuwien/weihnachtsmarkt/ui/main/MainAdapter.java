package at.gren.tuwien.weihnachtsmarkt.ui.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import at.gren.tuwien.weihnachtsmarkt.R;
import at.gren.tuwien.weihnachtsmarkt.data.DataManager;
import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.injection.ApplicationContext;
import at.gren.tuwien.weihnachtsmarkt.ui.detailed.DetailedActivity;
import at.gren.tuwien.weihnachtsmarkt.util.DistanceUtil;
import at.gren.tuwien.weihnachtsmarkt.util.events.LocationUpdatedEvent;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MarktViewHolder> {

    private List<Weihnachtsmarkt> mWeihnachtsmärkte;
    private final DataManager mDataManager;
    private static Context mContext = null;
    private boolean mHasLocation = false;

    @Inject
    public MainAdapter(DataManager dataManager, @ApplicationContext Context context) {
        mWeihnachtsmärkte = new ArrayList<>();
        this.mDataManager = dataManager;
        this.mContext = context;
        this.mHasLocation = mDataManager.getPreferencesHelper().hasLocation();

        EventBus.getDefault().register(this);
    }

    public void setWeihnachtsmärkte(List<Weihnachtsmarkt> weihnachtsmärkte) {
        mWeihnachtsmärkte = weihnachtsmärkte;
    }

    @Override
    public MarktViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.market_card_view, parent, false);
        return new MarktViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MarktViewHolder holder, int position) {
        final Weihnachtsmarkt markt = mWeihnachtsmärkte.get(position);

        holder.title.setText(markt.properties().BEZEICHNUNG());

        if (mHasLocation) {
            holder.distance.setText(getDistanceToMarket(markt));
            holder.navigationLayout.setOnClickListener(new NavigateToMarktOnClick(markt, holder));
        }
        else {
            holder.navigationLayout.setVisibility(View.INVISIBLE);
        }

        holder.ratingBar.setNumStars(markt.getAverageRating());

        holder.shareIcon.setOnClickListener(new ShareMarktOnClick(markt, holder));
        holder.marketImage.setOnClickListener(new ViewMarktOnClick(markt, holder));
    }

    private String getDistanceToMarket(Weihnachtsmarkt markt) {

        double userLocationLatitude = mDataManager.getPreferencesHelper().getLocationLatitude();
        double userLocationLongitude = mDataManager.getPreferencesHelper().getLocationLongitude();

        double marktLocationLatitude = markt.geometry().coordinates().get(0);
        double marktLocationLongitude = markt.geometry().coordinates().get(1);

        String distance = DistanceUtil.getDistance( marktLocationLatitude,
                                                    marktLocationLongitude,
                                                    userLocationLatitude,
                                                    userLocationLongitude);
        return distance + " m";
    }

    @Override
    public int getItemCount() {
        return mWeihnachtsmärkte.size();
    }

    @Subscribe
    public void locationUpdated(LocationUpdatedEvent event) {
        notifyDataSetChanged();
    }

    class MarktViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.shareIcon) ImageView shareIcon;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.distance) TextView distance;
        @BindView(R.id.navigationLayout) LinearLayout navigationLayout;
        @BindView(R.id.marketImage) ImageView marketImage;
        @BindView(R.id.ratingBar) RatingBar ratingBar;

        public MarktViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static class ShareMarktOnClick implements View.OnClickListener {
        private final Weihnachtsmarkt mMarkt;
        private final MarktViewHolder mHolder;

        public ShareMarktOnClick(Weihnachtsmarkt markt, MarktViewHolder holder) {
            mMarkt = markt;
            mHolder = holder;
        }

        @Override
        public void onClick(View v) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Schau dir diesen tollen Weihnachtsmarkt an " + mMarkt.properties().BEZEICHNUNG());
            sendIntent.setType("text/plain");
            mHolder.itemView.getContext().startActivity(sendIntent);
        }
    }

    private static class ViewMarktOnClick implements View.OnClickListener {
        private final Weihnachtsmarkt mMarkt;
        private final MarktViewHolder mHolder;

        public ViewMarktOnClick(Weihnachtsmarkt markt, MarktViewHolder holder) {
            mMarkt = markt;
            mHolder = holder;
        }

        @Override
        public void onClick(View v) {
            Intent viewIntent = new Intent(mContext, DetailedActivity.class);
            viewIntent.putExtra("key",mMarkt.id());
            mHolder.itemView.getContext().startActivity(viewIntent);
        }
    }

    private static class NavigateToMarktOnClick implements View.OnClickListener {
        private final Weihnachtsmarkt mMarkt;
        private final MarktViewHolder mHolder;

        public NavigateToMarktOnClick(Weihnachtsmarkt markt, MarktViewHolder holder) {
            mMarkt = markt;
            mHolder = holder;
        }

        @Override
        public void onClick(View v) {
            Intent navigationIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q=" +
                            mMarkt.geometry().coordinates().get(0) + "," +
                            mMarkt.geometry().coordinates().get(1) +
                            "&mode=w"));

            mHolder.itemView.getContext().startActivity(navigationIntent);
        }
    }
}
