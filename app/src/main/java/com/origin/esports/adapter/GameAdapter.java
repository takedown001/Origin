package com.origin.esports.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.origin.esports.Originconfig.URL;
import com.origin.esports.R;
import com.origin.esports.data.Game;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.MyViewHolder> {

    private final Context ctx;

    private final List<Game> moviesList;
    private static final String TAG_GAME = "games";
    public GameAdapter(Context ctx, List<Game> moviesList) {
        this.ctx = ctx;
        this.moviesList = moviesList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final CardView cardView;
        final ImageView topImage;


        MyViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.mainCard);
            title = (TextView) view.findViewById(R.id.Gametitle);
            topImage = (ImageView) view.findViewById(R.id.Gameimg);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.games, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Game game = moviesList.get(position);
        holder.title.setText(game.getTitle());
        if (game.getTopImage().contains("png") || game.getTopImage().contains("jpg")) {
            holder.topImage.setVisibility(View.VISIBLE);
            String img = URL.gameimg + game.getTopImage();
            Glide.with(ctx).load(img).placeholder(R.drawable.wp).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.topImage);
        }

    }


    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
