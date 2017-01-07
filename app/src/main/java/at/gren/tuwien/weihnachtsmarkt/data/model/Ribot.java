package at.gren.tuwien.weihnachtsmarkt.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Ribot implements Parcelable {

    public abstract Profile profile();

    public static Ribot create(Profile weihnachtsmarkt) {
        return new AutoValue_Ribot(weihnachtsmarkt);
    }

    public static TypeAdapter<Ribot> typeAdapter(Gson gson) {
        return new AutoValue_Ribot.GsonTypeAdapter(gson);
    }
}

