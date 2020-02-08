package com.example.caralert;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class CompteActivity extends AppCompatActivity {

    //déclaration des variables !!! Encore !!!
    EditText etTelephone, etListePlaque;

    //Déclaration des constantes URL
    String URL_RECHERCHE = "https://www.declique.net/cours/caralert/index.php/load/";
    String URL_SAVE = "https://www.declique.net/cours/caralert/index.php/save/";

    //il nous faut un adapteur (qui utilise une class générique ArrayAdapter qui est spécialisé en String
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compte);


        //instanciation !!! Encore !!!
        etTelephone = findViewById(R.id.etTelephone);
        etListePlaque = findViewById(R.id.etListePlaque);

    }

    public void chercher(View view) throws JSONException,InterruptedException {
        //try { plus necessaire après avoir ajouté les throws exception
            //récupérer le numéro de téléphone saisi
            String numero = etTelephone.getText().toString();

            //interroger le Web Service
            HttpClient client = new HttpClient(URL_RECHERCHE + numero);
            client.start();
            client.join();
            String responseWeb = client.getReponse();

            //on récupère le tableau JSON de la réponse
            JSONArray json = new JSONArray(responseWeb);


            //on crée une variable qui contiendra la liste à afficher
            String plaque = "";

            //on boucle pour afficher toutes les plaques
            for(int i=0; i<json.length();i++){
                JSONObject plaques = json.getJSONObject(i);
                plaque += plaques.getString("plaque") + "\n\r";

            }

            //on affiche le résultat
            etListePlaque.setText(plaque);





//            NE FONCTIONNE PAS, MAIS PROF DIT QUE SI ... MOI J'AI VERIFIE ET NON
//            VU QUE CA M'A SAOULEE ... J'AI PRIS UN AUTRE CODE
//
//            //FAIT PAS MOI AVEC L'ADRESSE URL DE LA LIST ENTIERE
//            //https://www.declique.net/cours/caralert/index.php/list/all
//
//            //on récupère le tableau JSON !!!!!! l'architecture de l'objet JSON est très importante !!!!!!
//            JSONArray json = new JSONArray(responseWeb);
//
//            //on crée un tableau pour chaque objet a afficher
//            String[] listes = new String[json.length()];
//
//            for (int i = 0; i < json.length(); i++){
//
//                //on récupère les objets JSON du tableau
//                JSONObject result = json.getJSONObject(i);
//
//                //si la plaque correspond au numéro, on la met dans le tableau
//                if (numero.equals(result.getString("numero"))){
//                    //on prend la valeur de la plaque et on la met dans le tableau à afficher
//                    listes[i] = result.getString("plaque");
//                }
//
//
//            }
//
//            //on affiche la liste avec l'adapteur
//            ArrayAdapter<String> adapter;
//            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listes);
//            lvPlaque.setAdapter(adapter);





//        } idem plus nécessaire après avoir ajouté les throws exception
//        catch (Exception e){
//            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
//        }

    }


    public void valider (View view)throws JSONException{
        try {
        //récupérer le numéro de téléphone saisi et la plaque saisi
        String telephone = etTelephone.getText().toString();
        String plaque = etListePlaque.getText().toString();

        //construire l'objet JSON à envoyer
        JSONObject objetJson = new JSONObject();

        //on le "remplit"
        objetJson.put("plaque", plaque);
        objetJson.put("numero", telephone);

        //on transforme en string
        String objetString = "[" + objetJson.toString() +"]";


        //interroger le Web Service
        HttpClient client = new HttpClient(URL_SAVE + telephone);

        client.setMethode("POST");
        client.setBody(objetString);
        client.start();
        client.join();


        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }


    public void ajouter (View view){

    }


    public void annuler (View view){
        //je crois qu'on est obligé de recréer un Intent
        Intent i = new Intent();

        //on annule le résultat de l'activité en cours
        setResult(Activity.RESULT_CANCELED);

        //on ferme la page et on revient sur la première
        finish();
    }


}