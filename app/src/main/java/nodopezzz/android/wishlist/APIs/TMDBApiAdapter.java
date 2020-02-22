package nodopezzz.android.wishlist.APIs;

import java.util.List;

import nodopezzz.android.wishlist.Models.ActorsResult;
import nodopezzz.android.wishlist.Models.ImageResult;
import nodopezzz.android.wishlist.Models.Movie;
import nodopezzz.android.wishlist.Models.SearchMovieResult;
import nodopezzz.android.wishlist.Models.SearchTVResult;
import nodopezzz.android.wishlist.Models.Season;
import nodopezzz.android.wishlist.Models.TVShow;
import nodopezzz.android.wishlist.Models.VideoResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDBApiAdapter {
    @GET("search/movie")
    public Call<SearchMovieResult> searchItemMovie(@Query("query") String query, @Query("page") int page);

    @GET("search/tv")
    public Call<SearchTVResult> searchItemTV(@Query("query") String query, @Query("page") int page);

    @GET("movie/{id}")
    public Call<Movie> getMovie(@Path("id") String id);

    @GET("tv/{id}")
    public Call<TVShow> getShow(@Path("id") String id);

    @GET("movie/{id}/credits")
    public Call<ActorsResult> getMovieActors(@Path("id") String id);

    @GET("tv/{id}/credits")
    public Call<ActorsResult> getTVActors(@Path("id") String id);

    @GET("movie/{id}/images")
    public Call<ImageResult> getMovieImages(@Path("id") String id);

    @GET("tv/{id}/images")
    public Call<ImageResult> getTVImages(@Path("id") String id);

    @GET("movie/{id}/videos")
    public Call<VideoResponse> getMovieVideos(@Path("id") String id, @Query("language") String language);

    @GET("tv/{id}/videos")
    public Call<VideoResponse> getTVVideos(@Path("id") String id, @Query("language") String language);

    @GET("tv/{id}/season/{number}")
    public Call<Season> getSeason(@Path("id") String id, @Path("number") String number);
}
