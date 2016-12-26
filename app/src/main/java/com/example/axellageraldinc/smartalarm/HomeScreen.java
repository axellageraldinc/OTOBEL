package com.example.axellageraldinc.smartalarm;

import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.example.axellageraldinc.smartalarm.Menu.MenuSetting;
import com.example.axellageraldinc.smartalarm.RecyclerViewListAlarm.ListActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        //Bagian tabs
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        //Bagian tabs
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        //tabLayout.setSelectedTabIndicatorColor(this.getResources().getColor(R.color.FAB));

    }

    //Set tulisan untuk di tabs (gak terpakai karena pakainya icon [source code di atas])
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ListActivity(), "Bel Otomatis");
        adapter.addFrag(new ManualAlarm(), "Bel Manual");
        viewPager.setAdapter(adapter);
    }

    //Adapter untuk tabs
    class ViewPagerAdapter extends FragmentPagerAdapter {
        public final List<Fragment> mFragmentList = new ArrayList<>();
        public final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    //Apply menu supaya ada titik 3 di kanan atas
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.MenuSetting:
                Intent i = new Intent(HomeScreen.this, MenuSetting.class);
                startActivity(i);
                return true;
            case R.id.MenuAbout:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
