package com.codepath.gumapathi.nytsearch.Activities;

import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.gumapathi.nytsearch.Adapter.ArticlesAdapter;
import com.codepath.gumapathi.nytsearch.Fragments.ArticleFilterDialogFragment;
import com.codepath.gumapathi.nytsearch.Helpers.APIQueryStringBuilder;
import com.codepath.gumapathi.nytsearch.Helpers.EndlessRecyclerViewScrollListener;
import com.codepath.gumapathi.nytsearch.Helpers.NewsDesk;
import com.codepath.gumapathi.nytsearch.Model.ArticleResponse;
import com.codepath.gumapathi.nytsearch.Model.Doc;
import com.codepath.gumapathi.nytsearch.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ListActivity extends AppCompatActivity {

    final Gson gson = new Gson();

    //ProgressDialog pd = new ProgressDialog(this);
    ArticleFilterDialogFragment alertDialog;
    ArticlesAdapter articlesAdapter;
    ArrayList<Doc> allArticles;
    private TextView tvBeginDate;
    private TextView tvEndDate;
    private CheckBox cbArts;
    private CheckBox cbSports;
    private CheckBox cbFashion;
    private Switch swOldNew;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private TextView toolbar_title;
    private APIQueryStringBuilder apiStringQuery;
    public static boolean isNightModeEnabled = false;
    private EndlessRecyclerViewScrollListener scrollListener;

    public static String ordinal(int i) {
        String[] sufixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + sufixes[i % 10];

        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        apiStringQuery = new APIQueryStringBuilder("", false, "", "");
        super.onCreate(savedInstanceState);

        SharedPreferences mPrefs =  PreferenceManager.getDefaultSharedPreferences(this);
        this.isNightModeEnabled = mPrefs.getBoolean("NIGHT_MODE", false);

        if(isNightModeEnabled) {
            Log.i("SAMY_nightMode","inhere");
            this.setTheme(R.style.AppTheme_Primary_Base_Dark);
        }
        else {
            Log.i("SAMY_dayMode","inhere-day");
            this.setTheme(R.style.AppTheme_Primary_Base_Light);
        }
        setContentView(R.layout.activity_list);

        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        Typeface titleFont = Typeface.createFromAsset(this.getAssets(), "fonts/englishtowne.ttf");
        toolbar_title.setTypeface(titleFont);
        toolbar_title.setText("NYT Search");
        //getSupportActionBar().setTitle("Home");
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);

        RecyclerView rvArticles = (RecyclerView) findViewById(R.id.rvArticles);
        allArticles = new ArrayList<>();
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        rvArticles.setLayoutManager(staggeredGridLayoutManager);
        articlesAdapter = new ArticlesAdapter(allArticles);
        rvArticles.setAdapter(articlesAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //Toast.makeText(ListActivity.this, "endless scroll " + apiStringQuery.getQueryString(), Toast.LENGTH_SHORT).show();
                searchArticles(apiStringQuery.getQueryString(), page);
            }
        };
        rvArticles.addOnScrollListener(scrollListener);
        if (!isOnline() || !isNetworkAvailable()) {
            Toast.makeText(ListActivity.this, "Please make sure your phone is online and can access internet.", Toast.LENGTH_LONG).show();
        }

        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            String query = getIntent().getStringExtra(SearchManager.QUERY);
            Log.i("SAMy", query);
            apiStringQuery.setQueryTerm(query);
            searchArticles(apiStringQuery.getQueryString(), 0);
        }

    }

    private void setupDrawerContent(NavigationView navigationView) {
        Log.i("Samy-nav", navigationView.toString());
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Log.i("Samy-nav", menuItem.getTitle().toString());
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_first_fragment:

                Toast.makeText(ListActivity.this, "First", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_second_fragment:
                Intent favItem = new Intent(this, FavoriteListActivity.class);
                startActivity(favItem);
                Toast.makeText(ListActivity.this, "2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_third_fragment:
                SharedPreferences mPrefs =  PreferenceManager.getDefaultSharedPreferences(this);
                mPrefs.edit().putBoolean("NIGHT_MODE", true).apply();
                this.setTheme(R.style.AppTheme_Primary_Base_Dark);
                this.finish();
                this.startActivity(new Intent(this, this.getClass()));
                //Toast.makeText(ListActivity.this, "3", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_fourth_fragment:
                SharedPreferences mPrefss =  PreferenceManager.getDefaultSharedPreferences(this);
                mPrefss.edit().putBoolean("NIGHT_MODE", false).apply();
                this.setTheme(R.style.AppTheme_Primary_Base_Light);
                this.finish();
                this.startActivity(new Intent(this, this.getClass()));
                //Toast.makeText(ListActivity.this, "3", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(ListActivity.this, "def", Toast.LENGTH_SHORT).show();
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }


    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setIconifiedByDefault(false);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.primaryTextColor, typedValue, true);
        @ColorInt int color = typedValue.data;
        int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
        EditText et = (EditText) searchView.findViewById(searchEditId);
        et.setTextColor(color);
        et.setHintTextColor(color);

        ImageView mSearchButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        int[] attrs = new int[]{R.attr.searchIconPlaceHolder /* index 0 */};
        TypedArray ta = this.obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = ta.getDrawable(0 );
        ta.recycle();
        mSearchButton.setImageDrawable(drawableFromTheme);

        ImageView mSubmitButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_go_btn);
        int[] attrs1 = new int[]{R.attr.searchIconPlaceHolder /* index 0 */};
        TypedArray ta1 = this.obtainStyledAttributes(attrs1);
        Drawable drawableFromTheme1 = ta1.getDrawable(0 );
        ta.recycle();
        mSubmitButton.setImageDrawable(drawableFromTheme1);

        ImageView mCloseButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        int[] attrs2 = new int[]{R.attr.closeIconPlaceHolder /* index 0 */};
        TypedArray ta2 = this.obtainStyledAttributes(attrs2);
        Drawable drawableFromTheme2 = ta2.getDrawable(0 );
        ta.recycle();
        mCloseButton.setImageDrawable(drawableFromTheme2);

        ImageView mVoiceButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_voice_btn);
        int[] attrs3 = new int[]{R.attr.voiceIconPlaceHolder};
        TypedArray ta3 = this.obtainStyledAttributes(attrs3);
        Drawable drawableFromTheme3 = ta3.getDrawable(0 );
        ta.recycle();
        mVoiceButton.setImageDrawable(drawableFromTheme3);

        View searchPlate = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchPlate.setBackgroundColor(Color.TRANSPARENT);


