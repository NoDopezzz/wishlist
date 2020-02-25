package nodopezzz.android.wishlist.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Book{

    @SerializedName("id")
    @Expose
    private String mId;

    @SerializedName("volumeInfo")
    @Expose
    private VolumeInfo mVolumeInfo;

    @SerializedName("accessInfo")
    @Expose
    private AccessInfo mAccessInfo;

    public class VolumeInfo{

        @SerializedName("title")
        @Expose
        private String mTitle;

        @SerializedName("description")
        @Expose
        private String mDescription;

        @SerializedName("authors")
        @Expose
        private List<String> mAuthors;

        @SerializedName("publisher")
        @Expose
        private String mPublisher;

        @SerializedName("imageLinks")
        @Expose
        private ImageLinks mImageLinks;

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public String getDescription() {
            return mDescription;
        }

        public void setDescription(String description) {
            mDescription = description;
        }

        public List<String> getAuthors() {
            return mAuthors;
        }

        public void setAuthors(List<String> authors) {
            mAuthors = authors;
        }

        public ImageLinks getImageLinks() {
            return mImageLinks;
        }

        public void setImageLinks(ImageLinks imageLinks) {
            mImageLinks = imageLinks;
        }

        public String getPublisher() {
            return mPublisher;
        }

        public void setPublisher(String publisher) {
            mPublisher = publisher;
        }


        public class ImageLinks{

            @SerializedName("smallThumbnail")
            @Expose
            private String mSmallThumbnail;

            public String getSmallThumbnail() {
                return mSmallThumbnail;
            }

            public void setSmallThumbnail(String smallThumbnail) {
                mSmallThumbnail = smallThumbnail;
            }
        }
    }

    public class AccessInfo{

        @SerializedName("accessViewStatus")
        @Expose
        private String mAccessViewStatus;

        @SerializedName("webReaderLink")
        @Expose
        private String mWebReaderLink;

        public String getAccessViewStatus() {
            return mAccessViewStatus;
        }

        public void setAccessViewStatus(String accessViewStatus) {
            mAccessViewStatus = accessViewStatus;
        }

        public String getWebReaderLink() {
            return mWebReaderLink;
        }

        public void setWebReaderLink(String webReaderLink) {
            mWebReaderLink = webReaderLink;
        }
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public VolumeInfo getVolumeInfo() {
        return mVolumeInfo;
    }

    public void setVolumeInfo(VolumeInfo volumeInfo) {
        mVolumeInfo = volumeInfo;
    }

    public AccessInfo getAccessInfo() {
        return mAccessInfo;
    }

    public void setAccessInfo(AccessInfo accessInfo) {
        mAccessInfo = accessInfo;
    }
}
