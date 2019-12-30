package nodopezzz.android.wishlist;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TMDBAPI {
    private static final String TAG = "TMDBAPI";

    private static final String API_KEY = "23c8bc74a45d2a77180c84c78e5cbccb";
    private static final Uri ENDPOINT = Uri.parse("https://api.themoviedb.org/3");
    private static final String IMAGE_URL_ENDPOINT = "https://image.tmdb.org/t/p/w1280";

    private static final String METHOD_SEARCH = "search";

    public static final String CONTENT_MOVIE = "movie";
    public static final String CONTENT_TV = "tv";

    public static List<SearchItem> search(String content, String query, int page){
        String sUrl = ENDPOINT.buildUpon()
                .appendPath(METHOD_SEARCH)
                .appendPath(content)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("language", "ru")
                .appendQueryParameter("query", query)
                .appendQueryParameter("include_adult", "true")
                .appendQueryParameter("page", Integer.toString(page))
                .toString();

        List<SearchItem> result = new ArrayList<>();

        try {
            String response = UrlDownloader.getResponse(sUrl);
            JSONArray array = new JSONObject(response).getJSONArray("results");
            for (int i = 0; i < array.length(); i++){
                SearchItem item = parseSearchItem(array.getJSONObject(i), content);
                if(item != null) {
                    result.add(item);
                }
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static SearchItem parseSearchItem(JSONObject jsonObject, String content){
        SearchItem searchItem = new SearchItem();
        searchItem.setContent(content);
        try {
            searchItem.setDate(jsonObject.getString("release_date"));
            searchItem.setId(jsonObject.getString("id"));
            searchItem.setImageUrl(IMAGE_URL_ENDPOINT + jsonObject.getString("poster_path"));
            searchItem.setTitle(jsonObject.getString("title"));
            searchItem.setOverview(jsonObject.getString("overview"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return searchItem;
    }

}
