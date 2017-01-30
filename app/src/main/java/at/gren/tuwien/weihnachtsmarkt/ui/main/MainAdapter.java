package at.gren.tuwien.weihnachtsmarkt.ui.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private Context mContext = null;
    private boolean mHasLocation = false;
    private final double mUserLocationLatitude;
    private final double mUserLocationLongitude;

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    @Inject
    public MainAdapter(DataManager dataManager, @ApplicationContext Context context) {
        mWeihnachtsmärkte = new ArrayList<>();
        this.mContext = context;
        this.mHasLocation = dataManager.getPreferencesHelper().hasLocation();

        this.mUserLocationLatitude = dataManager.getPreferencesHelper().getLocationLatitude();
        this.mUserLocationLongitude = dataManager.getPreferencesHelper().getLocationLongitude();

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl("gs://advent-hopper.appspot.com");

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

        Glide.with(holder.itemView.getContext())
                .using(new FirebaseImageLoader())
                .load(mStorageRef.child("images/" + markt.id() + ".jpg"))
                .placeholder(R.mipmap.snow_backdrop)
                .error(R.mipmap.snow_backdrop)
                .crossFade()
                .into(holder.marketImage);

        if (mHasLocation) {
            holder.distance.setText(DistanceUtil.getDistanceToMarket(mUserLocationLatitude, mUserLocationLongitude, markt));
            holder.navigationLayout.setOnClickListener((View v) -> {
                Intent navigationIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" +
                                markt.geometry().coordinates().get(0) + "," +
                                markt.geometry().coordinates().get(1) +
                                "&mode=w"));

                holder.itemView.getContext().startActivity(navigationIntent);
            });
        }
        else {
            holder.navigationLayout.setVisibility(View.INVISIBLE);
        }

        if(markt.properties().AVERAGERATING() != null && markt.properties().AVERAGERATING() != 0)
            holder.ratingBar.setRating(Float.parseFloat(Double.toString(markt.properties().AVERAGERATING())));
        else
            holder.ratingBar.setVisibility(View.INVISIBLE);

        holder.shareIcon.setOnClickListener((View v) -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Schau dir diesen tollen Weihnachtsmarkt an " + markt.properties().BEZEICHNUNG());
            sendIntent.setType("text/plain");
            holder.itemView.getContext().startActivity(sendIntent);
        });

        holder.marketImage.setOnClickListener((View v) -> {
            Intent viewIntent = new Intent(mContext, DetailedActivity.class);
            viewIntent.putExtra("key", markt.properties().OBJECTID());

            Pair<View, String> p1 = Pair.create(holder.marketImage, "marketImage");
            //Pair<View, String> p2 = Pair.create((View) mHolder.marketImage, "marketTitle");
            //Pair<View, String> p3 = Pair.create((View) mHolder.marketImage, "ratingBar");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((MainActivity) mContext, p1);

            holder.itemView.getContext().startActivity(viewIntent, options.toBundle());
        });
    }

    @Override
    public int getItemCount() {
        return mWeihnachtsmärkte.size();
    }

    @Subscribe
    public void locationUpdated(LocationUpdatedEvent event) {
        notifyDataSetChanged();
    }

    public void setActivity(Context mainActivity) {
        this.mContext = mainActivity;
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
}
