package com.example.dylan.cheesechaserv1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class Card {
    private int id;
    private int cote;
    private Bitmap picture;

    public Card(int id,int cote, Bitmap picture) {
        this.id = id;
        this.cote = cote;
        this.picture = picture;
    }
    //

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCote() {
        return cote;
    }

    public void setCote(int cote) {
        this.cote = cote;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    /**
     * Retourne une tuile sous le format d'une image Bitmap
     * Creer la tuile a partir de l'image et la taille de la carte
     * @return Images bitmap faisant office de tuile
     */
    public Bitmap getTuiles(){
        //Renvoie une tuile redimensionne a la taille suivante
        return Bitmap.createScaledBitmap(this.picture,this.cote,this.cote,false);
    }

}


