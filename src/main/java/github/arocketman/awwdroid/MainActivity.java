package github.arocketman.awwdroid;

import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
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
    private int mCurrentItem = -1;
    private TabLayout mTabLayout;
    private ScreenSlidePagerAdapter mLoading;

    private ArrayList<ImageEntry> imageEntries;

    RedditFetcher fetcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        mTabLayout.setOnTabSelectedListener(new tabListener());
        imageEntries = new ArrayList<>();
        mLoading = new ScreenSlidePagerAdapter(getSupportFragmentManager(),true);
        mPager = (ViewPager) findViewById(R.id.pager);
        if (savedInstanceState != null) {
            mPager.setAdapter(mLoading);
            // Restore value of members from saved state
            int currentTab = savedInstanceState.getInt("current_tab");
            mTabLayout.getTabAt(currentTab).select();
            fetcher = getFetcherFromSelectedTab(currentTab);
            mCurrentItem = savedInstanceState.getInt("current_item");
            imageEntries = savedInstanceState.getParcelableArrayList("entries");
            createPager();
        }
        else{
            fetcher = new RedditFetcher("https://www.reddit.com/r/aww/.json?limit=100");
            imageEntries = new ArrayList<>();
            new FetchJSONTask(true).execute("");
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int currentItem = mPager.getCurrentItem();
        outState.putInt("current_item",currentItem);
        outState.putParcelableArrayList("entries",imageEntries);
        outState.putInt("current_tab",mTabLayout.getSelectedTabPosition());
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public int getCurrentIndex(){
        return mPager.getCurrentItem();
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

        boolean isLoadingFragment;

        public ScreenSlidePagerAdapter(FragmentManager fm , boolean isLoadingFragment) {
            super(fm);
            this.isLoadingFragment = isLoadingFragment;
        }

        @Override
        public Fragment getItem(int position) {
            if(isLoadingFragment)
                return SingleImageFragment.newInstance(null, 0);
            // Loading new images if we're coming close to the end of the array.
            if(imageEntries.size() - position < 20)
                new FetchJSONTask(false).execute("");
            ImageEntry entry = getImageEntry(position);
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
    private class FetchJSONTask extends AsyncTask<String, Void , ArrayList<ImageEntry>> {

        private boolean tabSwapped;

        public FetchJSONTask(boolean tabSwapped) {
            super();
            this.tabSwapped = tabSwapped;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(tabSwapped) {
                imageEntries.add(0,null);
                mPager.setAdapter(mLoading);
            }
        }

        @Override
        protected ArrayList<ImageEntry> doInBackground(String... strings) {
            return fetcher.fetchNext();
        }

        @Override
        protected void onPostExecute(ArrayList<ImageEntry> results) {
            super.onPostExecute(results);
            // Adding the new entries to the already existing ones and removal of null elements.
            imageEntries.addAll(results);
            imageEntries.removeAll(Arrays.asList(null,""));
            createPager();
        }
    }

    /**
     * Checks wheter or not the pager is null. If it is, it will create it along with its adapter.
     * If it is not, it means that it already existed and we've called the execute() method on
     * FetchJSONTask, so we're just updating the list and we want to notify the data set
     * changed accordingly.
     */
    private void createPager() {
        if(mPager == null || mPager.getAdapter() == mLoading) {
            mPager = (ViewPager) findViewById(R.id.pager);
            mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),false);
            mPager.setAdapter(mPagerAdapter);

            // Used to restore the correct item of the pager when switching to/from landscape.
            if(mCurrentItem != -1)
                mPager.setCurrentItem(mCurrentItem);
        }
        doNotifyDataSetChangedOnce = true;
    }

    private class tabListener implements TabLayout.OnTabSelectedListener{

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            //We clear the imageEntries and the viewpager when we're switching tab.
            imageEntries.clear();

            //Creating a new fetcher based on the pressed tab.
            int pos = tab.getPosition();
            fetcher = getFetcherFromSelectedTab(pos);

            new FetchJSONTask(true).execute("");
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }

    private RedditFetcher getFetcherFromSelectedTab(int pos) {
        switch (pos){
            case 0 :
                return new RedditFetcher("https://www.reddit.com/r/aww/.json?limit=100");
            case 1:
                return new RedditFetcher("https://www.reddit.com/r/aww/top/.json?limit=100");
            default:
                return new RedditFetcher("https://www.reddit.com/r/aww/.json?limit=100");
        }
    }

}
