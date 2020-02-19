package nodopezzz.android.wishlist.Models;

import android.content.Context;

import nodopezzz.android.wishlist.Activities.ContentBookActivity;
import nodopezzz.android.wishlist.Activities.ContentMediaActivity;
import nodopezzz.android.wishlist.APIs.GoogleBooksAPI;

public class SearchItem {

    private String mId;
    private String mTitle;
    private String mSubtitle;
    private String mOverview;
    private String mThumbnailUrl;

    private String mContent;


    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public void setSubtitle(String subtitle) {
        mSubtitle = subtitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        mThumbnailUrl = thumbnailUrl;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public void onClickSearchItem(Context context){
        if(mContent.equals(GoogleBooksAPI.CONTENT_BOOKS)){
            context.startActivity(ContentBookActivity.newInstance(context, mId, mTitle));
        } else {
            context.startActivity(ContentMediaActivity.newInstance(context, mContent, mId, mTitle));
        }

    }

    @Override
    public String toString(){
        return mTitle + " " + mSubtitle;
    }
}
