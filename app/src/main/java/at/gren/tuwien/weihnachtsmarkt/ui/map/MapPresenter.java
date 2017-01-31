package at.gren.tuwien.weihnachtsmarkt.ui.map;

import android.location.Location;

import java.util.List;

import javax.inject.Inject;

import at.gren.tuwien.weihnachtsmarkt.data.DataManager;
import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.ui.base.BasePresenter;
import at.gren.tuwien.weihnachtsmarkt.util.RxUtil;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MapPresenter  extends BasePresenter<MapMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public MapPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(MapMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    void loadMärkte() {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getMärkte()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Weihnachtsmarkt>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Fehler beim Laden der Weihnachtsmärkte.");
                        getMvpView().showError();
                    }

                    @Override
                    public void onNext(List<Weihnachtsmarkt> märkte) {
                        if (!märkte.isEmpty()) {
                            getMvpView().showAdventmaerkteOnMap(märkte);
                        }
                    }
                });
    }

    void storeLocation(Location lastLocation) {
        mDataManager.getPreferencesHelper().storeLocation(lastLocation);
    }

    boolean userHasLocation() {
        return mDataManager.getPreferencesHelper().hasLocation();
    }

    double getUserLat() {
        return mDataManager.getPreferencesHelper().getLocationLatitude();
    }

    double getUserLng() {
        return mDataManager.getPreferencesHelper().getLocationLongitude();
    }
}
