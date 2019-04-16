package com.example.lejeudupendu;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Class MainActivity qui herite de AppCompatActivity pour gerer l activite liee a l ecran d accueil et
 * qui implement une interface d ecoute pour lorsque l utilisateur clique sur un bouton
 * @author Franck J
 * @version 2.49
 */
public class MainActivity extends AppCompatActivity implements OnClickListener {

   // Declaration des variables
   private Button playBtn;
   private MediaPlayer md_Home;

   /**
    * Methode lancee au demarrage de l application ou apres un kill par le systeme et initialise les
    * elements graphiques pour la duree de l activite
    * @param savedInstanceState : est un objet Bundle contenant l etat precedemment enregistre de l activite.
    *                           Si l activite n a jamais existe auparavant, la valeur de l objet Bundle est null.
    */
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      // Initialisation du MediaPlayer et appelle de la methode pour demarrer la lecture
      md_Home = new MediaPlayer();
      playMedia();

      // Recuperation des references aux elements crees lors de la mise en page
      playBtn = findViewById(R.id.playBtn);
      playBtn.setOnClickListener(this);
   }

   /**
    * Methode appelee lorsque l activite devient visible pour l utilisateur
    */
   @Override
   protected void onStart() {
      super.onStart();
      playMedia();
   }

   /**
    * Methode appelee par le systeme lorsque l activite entre dans l etat de reprise et passe au premier plan
    */
   @Override
   protected void onResume() {
      super.onResume();
      playMedia();
   }

   /**
    * Methode appelee par le systeme qui indique que l utilisateur quitte l activite et que celle-ci
    * n est plus au premier plan
    */
   @Override
   protected void onPause() {
      super.onPause();
      md_Home.stop();
   }

   /**
    * Methode qui verifie l etat du MediaPlayer et qui lance la lecture s il n est pas demarre
    */
   private void playMedia() {
      if (!md_Home.isPlaying()) {
         md_Home = MediaPlayer.create(MainActivity.this, R.raw.floorcracking);
         md_Home.start();
         md_Home.setLooping(true);
      }
   }

   /**
    * Methode qui verifie si le bouton a ete clique afin de demarrer l activite du jeu (GameActivity)    *
    * @param view
    */
   @Override
   public void onClick(View view) {
      if (view.getId() == R.id.playBtn) {
         Intent playIntent = new Intent(this, GameActivity.class);
         this.startActivity(playIntent);
         md_Home.stop();
      }
   }
}
