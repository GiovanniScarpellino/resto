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
    private String type;
    public final static String TOKEN_UTILISATEUR = "token utilisateur";
    public final static String TYPE_UTILISATEUR = "type utilisateur";
    public final static String NOM_UTILISATEUR = "nom utilisateur";
    public final static String MAIL_UTILISATEUR = "mail utilisateur";

    private Token() { // private constructor
        token = "";
        type = "";
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



    public static void definirType(Context contexte, String valeurType) {
        // Ajout dans les préférences partagées
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(contexte);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TYPE_UTILISATEUR, valeurType);
        editor.apply();
    }
    public static String recupererType(Context contexte) {
        // Récupération depuis les préférences partagées
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(contexte);
        return preferences.getString(TYPE_UTILISATEUR, "erreur");
    }

    public static void definirNom(Context contexte, String valeurNom) {
        // Ajout dans les préférences partagées
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(contexte);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(NOM_UTILISATEUR, valeurNom);
        editor.apply();
    }
    public static String recupererNom(Context contexte) {
        // Récupération depuis les préférences partagées
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(contexte);
        return preferences.getString(NOM_UTILISATEUR, "");
    }


    public static void definirMail(Context contexte, String valeurMail) {
        // Ajout dans les préférences partagées
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(contexte);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MAIL_UTILISATEUR, valeurMail);
        editor.apply();
    }
    public static String recupererMail(Context contexte) {
        // Récupération depuis les préférences partagées
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(contexte);
        return preferences.getString(MAIL_UTILISATEUR, "");
    }
}
