package com.example.mindflex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.example.mindflex.game.activites.TypingGameActivity;
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

    // game descriptions
    String sequenceMemoryDesc = "Memorize the sequence of buttons that light up, then press them in order.\n\nEvery time you finish the pattern, it gets longer.";
    String chimpGameDesc = "This is a test of working memory, that gets increasingly difficult every turn, starting at 4 digits, and adding one every turn.\n\nIf you pass a level, the number increases. If you fail, you get a strike. Three strikes and the test is over.";
    String typingGameDesc = "This is a simple test of typing speed, measuring words per minute, or WPM.";
    String reactionGameDesc = "This is a simple test of reaction speed. When the screen turns green, tap as quickly as you can.\n\nYour score is the time it takes you to react, measured in milliseconds.";
    String letterMemoryDesc = "This is a test of verbal memory. Words will appear one by one — tap SEEN if you've seen it before, or NEW if it's new.\n\nYou lose a life for every mistake. Survive as long as you can.";
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

        // prepare game data and add games
        gameItemList = new ArrayList<>();
        gameItemList.add(new GameItem("Reaction Time", R.drawable.lightning_bolt_white100, getResources().getColor(R.color.reaction_main), getResources().getColor(R.color.reaction_background_dark),reactionGameDesc, ReactionGameActivity.class));
        gameItemList.add(new GameItem("Number Memory", R.drawable.numbers_white100, getResources().getColor(R.color.orange), getResources().getColor(R.color.black), "Placeholder2", NumberGameActivity.class));
        gameItemList.add(new GameItem("Verbal Memory", R.drawable.wordbook_white100, getResources().getColor(R.color.green), getResources().getColor(R.color.black),letterMemoryDesc, LetterGameActivity.class));
        gameItemList.add(new GameItem("Sequence Memory", R.drawable.grid_white100, getResources().getColor(R.color.sequence_main), getResources().getColor(R.color.sequence_background_dark), sequenceMemoryDesc, SequenceMemoryGameActivity.class));
        gameItemList.add(new GameItem("Chimp Game", R.drawable.monkey_white100, getResources().getColor(R.color.chimp_main), getResources().getColor(R.color.chimp_background_dark),chimpGameDesc, ChimpTestActivity.class));
        gameItemList.add(new GameItem("Typing Game", R.drawable.keyboard_white100, getResources().getColor(R.color.type_main), getResources().getColor(R.color.type_background_dark),typingGameDesc, TypingGameActivity.class));

        //set adapter
        tileAdapter = new TileAdapter(gameItemList);
        recyclerView.setAdapter(tileAdapter);

        //stats button
        statsButton = findViewById(R.id.stats_button);

        statsButton.setOnClickListener(v->{
            HapticFeedbackManager.HapticFeedbackLight(v);
            Context context = rootView.getContext();
            Intent intent = new Intent(context, StatsScreenActivity.class);
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
            HapticFeedbackManager.HapticFeedbackLight(v);
            drawerLayout.openDrawer(GravityCompat.START);
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {}

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                drawerBackButton = navigationView.findViewById(R.id.drawer_back_button);
                drawerBackButton.setOnClickListener(v->{
                    HapticFeedbackManager.HapticFeedbackLight(v);
                    drawerLayout.closeDrawer(GravityCompat.START);
                });
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if(navigationView != null){
                    int size = navigationView.getMenu().size();
                    for(int i = 0; i < size; i++){
                        navigationView.getMenu().getItem(i).setChecked(false);
                    }
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {}
        });
        
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.drawer_home) {
                //Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.drawer_settings) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            } else if (id == R.id.drawer_about) {
                Toast.makeText(this, "It just works!", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

    }


}