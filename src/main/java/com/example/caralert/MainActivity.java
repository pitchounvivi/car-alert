package com.example.caralert;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;


//import pour la partie photo et reconnaissance de texte
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;


//Pour pouvoir faire la partie photo
///////////////////////////////////////////////////////
//Dans onglet Tools
//dans SDK manager
//dans android SDK
//dans SDK tools
//cocher Google play Service et appliquer (il installe)
///////////////////////////////////////////////////////
//BIEN PENSER A AJOUTER
//implementation 'com.google.android.gms:play-services-vision:18.0.0'
//dans build.gradle(module)
//dans les dépendances
//faire sync now
///////////////////////////////////////////////////////


public class MainActivity extends AppCompatActivity {

    //Délaration des variables comme d'hab !!!!
    EditText etPlaque;
    TextView tvTest;
    ImageView ivPhoto;

    //Déclaration des constantes URL
    String URL_ALERTER = "https://www.declique.net/cours/caralert/index.php/get/";


//////Déclarations pour la partie photo
    Integer REQUEST_PHOTO = 1;

    //cet objet s'occupe de reconnaitre les textes dans une images
    TextRecognizer tr;

    //il s'agit d'un tableau à parser
    SparseArray<TextBlock> result;
    TextBlock results;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instantiation ... Comme d'hab !!!
        etPlaque = findViewById(R.id.etPlaque);
        tvTest = findViewById(R.id.tvTest);
        ivPhoto = findViewById(R.id.ivPhoto);

        //pour la partie photo
        TextRecognizer.Builder builder = new TextRecognizer.Builder(this);
        tr = builder.build();

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PHOTO){
            if(resultCode == Activity.RESULT_OK) {

                //récupérer un bitmap (photo)
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                //pour afficher le bitmap
                ivPhoto.setImageBitmap(bitmap);

                //création du Frame à partir d'un bitmap
                //Taper dans Google "Frame from bitmap" pour trouver cette formule
                //le frame est le cadre où on va mettre la photo, il faut le définir
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();


                //le tr.detect sert à rechercher du texte sur la photo
                result = tr.detect(frame); //le tr retourne un objet SparseArray de TextBlock (SparseArray<TextBlock>)

                for(int i=0; result.size()>i; i++){
                    results = result.valueAt(i);
                    String test = results.getValue();
                    etPlaque.setText(test);
                }
            }
        }
    }



    public void alerter(View view) {
        try {
            //on récupère la plaque saisie dans la zone de texte
            String plaque = etPlaque.getText().toString();

            //on interroge le Web Service
            HttpClient client = new HttpClient(URL_ALERTER + plaque);
            client.start();
            client.join();
            String reponseWeb = client.getReponse();

            //on récupère l'objet JSON
            JSONObject json = new JSONObject(reponseWeb);

            //transformation en String pour pouvoir tester
            String numeroString = json.getString("numero");

            //Pour test on affiche la valeur récupérée
            tvTest.setText(numeroString);


            ///////////////////////////////////////
            //infomation intéressante sur :
            //https://stackoverflow.com/questions/2372248/launch-sms-application-with-an-intent
            /////////////envoi du SMS//////////////
            //création de l'Intent d'envoi
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);//permet juste de voir l'envoi
            //Intent sendIntent = new Intent(Intent.ACTION_SENDTO);//permet envoyer en choisissant avec quelle appli

            //on récupère la plaque
            String plaqueString = json.getString("plaque");

            //le message
            sendIntent.putExtra("address", numeroString);
            sendIntent.putExtra("sms_body", "Votre voiture immatriculée " +
                    plaqueString + " gène. Veuillez la déplacer.");

            startActivity(sendIntent);



            /*autre possibilité plus fiable ?
            elle ne demande pas à l'utilisateur d'envoyer, elle envoie directement
            Et lui n'utilise pas le Intent
            Info sur l'adresse : https://www.tutorialspoint.com/android/android_sending_sms.htm

            SmsManager smsManager = smsManager.getDefault();
            smsManager.sendTextMessage("numéro téléphone",null,"sms message", null,null);
             */


        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }


    public void compte(View view) {
        //création de l'Intent pour lancer l'activité 2 qui fait partie de la même activité
        Intent i = new Intent(this, CompteActivity.class);

        //lancer la deuxième activité
        startActivity(i);
    }


    public void photo(View view) {
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        setResult(Activity.RESULT_OK);
        startActivityForResult(i, REQUEST_PHOTO);
    }


    public void alerterTous(View view) {
    }
}
