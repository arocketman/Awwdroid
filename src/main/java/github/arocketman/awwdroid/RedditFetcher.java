package github.arocketman.awwdroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class has the duty to fetch all the data from Reddit, parse it and give it as usable data to
 * its users.
 */
public class RedditFetcher {

    JSONArray JSONEntries;
    Integer currentEntry = 0;

    /**
     * Builds a RedditFetcher object.
     * @param url the URL in string representation from which you want to parse content.
     */
    public RedditFetcher(String url){
        try {
            String sJson = URLtoString(url);
            this.JSONEntries = StringToJson(sJson).getJSONObject("data").getJSONArray("children");
            System.out.println("hi");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an ImageEntry object and returns it. Such object is filled with data from the JSONEntries array at the currentEntry index.
     * @return
     * @throws JSONException
     */
    private ImageEntry getNextEntry() {
        JSONObject entry = null;
        try {
            entry = this.JSONEntries.getJSONObject(currentEntry++).getJSONObject("data");
            ImageEntry imageEntry = new ImageEntry(entry.getString("title"),entry.getString("url"));
            if(!isValidImage(imageEntry.getURL()))
                return getNextEntry();
            return imageEntry;
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Gets all the entries converting the json format in ImageEntry objects.
     * @return
     */
    public ArrayList<ImageEntry> getAllEntries(){
        ArrayList<ImageEntry> entries = new ArrayList<>();
        while(currentEntry < JSONEntries.length()){
            entries.add(getNextEntry());
        }
        return entries;
    }

    /**
     * Checks wheter or not an image is either a gif, jpg or png. Also checks the host to be imgur.
     * @param imageURL
     * @return
     */
    private boolean isValidImage(String imageURL){
        String newstr = imageURL.replaceAll("\"", "");
        //Making sure it's a .jpg || .png extension
        return (newstr.endsWith(".jpg") || newstr.endsWith(".png") || newstr.endsWith(".gif")) && newstr.contains("imgur");
    }


    /**
     * Given a URLString it fetches the content of the page and returns it in a stringified format.
     * @param URLString
     * @return a string with the content of the given page.
     * @throws IOException
     */
    private String URLtoString(String URLString) throws IOException {
        URL url = new URL(URLString);
        URLConnection urlconnection = url.openConnection();
        //Just a random user agent.
        urlconnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0a2) Gecko/20110613 Firefox/6.0a2");

        Scanner scanner = new Scanner(urlconnection.getInputStream());
        String bufferString = new String();
        while(scanner.hasNext())
            bufferString+= scanner.nextLine();
        scanner.close();
        return bufferString;
    }

    /**
     * Returns the json representation of the object given the string one.
     * @param jsonString
     * @return
     */
    private JSONObject StringToJson(String jsonString){
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
