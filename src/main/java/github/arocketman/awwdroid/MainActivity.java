package github.arocketman.awwdroid;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity implements SingleImageFragment.OnFragmentInteractionListener {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    private ArrayList<ImageEntry> imageEntries;

    public ImageEntry getImageEntry(int id){
        return imageEntries.get(id);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return SingleImageFragment.newInstance(getImageEntry(position),position);
        }

        @Override
        public int getCount() {
            return 25;
        }
    }

    /**
     * Given a reddit.com json URL this task fetches the JSON.
     */
    private class FetchJSONTask extends AsyncTask<String, Void , ArrayList<String>> {
        RedditFetcher fetcher;
        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            fetcher = new RedditFetcher("https://www.reddit.com/r/aww/top/.json?limit=100");
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            imageEntries = fetcher.getAllEntries();
            // Instantiate a ViewPager and a PagerAdapter.
            mPager = (ViewPager) findViewById(R.id.pager);
            mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(mPagerAdapter);
        }
    }

}
