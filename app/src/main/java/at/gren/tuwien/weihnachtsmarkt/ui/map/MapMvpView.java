package at.gren.tuwien.weihnachtsmarkt.ui.map;

import java.util.List;

import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.ui.base.MvpView;

/**
 * Created by Michael on 30.01.2017.
 */

public interface MapMvpView extends MvpView {

    void showAdventmaerkteOnMap(List<Weihnachtsmarkt> märkte);

    void showError();
}
