package at.gren.tuwien.weihnachtsmarkt.ui.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        Weihnachtsmarkt markt = mWeihnachtsmarkts.get(position);
        //holder.hexColorView.setBackgroundColor(Color.parseColor(markt.profile().hexColor()));
        holder.title.setText(markt.properties().BEZEICHNUNG());
        //holder.emailTextView.setText(markt.profile().email());
    }

    @Override
    public int getItemCount() {
        return mWeihnachtsmarkts.size();
    }

    class RibotViewHolder extends RecyclerView.ViewHolder {

        //@BindView(R.id.view_hex_color) View hexColorView;
        @BindView(R.id.title) TextView title;
        //@BindView(R.id.text_email) TextView emailTextView;

        public RibotViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
