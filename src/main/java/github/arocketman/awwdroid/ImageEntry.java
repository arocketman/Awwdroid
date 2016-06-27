package github.arocketman.awwdroid;

import java.io.Serializable;

/**
 * Created by Andreuccio on 26/06/2016.
 */
public class ImageEntry implements Serializable {
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
}
