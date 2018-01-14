package com.mikebl71.android.irishweather;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.reflect.Field;

/**
 * Main application activity.
 */
public class MainActivity extends FragmentActivity {

    private ViewPager viewPager;
    private BottomNavigationView navigationBar;
    private Menu navigationMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // ViewPager and its adapters use support library fragments, so use getSupportFragmentManager
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);

        navigationBar = findViewById(R.id.navigationBar);
        navigationMenu = navigationBar.getMenu();
        disableShiftMode(navigationBar);

        navigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                for (int idx = 0; idx < navigationMenu.size(); idx++) {
                    if (navigationMenu.getItem(idx).getItemId() == item.getItemId()) {
                        viewPager.setCurrentItem(idx);
                        return true;
                    }
                }
                return false;
            }
        });

        viewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        navigationBar.setSelectedItemId(navigationMenu.getItem(position).getItemId());
                    }
                });
    }

    @Override
    protected void onDestroy() {
        viewPager = null;
        navigationBar = null;
        navigationMenu = null;
        super.onDestroy();
    }

    /**
     * By default, the bottom navigation menu starts collapsing items if more than 3 items are added,
     * even if there is enough space. This method disables this behaviour.
     * Unfortunately, the setting is not publicly exposed thus the hack.
     */
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (Exception e) {
        }
    }


    private static class MainPagerAdapter extends FragmentStatePagerAdapter {
        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int idx) {
            switch (idx) {
                case 0:
                    return new DublinFragment();
                case 1:
                    return new LatestFragment();
                case 2:
                    return new RainRadarFragment();
                case 3:
                    return new OutlookFragment();
            }
            throw new IllegalStateException();
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int idx) {
            return Integer.toString(idx);
        }
    }

}
