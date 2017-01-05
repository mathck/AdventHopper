package at.gren.tuwien.weihnachtsmarkt.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Feature implements Parcelable {
    public abstract String type();
    public abstract String id();
    public abstract Point geometry();
    public abstract String geometry_name();
    public abstract Properties properties();

    public static Feature create(String type, String id, Point geometry, String geometry_name, Properties properties) {
        return new AutoValue_Feature(type, id, geometry, geometry_name, properties);
    }

    public static TypeAdapter<Feature> typeAdapter(Gson gson) {
        return new AutoValue_Feature.GsonTypeAdapter(gson);
    }
}
