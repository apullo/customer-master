package co.cvdcc.brojekcustomer.home.submenu.history;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItemAdapter;

import co.cvdcc.brojekcustomer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bradhawk on 10/31/2016.
 */

public class HistoryFragment extends Fragment {

    @BindView(R.id.history_tabLayout)
    SmartTabLayout historyTabLayout;

    @BindView(R.id.history_viewPager)
    ViewPager historyViewPager;

    @BindView(R.id.history_swipeRefresh)
    SwipeRefreshLayout historySwipeRefresh;

    private ViewPagerItemAdapter adapter;
    private MyPagerAdapter mAdapter;

    InProgressHistoryFragment inProgressFragment;
    CompletedHistoryFragment completedHistoryFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setupViewPagerAdapter();
        setupSwipeRefresh();
    }

    private void setupViewPagerAdapter() {
//        adapter = new ViewPagerItemAdapter(
//                ViewPagerItems.with(getActivity())
//                .add(R.string.history_inProgress, R.layout.view_empty)
//                .add(R.string.history_completed, R.layout.view_empty)
//                .create());
//
        mAdapter = new MyPagerAdapter(getFragmentManager());
        historyViewPager.setAdapter(mAdapter);
        historyTabLayout.setViewPager(historyViewPager);

        historyViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                enableSwipeRefresh(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setupViewPagerAdapter();
    }

    private void setupSwipeRefresh() {
        historySwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setupViewPagerAdapter();
                new CountDownTimer(5000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        historySwipeRefresh.setRefreshing(false);
                    }
                }.start();
            }
        });
    }

    public void changeFragment(Fragment frag, boolean addToBackStack) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.history_viewPager, frag);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void enableSwipeRefresh(boolean isEnable) {
        if(!historySwipeRefresh.isRefreshing()) historySwipeRefresh.setEnabled(isEnable);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: inProgressFragment = InProgressHistoryFragment.newInstance();
                    return inProgressFragment;
                case 1:
                    completedHistoryFragment = CompletedHistoryFragment.newInstance();
                    return completedHistoryFragment;
                default:
                    return inProgressFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "DALAM PROSES";
                case 1:
                    return "SELESAI";
                default:
                    return "title";

            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    public interface OnSwipeRefresh {
        void onRefresh();
    }
}
