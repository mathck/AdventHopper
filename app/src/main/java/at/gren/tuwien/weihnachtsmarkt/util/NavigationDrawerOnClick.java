package at.gren.tuwien.weihnachtsmarkt.util;

/**
 * Created by Michael on 26.01.2017.
 */

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

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

        switch ((String) textView.getTag()) {
            case "2":
                intent = new Intent(mContext, MainActivity.class);
                //TODO: Navigation to MapView
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
                break;
            default:
                intent = new Intent(mContext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
                break;
        }
    }
}
