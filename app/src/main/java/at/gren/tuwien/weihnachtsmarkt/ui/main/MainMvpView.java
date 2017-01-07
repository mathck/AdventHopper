package at.gren.tuwien.weihnachtsmarkt.ui.main;

import java.util.List;

import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.ui.base.MvpView;

public interface MainMvpView extends MvpView {

    void showAdventmaerkte(List<Weihnachtsmarkt> märkte);

    void showAdventmaerkteEmpty();

    void showError();

}
