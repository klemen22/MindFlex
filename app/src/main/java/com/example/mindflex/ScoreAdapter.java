package com.example.mindflex;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private List<ScoreItem> scoreItems;

    public static class ScoreViewHolder extends RecyclerView.ViewHolder{
        TextView textView_game, textView_score;
        public ScoreViewHolder(View item){
            super(item);
            textView_game = item.findViewById(R.id.TextViewGame);
            textView_score = item.findViewById(R.id.TextViewScore);
        }
    }

    public ScoreAdapter(List<ScoreItem> scoreItem){
        this.scoreItems = scoreItem;

    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(ViewGroup parent, int ViewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_item_layout, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScoreViewHolder holder, int position){
        ScoreItem item = scoreItems.get(position);
        String gameID = item.gameID;
        String displayScore = String.valueOf(item.score);

        switch(gameID){
            case "Reaction Game":
                displayScore = displayScore + "ms";
                break;

            case "Typing Game":
                displayScore = displayScore + "wpm";
                break;

            default:
                break;
        }

        holder.textView_game.setText(gameID + ": ");
        holder.textView_score.setText(displayScore);
    }
    @Override
    public int getItemCount(){
        return scoreItems.size();
    }
}
