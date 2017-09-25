package com.codepath.gumapathi.nytsearch.Activities;

import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.gumapathi.nytsearch.Adapter.MyFavoriteItemRecyclerViewAdapter;
import com.codepath.gumapathi.nytsearch.Database.Favorites;
import com.codepath.gumapathi.nytsearch.R;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Calendar;
import java.util.List;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;


public class FavoriteListActivity extends AppCompatActivity {
    List<Favorites> allFavs;

    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private TextView toolbar_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorite_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        int[] attrs = new int[]{R.attr.hambIconPlaceHolder};
        TypedArray ta = this.obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = ta.getDrawable(0 );
        ta.recycle();

        getSupportActionBar().setHomeAsUpIndicator(drawableFromTheme);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //getSupportActionBar().setTitle("Favorites");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        Typeface titleFont = Typeface.createFromAsset(this.getAssets(), "fonts/englishtowne.ttf");
        toolbar_title.setText("Favorites");
        toolbar_title.setTypeface(titleFont);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);

        allFavs = SQLite
                .select()
                .from(Favorites.class)
                .queryList();

        RecyclerView rvFavs = (RecyclerView) findViewById(R.id.rvFavs);

        rvFavs.setLayoutManager(new GridLayoutManager(getContext(),2));
        rvFavs.setAdapter(new MyFavoriteItemRecyclerViewAdapter(allFavs));
    }

    private void setupDrawerContent(NavigationView navigationView) {
        Log.i("Samy-fav-nav",navigationView.toString());
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Log.i("Samy-fav-nav",menuItem.getTitle().toString());
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Log.i("Samy-fav-draw",menuItem.getTitle().toString());
        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                //Intent allItems = new Intent(this, ListActivity.class);
                //startActivity(allItems);
                menuItem.setChecked(true);
                mDrawer.closeDrawers();
                finish();
                //Toast.makeText(FavoriteListActivity.this, "First", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_second_fragment:
                Toast.makeText(FavoriteListActivity.this, "2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_third_fragment:
                Toast.makeText(FavoriteListActivity.this, "3", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(FavoriteListActivity.this, "def", Toast.LENGTH_SHORT).show();
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        //setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            Toast.makeText(this, "home clicked", Toast.LENGTH_LONG).show();
            mDrawer.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
