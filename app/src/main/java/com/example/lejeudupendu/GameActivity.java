package com.example.lejeudupendu;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe qui herite de AppCompatActivity pour gerer l activite liee au deroulement du jeu
 * @author Franck J
 * @version 2.49
 */
public class GameActivity extends AppCompatActivity {

   /**************************************************************************
    **                      Declaration des variables                       **
    *************************************************************************/
   /*--                          Variables pour la gestion des mots                             --*/
   private List<String> wordList = new ArrayList<>(); // recupere la liste de mots de notre fichier text
   private String currentWord; // Stocke le mot courant
   private TextView[] charViews;
   private char[] utfChar; // Tableau de caracteres pour stocker les variantes des lettres accentues

   /*                   Variables pour recuperer les elements graphiques                        --*/
   private LinearLayout wordContainer;
   // Variables pour la grille des boutons des lettres
   private GridView keyboardLetters;
   private lettresAdaptateur  letterAdapt;

   /*--                 Variables pour la gestion des parties du corps du pendu                 --*/
   private ImageView[] bodyParts;
   private int numParts=7;    // Nombre total de parties du corps
   private int currentPart;   // Numero de la partie en cours
   private int numChars;      // Nombre de caracteres du mot a trouver
   private int numCorrect;    // Nombre de reponses correctes

   /*--                    Variable pour la barre de menu et les messages                       --*/
   private Toolbar myToolBar;
   private AlertDialog helpAlert;
   private AlertDialog remainAlert;
   private MediaPlayer md_PlaySound;   //
   private boolean first = false;      // Booleen pour la gestion du onResume()

