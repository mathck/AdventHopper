package at.gren.tuwien.weihnachtsmarkt.ui.main;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import at.gren.tuwien.weihnachtsmarkt.R;
import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WeihnachtsmarktAdapter extends RecyclerView.Adapter<WeihnachtsmarktAdapter.RibotViewHolder> {

    private List<Weihnachtsmarkt> mWeihnachtsmarkts;

    @Inject
    public WeihnachtsmarktAdapter() {
        mWeihnachtsmarkts = new ArrayList<>();
    }

    public void setWeihnachtsmarkts(List<Weihnachtsmarkt> weihnachtsmarkts) {
        mWeihnachtsmarkts = weihnachtsmarkts;
    }

    @Override
    public RibotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.market_card_view, parent, false);
        return new RibotViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RibotViewHolder holder, int position) {
        final Weihnachtsmarkt markt = mWeihnachtsmarkts.get(position);

        // TODO: Get values from SharedPreferences Longitude/Latitude
        double longitudeSharedPreferences = 48.173333333333;
        double latitudeSharedPreferences = 16.413888888889;

        holder.distance.setText(getDistance(markt.geometry().coordinates().get(0),markt.geometry().coordinates().get(1),longitudeSharedPreferences,latitudeSharedPreferences));
        holder.title.setText(markt.properties().BEZEICHNUNG());
        holder.shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Schau dir diesen tollen Weihnachtsmarkt an " + markt.properties().BEZEICHNUNG());
                sendIntent.setType("text/plain");
                holder.itemView.getContext().startActivity(sendIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWeihnachtsmarkts.size();
    }

    class RibotViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.shareIcon) ImageView shareIcon;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.distance) TextView distance;

        public RibotViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public String getDistance (double lat_a, double lng_a, double lat_b, double lng_b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        long meterConversion = Math.round(distance * 1609);

        return Long.toString(meterConversion);
    }
}
