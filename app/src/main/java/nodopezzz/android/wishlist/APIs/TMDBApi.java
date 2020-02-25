package nodopezzz.android.wishlist.APIs;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TMDBApi {

    private static TMDBApi mInstance;
    public static TMDBApi getInstance(){
        if(mInstance == null){
            mInstance = new TMDBApi();
        }
        return mInstance;
    }

    private static final String API_KEY = "23c8bc74a45d2a77180c84c78e5cbccb";
    private static final Uri ENDPOINT = Uri.parse("https://api.themoviedb.org/3/");
    public static final String IMAGE_URL_ENDPOINT_THUMBNAIL = "https://image.tmdb.org/t/p/w260_and_h390_bestv2";
    public static final String IMAGE_URL_ENDPOINT_ORIGINAL = "https://image.tmdb.org/t/p/original";
    public static final String YOUTUBE_ENDPOINT = "https://www.youtube.com/watch";

    private static final String METHOD_SEARCH = "search";
    private static final String METHOD_IMAGES = "images";
    private static final String METHOD_CREDITS = "credits";
    private static final String METHOD_VIDEO = "videos";
    private static final String METHOD_SEASON = "season";

    public static final String CONTENT_MOVIE = "movie";
    public static final String CONTENT_TV = "tv";

    private Retrofit mRetrofit;

    private TMDBApi(){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl url;
                if(!originalHttpUrl.toString().contains("images") && !originalHttpUrl.toString().contains("videos")){
                    url = originalHttpUrl.newBuilder()
                            .addQueryParameter("api_key", API_KEY)
                            .addQueryParameter("language", "ru")
                            .build();
                } else {
                    url = originalHttpUrl.newBuilder()
                            .addQueryParameter("api_key", API_KEY)
                            .build();
                }

                Request.Builder requestBuilder = original.newBuilder().url(url);
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        mRetrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT.toString())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }

    public TMDBApiAdapter getAdapter(){
        return mRetrofit.create(TMDBApiAdapter.class);
    }
}
