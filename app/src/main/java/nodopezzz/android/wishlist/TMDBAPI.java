package nodopezzz.android.wishlist;

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

import nodopezzz.android.wishlist.Models.Actor;
import nodopezzz.android.wishlist.Models.Episode;
import nodopezzz.android.wishlist.Models.MediaContent;
import nodopezzz.android.wishlist.Models.Movie;
import nodopezzz.android.wishlist.Models.SearchItem;
import nodopezzz.android.wishlist.Models.Season;
import nodopezzz.android.wishlist.Models.TVShow;
import nodopezzz.android.wishlist.Network.UrlDownloader;

public class TMDBAPI {
    private static final String TAG = "TMDBAPI";

    private static final String API_KEY = "23c8bc74a45d2a77180c84c78e5cbccb";
    private static final Uri ENDPOINT = Uri.parse("https://api.themoviedb.org/3");
    private static final String IMAGE_URL_ENDPOINT_THUMBNAIL = "https://image.tmdb.org/t/p/w260_and_h390_bestv2";
    private static final String IMAGE_URL_ENDPOINT_ORIGINAL = "https://image.tmdb.org/t/p/original";
    private static final String YOUTUBE_ENDPOINT = "https://www.youtube.com/watch";

    private static final String METHOD_SEARCH = "search";
    private static final String METHOD_IMAGES = "images";
    private static final String METHOD_CREDITS = "credits";
    private static final String METHOD_VIDEO = "videos";
    private static final String METHOD_SEASON = "season";

    public static final String CONTENT_MOVIE = "movie";
    public static final String CONTENT_TV = "tv";

