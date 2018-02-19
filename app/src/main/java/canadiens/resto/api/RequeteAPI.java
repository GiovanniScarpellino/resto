package canadiens.resto.api;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RequeteAPI {
    /**
     * URL de l'API
     */
    public static final String API_URL = "http://localhost";

    /**
     * Genere l'URL de la requete vers l'API en encodant les parametres JSON
     * @param typeRequete Type de la requete ciblee (Les types de requetes sont disponibles dans la classe TypeRequeteAPI)
     * @param parametres Parametres de la requete (String)
     * @return URL finale
     */
    private static String genererURL(String typeRequete, String parametres){
        try{
            return API_URL + "/" + typeRequete + "/" + URLEncoder.encode(parametres, "UTF-8");
        }
        catch(Exception e){
            Log.e("Erreur encodage URL", e.getMessage());
            return API_URL + "/" + typeRequete + "/" + parametres;
        }
    }

    /**
     * Contructeur de la classe RequeteAPI
     * @param typeRequete Type de la requete ciblee (Les types de requetes sont disponibles dans la classe TypeRequeteAPI)
     * @param parametres Parametres de la requete (JSONObject)
     * @param actionsResultatDemande Methodes a executer en cas de reussite ou d'echec de la demande vers l'API
     */
    @SuppressLint("StaticFieldLeak")
    public static void effectuerRequete(String typeRequete, JSONObject parametres, final ActionsResultatAPI actionsResultatDemande){
        //Creation de l'URL entiere
        final String url = genererURL(typeRequete, parametres.toString());
        //Creation et demarrage de la tache asynchrone qui acceder et lire l'API
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try{
                    //Creation de l'URL et de la connexion HTTP
                    URL urlRequete = new URL(url);
                    HttpURLConnection connexion = (HttpURLConnection) urlRequete.openConnection();
                    //Definition des parametres de la connexion
                    connexion.setRequestMethod("GET");
                    connexion.setReadTimeout(15000);
                    connexion.setConnectTimeout(15000);
                    //Connexion a l'URL
                    connexion.connect();

                    //Creation des objets necessaire a la lecture du resultat
                    InputStreamReader streamReader = new InputStreamReader(connexion.getInputStream());
                    BufferedReader reader = new BufferedReader(streamReader);
                    StringBuilder stringBuilder = new StringBuilder();

                    //Lecture du resultat
                    String inputLine;
                    while((inputLine = reader.readLine()) != null){
                        stringBuilder.append(inputLine);
                    }

                    //Fermeture des flux
                    reader.close();
                    streamReader.close();

                    //Envoi du resultat a la fin de la tache asynchrone
                    return stringBuilder.toString();
                }
                catch(IOException e){
                    e.printStackTrace();
                    Log.e("API", "Erreur lors de la connexion et de la lecture du site");
                }
                //Si il y a eu une erreur, on envoie null
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                //On regarde si il y a eu une erreur et si c'est le cas on execute quandErreur()
                if(s == null){
                    actionsResultatDemande.quandErreur();
                    return;
                }

                try{
                    //On convertit le resultat en JSON
                    JSONObject resultat = new JSONObject(s);

                    //On regarde la valeur du parametre resultat
                    //Si c'est 1 on execute la fonction quandSucces() en envoyant le resultat en JSONObject
                    //Sinon ou pour toute erreur on execute la fonction quandErreur()
                    if(resultat.getInt("resultat") == 1){
                        actionsResultatDemande.quandSucces(resultat);
                    }
                    else{
                        actionsResultatDemande.quandErreur();
                    }
                }
                catch(JSONException e){
                    actionsResultatDemande.quandErreur();
                }
            }
        }.execute(); //On lance la tache asynchrone diretement
    }
}
