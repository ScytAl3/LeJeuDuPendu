package com.example.lejeudupendu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

/**
 * Class qui herite de la class abstraite BaseAdapter pour mettre en place les lettres du clavier
 * @author Franck J
 * @version 2.49
 */
public class lettresAdaptateur extends BaseAdapter {

   // Tableau de strings pour stocker les lettres du clavier
   private String[] keyboardLetters;
   // Inflater pour la position des boutons
   private LayoutInflater keyInflater;

   /**
    * Methode pour la presentation du layout des lettres qui sera transmise a l activite principale
    * @param c context dans lequel nous voulons faire monter la disposition
    */
   public lettresAdaptateur(Context c) {
      // Instanciation du tableau avec un taille des 26 lettres de l alphabet
      keyboardLetters = new String[26];
      // On ajoute la valeur du caractere 'A' a chaque index du tableau A = 65, B = 66, C = 67...
      for(int k = 0; k < keyboardLetters.length; k++ ) {
         keyboardLetters[k] = "" + (char)(k + 'A');
      }
      keyInflater = LayoutInflater.from(c);
   }

   /**
    * Represente de nombre de vue, une pour chaque lettres
    * @return
    */
   @Override
   public int getCount() {
      return keyboardLetters.length;
   }

   @Override
   public Object getItem(int position) {
      return null;
   }

   @Override
   public long getItemId(int position) {
      return 0;
   }

   /**
    * Methode qui genere chaque vue de lettre mappee sur l element d interface utlisateur via l adaptateur
    * @param position
    * @param convertView
    * @param parent
    * @return
    */
   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
      // Creation d un bouton pour la lettre a sa position dans l alphabet
      Button lettreBtn;
      if (convertView == null) {
         lettreBtn = (Button)keyInflater.inflate(R.layout.lettres_clavier, parent, false);
      } else {
         lettreBtn = (Button)convertView;
      }
      // Mise en place du texte de la lettre
      lettreBtn.setText(keyboardLetters[position]);
      return  lettreBtn;
   }
}
