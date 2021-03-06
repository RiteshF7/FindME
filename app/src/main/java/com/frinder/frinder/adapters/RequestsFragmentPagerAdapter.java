package com.frinder.frinder.adapters;


import android.content.Context;
import android.content.res.Resources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.frinder.frinder.R;
import com.frinder.frinder.fragments.AcceptedRequestFragment;
import com.frinder.frinder.fragments.NoRequestsFragment;
import com.frinder.frinder.fragments.ReceivedRequestFragment;
import com.frinder.frinder.fragments.SentRequestFragment;

public class RequestsFragmentPagerAdapter extends FragmentPagerAdapter {

    private String mTabTitles[];
    private Context mContext;

    public RequestsFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        Resources resources = context.getResources();
        mTabTitles = new String[] {
                resources.getString(R.string.title_incoming),
                resources.getString(R.string.title_outgoing),
                resources.getString(R.string.title_accepted)
        };
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ReceivedRequestFragment.newInstance();
            case 1:
                return SentRequestFragment.newInstance();
            case 2:
                return AcceptedRequestFragment.newInstance();
            default:
                // TODO: Change this
                return NoRequestsFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return mTabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return mTabTitles[position];
    }
}
