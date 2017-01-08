package at.gren.tuwien.weihnachtsmarkt.data.local;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

import at.gren.tuwien.weihnachtsmarkt.data.model.Point;
import at.gren.tuwien.weihnachtsmarkt.data.model.Properties;

public class Db {

    public Db() { }

    public abstract static class Weihnachtsmarkt {
        public static final String TABLE_NAME = "weihnachtsmarkt";

        public static final String COLUMN_OBJECTID = "object_id";
        public static final String COLUMN_BEZEICHNUNG = "bezeichnung";
        public static final String COLUMN_ADRESSE = "adresse";
        public static final String COLUMN_DATUM = "datum";
        public static final String COLUMN_OEFFNUNGSZEIT = "oeffnungszeit";
        public static final String COLUMN_WEBLINK1 = "weblink1";
        public static final String COLUMN_SILVESTERMARKT = "silvestermarkt";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";

        public static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_OBJECTID             + " TEXT PRIMARY KEY, " +
                        COLUMN_BEZEICHNUNG          + " TEXT, " +
                        COLUMN_ADRESSE              + " TEXT, " +
                        COLUMN_DATUM                + " TEXT, " +
                        COLUMN_OEFFNUNGSZEIT        + " TEXT, " +
                        COLUMN_WEBLINK1             + " TEXT, " +
                        COLUMN_SILVESTERMARKT       + " INTEGER, " +
                        COLUMN_LATITUDE             + " TEXT, " +
                        COLUMN_LONGITUDE            + " TEXT" +
                        " ); ";

        public static ContentValues toContentValues(at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt profile) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_OBJECTID, profile.id());
            values.put(COLUMN_BEZEICHNUNG, profile.properties().BEZEICHNUNG());
            values.put(COLUMN_ADRESSE, profile.properties().ADRESSE());
            values.put(COLUMN_DATUM, profile.properties().DATUM());
            values.put(COLUMN_OEFFNUNGSZEIT, profile.properties().OEFFNUNGSZEIT());
            values.put(COLUMN_WEBLINK1, profile.properties().WEBLINK1());
            values.put(COLUMN_SILVESTERMARKT, profile.properties().SILVESTERMARKT());
            values.put(COLUMN_LATITUDE, profile.geometry().coordinates().get(0));
            values.put(COLUMN_LONGITUDE, profile.geometry().coordinates().get(1));
            return values;
        }

        public static at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt parseCursor(final Cursor cursor) {

            String object_id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OBJECTID));
            String bezeichnung = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BEZEICHNUNG));
            String adresse = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADRESSE));
            String datum = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATUM));
            String oeffnungszeit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OEFFNUNGSZEIT));
            String weblink1 = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEBLINK1));
            String silvestermarkt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SILVESTERMARKT));
            ArrayList<Double> coordinates =  new ArrayList<Double>() {{
                add(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))));
                add(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE))));
            }};

            Point geometry = Point.create("POINT", coordinates);

            Properties prop = at.gren.tuwien.weihnachtsmarkt.data.model.Properties.builder()
                    .setOBJECTID(object_id)
                    .setBEZEICHNUNG(bezeichnung)
                    .setADRESSE(adresse)
                    .setDATUM(datum)
                    .setOEFFNUNGSZEIT(oeffnungszeit)
                    .setWEBLINK1(weblink1)
                    .setSILVESTERMARKT(Integer.parseInt(silvestermarkt))
                    .build();

            return at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt.builder()
                    .setId(object_id)
                    .setType("Feature")
                    .setGeometry(geometry)
                    .setGeometry_name("SHAPE")
                    .setProperties(prop)
                    .build();
        }
    }
}
