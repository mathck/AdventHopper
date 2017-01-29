package at.gren.tuwien.weihnachtsmarkt.ui.detailed;

import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.ui.base.MvpView;

interface DetailedMvpView extends MvpView {

    void showAdventmarkt(Weihnachtsmarkt markt);

    void showError();
}
