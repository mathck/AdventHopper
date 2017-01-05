package at.gren.tuwien.weihnachtsmarkt.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.util.List;

@AutoValue
public abstract class FeatureCollection implements Parcelable {

    public abstract String type();
    public abstract int totalFeatures();
    public abstract List<Feature> features();

    public static FeatureCollection create(String type, int totalFeatures, List<Feature> features) {
        return new AutoValue_FeatureCollection(type, totalFeatures, features);
    }

    public static TypeAdapter<FeatureCollection> typeAdapter(Gson gson) {
        return new AutoValue_FeatureCollection.GsonTypeAdapter(gson);
    }
}

