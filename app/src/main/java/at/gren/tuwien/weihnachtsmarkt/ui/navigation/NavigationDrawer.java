package at.gren.tuwien.weihnachtsmarkt.ui.navigation;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.StringRes;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.Toolbar;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import at.gren.tuwien.weihnachtsmarkt.R;
import at.gren.tuwien.weihnachtsmarkt.ui.base.BaseActivity;
import at.gren.tuwien.weihnachtsmarkt.ui.main.MainActivity;
import at.gren.tuwien.weihnachtsmarkt.ui.map.MapActivity;

public class NavigationDrawer {

    public Drawer build(BaseActivity context, Toolbar toolbar) {

        Drawer result = new DrawerBuilder()
            .withActivity(context)
            .withToolbar(toolbar)
            .withHeader(R.layout.drawer_header)
            .addDrawerItems(
                    createNavbarItem(context, R.string.title_cardView, new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_place)),
                    createNavbarItem(context, R.string.title_bestRated, new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_star)),
                    createNavbarItem(context, R.string.title_map, new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_map)),
                    new DividerDrawerItem(),
                    new SecondaryDrawerItem().withName(R.string.others),
                    createNavbarItem(context, R.string.settings, new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_settings)).withEnabled(false).withBadge("bald verfügbar!"),
                    createNavbarItem(context, R.string.opensource, new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_code)),
                    createNavbarItem(context, R.string.contact, new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_mail))
            )
            .withOnDrawerItemClickListener((view, position, drawerItem) -> {

                Intent intent;

                switch ((int) drawerItem.getIdentifier()) {

                    case 1:
                        intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(context, MapActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                        break;
                    case 4:
                        break;
                    case 5:
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mathck/AdventHopper"));
                        context.startActivity(intent);
                        break;
                    case 6:
                        ShareCompat.IntentBuilder.from(context)
                                .setType("message/rfc822")
                                .addEmailTo("mateusz@gren.at")
                                .setSubject("Weihnachtsmarkt App")
                                .setChooserTitle("E-Mail versenden mit ...")
                                .startChooser();
                        break;
                    default:
                        intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);

                        break;
                }

                return true;
            })
            .build();

        context.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        return result;
    }

    private int mNavbarItemId = 1;
    private PrimaryDrawerItem createNavbarItem(Context context, @StringRes int stringId, Drawable drawable) {
        return new PrimaryDrawerItem()
                .withIdentifier(mNavbarItemId++)
                .withName(stringId)
                .withIcon(drawable)
                .withIconTintingEnabled(true)
                .withIconColor(context.getResources().getColor(R.color.grey_600))
                .withSelectedIconColor(context.getResources().getColor(R.color.blue_500));
    }
}
