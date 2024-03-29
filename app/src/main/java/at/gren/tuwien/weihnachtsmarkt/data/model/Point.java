package at.gren.tuwien.weihnachtsmarkt.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.util.List;

@AutoValue
public abstract class Point implements Parcelable {
    public abstract String type();
    public abstract List<Double> coordinates();

    public static Point create(String type, List<Double> position) {
        return new AutoValue_Point(type, position);
    }

    public static TypeAdapter<Point> typeAdapter(Gson gson) {
        return new AutoValue_Point.GsonTypeAdapter(gson);
    }

}
