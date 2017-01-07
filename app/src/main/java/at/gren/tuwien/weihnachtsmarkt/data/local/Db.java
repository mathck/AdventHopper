package at.gren.tuwien.weihnachtsmarkt.data.local;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;

import at.gren.tuwien.weihnachtsmarkt.data.model.Name;
import at.gren.tuwien.weihnachtsmarkt.data.model.Point;
import at.gren.tuwien.weihnachtsmarkt.data.model.Profile;
import at.gren.tuwien.weihnachtsmarkt.data.model.Properties;

import static at.gren.tuwien.weihnachtsmarkt.data.local.Db.RibotProfileTable.COLUMN_FIRST_NAME;

public class Db {

    public Db() { }

    public abstract static class RibotProfileTable {
        public static final String TABLE_NAME = "ribot_profile";

        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_HEX_COLOR = "hex_color";
        public static final String COLUMN_DATE_OF_BIRTH = "date_of_birth";
        public static final String COLUMN_AVATAR = "avatar";
        public static final String COLUMN_BIO = "bio";

        public static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_EMAIL + " TEXT PRIMARY KEY, " +
                        COLUMN_FIRST_NAME + " TEXT NOT NULL, " +
                        COLUMN_LAST_NAME + " TEXT NOT NULL, " +
                        COLUMN_HEX_COLOR + " TEXT NOT NULL, " +
                        COLUMN_DATE_OF_BIRTH + " INTEGER NOT NULL, " +
                        COLUMN_AVATAR + " TEXT, " +
                        COLUMN_BIO + " TEXT" +
                " ); ";

        public static ContentValues toContentValues(Profile profile) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_EMAIL, profile.email());
            values.put(COLUMN_FIRST_NAME, profile.name().first());
            values.put(COLUMN_LAST_NAME, profile.name().last());
            values.put(COLUMN_HEX_COLOR, profile.hexColor());
            values.put(COLUMN_DATE_OF_BIRTH, profile.dateOfBirth().getTime());
            values.put(COLUMN_AVATAR, profile.avatar());
            if (profile.bio() != null) values.put(COLUMN_BIO, profile.bio());
            return values;
        }

        public static Profile parseCursor(Cursor cursor) {
            Name name = Name.create(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)));
            long dobTime = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE_OF_BIRTH));

            return Profile.builder()
                    .setName(name)
                    .setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)))
                    .setHexColor(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HEX_COLOR)))
                    .setDateOfBirth(new Date(dobTime))
                    .setAvatar(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR)))
                    .setBio(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIO)))
                    .build();
        }
    }

    // TODO table cols
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
                        COLUMN_BEZEICHNUNG          + " TEXT" +
                        COLUMN_ADRESSE              + " TEXT" +
                        COLUMN_DATUM                + " TEXT" +
                        COLUMN_OEFFNUNGSZEIT        + " TEXT" +
                        COLUMN_WEBLINK1             + " TEXT" +
                        COLUMN_SILVESTERMARKT       + " TEXT" +
                        COLUMN_LATITUDE             + " TEXT" +
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
            values.put(COLUMN_LATITUDE, profile.geometry().coordinates()[0]);
            values.put(COLUMN_LONGITUDE, profile.geometry().coordinates()[1]);
            return values;
        }

        public static at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt parseCursor(Cursor cursor) {


            String object_id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OBJECTID));
            String bezeichnung = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BEZEICHNUNG));
            String adresse = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADRESSE));
            String datum = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATUM));
            String oeffnungszeit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OEFFNUNGSZEIT));
            String weblink1 = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEBLINK1));
            String silvestermarkt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SILVESTERMARKT));
            double[] coordinates = {Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE))),Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE)))};

            Point geometry = Point.create("",coordinates);

            Properties prop = at.gren.tuwien.weihnachtsmarkt.data.model.Properties.builder()
                    .setOBJECTID(Integer.parseInt(object_id))
                    .setBEZEICHNUNG(bezeichnung)
                    .setADRESSE(adresse)
                    .setDATUM(datum)
                    .setOEFFNUNGSZEIT(oeffnungszeit)
                    .setWEBLINK1(weblink1)
                    .setSILVESTERMARKT(Integer.parseInt(silvestermarkt))
                    .build();

            return at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt.builder()
                    .setId(object_id)
                    .setGeometry(geometry)
                    .setProperties(prop)
                    .build();
        }
    }
}
