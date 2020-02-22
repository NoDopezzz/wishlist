package nodopezzz.android.wishlist.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchBookResult {

    @SerializedName("items")
    @Expose
    private List<Book> mBooks;

    public List<Book> getBooks() {
        return mBooks;
    }

    public void setBooks(List<Book> books) {
        mBooks = books;
    }
}
