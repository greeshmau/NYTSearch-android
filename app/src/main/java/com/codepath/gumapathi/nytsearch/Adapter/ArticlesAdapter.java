package com.codepath.gumapathi.nytsearch.Adapter;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.gumapathi.nytsearch.Database.Favorites;
import com.codepath.gumapathi.nytsearch.Model.Doc;
import com.codepath.gumapathi.nytsearch.Model.Multimedium;
import com.codepath.gumapathi.nytsearch.R;

import java.util.List;

/**
 * Created by gumapathi on 9/13/2017.
 */

public class ArticlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Doc> articlesList;
    final int WITH_IMAGE = 1;
    final int WITHOUT_IMAGE = 0;

    @Override
    public int getItemViewType(int position) {
        Doc mi = articlesList.get(position);
        if(!articlesList.get(position).getMultimedia().isEmpty()) {
            return WITH_IMAGE;
        }
        return WITHOUT_IMAGE;
    }

    public ArticlesAdapter(List<Doc> articlesList) {
        this.articlesList = articlesList;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder articleViewHolder, int position) {
        Doc item = articlesList.get(position);
        if(articleViewHolder.getItemViewType() == WITH_IMAGE) {
            SingleArticleHolderWithImage withImageHolder = (SingleArticleHolderWithImage) articleViewHolder;
            withImageHolder.tvHeadline.setText(articlesList.get(position).getHeadline().getMain());
            withImageHolder.tvSnippet.setText(articlesList.get(position).getSnippet());
            String imageUri = "";
            for (Multimedium media : item.getMultimedia()) {
                //Log.i("SAMY-media", media.getSubtype());
                if (media.getSubtype().equals("thumbnail")) {
                    imageUri = "http://www.nytimes.com/" + media.getUrl();
                    //Log.i("SAMY-adp-else", imageUri);
                }
            }

            ImageView ivArticleImage = withImageHolder.ivArticleImage;
            ivArticleImage.setImageResource(0);
            Glide.with(withImageHolder.ivArticleImage.getContext())
                    .load(imageUri)
                    .override(400,500)
                    .centerCrop()
                    .fitCenter()
                    .into(ivArticleImage);

        }
        else {
            SingleArticleHolderWithoutImage withoutImageHolder = (SingleArticleHolderWithoutImage) articleViewHolder;
            withoutImageHolder.tvHeadline.setText(articlesList.get(position).getHeadline().getMain());
            withoutImageHolder.tvSnippet.setText(articlesList.get(position).getSnippet());
        }
    }

    @Override
    public int getItemCount() {
        return this.articlesList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View layoutView = null;
        if(viewType == WITH_IMAGE) {
            layoutView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.article_item_with_image, viewGroup, false);
            SingleArticleHolderWithImage rcv = new SingleArticleHolderWithImage(layoutView);
            return rcv;
        }
        else {
            layoutView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.article_item_without_image, viewGroup, false);
            SingleArticleHolderWithoutImage rcv = new SingleArticleHolderWithoutImage(layoutView);
            return rcv;

        }

    }

    public class SingleArticleHolderWithImage extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener{
        public TextView tvHeadline;
        public TextView tvSnippet;
        public ImageView ivArticleImage;
        public ImageButton ibFav;

        public SingleArticleHolderWithImage(View itemView)
        {
            super(itemView);
            //itemView.setOnClickListener(this);
            tvHeadline = (TextView) itemView.findViewById(R.id.tvHeadline);
            ivArticleImage = (ImageView) itemView.findViewById(R.id.ivArticleImage);
            //tvHeadline.setTypeface(typeFace);
            tvSnippet = (TextView) itemView.findViewById(R.id.tvSnippet);
            ibFav = (ImageButton) itemView.findViewById(R.id.ibFav);
            ibFav.setOnClickListener(v -> {
                Doc article = articlesList.get(getAdapterPosition());
                String url = article.getWebUrl();
                String title = article.getHeadline().getMain();
                Favorites newFav = new Favorites(title,url);
                newFav.save();
                Toast.makeText(v.getContext(), "Favorite", Toast.LENGTH_LONG).show();
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            Doc article = articlesList.get(getAdapterPosition());
            String url = article.getWebUrl(); //"https://www.codepath.com/";
            Log.i("SAMY-click", "onClick: "+url);
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = view.getContext().getTheme();
            theme.resolveAttribute(R.attr.backgroundCardColor, typedValue, true);
            @ColorInt int color = typedValue.data;
            builder.setToolbarColor(color);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, url);
            int requestCode = 100;
            PendingIntent pendingIntent = PendingIntent.getActivity(view.getContext(),
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Bitmap bitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.ic_share);
            builder.setActionButton(bitmap, "Share Link", pendingIntent, true);

            CustomTabsIntent customTabsIntent = builder.build();
            // and launch the desired Url with CustomTabsIntent.launchUrl()
            customTabsIntent.launchUrl(view.getContext(), Uri.parse(url));
            /*Toast.makeText(view.getContext(),
                    "Clicked Position = " + getAdapterPosition(), Toast.LENGTH_SHORT)
                    .show();*/
        }
    }

    public class SingleArticleHolderWithoutImage extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener{
        public TextView tvHeadline;
        public TextView tvSnippet;
        public ImageButton ibFav;

        public SingleArticleHolderWithoutImage(View itemView)
        {
            super(itemView);

            tvHeadline = (TextView) itemView.findViewById(R.id.tvHeadline);

            tvSnippet = (TextView) itemView.findViewById(R.id.tvSnippet);
            ibFav = (ImageButton) itemView.findViewById(R.id.ibFav);
            ibFav.setOnClickListener(v -> {
                ibFav.setImageResource(R.drawable.ic_favorite_red_24dp);
                Doc article = articlesList.get(getAdapterPosition());
                String url = article.getWebUrl();
                String title = article.getHeadline().getMain();
                Favorites newFav = new Favorites(title,url);
                newFav.save();
                Toast.makeText(v.getContext(), "Favorited the article", Toast.LENGTH_LONG).show();
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            Doc article = articlesList.get(getAdapterPosition());
            String url = article.getWebUrl(); //"https://www.codepath.com/";
            Log.i("SAMY-click", "onClick: "+url);
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = view.getContext().getTheme();
            theme.resolveAttribute(R.attr.backgroundCardColor, typedValue, true);
            @ColorInt int color = typedValue.data;
            builder.setToolbarColor(color);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, url);
            int requestCode = 100;
            PendingIntent pendingIntent = PendingIntent.getActivity(view.getContext(),
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Bitmap bitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.ic_share);
            builder.setActionButton(bitmap, "Share Link", pendingIntent, true);

            CustomTabsIntent customTabsIntent = builder.build();
            // and launch the desired Url with CustomTabsIntent.launchUrl()
            customTabsIntent.launchUrl(view.getContext(), Uri.parse(url));
            /*Toast.makeText(view.getContext(),
                    "Clicked Position = " + getAdapterPosition(), Toast.LENGTH_SHORT)
                    .show();*/
        }
    }
}

