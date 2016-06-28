package github.arocketman.awwdroid;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

/**
 * Represents a single Image. It contains the fetched image title and URL. This could be used in the
 * future to easily extend it to other attributes such as upvotes/downvotes ..
 */
public class ImageEntry implements Serializable , Parcelable{
    String title;
    String URL;

    public ImageEntry(String title, String URL) {
        this.title = title;
        this.URL = URL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getURL() {
        return URL;
    }

    // The following methods are used to put in the savedInstanceState an ArrayList of ImageEntry
    // according to the Parcelable interface.

    protected ImageEntry(Parcel in) {
        title = in.readString();
        URL = in.readString();
    }

    public static final Creator<ImageEntry> CREATOR = new Creator<ImageEntry>() {
        @Override
        public ImageEntry createFromParcel(Parcel in) {
            return new ImageEntry(in);
        }

        @Override
        public ImageEntry[] newArray(int size) {
            return new ImageEntry[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(URL);
    }
}
