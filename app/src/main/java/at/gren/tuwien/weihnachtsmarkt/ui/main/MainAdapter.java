package at.gren.tuwien.weihnachtsmarkt.ui.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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

import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import at.gren.tuwien.weihnachtsmarkt.R;
import at.gren.tuwien.weihnachtsmarkt.data.DataManager;
import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.injection.ApplicationContext;
import at.gren.tuwien.weihnachtsmarkt.ui.detailed.DetailedActivity;
import at.gren.tuwien.weihnachtsmarkt.util.DistanceUtil;
import at.gren.tuwien.weihnachtsmarkt.util.events.LocationUpdatedEvent;
import at.gren.tuwien.weihnachtsmarkt.util.sort.CompareName;
import at.gren.tuwien.weihnachtsmarkt.util.sort.CompareRating;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MarktViewHolder> {

    private Context mContext = null;
    private boolean mHasLocation = false;
    private final double mUserLocationLatitude;
    private final double mUserLocationLongitude;

    private StorageReference mStorageRef;

    private Comparator<Weihnachtsmarkt> mComparator;

    @Inject
    public MainAdapter(DataManager dataManager, @ApplicationContext Context context) {
        this.mContext = context;
        this.mHasLocation = dataManager.getPreferencesHelper().hasLocation();

        this.mUserLocationLatitude = dataManager.getPreferencesHelper().getLocationLatitude();
        this.mUserLocationLongitude = dataManager.getPreferencesHelper().getLocationLongitude();

        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl("gs://advent-hopper.appspot.com");

        mComparator = new CompareRating();

        EventBus.getDefault().register(this);
    }

    public void setComparator(Comparator<Weihnachtsmarkt> comparator) {
        this.mComparator = comparator;
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
                .placeholder(R.mipmap.market_placeholder)
                .error(R.mipmap.market_placeholder)
                .fitCenter()
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

        if(markt.properties().AVERAGERATING() != null && markt.properties().AVERAGERATING() != 0) {
            holder.ratingBar.setRating(Float.parseFloat(Double.toString(markt.properties().AVERAGERATING())));
            holder.ratingBar.setVisibility(View.VISIBLE);
        }
        else
            holder.ratingBar.setVisibility(View.INVISIBLE);

        holder.shareIcon.setOnClickListener((View v) -> {
            Intent sendIntent = new Intent();

            sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    markt.properties().BEZEICHNUNG() + "\n" +
                    holder.itemView.getContext().getString(R.string.recommend_txt) +
                    "https://play.google.com/store/apps/details?id=at.gren.tuwien.weihnachtsmarkt");
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

        MarktViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    void replaceAll(List<Weihnachtsmarkt> models) {
        mWeihnachtsmärkte.beginBatchedUpdates();
        for (int i = mWeihnachtsmärkte.size() - 1; i >= 0; i--) {
            final Weihnachtsmarkt model = mWeihnachtsmärkte.get(i);
            if (!models.contains(model)) {
                mWeihnachtsmärkte.remove(model);
            }
        }
        mWeihnachtsmärkte.addAll(models);
        mWeihnachtsmärkte.endBatchedUpdates();
    }

    public void add(Weihnachtsmarkt model) {
        mWeihnachtsmärkte.add(model);
    }

    public void remove(Weihnachtsmarkt model) {
        mWeihnachtsmärkte.remove(model);
    }

    public void add(List<Weihnachtsmarkt> models) {
        mWeihnachtsmärkte.addAll(models);
    }

    public void remove(List<Weihnachtsmarkt> models) {
        mWeihnachtsmärkte.beginBatchedUpdates();
        for (Weihnachtsmarkt model : models) {
            mWeihnachtsmärkte.remove(model);
        }
        mWeihnachtsmärkte.endBatchedUpdates();
    }

    private final SortedList<Weihnachtsmarkt> mWeihnachtsmärkte = new SortedList<>(Weihnachtsmarkt.class, new SortedList.Callback<Weihnachtsmarkt>() {

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public int compare(Weihnachtsmarkt a, Weihnachtsmarkt b) {
            return mComparator.compare(a, b);
        }

        @Override
        public boolean areContentsTheSame(Weihnachtsmarkt oldItem, Weihnachtsmarkt newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Weihnachtsmarkt item1, Weihnachtsmarkt item2) {
            return item1.id().equals(item2.id());
        }
    });
}
