package at.gren.tuwien.weihnachtsmarkt.ui.main;

import android.location.Location;

import java.util.List;

import javax.inject.Inject;

import at.gren.tuwien.weihnachtsmarkt.data.DataManager;
import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.injection.ConfigPersistent;
import at.gren.tuwien.weihnachtsmarkt.ui.base.BasePresenter;
import at.gren.tuwien.weihnachtsmarkt.util.RxUtil;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@ConfigPersistent
public class MainPresenter extends BasePresenter<MainMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public MainPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(MainMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadMärkte() {
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
                        if (märkte.isEmpty()) {
                            getMvpView().showAdventmaerkteEmpty();
                        } else {
                            getMvpView().showAdventmaerkte(märkte);
                        }
                    }
                });
    }

    void storeLocation(Location lastLocation) {
        mDataManager.getPreferencesHelper().storeLocation(lastLocation);
    }
}
