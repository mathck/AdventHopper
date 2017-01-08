package at.gren.tuwien.weihnachtsmarkt.data.remote;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URL;

import javax.inject.Inject;

import at.gren.tuwien.weihnachtsmarkt.data.model.FeatureCollection;
import at.gren.tuwien.weihnachtsmarkt.util.MyGsonTypeAdapterFactory;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import rx.Observable;

public interface GovernmentDataService {

    //String ENDPOINT = "http://data.wien.gv.at/daten/";
    String ENDPOINT = "http://homepage.univie.ac.at/a0302840/inf/";

    //@GET("geo?service=WFS&request=GetFeature&version=1.1.0&typeName=ogdwien:ADVENTMARKTOGD&srsName=EPSG:4326&outputFormat=json")
    @GET("adventmarkt.json")
    Observable<FeatureCollection> getWeihnachtsmärkteUndSilvesterständeWien();

    /******** Helper class that sets up a new services *******/
    class Creator {
        public static GovernmentDataService newGovernmentDataService(Context context) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(GovernmentDataService.ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            return retrofit.create(GovernmentDataService.class);
        }
    }
}
