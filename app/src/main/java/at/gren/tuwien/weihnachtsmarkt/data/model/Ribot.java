package at.gren.tuwien.weihnachtsmarkt.data.model;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Ribot implements Comparable<Ribot>, Parcelable {

    public abstract Weihnachtsmarkt weihnachtsmarkt();

    public static Ribot create(Weihnachtsmarkt weihnachtsmarkt) {
        return new AutoValue_Ribot(weihnachtsmarkt);
    }

    public static TypeAdapter<Ribot> typeAdapter(Gson gson) {
        return new AutoValue_Ribot.GsonTypeAdapter(gson);
    }

    @Override
    public int compareTo(@NonNull Ribot another) {
        return weihnachtsmarkt().id().compareToIgnoreCase(another.weihnachtsmarkt().id());
    }
}

