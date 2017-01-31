package at.gren.tuwien.weihnachtsmarkt.ui.detailed;

import javax.inject.Inject;

import at.gren.tuwien.weihnachtsmarkt.data.DataManager;
import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.ui.base.BasePresenter;
import at.gren.tuwien.weihnachtsmarkt.util.RxUtil;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DetailedPresenter extends BasePresenter<DetailedMvpView>{
    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public DetailedPresenter(DataManager dataManager) {
        this.mDataManager = dataManager;
    }

    @Override
    public void attachView(DetailedMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    void loadMarkt(String key) {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getMarkt(key)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new Subscriber<Weihnachtsmarkt>() {
                @Override
                public void onCompleted() {
                }
                @Override
                public void onError(Throwable e) {
                    getMvpView().showError();
                }

                @Override
                public void onNext(Weihnachtsmarkt markt) {
                    if (markt == null) {
                        getMvpView().showError();
                    } else {
                        getMvpView().showAdventmarkt(markt);
                    }
                }
            });
    }

}
