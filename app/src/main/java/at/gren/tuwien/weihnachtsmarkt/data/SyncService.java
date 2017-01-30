package at.gren.tuwien.weihnachtsmarkt.data;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import at.gren.tuwien.weihnachtsmarkt.BoilerplateApplication;
import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.util.AndroidComponentUtil;
import at.gren.tuwien.weihnachtsmarkt.util.NetworkUtil;
import at.gren.tuwien.weihnachtsmarkt.util.events.SyncCompletedEvent;
import rx.Observer;
import rx.Subscription;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SyncService extends Service {

    @Inject DataManager mDataManager;
    private Subscription mSubscription;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, SyncService.class);
    }

    public static boolean isRunning(Context context) {
        return AndroidComponentUtil.isServiceRunning(context, SyncService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BoilerplateApplication.get(this).getComponent().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        Timber.i("Starting sync...");

        if (!NetworkUtil.isNetworkConnected(this)) {
            Timber.i("Sync canceled, connection not available");
            AndroidComponentUtil.toggleComponent(this, SyncOnConnectionAvailable.class, true);
            stopSelf(startId);
            return START_NOT_STICKY;
        }

        if (mSubscription != null && !mSubscription.isUnsubscribed())
            mSubscription.unsubscribe();

        mSubscription = mDataManager.syncMärkte()
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Weihnachtsmarkt>() {
                    @Override
                    public void onCompleted() {
                        EventBus.getDefault().post(new SyncCompletedEvent());
                        stopSelf(startId);
                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new SyncCompletedEvent());
                        stopSelf(startId);
                    }

                    @Override
                    public void onNext(Weihnachtsmarkt markt) {
                    }
                });

        mDataManager.syncRatings();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mSubscription != null) mSubscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static class SyncOnConnectionAvailable extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)
                    && NetworkUtil.isNetworkConnected(context)) {
                Timber.i("Connection is now available, triggering sync...");
                AndroidComponentUtil.toggleComponent(context, this.getClass(), false);
                context.startService(getStartIntent(context));
            }
        }
    }

}