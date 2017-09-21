package com.codepath.gumapathi.nytsearch.Activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.gumapathi.nytsearch.Adapter.ArticlesAdapter;
import com.codepath.gumapathi.nytsearch.Fragments.ArticleFilterDialogFragment;
import com.codepath.gumapathi.nytsearch.Helpers.APIQueryStringBuilder;
import com.codepath.gumapathi.nytsearch.Helpers.EndlessRecyclerViewScrollListener;
import com.codepath.gumapathi.nytsearch.Helpers.NewsDesks;
import com.codepath.gumapathi.nytsearch.Model.ArticleResponse;
import com.codepath.gumapathi.nytsearch.Model.Doc;
import com.codepath.gumapathi.nytsearch.R;
import com.google.gson.Gson;

import java.io.IOException;
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

//import android.support.v7.widget.


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

    private APIQueryStringBuilder apiStringQuery;

    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

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
                searchArticles(apiStringQuery.getQueryString(),page);
            }
        };
        rvArticles.addOnScrollListener(scrollListener);

        apiStringQuery = new APIQueryStringBuilder("", false, "","", new NewsDesks[]{NewsDesks.NONE});
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //Log ("SAMY", "Searching articles");
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                //pd.show();
                apiStringQuery.setQueryTerm(query);
                searchArticles(apiStringQuery.getQueryString(),0);
                Toast.makeText(ListActivity.this, "Searching! " + query, Toast.LENGTH_SHORT).show();
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
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
            Toast.makeText(this, "Filtering clicked", Toast.LENGTH_LONG).show();
            //Intent i = new Intent(this, FilterDialogFragmentActivity.class);
            //i.putExtra("EditingTitle", "Test");
            //startActivityForResult(i, FILTER_CODE);

            showFilterDialog();
        }

        if (id == R.id.action_search) {
            Toast.makeText(this, "Search clicked", Toast.LENGTH_LONG).show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        alertDialog = ArticleFilterDialogFragment.newInstance("Some title");
        alertDialog.show(fm, "fragment_alert");
    }

    public void onApplyFilterClicked(View view) {
        tvBeginDate = (TextView) (alertDialog).getView().findViewById(R.id.tvBeginDate);

        apiStringQuery.setBeginDate(tvBeginDate.getText().toString());
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

    /*public void loadNextSetOfArticles(int pageNum) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=054c6d1972c142b4a8c84bbb300c4d87" + "&page=" + pageNum;
        Log.i("SAMY-againsearch", url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.i("SAMY", "Searching articles");
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
                    Log.i("SAMY", "unsuccessful" + response.toString());
                    throw new IOException("Unexpected code " + response);
                } else {
                    Log.i("SAMY", "successful");
                    Log.i("SAMY", "running on UI Thread");
                    final ArticleResponse artResponse = gson.fromJson(response.body().charStream(), ArticleResponse.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                //JSONArray movieResults = json.getJSONArray("results");
                                allArticles.addAll(artResponse.getResponse().getDocs());
                                articlesAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                Log.i("SAMY-error", e.toString());
                                //e.printStackTrace();
                            }
                        }//end run()
                    });//end runOnUiThread
                }
            }
        });
    }*/

    private void searchArticles(String query,int pageNum) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=054c6d1972c142b4a8c84bbb300c4d87" + "&q=" + query)
                .build();
        Log.i("SAMY", "Searching articles");
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

    class mBeginDateSetListener implements DatePickerDialog.OnDateSetListener {

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

    class mEndDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int datePickerYear, int monthOfYear,
                              int dayOfMonth) {
            int year = datePickerYear;
            int month = monthOfYear;
            int day = dayOfMonth;
            tvEndDate = (TextView) (alertDialog).getView().findViewById(R.id.tvEndDate);
            String endDate = getStringForDate(year, month, day);
            Log.i("SAMY-endDate", endDate);
            Log.i("SAMY-TVendDate", String.valueOf(tvEndDate.getHighlightColor()));
            tvEndDate.setText(endDate);
        }
    }
}
