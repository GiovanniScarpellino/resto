package canadiens.resto.assistants;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by nicog on 23/02/2018.
 *
 * Classe static qui permet de récupérer le token d'un client n'importe où dans l'application.
 */

public final class Token {

    private static String token;
    public final static String TOKEN_UTILISATEUR = "token utilisateur";

    private Token() { // private constructor
        token = "";
    }

    public static void definirToken(Context contexte, String valeurToken) {
        // Ajout dans les préférences partagées
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(contexte);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN_UTILISATEUR, valeurToken);
        editor.commit();
    }
    public static String recupererToken(Context contexte) {
        // Récupération depuis les préférences partagées
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(contexte);
        return preferences.getString(TOKEN_UTILISATEUR, "erreur");
    }
}
