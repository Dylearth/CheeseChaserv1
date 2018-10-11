package com.example.dylan.cheesechaserv1;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Game extends View implements View.OnTouchListener {

    private Board context;

    private int picturesID[] = {R.drawable.mouse,R.drawable.cat,R.drawable.cheese,R.drawable.mousetrap};

    private HashMap<Map.Entry<Integer,Integer>,Card> cardsOnBoard;

    private int cardSize;
    private int lastActionMoveX;
    private int lastActionMoveY;
    private int offsetX;
    private int offsetY;

    //test


    public Game(Context context,AttributeSet attrs) {
        super(context, attrs);
        this.cardsOnBoard = new HashMap<Map.Entry<Integer,Integer>,Card>();

        this.cardSize = 200;
        this.lastActionMoveX = 0;
        this.lastActionMoveY = 0;
        this.offsetX = 0;
        this.offsetY = 0;

        this.context.setTraps(0);
        this.context.setPoints(0);
        this.context.setDeck(new LinkedList<Card>());

        this.fillDeck();
        setOnTouchListener(this);

        //Tuile par défaut
        this.placeTileOnBoard(400,800);
    }

    @Override
    protected void onDraw(Canvas canvas) {


        for (Map.Entry<Integer,Integer> cardPosition : this.cardsOnBoard.keySet()) {
            //Definition des param de style avec un paint
            Paint paint=new Paint();

            //Definit le fond des images (si aucun fond ou transparent, l'image ne s'affiche pas)
            paint.setColor(Color.RED);

            //Recuperation de la carte dans le HashMap
            Card card = this.cardsOnBoard.get(cardPosition);

            //Dessin de l'image ou la cle de l'Entry est X et la valeur Y
            canvas.drawBitmap(card.getTuiles(), cardPosition.getKey(), cardPosition.getValue(), paint);


        }

    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        int xTouch = 0;
        int yTouch = 0;

        Log.d("X" , String.valueOf(x));
        Log.d("y" , String.valueOf(y));

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN :
                //Par la suite il faudra enregistrer le dernier decalage
                //fait par l'utilisateur (ACTION_MOVE) et l'ajouter a la
                //variable titleSize pour gerer l'ajout de Tuile en
                // post-decalage du board


                //Place un carres a la position appuyés dans un quadrillage
                // dont la taille des carreaux depend de la taille des tuiles
                // Dans le cas d'un X ou Y negatif, on retire decale la tuile
                // d'une taille de carte
                if((int)(event.getX()-this.offsetX) < 0){
                    xTouch = ((int) (event.getX()-this.offsetX-this.cardSize)/this.cardSize)*this.cardSize;
                }else{
                    xTouch = ((int) (event.getX()-this.offsetX)/this.cardSize)*this.cardSize;
                }
                if((int)(event.getY()-this.offsetY) < 0){
                    yTouch = ((int) (event.getY()-this.offsetY-this.cardSize)/this.cardSize)*this.cardSize;
                }else{
                    yTouch = ((int) (event.getY()-this.offsetY)/this.cardSize)*this.cardSize;
                }

                //Ajout des offset
                xTouch += this.offsetX;
                yTouch += this.offsetY;

                Card cardInPosition = this.getTileByPosition(xTouch,yTouch);

                if(cardInPosition != null){
                    if(cardInPosition.getId() == R.drawable.plus){

                        //Suppression de plus
                        this.removePlus();

                        //Recupere et supprime la premiere carte du deck
                        //Ajoute les plus autour de la cart
                        this.placeTileOnBoard(xTouch,yTouch);

                        this.updateBoard();


                        //Enregistre la derniere position du touch
                    }
                }

                this.lastActionMoveX = x;
                this.lastActionMoveY = y;
                break;

            //CE TRUC FONCTIONNE MAIS RAM A MORT
            case MotionEvent.ACTION_MOVE :
                HashMap newTilses = new HashMap<Map.Entry<Integer,Integer>,Card>();

                for (Map.Entry<Integer,Integer> newCardPosition : this.cardsOnBoard.keySet()) {
                    //Info de la card actuelle
                    int lastX = newCardPosition.getKey();
                    int lastY =newCardPosition.getValue();
                    Card c = cardsOnBoard.get(newCardPosition);

                    //Ajout des valeurs et de la carte dans la nouvelle liste
                    //Durant un move, la valeur x ou y devient celle d'avant
                    // l'event plus la difference entre la position du touch
                    // actuel et l'anciennne position du touch
                    newTilses.put(new AbstractMap.SimpleEntry<Integer, Integer>(
                            (lastX + (x - this.lastActionMoveX)),(lastY + (y - this.lastActionMoveY))),c);
                }

                this.cardsOnBoard.clear();
                this.cardsOnBoard.putAll(newTilses);

                this.offsetX += (x - this.lastActionMoveX);
                this.offsetY += (y - this.lastActionMoveY);

                Log.d("xOffset" , String.valueOf(offsetX));
                Log.d("yOffset" , String.valueOf(offsetY));

                this.lastActionMoveX = x;
                this.lastActionMoveY = y;
                break;
        }

        this.invalidate();
        return true;
    }

    /**
     * Supprime tous les plus du board
     */
    private void removePlus() {
        HashMap<Map.Entry<Integer,Integer>,Card> plusCleared = new HashMap<Map.Entry<Integer,Integer>,Card>();

        for (Map.Entry<Integer,Integer> position: this.cardsOnBoard.keySet()) {
            Card c = this.cardsOnBoard.get(position);
            if(c.getId() != R.drawable.plus){
                plusCleared.put(position,c);
            }
        }

        this.cardsOnBoard.clear();
        this.cardsOnBoard.putAll(plusCleared);

    }

    /**
     * Update le board
     */
    private void updateBoard() {
        HashMap<Map.Entry<Integer,Integer>,Card> plusCleared = new HashMap<Map.Entry<Integer,Integer>,Card>();

        for (Map.Entry<Integer,Integer> position: this.cardsOnBoard.keySet()) {
            Card c = this.cardsOnBoard.get(position);

            switch (c.getId()){
                case R.drawable.mouse :
                    this.mouseBehavior(position);
                    break;

                case R.drawable.mousetrap :
                    this.trapBehavior(position);
                    break;

                case R.drawable.cheese:
                    this.cheeseBehavior(position);
                    break;

            }
            plusCleared.put(position,c);
        }

        this.cardsOnBoard.clear();
        this.cardsOnBoard.putAll(plusCleared);

    }

    /**
     * Gere le comportement d'une souris dans le jeu
     * @param mousePosition Tuile souris
     */
    private void mouseBehavior(Map.Entry<Integer,Integer> mousePosition) {

        List<Card> mouseNeighboor = new ArrayList<Card>();
        mouseNeighboor.add(this.getTileByPosition(mousePosition.getKey()-this.cardSize,mousePosition.getValue()));
        mouseNeighboor.add(this.getTileByPosition(mousePosition.getKey()+this.cardSize,mousePosition.getValue()));
        mouseNeighboor.add(this.getTileByPosition(mousePosition.getKey(),mousePosition.getValue()-this.cardSize));
        mouseNeighboor.add(this.getTileByPosition(mousePosition.getKey(),mousePosition.getValue()+this.cardSize));

        boolean isDead = false;
        int point = 1;

        for (Card card: mouseNeighboor) {
            if (card != null){
                if(card.getId() == R.drawable.cat){
                    isDead = true;
                }else if (card.getId() == R.drawable.cheese){
                    point *= 2;
                }
            }
        }

        if(isDead){
            Card c = this.cardsOnBoard.get(mousePosition);
            c.setId(R.drawable.deadmouse);

            Bitmap deadMouse = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.deadmouse)
                    ,this.cardSize,this.cardSize,false);

            c.setPicture(deadMouse);
        }else{
            context.setPoints(context.getPoints()+point);
        }
    }

    /**
     * Gere le comportement d'un piege dans le jeu
     * @param trapPosition Tuile trap
     */
    private void trapBehavior(Map.Entry<Integer,Integer> trapPosition) {

        List<Card> trapNeighboor = new ArrayList<Card>();
        trapNeighboor.add(this.getTileByPosition(trapPosition.getKey()-this.cardSize,trapPosition.getValue()));
        trapNeighboor.add(this.getTileByPosition(trapPosition.getKey()+this.cardSize,trapPosition.getValue()));
        trapNeighboor.add(this.getTileByPosition(trapPosition.getKey(),trapPosition.getValue()-this.cardSize));
        trapNeighboor.add(this.getTileByPosition(trapPosition.getKey(),trapPosition.getValue()+this.cardSize));

        int mouseNeighboor = 0;

        for (Card card: trapNeighboor) {
            if (card != null){
                if(card.getId() == R.drawable.mouse){
                    mouseNeighboor++;
                }
            }
        }

        if(mouseNeighboor == 4){
            Card c = this.cardsOnBoard.get(trapPosition);
            c.setId(R.drawable.deadmousetrap);

            Bitmap deadTrap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.deadmousetrap)
                    ,this.cardSize,this.cardSize,false);

            c.setPicture(deadTrap);
        }else{
            context.setTraps(context.getTraps()+1);
        }
    }

    /**
     * Gere le comportement d'un piege dans le jeu
     * @param cheesePosition Tuile trap
     */
    private void cheeseBehavior(Map.Entry<Integer,Integer> cheesePosition) {

        List<Card> cheeseNeighboor = new ArrayList<Card>();
        cheeseNeighboor.add(this.getTileByPosition(cheesePosition.getKey()-this.cardSize, cheesePosition.getValue()));
        cheeseNeighboor.add(this.getTileByPosition(cheesePosition.getKey()+this.cardSize, cheesePosition.getValue()));
        cheeseNeighboor.add(this.getTileByPosition(cheesePosition.getKey(), cheesePosition.getValue()-this.cardSize));
        cheeseNeighboor.add(this.getTileByPosition(cheesePosition.getKey(), cheesePosition.getValue()+this.cardSize));

        int mouseNeighboor = 0;

        for (Card card: cheeseNeighboor) {
            if (card != null){
                if(card.getId() == R.drawable.mouse){
                    mouseNeighboor++;
                }
            }
        }

        if(mouseNeighboor == 4){
            context.setPoints(context.getPoints()+10);
        }
    }


    /**
     * Retourne une carte selon sa position ou null si
     * la position est vide
     * @param x Axe X
     * @param y Axe Y
     * @return Tuile trouvé ou null si position vide
     */
    private Card getTileByPosition(int x, int y) {
        Card foundedCard = null;
        for (Map.Entry<Integer,Integer> position: this.cardsOnBoard.keySet()) {
            if(position.getKey() == x && position.getValue() == y){
                foundedCard = cardsOnBoard.get(position);
            }
        }

        return foundedCard;
    }

    /**
     * Ajout une tuile sur le board, la retire du deck
     * et genere les tuiles "+"
     * @param x Axe x
     * @param y Axe y
     */
    private void placeTileOnBoard(int x, int y){
        Card cardPicked = context.getDeck().removeLast();
        if(context.getDeck().size() == 0){

        }
        Map.Entry<Integer,Integer> cardPosition;
        this.cardsOnBoard.put(new AbstractMap.SimpleEntry<Integer, Integer>(x,y),cardPicked);

        this.placePlusTiles(x,y);
    }

    private void placePlusTiles(int x, int y) {

        //Tuile plus
        Bitmap plusPicture = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.plus)
                ,this.cardSize,this.cardSize,false);
        Card plusTile = new Card(R.drawable.plus,this.cardSize,plusPicture);

        //Future board
        HashMap<Map.Entry<Integer,Integer>,Card> plusAdded = new HashMap<Map.Entry<Integer,Integer>,Card>();

        plusAdded.put(new AbstractMap.SimpleEntry<Integer, Integer>(x - this.cardSize, y - this.cardSize), plusTile);
        plusAdded.put(new AbstractMap.SimpleEntry<Integer, Integer>(x, y - this.cardSize), plusTile);
        plusAdded.put(new AbstractMap.SimpleEntry<Integer, Integer>(x + this.cardSize, y - this.cardSize), plusTile);
        plusAdded.put(new AbstractMap.SimpleEntry<Integer, Integer>(x - this.cardSize, y), plusTile);
        plusAdded.put(new AbstractMap.SimpleEntry<Integer, Integer>(x + this.cardSize, y ), plusTile);
        plusAdded.put(new AbstractMap.SimpleEntry<Integer, Integer>(x - this.cardSize, y + this.cardSize), plusTile);
        plusAdded.put(new AbstractMap.SimpleEntry<Integer, Integer>(x, y + this.cardSize), plusTile);
        plusAdded.put(new AbstractMap.SimpleEntry<Integer, Integer>(x + this.cardSize, y + this.cardSize), plusTile);

        plusAdded.putAll(this.cardsOnBoard);
        this.cardsOnBoard.putAll(plusAdded);
    }

    /**
     * Rempli de cartes le deck de la partie
     * Un deck contient 40 cartes
     */
    private void fillDeck(){
/*        for (int i = 0; i < 20; i++) {
            Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.mouse);
            Card c = new Card(R.drawable.mouse,this.cardSize,bitmap);
            this.deck.add(c);
        }
        for (int i = 0; i < 20; i++){
            int typeCardPicked = (int) (Math.random() * this.picturesID.length -1);
            int pictureRan = this.picturesID[typeCardPicked+1];
            Bitmap bitmap=BitmapFactory.decodeResource(getResources(),pictureRan);
            Card c = new Card(pictureRan,this.cardSize,bitmap);
            this.deck.add(c);
        }*/

        int mouse = 0;
        int trap = 0;
        int cheese = 0;
        int cat = 0;
        boolean toDraw = false;
        while (mouse+trap+cheese+cat != 40) {
            int typeCardPicked = (int) (Math.random() * this.picturesID.length);
            int pictureRan = this.picturesID[typeCardPicked];
            switch (pictureRan) {
                case R.drawable.mouse:
                    if(mouse < 20){
                        mouse++;
                        toDraw = true;
                    }else{
                        toDraw = false;
                    }
                    break;

                case R.drawable.cat:
                    if(cat < 8){
                        cat++;
                        toDraw = true;
                    }else{
                        toDraw = false;
                    }
                    break;

                case R.drawable.mousetrap:
                    if(trap < 4){
                        trap++;
                        toDraw = true;
                    }else{
                        toDraw = false;
                    }
                    break;

                case R.drawable.cheese:
                    if(cheese < 10){
                        cheese++;
                    }else{
                        toDraw = false;
                    }
                    break;
            }

            if (toDraw){
                Bitmap bitmap=BitmapFactory.decodeResource(getResources(),pictureRan);
                Card c = new Card(pictureRan,this.cardSize,bitmap);
                context.getDeck().add(c);
            }
        }


        Collections.shuffle(context.getDeck());

    }


    public void setContext(Context context) {
        this.context = (Board) context;
    }
}