   /**
    * Methode lancee au demarrage de l application ou apres un kill par le systeme et initialise les
    * elements graphiques pour la duree de l activite
    * @param savedInstanceState : est un objet Bundle contenant l etat precedemment enregistre de l activite.
    *                          Si l activite n a jamais existe auparavant, la valeur de l objet Bundle est null.
    */
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_game);
      //--------------------------------------------------------------------------------------------
      // Recuperation des references aux elements crees dans la mise en page
      //--------------------------------------------------------------------------------------------
      // Recuperation de la zone du mot a rechercher
      wordContainer = findViewById(R.id.ll_word_container);
      // Recuperation de la grille des boutons
      keyboardLetters = findViewById(R.id.keyboard);
      // Recuperation des images des parties du corps
      bodyParts = new ImageView[numParts];
      bodyParts[0] = findViewById(R.id.iv_rope);
      bodyParts[1] = findViewById(R.id.iv_head);
      bodyParts[2] = findViewById(R.id.iv_arm1);
      bodyParts[3] = findViewById(R.id.iv_arm2);
      bodyParts[4] = findViewById(R.id.iv_body);
      bodyParts[5] = findViewById(R.id.iv_leg1);
      bodyParts[6] = findViewById(R.id.iv_leg2);

      // Initialisation du lecteur audio
      md_PlaySound = new MediaPlayer();

      // Intialisation du mot en cours
      currentWord = "";

      // Appelle de la methode setSupportActionBar() de l activite pour transmettre la barre d outils
      myToolBar = (Toolbar) findViewById(R.id.my_toolbar);
      setSupportActionBar(myToolBar);

      playGame();
   }

   /**
    * Methode appelee par le systeme lorsque l activite entre dans l etat de reprise et passe au premier plan
    */
   @Override
   protected void onResume() {
      super.onResume();
      if (first) {
         int remainErrors = numParts - currentPart;
         Toast.makeText(getApplicationContext(), "onResumed called", Toast.LENGTH_LONG).show();
         // On averti l utilisateur sur le nombre d essai restants et on lui demande s il veut continuer
         AlertDialog.Builder remainBuild = new AlertDialog.Builder(this);
         remainBuild.setTitle("Previously on this game !");
         remainBuild.setMessage("Nombre d'erreurs encore autorisées :\n\n" + remainErrors);
         remainBuild.setPositiveButton("Continuer ?",
             new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                   remainAlert.dismiss();
                }
             });
         remainBuild.setNegativeButton("Exit",
             new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                   GameActivity.this.finish();
                   GameActivity.this.onDestroy();
                }
             });
         remainAlert = remainBuild.create();
         remainBuild.show();
      }
   }

   /**
    * Methode appelee par le systeme qui indique que l utilisateur quitte l activite et que celle-ci
    * n est plus au premier plan
    */
   @Override
   protected void onPause() {
      super.onPause();
      first = true;
      Toast.makeText(getApplicationContext(), "onPause called", Toast.LENGTH_LONG).show();
   }

   /**
    * Methode pour declarer un menu dans l activite
    * @param menu
    * @return
    */
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate le menu; cela ajoute des elements a la barre d action si elle est presente
      getMenuInflater().inflate(R.menu.menu, menu);
      return true;
   }

   /**
    * Methode qui definit la reaction de l application lorsqu on clique sur une action de l ActionBar
    * @param item
    * @return
    */
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
         case R.id.action_help:
            showHelp();
            return true;
         default:
            return super.onOptionsItemSelected(item);
      }
   }

   /**
    * Methode pour afficher une boite de dialogue quand on presse l icone d aide
    */
   public void showHelp(){
      AlertDialog.Builder helpBuild = new AlertDialog.Builder(this);
      helpBuild.setTitle("Aide");
      helpBuild.setMessage("Devinez le mot en sélectionnant les lettres sur le clavier.\n\n"
          + "Vous n'avez droit qu'à 7 erreurs avant le\n\nGAME OVER !!");
      helpBuild.setPositiveButton("OK",
          new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
                helpAlert.dismiss();
             }});
      helpAlert = helpBuild.create();
      helpBuild.show();
   }

   /**
    * Methode pour la gestion d une partie de pendu
    */
   private void playGame() {
      // Instanciation du mot a trouver
      String newWord = generateWord();
      // Tant que le mot genere aleatoirement et egal au mot courant on en choisi un autre
      while (newWord.equals(currentWord)) newWord = generateWord();
      currentWord = newWord;
      // On stocke le mot sous forme d un tableau de caracteres
      charViews = new TextView[currentWord.length()];

      // On enleve les lettres d un precedent mot
      wordContainer.removeAllViews();

      // On fait une boucle pour recuperer les lettres du mot a trouver
      for(int c = 0; c < currentWord.length(); c++){
         charViews[c] = new TextView(this);
         //set the current letter
         charViews[c].setText("" + currentWord.charAt(c));
         //set layout
         charViews[c].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
             ViewGroup.LayoutParams.WRAP_CONTENT));
         charViews[c].setGravity(Gravity.CENTER);
         charViews[c].setTextColor(Color.WHITE);
         charViews[c].setBackgroundResource(R.drawable.letter_bg);
         //add to display
         wordContainer.addView(charViews[c]);
      }
      // Instanciation de l adaptateur et mise en place dans le GridView
      letterAdapt = new lettresAdaptateur(this);
      keyboardLetters.setAdapter(letterAdapt);

      // Initialisation  de la partie du corps visible au demarrage
      currentPart = 0;
      // Initilisation de la longueur du mots a trouver et du nombre de reponse correcte
      numChars = currentWord.length();
      numCorrect = 0;
      // On cache les parties du corps suivantes
      for(int p = 0; p < numParts; p++){
         bodyParts[p].setVisibility(View.INVISIBLE);
      }
   }

   /**
    * Methode associee a la methode onClick declaree lors de la creation de la disposition de bouton
    * dans le fichier lettres_clavier.xml
    * @param view
    */
   public void letterPressed(View view) {
      // On recupere la lettre qui a ete pressee
      String ltr = ((TextView)view).getText().toString();
      char letterChar = ltr.charAt(0);
      // On desactive la vue correspondant a cette touche
      view.setEnabled(false);
      view.setBackgroundResource(R.drawable.letter_down);
      // On initialise un booleen pour verifier si la lettre est dans le mot
      boolean correct = false;
      // On appelle la methode des tableaux de carcteres accentues
      control(letterChar);
      // Pour chaque lettre dans le tableau de lettres accentues
      for (char c : utfChar) {
         letterChar = c;
         // On parcours la longueur du mot
         for(int k = 0; k < currentWord.length(); k++){
            // Si a la position de l index le caractere du mot correspond a la lettre pressee ( ou une variante accentuee)
            if(currentWord.charAt(k) == letterChar){
               // le Booleen passe a true
               correct = true;
               // on ajoute 1 au nombre de reponse correcte
               numCorrect++;
               // On affiche la lettre trouvee en noir
               charViews[k].setTextColor(Color.BLACK);
            }
         }
      }

      // Si c est une bonne lettre
      if (correct) {
         // Lance la musique de bonne lettre
         md_PlaySound = MediaPlayer.create(this, R.raw.correct);
         md_PlaySound.start();
         // si le nombre de lettres trouver correspond au nombre de lettres du mot cache
         if(numCorrect == numChars){
            // On desactive les boutons des lettres du clavier
            disableBtns();
            // Lance la musique de reussite
            md_PlaySound = MediaPlayer.create(this, R.raw.trumpet_fanfare);
            md_PlaySound.start();
            // On averti l utilisateur qu il a gagne et on lui demande s il veut rejouer
            AlertDialog.Builder winBuild = new AlertDialog.Builder(this);
            winBuild.setTitle("Bravo !");
            winBuild.setMessage("Vous avez gagné !\n\nLe mot à trouver était:\n\n"+currentWord);
            winBuild.setPositiveButton("Voulez-vous rejouer ?",
                new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                      GameActivity.this.playGame();
                      md_PlaySound.stop();
                   }});
            winBuild.setNegativeButton("Exit",
                new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                      GameActivity.this.finish();
                      md_PlaySound.stop();
                   }});
            winBuild.show();
         }
      }
      // Sinon si il reste des parties du corps du pendu a afficher
      else if ((currentPart < numParts) && (currentPart + 1 != numParts)) {
         // Lance la musique de mauvaise lettre
         md_PlaySound = MediaPlayer.create(this, R.raw.fail);
         md_PlaySound.start();
         // On affiche la partie du corps suivante
         bodyParts[currentPart].setVisibility(View.VISIBLE);
         currentPart++;
      }
      // Sinon le joueur a perdu
      else {
         bodyParts[currentPart].setVisibility(View.VISIBLE);
         // On desactive les boutons des lettres du clavier
         disableBtns();
         // Lance la musique de game over
         md_PlaySound = MediaPlayer.create(this, R.raw.os_nuque_briser);
         md_PlaySound.start();
         md_PlaySound = MediaPlayer.create(this, R.raw.funeral_trumpet);
         md_PlaySound.start();
         // On averti l utilisateur qu il a perdu et on lui demande s il veut rejouer
         AlertDialog.Builder loseBuild = new AlertDialog.Builder(this);
         loseBuild.setTitle("Dommage !");
         loseBuild.setMessage("Vous avez perdu !\n\nLe mot à trouver était:\n\n"+currentWord);
         loseBuild.setPositiveButton("Voulez-vous rejouer ?",
             new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                   GameActivity.this.playGame();
                   md_PlaySound.stop();
                }});
         loseBuild.setNegativeButton("Exit",
             new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                   GameActivity.this.finish();
                   md_PlaySound.stop();
                }});
         loseBuild.show();
      }
   }

   /**
    * Methode pour desactiver les lettres du clavier a la fin de la partie
    */
   public void disableBtns(){
      int numLetters = keyboardLetters.getChildCount();
      for(int l = 0; l < numLetters; l++){
         keyboardLetters.getChildAt(l).setEnabled(false);
      }
   }

   /************************************************************************************************
    *    Methodes pour la lecture du fichier de la liste de mot et la gestion aleatoire d un mot
    * *********************************************************************************************/

   /**
    * Methode pour parcourir le fichier texte contenant la liste de mots
    *
    * @return une liste de string wordList
    * @throws IOException
    */
   public List<String> getListOfWord() {

      try {
         BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("wordList.txt")));
         String line;
         // Tant qu il y a un mot on l ajoute
         while ((line = br.readLine()) != null) {
            wordList.add(line);
         }
         br.close();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
      return wordList;
   }

   /**
    * Methode pour selectionner aleatoirement un mot de la liste
    *
    * @return
    */
   public String generateWord() {
      wordList = getListOfWord();
      // On recupere aleatoirement un indexe base sur la taille de la liste de mots
      int random = (int) (Math.floor(Math.random() * wordList.size()));
      // On recupere le mot correspondant a cet indexe
      String searchWord = wordList.get(random).trim().toUpperCase();
      return searchWord;
   }

   /**
    * Methode qui en fonction de la voyelle creee un tableau de caracteres des variantes accentues
    * @param c
    */
   public void control(char c){

      if (c == 'E'){
         char tab[] = {'É', 'È', 'Ê', 'Ë', c};
         utfChar = tab;
      }
      else if (c == 'A'){
         char tab[] = {'À', 'Â', 'Ä', c};
         utfChar = tab;
      }
      else if(c == 'I'){
         char tab[] = {'Î', 'Ï', c};
         utfChar = tab;
      }
      else if (c == 'O'){
         char tab[] = {'Ô', 'Ö', c};
         utfChar = tab;
      }
      else if (c == 'U'){
         char tab[] = {'Û', 'Ü', c};
         utfChar = tab;
      }
      else if (c == 'C'){
         char tab[] = {'Ç', c};
         utfChar = tab;
      }
      else
         utfChar = new char[]{c};
   }
}
