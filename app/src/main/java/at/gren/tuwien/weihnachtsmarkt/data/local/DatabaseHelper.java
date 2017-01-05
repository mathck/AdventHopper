package at.gren.tuwien.weihnachtsmarkt.data.local;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import at.gren.tuwien.weihnachtsmarkt.data.model.Ribot;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

@Singleton
public class DatabaseHelper {

    private final BriteDatabase mDb;

    @Inject
    public DatabaseHelper(DbOpenHelper dbOpenHelper) {
        SqlBrite.Builder briteBuilder = new SqlBrite.Builder();
        mDb = briteBuilder.build().wrapDatabaseHelper(dbOpenHelper, Schedulers.immediate());
    }

    public BriteDatabase getBriteDb() {
        return mDb;
    }

    public Observable<Ribot> setRibots(final Collection<Ribot> newRibots) {
        return Observable.create(new Observable.OnSubscribe<Ribot>() {
            @Override
            public void call(Subscriber<? super Ribot> subscriber) {
                if (subscriber.isUnsubscribed()) return;
                BriteDatabase.Transaction transaction = mDb.newTransaction();
                try {
                    mDb.delete(Db.RibotProfileTable.TABLE_NAME, null);
                    for (Ribot ribot : newRibots) {
                        long result = mDb.insert(Db.RibotProfileTable.TABLE_NAME,
                                Db.RibotProfileTable.toContentValues(ribot.profile()),
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (result >= 0) subscriber.onNext(ribot);
                    }
                    transaction.markSuccessful();
                    subscriber.onCompleted();
                } finally {
                    transaction.end();
                }
            }
        });
    }

    public Observable<List<Ribot>> getRibots() {
        return mDb.createQuery(Db.RibotProfileTable.TABLE_NAME,
                "SELECT * FROM " + Db.RibotProfileTable.TABLE_NAME)
                .mapToList(new Func1<Cursor, Ribot>() {
                    @Override
                    public Ribot call(Cursor cursor) {
                        return Ribot.create(Db.RibotProfileTable.parseCursor(cursor));
                    }
                });
    }

    public Observable<Weihnachtsmarkt> setMärkte(final Collection<Weihnachtsmarkt> newMärkte) {
        return Observable.create(new Observable.OnSubscribe<Weihnachtsmarkt>() {
            @Override
            public void call(Subscriber<? super Weihnachtsmarkt> subscriber) {
                if (subscriber.isUnsubscribed()) return;
                BriteDatabase.Transaction transaction = mDb.newTransaction();
                try {
                    mDb.delete(Db.FeatureTable.TABLE_NAME, null);
                    for (Weihnachtsmarkt weihnachtsmarkt : newMärkte) {
                        long result = mDb.insert(Db.RibotProfileTable.TABLE_NAME,
                                Db.FeatureTable.toContentValues(weihnachtsmarkt),
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (result >= 0) subscriber.onNext(weihnachtsmarkt);
                    }
                    transaction.markSuccessful();
                    subscriber.onCompleted();
                } finally {
                    transaction.end();
                }
            }
        });
    }

    public Observable<List<Weihnachtsmarkt>> getMärkte() {
        return mDb.createQuery(Db.FeatureTable.TABLE_NAME,
                "SELECT * FROM " + Db.FeatureTable.TABLE_NAME)
                .mapToList(new Func1<Cursor, Weihnachtsmarkt>() {
                    @Override
                    public Weihnachtsmarkt call(Cursor cursor) {
                        Weihnachtsmarkt weihnachtsmarkt = Db.FeatureTable.parseCursor(cursor);
                        return Weihnachtsmarkt.create(weihnachtsmarkt.type(), weihnachtsmarkt.id(), weihnachtsmarkt.geometry(), weihnachtsmarkt.geometry_name(), weihnachtsmarkt.properties());
                    }
                });
    }
}
