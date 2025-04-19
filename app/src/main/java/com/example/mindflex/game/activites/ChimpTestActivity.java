package com.example.mindflex.game.activites;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Grid;


import com.example.mindflex.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ChimpTestActivity extends AppCompatActivity {
    private Button backButton;
    private GridLayout gridLayout;

    private int round = 0;
    private int index = 1;
    private int rowsNum = 7;
    private int colsNum = 4;
    private int tileSize = 240;
    private ArrayList<Tile> tilesList = new ArrayList<>();

    private class Tile{
        int number;
        View view;

        Tile(int number, View view){
            this.number = number;
            this.view = view;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chimp_test);

        // fix screen space
        View rootView = findViewById(android.R.id.content);

        // for now bottom and top part of the screen space will be limited
        rootView.setOnApplyWindowInsetsListener((v, insets) -> {
            int topInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).top;
            int bottomInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).bottom;
            v.setPadding(0, topInset, 0, bottomInset);
            return insets;
        });

        // xml elements initialization
        backButton = findViewById(R.id.chimp_test_back);
        gridLayout = findViewById(R.id.chimp_test_grid);

        // back button
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        // TO DO: add start screen and start button

        startRound();

    }

    private void startRound(){
        int tilesNum = round + 4;
        index = 1;
        gridLayout.setRowCount(rowsNum);
        gridLayout.setColumnCount(colsNum);


        // clear tiles
        gridLayout.removeAllViews();
        tilesList.clear();

        ArrayList<Integer> numbers = new ArrayList<>();
        for(int i = 0; i < rowsNum * colsNum; i++){
            numbers.add(i);
        }
        Collections.shuffle(numbers);

        // first generate all tiles
        for(int i = 0; i < rowsNum; i++){
            for(int j = 0; j < colsNum; j++){
                View tile = new View(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = tileSize;
                params.height = tileSize;
                params.setMargins(5,5,5,5);
                tile.setLayoutParams(params);
                tile.setBackgroundResource(R.drawable.chimp_test_tile_background);

                TextView textView = new TextView(this);
                textView.setText("");
                textView.setTextSize(20);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setLayoutParams(params);

                tile = textView;
                gridLayout.addView(tile);

                tilesList.add(new Tile(0, textView));

            }
        }

        Collections.shuffle(tilesList);
        for (int i = 0; i < tilesNum; i++) {
            Tile tile = tilesList.get(i);
            tile.number = i + 1;

            ((TextView) tile.view).setText(String.valueOf(tile.number));
            tile.view.setBackgroundResource(R.drawable.chimp_test_tile_background);
            tile.view.setVisibility(View.VISIBLE);


        }
        tileGuessing(tilesNum);

    }

    private void tileGuessing(int tilesNum){

        for(Tile tile : tilesList){
            tile.view.setOnClickListener(v -> {
                // check if the right tile was clicked
                if(tile.number == index){
                    if(tile.number == 1){
                        tile.view.setVisibility(View.INVISIBLE);

                        // hide numbers of other tiles
                        for(int i = 1; i < tilesNum; i++){
                            Tile tileTemp = tilesList.get(i);
                            ((TextView) tileTemp.view).setText("");
                            tileTemp.view.setBackgroundResource(R.drawable.chimp_test_tile_background_highlight);
                            tileTemp.view.setVisibility(View.VISIBLE);
                        }

                    }
                    else{
                        tile.view.setVisibility(View.GONE);
                    }


                    index = index + 1;
                    if(index == tilesNum + 1){
                        round++;
                        Toast.makeText(this, "GG WP", Toast.LENGTH_LONG).show();
                        startRound();
                    }
                }
                else{
                    Toast.makeText(this, "lmao you suck", Toast.LENGTH_LONG).show();
                    onBackPressed();
                }

            });
        }
    }

}