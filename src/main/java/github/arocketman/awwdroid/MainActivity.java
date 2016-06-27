package github.arocketman.awwdroid;

import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends FragmentActivity implements SingleImageFragment.OnFragmentInteractionListener {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    private ArrayList<ImageEntry> imageEntries;

    RedditFetcher fetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fetcher = new RedditFetcher("https://www.reddit.com/r/aww/.json");
        imageEntries = new ArrayList<>();
        new FetchJSONTask().execute("");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * Returns an ImageEntry object from the imageEntries ArrayList given an ID.
     * @param id
     * @return
     */
    public ImageEntry getImageEntry(int id){
        return imageEntries.get(id);
    }

    boolean doNotifyDataSetChangedOnce = false;

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // Loading new images if we're coming close to the end of the array.
            if(imageEntries.size() - position < 10 && imageEntries.size() - position > 0)
                new FetchJSONTask().execute("");
            ImageEntry entry = getImageEntry(position);
            if(entry==null){
                return SingleImageFragment.newInstance(entry,position);
            }
            return SingleImageFragment.newInstance(entry,position);
        }

        @Override
        public int getCount() {
            if (doNotifyDataSetChangedOnce) {
                doNotifyDataSetChangedOnce = false;
                notifyDataSetChanged();
            }
            return imageEntries.size();
        }
    }

    /**
     * Given a reddit.com json URL this task fetches the JSON.
     */
    private class FetchJSONTask extends AsyncTask<String, Void , ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            fetcher.fetchNext();
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            imageEntries.addAll(fetcher.getEntries());
            //Removing null elements.
            imageEntries.removeAll(Arrays.asList(null,""));

            // Instantiate a ViewPager and a PagerAdapter if not created already.
            if(mPager == null) {
                mPager = (ViewPager) findViewById(R.id.pager);
                mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
                mPager.setAdapter(mPagerAdapter);
            }
            else
                doNotifyDataSetChangedOnce = true;
        }
    }

}
