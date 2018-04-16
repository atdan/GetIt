package com.example.eets_nostredame.getit;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.Manifest;
import android.view.Menu;
import android.view.MenuItem;

import com.example.eets_nostredame.getit.adapters.SectionPagerAdapter;
import com.example.eets_nostredame.getit.fragments.AccountFragment;
//import com.example.eets_nostredame.getit.fragments.SearchFragment;
import com.example.eets_nostredame.getit.fragments.SearchFragment;
import com.example.eets_nostredame.getit.fragments.WatchListFragment;

public class SearchActivity extends AppCompatActivity implements SeearchActivity {

    public static final String TAG = "SearchActivity";
    public static final int REQUEST_CODE = 1;


    //widgets
    private TabLayout mTabLayout;
    public ViewPager mViewPager;

    // Vars
    public SectionPagerAdapter mPagerAdapter;
    private int[] tabIcons = {R.drawable.ic_search, R.drawable.ic_watch_list, R.drawable.ic_post, R.drawable.ic_account};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mTabLayout = findViewById(R.id.tabs);
        mViewPager = findViewById(R.id.viewPagerContainer);
        try{
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }catch (Exception e){
            Log.d(TAG, "onCreate: toolbar " + e);
        }


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.nav_camera) {
                    // Handle the camera action
                } else if (id == R.id.nav_gallery) {

                } else if (id == R.id.nav_slideshow) {

                } else if (id == R.id.nav_manage) {

                } else if (id == R.id.nav_share) {

                } else if (id == R.id.nav_send) {

                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });



        verifyPermissions();


    }

    private void setupViewPager() {
        mPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new SearchFragment());

        mPagerAdapter.addFragment(new WatchListFragment());

        mPagerAdapter.addFragment(new PostFragment());

        mPagerAdapter.addFragment(new AccountFragment());

        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setText(R.string.fragment_search).setIcon(tabIcons[0]);
        mTabLayout.getTabAt(1).setText(R.string.fragment_watch_list).setIcon(tabIcons[1]);
        mTabLayout.getTabAt(3).setText(R.string.fragment_account).setIcon(tabIcons[3]);
        mTabLayout.getTabAt(2).setText(R.string.fragment_post).setIcon(tabIcons[2]);


    }

    private void verifyPermissions() {
        Log.d(TAG, "verifyPermissions: Ask for user permissions");
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0])
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1]) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[2]) == PackageManager.PERMISSION_GRANTED) {
            setupViewPager();

        } else {
//            verifyPermissions();
            ActivityCompat.requestPermissions(SearchActivity.this, permissions, REQUEST_CODE);
        }
//        for (int i =0; i< permissions.length; i++){
//            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[i])!=PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(this,"Required permission " + permissions[i] +" not granted" +
//                        ", exiting",Toast.LENGTH_SHORT).show();
//                // Exit the app if one permission is not granted
//                ActivityCompat.requestPermissions(SearchActivity.this,permissions,REQUEST_CODE);
//            }
//        }
//        // all the permissions are granted
//        setupViewPager();
//
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        verifyPermissions();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
