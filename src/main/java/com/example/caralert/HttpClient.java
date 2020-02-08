package com.example.caralert;

/* Cette classe accède à une adresse par HTTP
    dans un Thread séparé

    Exemple d'usage :
        HttpClient client = new HttpClient("http://.....");
        client.start();
		client.join();
        String result = client.getResponse();
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

//il faut un Thread à part
public class HttpClient extends Thread {

//////Attributs
    private String adresse = "";
    private String methode = "GET";
    private String body = "";
    private String reponse = "";


//////Getters/setters
    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getMethode() {
        return methode;
    }

    public void setMethode(String methode) { //Pour envoyer avec la méthode POST
        this.methode = methode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) { //Pour les paramètres

        this.body = body;
    }

    public String getReponse() {
        return reponse;
    }




//////Constructeur
    public HttpClient(String adresse){
        setAdresse(adresse);
    }


///////Méthode
    /*Méthode run():
    *   Effectuer la connexion,
    *   Ecrire les données,
    *   Lire la réponse
     */

    @Override
    public void run() {
        URL url;
        HttpURLConnection cnt = null;
        try {
            url = new URL(adresse);

            // Établir la connexion :

            cnt = (HttpURLConnection) url.openConnection();
            cnt.setRequestMethod(methode);
            cnt.setDoInput(true);

            // Envoyer les données si méthode POST :

            if(methode.equals("POST")) {
                cnt.setDoOutput(true);
                OutputStream out = cnt.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(body);
                writer.flush();
                writer.close();
                out.close();
            }

            // Lire la réponse :

            InputStream in = cnt.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            reponse = "";
            String line = "";
            while ((line = reader.readLine()) != null) {
                reponse += line;
            }
            reader.close();
            in.close();

        } catch (Exception ex) {
            reponse += "\nErreur : " + ex.getMessage();
        } finally {
            cnt.disconnect();
        }
    }
}
