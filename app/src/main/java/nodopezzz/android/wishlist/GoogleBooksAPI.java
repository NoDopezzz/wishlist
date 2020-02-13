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

import nodopezzz.android.wishlist.Models.Book;
import nodopezzz.android.wishlist.Models.SearchItem;
import nodopezzz.android.wishlist.Network.UrlDownloader;

public class GoogleBooksAPI {
    public static final String TAG = "GoogleBooksAPI";

    public static final Uri ENDPOINT = Uri.parse("https://www.googleapis.com/books/v1/volumes");
    public static final String CONTENT_BOOKS = "CONTENT_BOOK";

    public static List<SearchItem> search(String q, int startIndex){
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
                SearchItem book = parseBookSearch(array.getJSONObject(i));
                if (book != null) {
                    books.add(book);
                }
            } catch(JSONException e){
                Log.i(TAG, e.toString());
                e.printStackTrace();
            }
        }
        return books;
    }

    private static Book parseBook(JSONObject object) throws JSONException{
        Book book = new Book();

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

        book.setId(id);
        book.setAuthors(authors);
        book.setCategories(categoriesString);
        book.setDate(date);
        book.setOverview(description);
        book.setTitle(title);
        book.setUrlImage(urlImage);
        book.setThumbnailUrl(urlThumbnail);
        book.setUrlBook(urlBook);
        book.setPublisher(publisher);

        return book;
    }

    private static SearchItem parseBookSearch(JSONObject object) throws JSONException{
        SearchItem book = new SearchItem();

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

        book.setId(id);
        book.setSubtitle(authorString);
        book.setOverview(description);
        book.setTitle(title);
        book.setThumbnailUrl(urlThumbnail);

        book.setContent(CONTENT_BOOKS);

        return book;
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