//        / Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        // Assumes current activity is the searchable activity
        ComponentName cn = new ComponentName(this, ListActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //Log ("SAMY", "Searching articles");
            @Override
            public boolean onQueryTextSubmit(String query) {
                allArticles.clear();
                articlesAdapter.notifyDataSetChanged();
                apiStringQuery.setQueryTerm(query.trim());
                searchArticles(apiStringQuery.getQueryString(), 0);
                Toast.makeText(ListActivity.this, "Searching! " + query, Toast.LENGTH_SHORT).show();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_filter) {
            //Toast.makeText(this, "Filtering clicked", Toast.LENGTH_LONG).show();
            showFilterDialog();
        }

        if (id == R.id.action_search) {
            //Toast.makeText(this, "Search clicked", Toast.LENGTH_LONG).show();
            return true;
        }
        if (id == android.R.id.home) {
            //Toast.makeText(this, "home clicked", Toast.LENGTH_LONG).show();
            mDrawer.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        alertDialog = ArticleFilterDialogFragment.newInstance("Filter");
        /*tvBeginDate = (TextView) (alertDialog).getView().findViewById(R.id.tvBeginDate);
        tvEndDate = (TextView) (alertDialog).getView().findViewById(R.id.tvEndDate);
        swOldNew = (Switch) (alertDialog).getView().findViewById(R.id.swOldNew);
        cbArts = (CheckBox) (alertDialog).getView().findViewById(R.id.cbArts);
        cbFashion = (CheckBox) (alertDialog).getView().findViewById(R.id.cbFashion);
        cbSports = (CheckBox) (alertDialog).getView().findViewById(R.id.cbSports);
        */
        alertDialog.show(fm, "fragment_alert");
    }

    public void onApplyFilterClicked(View view) {
        tvBeginDate = (TextView) (alertDialog).getView().findViewById(R.id.tvBeginDate);
        tvEndDate = (TextView) (alertDialog).getView().findViewById(R.id.tvEndDate);
        swOldNew = (Switch) (alertDialog).getView().findViewById(R.id.swOldNew);
        cbArts = (CheckBox) (alertDialog).getView().findViewById(R.id.cbArts);
        cbFashion = (CheckBox) (alertDialog).getView().findViewById(R.id.cbFashion);
        cbSports = (CheckBox) (alertDialog).getView().findViewById(R.id.cbSports);

        String beginDate = tvBeginDate.getText().toString();
        String endDate = tvEndDate.getText().toString();
        boolean oldNew = swOldNew.isChecked();
        boolean arts = cbArts.isChecked();
        boolean fashion = cbFashion.isChecked();
        boolean sports = cbSports.isChecked();

        Log.i("SAMY-f bgndt", beginDate);
        Log.i("SAMY-f enddt", endDate);
        Log.i("SAMY-f ON", String.valueOf(oldNew));
        Log.i("SAMY-f art", String.valueOf(arts));
        Log.i("SAMY-f fash", String.valueOf(fashion));
        Log.i("SAMY-f sport", String.valueOf(sports));

        if (!beginDate.isEmpty() && !endDate.isEmpty())
            try {
                Date bgnDate;
                Date edDate;
                DateFormat format = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);

                bgnDate = format.parse(beginDate.replaceAll("(?<=\\d)(st|nd|rd|th)", ""));
                edDate = format.parse(endDate.replaceAll("(?<=\\d)(st|nd|rd|th)", ""));

                Calendar cal = Calendar.getInstance();
                cal.setTime(bgnDate);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                String tempMonth = month > 9 ? String.valueOf(month + 1) : "0" + String.valueOf(month + 1);
                String tempDay = day > 9 ? String.valueOf(day) : "0" + String.valueOf(day);
                beginDate = String.valueOf(year) + tempMonth + tempDay;

                cal.setTime(edDate);
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
                tempMonth = month > 9 ? String.valueOf(month + 1) : "0" + String.valueOf(month + 1);
                tempDay = day > 9 ? String.valueOf(day) : "0" + String.valueOf(day);
                endDate = String.valueOf(year) +
                        tempMonth +
                        tempDay;

                Log.i("SAMY-parBDT", beginDate);
                Log.i("SAMY-pa-EDT", endDate);
                apiStringQuery.setBeginDate(beginDate);
                apiStringQuery.setEndDate(endDate);
            } catch (ParseException ex) {
                Log.i("SAMY-dateexception", ex.toString());
            }

        if (oldNew) {
            apiStringQuery.setOldestFirst(false);
        } else {
            apiStringQuery.setOldestFirst(true);
        }

        if (arts)
            apiStringQuery.addNewsDesks(NewsDesk.ARTS);
        if (sports)
            apiStringQuery.addNewsDesks(NewsDesk.SPORTS);
        if (fashion)
            apiStringQuery.addNewsDesks(NewsDesk.FASHION);

        apiStringQuery.getQueryString();
        alertDialog.dismiss();
    }

    public void onSwitchClicked(View view) {
    }

    public void onCheckboxClicked(View view) {

    }

    public void onBeginDateClicked(View view) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
                new mBeginDateSetListener(), year, month, day);
        dialog.show();
    }

    public void onEndDateClicked(View view) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
                new mEndDateSetListener(), year, month, day);
        dialog.show();
    }

    private String getStringForDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, day);
        Date date = calendar.getTime();
        return new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime()) + " " + ordinal(Integer.parseInt(new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime()))) + ", " + new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date.getTime());
    }

    private void searchArticles(String query, int pageNum) {

        OkHttpClient client = new OkHttpClient();
        String url = "";
        if (pageNum == 0) {
            url = "https://api.nytimes.com/svc/search/v2/articlesearch.json" + query;
        } else {
            url = "https://api.nytimes.com/svc/search/v2/articlesearch.json" + query + "&page=" + pageNum;
        }
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.i("SAMY", "Searching articles " + url);
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("SAMY", "Failed Search");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                Log.i("SAMY", "response");
                if (!response.isSuccessful()) {
                    Log.i("SAMY", "unsuccessful");
                    throw new IOException("Unexpected code " + response);
                } else {
                    Log.i("SAMY", "successful");
                    Log.i("SAMY", "running on UI Thread");
                    String responseData = response.body().string();
                    Log.i("SAMY-response", responseData);
                    //JSONObject json = new JSONObject(responseData);
                    final ArticleResponse artResponse = gson.fromJson(responseData, ArticleResponse.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                allArticles.addAll(artResponse.getResponse().getDocs());
                                articlesAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                Log.i("SAMY-error", e.toString());
                                e.printStackTrace();
                                //e.printStackTrace();
                            }
                        }//end run()
                    });//end runOnUiThread
                }
            }
        });
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private class mBeginDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int datePickerYear, int monthOfYear,
                              int dayOfMonth) {
            int year = datePickerYear;
            int month = monthOfYear;
            int day = dayOfMonth;

            tvBeginDate = (TextView) (alertDialog).getView().findViewById(R.id.tvBeginDate);
            String dueDate = getStringForDate(year, month, day);
            tvBeginDate.setText(dueDate);
        }
    }

    private class mEndDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int datePickerYear, int monthOfYear,
                              int dayOfMonth) {
            int year = datePickerYear;
            int month = monthOfYear;
            int day = dayOfMonth;
            tvEndDate = (TextView) (alertDialog).getView().findViewById(R.id.tvEndDate);
            String endDate = getStringForDate(year, month, day);
            tvEndDate.setText(endDate);
        }
    }
}
