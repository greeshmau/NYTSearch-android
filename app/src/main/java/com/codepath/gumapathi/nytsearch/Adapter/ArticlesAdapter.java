package com.codepath.gumapathi.nytsearch.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.gumapathi.nytsearch.Model.Doc;
import com.codepath.gumapathi.nytsearch.R;

import java.util.List;

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

        public SingleArticleHolder(View itemView)
        {
            super(itemView);
            //itemView.setOnClickListener(this);
            tvHeadline = (TextView) itemView.findViewById(R.id.tvHeadline);
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

