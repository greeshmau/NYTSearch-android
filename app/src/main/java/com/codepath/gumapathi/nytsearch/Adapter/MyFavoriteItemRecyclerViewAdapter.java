package com.codepath.gumapathi.nytsearch.Adapter;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.gumapathi.nytsearch.Database.Favorites;
import com.codepath.gumapathi.nytsearch.R;

import java.util.List;

public class MyFavoriteItemRecyclerViewAdapter extends RecyclerView.Adapter<MyFavoriteItemRecyclerViewAdapter.ViewHolder> {

    private final List<Favorites> mValues;

    public MyFavoriteItemRecyclerViewAdapter(List<Favorites> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        final Favorites fav =  mValues.get(position);
        holder.mIdView.setText(String.valueOf(fav.id));
        holder.mContentView.setText(fav.ArticleTitle);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = fav.ArticleURL;
                Log.i("SAMY-click", "onClick: "+url);
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(v.getContext(), R.color.colorAccent));

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, url);
                int requestCode = 100;
                PendingIntent pendingIntent = PendingIntent.getActivity(v.getContext(),
                        requestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                Bitmap bitmap = BitmapFactory.decodeResource(v.getResources(), R.drawable.ic_share);
                builder.setActionButton(bitmap, "Share Link", pendingIntent, true);

                CustomTabsIntent customTabsIntent = builder.build();
                // and launch the desired Url with CustomTabsIntent.launchUrl()
                customTabsIntent.launchUrl(v.getContext(), Uri.parse(url));
            /*Toast.makeText(view.getContext(),
                    "Clicked Position = " + getAdapterPosition(), Toast.LENGTH_SHORT)
                    .show();*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Favorites mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
