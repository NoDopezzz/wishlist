package nodopezzz.android.wishlist.APIs;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nodopezzz.android.wishlist.Models.Book;
import nodopezzz.android.wishlist.Models.SearchItem;
import nodopezzz.android.wishlist.Network.UrlDownloader;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleBooksAPI {
    public static final String TAG = "GoogleBooksAPI";

    public static final Uri ENDPOINT = Uri.parse("https://www.googleapis.com/books/v1/volumes/");

    private static GoogleBooksAPI mInstance;
    public static GoogleBooksAPI getInstance(){
        if(mInstance == null){
            mInstance = new GoogleBooksAPI();
        }
        return mInstance;
    }

    private Retrofit mRetrofit;

    private GoogleBooksAPI(){
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        HttpUrl url = chain.request().url();
                        Log.i("Retrofit", url.toString());
                        return chain.proceed(chain.request());
                    }
                }).build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT.toString())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public GoogleBooksAPIAdapter getAdapter(){
        return mRetrofit.create(GoogleBooksAPIAdapter.class);
    }
}
