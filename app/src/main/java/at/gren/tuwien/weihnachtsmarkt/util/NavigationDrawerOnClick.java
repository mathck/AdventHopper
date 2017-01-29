package at.gren.tuwien.weihnachtsmarkt.util;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import at.gren.tuwien.weihnachtsmarkt.R;
import at.gren.tuwien.weihnachtsmarkt.ui.main.MainActivity;

public class NavigationDrawerOnClick implements TextView.OnClickListener {
    private final TextView textView;
    private final Context mContext;

    public NavigationDrawerOnClick(TextView textView,Context context) {
        this.textView = textView;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        textView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey_800));

        switch ((String) textView.getTag()) {

            case "1":
                intent = new Intent(mContext, MainActivity.class);
                break;
            case "2":
                intent = new Intent(mContext, MainActivity.class);
                //TODO: Navigation to MapView
                break;
            default:
                intent = new Intent(mContext, MainActivity.class);
                break;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }
}
