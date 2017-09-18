package com.codepath.gumapathi.nytsearch.Adapter;

import android.graphics.Typeface;
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

import java.io.File;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

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
            Log.i("SAMY-media-out", articlesList.get(position).getMultimedia().get(0).getSubtype());
            for (Multimedium media : articlesList.get(position).getMultimedia()) {
                Log.i("SAMY-media", media.getSubtype());
                if (media.getSubtype().equals("thumbnail")) {
                    imageUri = "http://www.nytimes.com/" + media.getUrl();
                    Log.i("SAMY-adp-else", imageUri);
                }
            }
            ImageView ivArticleImage = articleViewHolder.ivArticleImage;
            Picasso.with(articleViewHolder.ivArticleImage.getContext())
                .load(imageUri)
                //.placeholder(R.drawable.placeholder)
                //.transform(new RoundedCornersTransformation(15, 15, RoundedCornersTransformation.CornerType.ALL))
                .into(ivArticleImage);
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

    public class SingleArticleHolder extends RecyclerView.ViewHolder {
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
        }

        /*@Override
        public void onClick(View view)
        {
            Toast.makeText(view.getContext(),
                    "Clicked Position = " + getPosition(), Toast.LENGTH_SHORT)
                    .show();
        }*/
    }
}

