package github.arocketman.awwdroid;

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

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends FragmentActivity implements SingleImageFragment.OnFragmentInteractionListener {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private ScreenSlidePagerAdapter mLoading;

    private ArrayList<ImageEntry> mImageEntries;
    RedditFetcher mFetcher;
    private int mCurrentItem = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        mTabLayout.setOnTabSelectedListener(new tabListener());
        mImageEntries = new ArrayList<>();
        mLoading = new ScreenSlidePagerAdapter(getSupportFragmentManager(),true);
        mPager = (ViewPager) findViewById(R.id.pager);
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            mPager.setAdapter(mLoading);
            int currentTab = savedInstanceState.getInt("current_tab");
            mTabLayout.getTabAt(currentTab).select();
            mFetcher = getFetcherFromSelectedTab(currentTab);
            mCurrentItem = savedInstanceState.getInt("current_item");
            mImageEntries = savedInstanceState.getParcelableArrayList("entries");
            createPager();
        }
        else{
            mFetcher = new RedditFetcher("https://www.reddit.com/r/aww/.json?limit=100");
            mImageEntries = new ArrayList<>();
            new FetchJSONTask(true).execute("");
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int currentItem = mPager.getCurrentItem();
        outState.putInt("current_item",currentItem);
        outState.putParcelableArrayList("entries", mImageEntries);
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
     * Returns an ImageEntry object from the mImageEntries ArrayList given an ID.
     * @param id
     * @return
     */
    public ImageEntry getImageEntry(int id){
        return mImageEntries.get(id);
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
            if(mImageEntries.size() - position < 20)
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
            return mImageEntries.size();
        }

    }

    /**
     * Given a reddit.com json URL this task fetches the JSON.
     * This AsyncTask is launched in two different ways:
     * 1) Task is launched when we need to fetch new images because the old ones are running short (eager loading)
     * 2) Task is launched when a tab is switched or the app itself is started. In this case we want to show the loading splash,
     *    we do this by setting the mPager adapter to mLoading.
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

            // We show the loading splash if we need to.
            if(tabSwapped) {
                mImageEntries.add(0,null);
                mPager.setAdapter(mLoading);
            }
        }

        @Override
        protected ArrayList<ImageEntry> doInBackground(String... strings) {
            return mFetcher.fetchNext();
        }

        @Override
        protected void onPostExecute(ArrayList<ImageEntry> results) {
            super.onPostExecute(results);
            // Adding the new entries to the already existing ones and removal of null elements.
            mImageEntries.addAll(results);
            mImageEntries.removeAll(Arrays.asList(null,""));
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
            //We clear the mImageEntries when we're switching tab.
            mImageEntries.clear();

            //Creating a new mFetcher based on the pressed tab.
            int pos = tab.getPosition();
            mFetcher = getFetcherFromSelectedTab(pos);

            new FetchJSONTask(true).execute("");
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }

    /**
     * Returns a RedditFetcher object based on the given tab position. Base Urls are different for
     * different tabs.
     * @param pos
     * @return
     */
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
