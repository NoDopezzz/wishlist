package nodopezzz.android.wishlist.APIs;

import nodopezzz.android.wishlist.Models.Book;
import nodopezzz.android.wishlist.Models.SearchBookResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GoogleBooksAPIAdapter {
    @GET(".")
    public Call<SearchBookResult> search(@Query("q") String q, @Query("startIndex") String startIndex);

    @GET("{id}")
    public Call<Book> getBook(@Path("id") String id);
}
