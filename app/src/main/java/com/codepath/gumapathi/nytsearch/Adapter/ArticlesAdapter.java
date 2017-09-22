package com.codepath.gumapathi.nytsearch.Adapter;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.gumapathi.nytsearch.Model.Doc;
import com.codepath.gumapathi.nytsearch.Model.Multimedium;
import com.codepath.gumapathi.nytsearch.R;
import com.squareup.picasso.Picasso;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static java.security.AccessController.getContext;

/**
 * Created by gumapathi on 9/13/2017.
 */

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.SingleArticleHolder> {
    private List<Doc> articlesList;

    public ArticlesAdapter(List<Doc> articlesList) {
        this.articlesList = articlesList;
    }

    @Override
    public void onBindViewHolder(SingleArticleHolder articleViewHolder, int position) {
        articleViewHolder.tvHeadline.setText(articlesList.get(position).getHeadline().getMain());
        articleViewHolder.tvSnippet.setText(articlesList.get(position).getSnippet());
        String imageUri = "";
        if(!articlesList.get(position).getMultimedia().isEmpty()) {
            //Log.i("SAMY-media-out", articlesList.get(position).getMultimedia().get(0).getSubtype());
            for (Multimedium media : articlesList.get(position).getMultimedia()) {
                //Log.i("SAMY-media", media.getSubtype());
                if (media.getSubtype().equals("thumbnail")) {
                    imageUri = "http://www.nytimes.com/" + media.getUrl();
                    //Log.i("SAMY-adp-else", imageUri);
                }
            }

            ImageView ivArticleImage = articleViewHolder.ivArticleImage;
            Glide.with(articleViewHolder.ivArticleImage.getContext()).load(imageUri).into(ivArticleImage);
            /*Picasso.with(articleViewHolder.ivArticleImage.getContext())
                .load(imageUri)
                //.placeholder(R.drawable.placeholder)
                //.transform(new RoundedCornersTransformation(15, 15, RoundedCornersTransformation.CornerType.ALL))
                .into(ivArticleImage);
                */
        }
        else {
        }

    }

    @Override
    public int getItemCount() {
        return this.articlesList.size();
    }



    @Override
    public SingleArticleHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View layoutView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.article_item, viewGroup, false);
        SingleArticleHolder rcv = new SingleArticleHolder(layoutView);
        return rcv;
    }

    public class SingleArticleHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener{
        public TextView tvHeadline;
        public TextView tvSnippet;
        public ImageView ivArticleImage;

        public SingleArticleHolder(View itemView)
        {
            super(itemView);
            //itemView.setOnClickListener(this);
            tvHeadline = (TextView) itemView.findViewById(R.id.tvHeadline);
            ivArticleImage = (ImageView) itemView.findViewById(R.id.ivArticleImage);
            //tvHeadline.setTypeface(typeFace);
            tvSnippet = (TextView) itemView.findViewById(R.id.tvSnippet);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            Doc article = articlesList.get(getAdapterPosition());
            // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
            String url = article.getWebUrl(); //"https://www.codepath.com/";
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(ContextCompat.getColor(view.getContext(), R.color.colorAccent));
            // set toolbar color and/or setting custom actions before invoking build()
            // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
            Bitmap bitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.ic_share_white_24dp);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, url);

            int requestCode = 100;
            PendingIntent pendingIntent = PendingIntent.getActivity(view.getContext(),
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setActionButton(bitmap, "uu", pendingIntent, false);
            builder.addDefaultShareMenuItem();
            CustomTabsIntent customTabsIntent = builder.build();
            // and launch the desired Url with CustomTabsIntent.launchUrl()
            customTabsIntent.launchUrl(view.getContext(), Uri.parse(url));
            /*Toast.makeText(view.getContext(),
                    "Clicked Position = " + getAdapterPosition(), Toast.LENGTH_SHORT)
                    .show();*/
        }
    }
}

