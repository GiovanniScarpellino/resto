package canadiens.resto.vues;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import canadiens.resto.R;
import canadiens.resto.api.ActionsResultatAPI;
import canadiens.resto.api.RequeteAPI;
import canadiens.resto.api.TypeRequeteAPI;
import canadiens.resto.assistants.Token;

public class VueConnexion extends AppCompatActivity {
    protected EditText champsIdentifiant;
    protected EditText champsMDP;

    protected Button btnConnexion;
    protected Button btnClient;
    protected Button btnRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vue_connexion);

        champsIdentifiant = (EditText) findViewById(R.id.champ_identifiant);
        champsMDP = (EditText) findViewById(R.id.champ_mdp);
        btnConnexion = (Button) findViewById(R.id.action_connexion);
        btnClient = (Button) findViewById(R.id.action_inscription_client);
        btnRestaurant = (Button) findViewById(R.id.action_inscription_restaurant);

        ajouterEcouteur();

        Intent intentionVersHamburgerMenu = new Intent(VueConnexion.this, VuePrincipaleClient.class);
        //startActivity(intentionVersHamburgerMenu);
    }

    /**
     * Listeners des boutons
     */
    private void ajouterEcouteur(){

        btnClient.setOnClickListener(new View.OnClickListener() { // fait la fonction modifier dans le boutton valider
            @Override
            public void onClick(View v) {
                Intent intentionVersInscriptionClient = new Intent(VueConnexion.this, VueInscriptionClient.class);
                intentionVersInscriptionClient.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentionVersInscriptionClient);
            }
        });

        btnRestaurant.setOnClickListener(new View.OnClickListener() { // fait la fonction modifier dans le boutton valider
            @Override
            public void onClick(View v) {
                Intent intentionVersInscriptionRestaurant = new Intent(VueConnexion.this, VueInscriptionRestaurant.class);
                intentionVersInscriptionRestaurant.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentionVersInscriptionRestaurant);
            }
        });

        btnConnexion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try{
                    JSONObject parametres = new JSONObject();
                    parametres.put("login", champsIdentifiant.getText());
                    parametres.put("motDePasse", champsMDP.getText());

                    RequeteAPI.effectuerRequete(TypeRequeteAPI.CONNEXION, parametres, new ActionsResultatAPI() {
                        @Override
                        public void quandErreur() {
                            Toast.makeText(getApplicationContext(), "ERREUR", Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void quandSucces(JSONObject donnees) throws JSONException {
                            Token.definirToken(getApplicationContext(), donnees.get("token").toString());
                            switch (donnees.get("type").toString()){
                                case "client":
                                    startActivity(new Intent(getApplicationContext(), VuePrincipaleClient.class));
                                    break;
                                case "restaurant":
                                    startActivity(new Intent(getApplicationContext(), VuePrincipaleRestaurant.class));
                                    break;
                            }
                        }
                    });
                }
                catch(JSONException e) { e.printStackTrace(); }
            }
        });
    }
}
