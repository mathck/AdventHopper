package at.gren.tuwien.weihnachtsmarkt.ui.map;

import java.util.List;

import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.ui.base.MvpView;

interface MapMvpView extends MvpView {

    void showAdventmaerkteOnMap(List<Weihnachtsmarkt> märkte);

    void showError();
}
