package at.gren.tuwien.weihnachtsmarkt.data.local;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

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

    public Observable<at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt> setMärkte(final Collection<at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt> newMärkte) {
        return Observable.create(new Observable.OnSubscribe<at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt>() {
            @Override
            public void call(Subscriber<? super at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt> subscriber) {
                if (subscriber.isUnsubscribed()) return;
                BriteDatabase.Transaction transaction = mDb.newTransaction();
                try {
                    mDb.delete(Db.Weihnachtsmarkt.TABLE_NAME, null);
                    for (at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt weihnachtsmarkt : newMärkte) {
                        long result = mDb.insert(Db.Weihnachtsmarkt.TABLE_NAME,
                                Db.Weihnachtsmarkt.toContentValues(weihnachtsmarkt),
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

    public Observable<List<at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt>> getMärkte() {
        return mDb.createQuery(Db.Weihnachtsmarkt.TABLE_NAME,
                "SELECT * FROM " + Db.Weihnachtsmarkt.TABLE_NAME)
                .mapToList(new Func1<Cursor, at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt>() {
                    @Override
                    public at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt call(Cursor cursor) {
                        at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt weihnachtsmarkt = Db.Weihnachtsmarkt.parseCursor(cursor);
                        return at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt.create(weihnachtsmarkt.type(), weihnachtsmarkt.id(), weihnachtsmarkt.geometry(), weihnachtsmarkt.geometry_name(), weihnachtsmarkt.properties());
                    }
                });
    }

    public void updateRatings(Map ratings) {
        // TODO insert ratings into db
        //mDb.update(Db.Weihnachtsmarkt.TABLE_NAME, )
    }
}
