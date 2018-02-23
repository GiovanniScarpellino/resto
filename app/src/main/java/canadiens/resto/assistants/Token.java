package canadiens.resto.assistants;

/**
 * Created by nicog on 23/02/2018.
 *
 * Classe static qui permet de récupérer le token d'un client n'importe où dans l'application.
 */

public final class Token {

    private static String token;

    private Token() { // private constructor
        token = "";
    }

    public static void definirToken(String val) {
        token = val;
    }
    public static String recupererToken() {
        return token;
    }
}