    public static List<SearchItem> search(String content, String query, int page){
        String sUrl = ENDPOINT.buildUpon()
                .appendPath(METHOD_SEARCH)
                .appendPath(content)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("language", "ru")
                .appendQueryParameter("query", query)
                .appendQueryParameter("include_adult", "false")
                .appendQueryParameter("page", Integer.toString(page))
                .toString();

        List<SearchItem> result = new ArrayList<>();

        try {
            String response = UrlDownloader.getResponse(sUrl);
            JSONArray array = new JSONObject(response).getJSONArray("results");
            int totalPage = new JSONObject(response).getInt("total_pages");
            if(page > totalPage){
                return null;
            }

            for (int i = 0; i < array.length(); i++){
                SearchItem item = parseSearchItem(array.getJSONObject(i), content);
                if(item != null) {
                    result.add(item);
                }
            }
            if(page < totalPage){
                result.add(null);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static MediaContent getContent(String content, String id){
        String sUrl = ENDPOINT.buildUpon()
                .appendPath(content)
                .appendPath(id)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("language", "ru-RU")
                .build().toString();

        try{
            String response = UrlDownloader.getResponse(sUrl);
            JSONObject object = new JSONObject(response);
            if(content.equals(CONTENT_MOVIE)) {
                return parseMovie(object);
            } else if(content.equals(CONTENT_TV)){
                return parseTV(object);
            } else{
                return null;
            }
        } catch(IOException | JSONException e){
            Log.i(TAG, e.toString());
            return null;
        }
    }

    private static List<String> getImages(String methodContent, String id){
        String url = ENDPOINT.buildUpon()
                .appendPath(methodContent)
                .appendPath(id)
                .appendPath(METHOD_IMAGES)
                .appendQueryParameter("api_key", API_KEY)
                .build().toString();
        try {
            String response = UrlDownloader.getResponse(url);
            return parseImages(new JSONObject(response));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<Actor> getActors(String methodContent, String id){
        String url = ENDPOINT.buildUpon()
                .appendPath(methodContent)
                .appendPath(id)
                .appendPath(METHOD_CREDITS)
                .appendQueryParameter("api_key", API_KEY)
                .build().toString();
        try {
            String response = UrlDownloader.getResponse(url);
            JSONObject object = new JSONObject(response);
            return parseCast(object);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getVideo(String methodContent, String id, String language){
        String url = ENDPOINT.buildUpon()
                .appendPath(methodContent)
                .appendPath(id)
                .appendPath(METHOD_VIDEO)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("language", language)
                .build().toString();
        try {
            String response = UrlDownloader.getResponse(url);
            JSONObject object = new JSONObject(response);
            return parseVideo(object);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Episode> getEpisodes(String id, String numberOfSeason){
        String url = ENDPOINT.buildUpon()
                .appendPath(CONTENT_TV)
                .appendPath(id)
                .appendPath(METHOD_SEASON)
                .appendPath(numberOfSeason)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("language", "ru-RU")
                .build().toString();
        try{
            String response = UrlDownloader.getResponse(url);
            JSONObject object = new JSONObject(response);
            return parseEpisodes(object);
        } catch(IOException | JSONException e){
            Log.i(TAG, url);
            return null;
        }
    }

    private static List<Episode> parseEpisodes(JSONObject object) throws JSONException{
        JSONArray array = object.getJSONArray("episodes");
        List<Episode> episodes = new ArrayList<>();
        for (int i = 0; i < array.length(); i++){
            Episode episode = new Episode();
            JSONObject objectEpisode = array.getJSONObject(i);

            String title = objectEpisode.getString("name");
            String overview = objectEpisode.getString("overview");
            String path = objectEpisode.getString("still_path");
            String urlImage = "";
            if(!path.equals("null")) {
                urlImage = IMAGE_URL_ENDPOINT_ORIGINAL + path;
            }

            episode.setTitle(title);
            episode.setOverview(overview);
            episode.setUrlImage(urlImage);

            episodes.add(episode);

            Log.i(TAG, episode.toString());
        }
        return episodes;
    }

    private static String parseVideo(JSONObject object) throws JSONException{
        JSONArray array = object.getJSONArray("results");
        for (int i = 0; i < array.length(); i++){
            if(array.getJSONObject(i).getString("type").equals("Trailer") &&
                    array.getJSONObject(i).getString("site").equals("YouTube")){
                return YOUTUBE_ENDPOINT + "?v=" + array.getJSONObject(i).getString("key");
            }
        }
        return null;
    }

    private static List<Actor> parseCast(JSONObject object) throws JSONException{
        List<Actor> actors = new ArrayList<>();

        JSONArray array = object.getJSONArray("cast");
        for (int i = 0; i < (array.length() > 10 ? 10 : array.length()); i++){
            Actor actor = new Actor();
            String character = array.getJSONObject(i).getString("character");
            String id = array.getJSONObject(i).getString("id");
            String name = array.getJSONObject(i).getString("name");

            String photo = array.getJSONObject(i).getString("profile_path");
            String urlProfilePhoto = "";
            if(!photo.equals("null")) {
                urlProfilePhoto = IMAGE_URL_ENDPOINT_ORIGINAL + photo;
            }

            actor.setCharacterName(character);
            actor.setId(id);
            actor.setName(name);
            actor.setUrlProfilePhoto(urlProfilePhoto);

            actors.add(actor);
        }

        return actors;
    }

    private static List<String> parseImages(JSONObject object) throws JSONException{
        List<String> result = new ArrayList<>();

        JSONArray array = object.getJSONArray("backdrops");
        if(array.length() > 1) {
            for (int i = 1; i < (array.length() > 6 ? 6 : array.length()); i++) {
                result.add(IMAGE_URL_ENDPOINT_ORIGINAL + array.getJSONObject(i).getString("file_path"));
            }
        }

        return result;
    }

    private static TVShow parseTV(JSONObject object) throws JSONException{
        TVShow tvShow = new TVShow();
        String id = object.getString("id");
        String title = object.getString("name");
        String overview = object.getString("overview");
        String voteAverage = object.getString("vote_average");
        String voteCount = object.getString("vote_count");
        String urlBackground = "";
        if(object.isNull("backdrop_path")){
            urlBackground = IMAGE_URL_ENDPOINT_ORIGINAL + object.getString("poster_path");;
        } else {
            urlBackground = IMAGE_URL_ENDPOINT_ORIGINAL + object.getString("backdrop_path");
        }
        String time = object.getJSONArray("episode_run_time").getString(0);
        String urlPoster = IMAGE_URL_ENDPOINT_ORIGINAL + object.getString("poster_path");

        String date = formDate(object.getString("first_air_date"));
        String numberOfSeasons = object.getString("number_of_seasons");

        JSONArray arrayGenres = object.getJSONArray("genres");
        List<String> genres = new ArrayList<>();
        for (int i = 0; i < arrayGenres.length(); i++){
            genres.add(arrayGenres.getJSONObject(i).getString("name"));
        }

        List<String> images = getImages(CONTENT_TV, id);
        List<Actor> actors = getActors(CONTENT_TV, id);

        String urlVideo = getVideo(CONTENT_TV, id, "ru");
        if(urlVideo == null){
            urlVideo = getVideo(CONTENT_TV, id, "");
        }

        List<Season> seasons = parseSeasons(id, object.getJSONArray(("seasons")));
        String status = object.getString("status");
        if(status.equals("Ended")){
            status = "завершен";
        } else if(status.equals("Returning Series")){
            status = "продолжается";
        }

        tvShow.setCast(actors);
        tvShow.setDate(date);
        tvShow.setGenres(genres);
        tvShow.setOverview(overview);
        tvShow.setTitle(title);
        tvShow.setUrlBackground(urlBackground);
        tvShow.setUrlImages(images);
        tvShow.setYoutubeUrl(urlVideo);
        tvShow.setVoteAverage(voteAverage);
        tvShow.setVoteCount(voteCount);
        tvShow.setId(id);
        tvShow.setTime(time);
        tvShow.setUrlPoster(urlPoster);
        tvShow.setSeasons(seasons);
        tvShow.setStatus(status);
        tvShow.setNumberOfSeasons(numberOfSeasons);

        return tvShow;
    }

    private static List<Season> parseSeasons(String tvshowId, JSONArray array) throws JSONException{
        List<Season> seasons = new ArrayList<Season>();
        for (int i = 0; i < array.length(); i++){
            JSONObject object = array.getJSONObject(i);
            Season season = new Season();

            String title = object.getString("name");
            String date = formDate(object.getString("air_date"));
            String id = object.getString("id");
            String urlImage = IMAGE_URL_ENDPOINT_THUMBNAIL + object.getString("poster_path");
            String seasonNumber = object.getString("season_number");

            season.setDate(date);
            season.setId(id);
            season.setTitle(title);
            season.setUrlImage(urlImage);
            season.setSeasonNumber(seasonNumber);
            season.setTVShowId(tvshowId);

            seasons.add(season);
        }
        return seasons;
    }

    private static MediaContent parseMovie(JSONObject object) throws JSONException{
        Movie movie= new Movie();
        String id = object.getString("id");
        String title = object.getString("title");
        String overview = object.getString("overview");
        String voteAverage = object.getString("vote_average");
        String voteCount = object.getString("vote_count");
        String urlBackground = "";
        if(object.isNull("backdrop_path")){
            urlBackground = IMAGE_URL_ENDPOINT_ORIGINAL + object.getString("poster_path");;
        } else {
            urlBackground = IMAGE_URL_ENDPOINT_ORIGINAL + object.getString("backdrop_path");
        }
        String time = object.getString("runtime");
        String urlPoster = IMAGE_URL_ENDPOINT_ORIGINAL + object.getString("poster_path");

        String date = formDate(object.getString("release_date"));

        JSONArray arrayGenres = object.getJSONArray("genres");
        List<String> genres = new ArrayList<>();
        for (int i = 0; i < arrayGenres.length(); i++){
            genres.add(arrayGenres.getJSONObject(i).getString("name"));
        }

        List<String> images = getImages(CONTENT_MOVIE, id);
        List<Actor> actors = getActors(CONTENT_MOVIE, id);

        String urlVideo = getVideo(CONTENT_MOVIE, id, "ru");
        if(urlVideo == null){
            urlVideo = getVideo(CONTENT_MOVIE, id, "");
        }

        String budget = object.getInt("budget") / 1000 == 0 ? "" : object.getInt("budget") / 1000 + " тыс. $";
        String revenue = object.getInt("revenue") / 1000 == 0 ? "" : object.getInt("revenue") / 1000 + " тыс. $";

        movie.setCast(actors);
        movie.setDate(date);
        movie.setGenres(genres);
        movie.setOverview(overview);
        movie.setTitle(title);
        movie.setUrlBackground(urlBackground);
        movie.setUrlImages(images);
        movie.setYoutubeUrl(urlVideo);
        movie.setVoteAverage(voteAverage);
        movie.setVoteCount(voteCount);
        movie.setId(id);
        movie.setTime(time);
        movie.setUrlPoster(urlPoster);
        movie.setBudget(budget);
        movie.setRevenue(revenue);

        return movie;
    }

    private static SearchItem parseSearchItem(JSONObject jsonObject, String content){
        SearchItem searchItem = new SearchItem();
        searchItem.setContent(content);
        try {
            searchItem.setId(jsonObject.getString("id"));
            searchItem.setThumbnailUrl(IMAGE_URL_ENDPOINT_THUMBNAIL + jsonObject.getString("poster_path"));
            searchItem.setOverview(jsonObject.getString("overview"));
            String date = "";
            if(content.equals(CONTENT_TV)) {
                date = jsonObject.getString("first_air_date");
                searchItem.setTitle(jsonObject.getString("name"));
            } else if(content.equals(CONTENT_MOVIE)) {
                date = jsonObject.getString("release_date");
                searchItem.setTitle(jsonObject.getString("title"));
            }
            date = formDate(date);
            searchItem.setSubtitle(date);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return searchItem;
    }

    private static String formDate(String date){
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date parseDate = ft.parse(date);
            return String.format("%te %<tB %<tY", parseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

}
