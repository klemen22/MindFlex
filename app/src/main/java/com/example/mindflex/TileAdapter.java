package com.example.mindflex;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TileAdapter extends RecyclerView.Adapter<TileAdapter.TileViewHolder> {

    private List<GameItem> game_list;
    private int expandedPosition = -1;

    public TileAdapter(List<GameItem> game_list){
        this.game_list = game_list;
    }

    @NonNull
    @Override
    public TileAdapter.TileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tile_item_layout, parent, false);
        return new TileViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull TileAdapter.TileViewHolder holder, @SuppressLint("RecyclerView") int position) {
        GameItem current_game = game_list.get(position);

        // set the title, image, and description
        holder.game_title.setText(current_game.getTitle());
        holder.game_image.setImageResource(current_game.getImage_resource());
        holder.game_description.setText(current_game.getDescription());

        // get background drawable
        Drawable backgroundDrawable = ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.tile_background);

        // change background color
        if (current_game.getBackgroundColor() != 0) {
            assert backgroundDrawable != null;
            backgroundDrawable.setTint(current_game.getBackgroundColor());
        }

        holder.itemView.setBackground(backgroundDrawable);

        // logic for expanding or collapsing the tile
        final boolean isExpanded = position == expandedPosition;


        if (!isExpanded) {
            holder.expanded_layout.setVisibility(View.GONE);
            holder.tile_layout_middle.setGravity(Gravity.CENTER);
        } else {
            holder.expanded_layout.setVisibility(View.VISIBLE);
            holder.tile_layout_middle.setGravity(Gravity.START);
        }

        holder.itemView.setOnClickListener(v -> {
            if (isExpanded) {
                expandedPosition = -1; // Collapse
                notifyItemChanged(position);
            } else {
                int prevExpandedPosition = expandedPosition;
                expandedPosition = position; // Expand
                notifyItemChanged(prevExpandedPosition);
                notifyItemChanged(expandedPosition);

                ViewParent parent = holder.itemView.getParent();
                RecyclerView recyclerView = (RecyclerView) holder.itemView.getParent();
                recyclerView.post(() -> recyclerView.smoothScrollToPosition(position));
            }

        });

        final ViewGroup.LayoutParams layoutParams = holder.layout_parent.getLayoutParams();


        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            layoutParams.height = holder.layout_parent.getHeight();
        }

        if (layoutParams != null) {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.layout_parent.setLayoutParams(layoutParams);
        }
        holder.itemView.requestLayout();


        holder.play_button.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, current_game.getTargetActivity());
            context.startActivity(intent);
        });

        Log.d("TileAdapter", "isExpanded: " + isExpanded + ", position: " + position + ", title: " + current_game.getTitle());

    }


    @Override
    public int getItemCount() {
        return game_list.size();
    }

    public static class TileViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout expanded_layout;
        private CardView layout_parent;

        private TextView game_description;
        private Button play_button;
        public TextView game_title;
        public ImageView game_image;

        public LinearLayout tile_layout_middle;


        public TileViewHolder(@NonNull View itemView) {
            super(itemView);
            game_title = itemView.findViewById(R.id.game_title);
            game_image = itemView.findViewById(R.id.game_image);
            expanded_layout = itemView.findViewById(R.id.expanded_layout);
            game_description = itemView.findViewById(R.id.game_description);
            play_button = itemView.findViewById(R.id.play_button);
            layout_parent = itemView.findViewById(R.id.tile_layout_parent);
            tile_layout_middle = itemView.findViewById(R.id.tile_layout_middle);
        }
    }

}
