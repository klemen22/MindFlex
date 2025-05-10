package com.example.mindflex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mindflex.database.ScoreInitialize;
import com.example.mindflex.game.activites.ChimpTestActivity;
import com.example.mindflex.game.activites.LetterGameActivity;
import com.example.mindflex.game.activites.NumberGameActivity;
import com.example.mindflex.game.activites.ReactionGameActivity;
import com.example.mindflex.game.activites.SequenceMemoryGameActivity;
import com.example.mindflex.game.activites.TypingGame;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TileAdapter tileAdapter;
    private List<GameItem> gameItemList;

    private ImageView statsButton;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView drawerMenuButton;

    private ImageView drawerBackButton;

    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // fix screen space
        View rootView = findViewById(android.R.id.content);

        // set default score values
        ScoreInitialize.initializeScores(this);

        // for now bottom and top part of the screen space will be limited
        rootView.setOnApplyWindowInsetsListener((v, insets) -> {
            int topInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).top;
            int bottomInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).bottom;
            v.setPadding(0, topInset, 0, bottomInset);
            return insets;
        });

        // prepare recycler view
        recyclerView = findViewById(R.id.tile_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //prepare game data and add games
        gameItemList = new ArrayList<>();
        gameItemList.add(new GameItem("Reaction Time", R.drawable.lightning_bolt_white100, getResources().getColor(R.color.red), "Placeholder1", ReactionGameActivity.class));
        gameItemList.add(new GameItem("Number Memory", R.drawable.numbers_white100, getResources().getColor(R.color.orange), "Placeholder2", NumberGameActivity.class));
        gameItemList.add(new GameItem("Letter Memory", R.drawable.wordbook_white100, getResources().getColor(R.color.green), "Placeholder3", LetterGameActivity.class));
        gameItemList.add(new GameItem("Sequence Memory", R.drawable.grid_white100, getResources().getColor(R.color.sequence_main), "Placeholder4", SequenceMemoryGameActivity.class));
        gameItemList.add(new GameItem("Chimp Game", R.drawable.monkey_white100, getResources().getColor(R.color.chimp_main), "Placeholder5", ChimpTestActivity.class));
        gameItemList.add(new GameItem("Typing Game", R.drawable.keyboard_white100, getResources().getColor(R.color.type_main), "This is a simple test of typing speed, measuring words per minute, or WPM.", TypingGame.class));

        //set adapter
        tileAdapter = new TileAdapter(gameItemList);
        recyclerView.setAdapter(tileAdapter);

        //stats button
        statsButton = findViewById(R.id.stats_button);

        statsButton.setOnClickListener(v->{
            Context context = rootView.getContext();
            Intent intent = new Intent(context, StatsScreen.class);
            context.startActivity(intent);

        });

        //drawer setup
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        drawerMenuButton = findViewById(R.id.menu_button);
        navigationView.setBackgroundColor(getResources().getColor(R.color.white));
        navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black)));
        navigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));

        drawerMenuButton.setOnClickListener(v->{
            drawerLayout.openDrawer(GravityCompat.START);
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {}

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                drawerBackButton = navigationView.findViewById(R.id.drawer_back_button);
                drawerBackButton.setOnClickListener(v->{
                    drawerLayout.closeDrawer(GravityCompat.START);
                });
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {}

            @Override
            public void onDrawerStateChanged(int newState) {}
        });
        
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.drawer_home) {
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.drawer_settings) {
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.drawer_about) {
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

    }

}