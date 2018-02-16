package canadiens.resto.assistants;

import android.util.Log;

import java.net.URLEncoder;

public class DemandeAPI {

    public static final String API_URL = "http://localhost";

    public static final String REQUETE_CONNEXION_CLIENT = "ConnexionClient";
    public static final String REQUETE_INSCRIPTION_CLIENT = "InscriptionClient";

    public static String genererURL(String nomRequete, String parametres){
        try{
            return API_URL + "/" + nomRequete + "/" + URLEncoder.encode(parametres, "UTF-8");
        }
        catch(Exception e){
            Log.e("Erreur encodage URL", e.getMessage());
            return API_URL + "/" + nomRequete + "/" + parametres;
        }
    }
}
