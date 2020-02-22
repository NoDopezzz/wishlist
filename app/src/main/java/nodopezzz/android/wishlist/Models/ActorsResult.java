package nodopezzz.android.wishlist.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ActorsResult {

    @SerializedName("cast")
    @Expose
    private List<Actor> mActors;

    public List<Actor> getActors() {
        return mActors;
    }

    public void setActors(List<Actor> actors) {
        mActors = actors;
    }

    public class Actor {

        @SerializedName("id")
        @Expose
        private String mId;

        @SerializedName("character")
        @Expose
        private String mCharacterName;

        @SerializedName("name")
        @Expose
        private String mName;

        @SerializedName("profile_path")
        @Expose
        private String mUrlProfilePhoto;

        public String getCharacterName() {
            return mCharacterName;
        }

        public void setCharacterName(String characterName) {
            mCharacterName = characterName;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public String getUrlProfilePhoto() {
            return mUrlProfilePhoto;
        }

        public void setUrlProfilePhoto(String urlProfilePhoto) {
            mUrlProfilePhoto = urlProfilePhoto;
        }

        public String getId() {
            return mId;
        }

        public void setId(String id) {
            mId = id;
        }

        @Override
        public String toString(){
            return mName + " - " + mUrlProfilePhoto;
        }
    }

}
