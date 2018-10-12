package com.example.dylan.cheesechaserv1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedList;

public class Board extends Activity {

    private LinkedList<Card> deck;
    private int points;
    private int traps;

    TextView nbCartes;
    TextView score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        final Game game = (Game) findViewById(R.id.game);

        this.score = (TextView) findViewById(R.id.score);
        this.nbCartes = (TextView) findViewById(R.id.nbCartes);


        game.setContext(this);
        //Affiche le nombre de carte restante
        //nbCartes.setText(String.valueOf(this.deck.size()));

        //Affiche le score actuel
        //Affiche la prochaine carte
                /*final TextView carte = (TextView) findViewById(R.id.carte);
               carte.setText(String.valueOf(Game.carte));*/



        //Redirection vers le MENU
        final TextView menu = (TextView) findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Board.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
