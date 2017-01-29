package at.gren.tuwien.weihnachtsmarkt.util;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import at.gren.tuwien.weihnachtsmarkt.R;

/**
 * Created by Michael on 26.01.2017.
 */

public class NavigationDrawer {

    private Activity mActivity;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private Drawable mDrawable;

    public NavigationDrawer(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar){
        this.mActivity = activity;
        this.mDrawerLayout = drawerLayout;
        this.mToolbar = toolbar;
        this.mDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.mipmap.ic_launcher, activity.getTheme());
    }

    public void setNavigationDrawer(){
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(mActivity,mDrawerLayout,mToolbar,R.string.app_name,R.string.app_name);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerToggle.setHomeAsUpIndicator(mDrawable);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }
}
