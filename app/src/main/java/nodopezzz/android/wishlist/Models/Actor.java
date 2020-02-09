package nodopezzz.android.wishlist.Models;

public class Actor {
    private String mId;
    private String mCharacterName;
    private String mName;
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
