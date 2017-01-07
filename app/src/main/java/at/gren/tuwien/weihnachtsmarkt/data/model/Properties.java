package at.gren.tuwien.weihnachtsmarkt.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Properties implements Parcelable {

    public abstract int OBJECTID();
    public abstract String BEZEICHNUNG();
    public abstract String ADRESSE();
    public abstract String DATUM();
    public abstract String OEFFNUNGSZEIT();
    public abstract String WEBLINK1();
    public abstract int SILVESTERMARKT();

    public static Properties create(int OBJECTID, String BEZEICHNUNG, String ADRESSE, String DATUM, String OEFFNUNGSZEIT, String WEBLINK1, int SILVESTERMARKT) {
        return new AutoValue_Properties(OBJECTID, BEZEICHNUNG, ADRESSE, DATUM, OEFFNUNGSZEIT, WEBLINK1, SILVESTERMARKT);
    }

    public static TypeAdapter<Properties> typeAdapter(Gson gson) {
        return new AutoValue_Properties.GsonTypeAdapter(gson);
    }

    /*
    public static Builder builder() {
        return new AutoValue_Properties.Builder();
    }
    */

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder OBJECTID(String OBJECTID);
        public abstract Builder BEZEICHNUNG(String BEZEICHNUNG);
        public abstract Builder ADRESSE(String ADRESSE);
        public abstract Builder DATUM(String DATUM);
        public abstract Builder OEFFNUNGSZEIT(String OEFFNUNGSZEIT);
        public abstract Builder WEBLINK1(String WEBLINK1);
        public abstract Builder SILVESTERMARKT(int SILVESTERMARKT);
        public abstract Properties build();
    }
}
