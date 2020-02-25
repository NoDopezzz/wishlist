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

    /*public static List<SearchItem> search(String q, int startIndex){
        String url = ENDPOINT.buildUpon()
                .appendQueryParameter("q", q)
                .appendQueryParameter("startIndex", Integer.toString((startIndex - 1) * 10 + 1 == 1 ? 0 : (startIndex - 1) * 10 + 1))
                .build().toString();
        try{
            String response = UrlDownloader.getResponse(url);
            JSONObject object = new JSONObject(response);
            return parseListBooksSearch(object);
        } catch(IOException | JSONException e){
            Log.i(TAG, e.toString());
            return null;
        }
    }

    public static Book getBook(String id){
        String url = ENDPOINT.buildUpon()
                .appendPath(id)
                .build().toString();
        try{
            String response = UrlDownloader.getResponse(url);
            JSONObject object = new JSONObject(response);
            return parseBook(object);
        } catch(IOException | JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    private static List<SearchItem> parseListBooksSearch(JSONObject object) throws JSONException{
        List<SearchItem> books = new ArrayList<>();
        JSONArray array = object.getJSONArray("items");
        for (int i = 0; i < array.length(); i++){
            try {
                SearchItem book_placeholder = parseBookSearch(array.getJSONObject(i));
                if (book_placeholder != null) {
                    books.add(book_placeholder);
                }
            } catch(JSONException e){
                Log.i(TAG, e.toString());
                e.printStackTrace();
            }
        }
        return books;
    }

    private static Book parseBook(JSONObject object) throws JSONException{
        Book book_placeholder = new Book();

        String id = object.getString("id");
        String title = object.getJSONObject("volumeInfo").getString("title");
        String date = formDate(object.getJSONObject("volumeInfo").getString("publishedDate"));
        String description = "";
        if(!object.getJSONObject("volumeInfo").isNull("description")) {
            description = object.getJSONObject("volumeInfo").getString("description");
        }

        StringBuilder authorsBuilder = new StringBuilder();
        String authors = "";
        if(!object.getJSONObject("volumeInfo").isNull("authors")) {
            for (int i = 0; i < object.getJSONObject("volumeInfo").getJSONArray("authors").length(); i++) {
                authorsBuilder.append(object.getJSONObject("volumeInfo").getJSONArray("authors").getString(i)).append(", ");
            }
        }
        if(authorsBuilder.length() > 0){
            authors = authorsBuilder.substring(0, authorsBuilder.length() - 2);
        }

        StringBuilder categories = new StringBuilder();
        String categoriesString = "";
        if(!object.getJSONObject("volumeInfo").isNull("categories")) {
            for (int i = 0; i < object.getJSONObject("volumeInfo").getJSONArray("categories").length(); i++) {
                categories.append(object.getJSONObject("volumeInfo").getJSONArray("categories").getString(i)).append(", ");
            }
        }
        if(categories.length() > 0){
            categoriesString = categories.substring(0, categories.length() - 2);
        }

        String urlThumbnail = "";
        if(!object.getJSONObject("volumeInfo").isNull("imageLinks") && !object.getJSONObject("volumeInfo").getJSONObject("imageLinks").isNull("smallThumbnail")) {
            urlThumbnail = object.getJSONObject("volumeInfo").getJSONObject("imageLinks").getString("smallThumbnail");
        }

        String urlImage = "https://books.google.com/books/content/images/frontcover/" + id + "?fife=w400-h600";

        String urlBook = "";
        if(!object.getJSONObject("accessInfo").isNull("webReaderLink") &&
                !object.getJSONObject("accessInfo").getString("accessViewStatus").equals("NONE")){
            urlBook = object.getJSONObject("accessInfo").getString("webReaderLink");
        }

        String publisher = object.getJSONObject("volumeInfo").getString("publisher");

        book_placeholder.setId(id);
        book_placeholder.setAuthors(authors);
        book_placeholder.setCategories(categoriesString);
        book_placeholder.setDate(date);
        book_placeholder.setOverview(description);
        book_placeholder.setTitle(title);
        book_placeholder.setUrlImage(urlImage);
        book_placeholder.setThumbnailUrl(urlThumbnail);
        book_placeholder.setUrlBook(urlBook);
        book_placeholder.setPublisher(publisher);

        return book_placeholder;
    }

    private static SearchItem parseBookSearch(JSONObject object) throws JSONException{

        String id = object.getString("id");
        String title = object.getJSONObject("volumeInfo").getString("title");
        String description =  object.getJSONObject("volumeInfo").getString("description");

        StringBuilder authors = new StringBuilder();
        String authorString = "";
        for (int i = 0; i < object.getJSONObject("volumeInfo").getJSONArray("authors").length(); i++) {
            authors.append(object.getJSONObject("volumeInfo").getJSONArray("authors").getString(i)).append(", ");
        }

        if(authors.length() > 0){
            authorString = authors.substring(0, authors.length() - 2);
        }

        String urlThumbnail = object.getJSONObject("volumeInfo").getJSONObject("imageLinks").getString("smallThumbnail");

        SearchItem book_placeholder = new SearchItem(title,authorString,description,urlThumbnail, id);

        return book_placeholder;
    }
    }*/
}
