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
import canadiens.resto.dialogues.DialogueChargement;

public class VueConnexion extends AppCompatActivity {
    protected EditText champsIdentifiant;
    protected EditText champsMDP;

    protected Button btnConnexion;
    protected Button btnClient;
    protected Button btnRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //On affiche la vue en fonction du type de l'utilisateur
        if (!Token.recupererToken(this).equals("erreur")){
            switch(Token.recupererType(this)){
                case "client":
                    Intent intentionVuePrincipaleClient = new Intent(getApplicationContext(), VuePrincipaleClient.class);
                    intentionVuePrincipaleClient.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentionVuePrincipaleClient);
                    startActivity(intentionVuePrincipaleClient);
                    break;
                case "restaurant":
                    Intent intentionVuePrincipaleRestaurant = new Intent(getApplicationContext(), VuePrincipaleRestaurant.class);
                    intentionVuePrincipaleRestaurant.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentionVuePrincipaleRestaurant);
                    break;
            }
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.vue_connexion);

        champsIdentifiant = (EditText) findViewById(R.id.champ_identifiant);
        champsMDP = (EditText) findViewById(R.id.champ_mdp);
        btnConnexion = (Button) findViewById(R.id.action_connexion);
        btnClient = (Button) findViewById(R.id.action_inscription_client);
        btnRestaurant = (Button) findViewById(R.id.action_inscription_restaurant);

        ajouterEcouteur();

        new Intent(VueConnexion.this, VuePrincipaleClient.class);
    }

    /**
     * Listeners des boutons
     */
    private void ajouterEcouteur() {

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

        btnConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject parametres = new JSONObject();
                    parametres.put("login", champsIdentifiant.getText());
                    parametres.put("motDePasse", champsMDP.getText());

                    final DialogueChargement dialogueChargement = new DialogueChargement(VueConnexion.this, "Connexion...");
                    dialogueChargement.show();

                    RequeteAPI.effectuerRequete(TypeRequeteAPI.CONNEXION, parametres, new ActionsResultatAPI() {
                        @Override
                        public void quandErreur() {
                            dialogueChargement.dismiss();
                            Toast.makeText(getApplicationContext(), "Connexion impossible", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void quandSucces(JSONObject donnees) throws JSONException {
                            dialogueChargement.dismiss();
                            Token.definirToken(getApplicationContext(), donnees.get("token").toString());
                            Token.definirType(getApplicationContext(), donnees.get("type").toString());
                            switch (donnees.get("type").toString()) {
                                case "client":
                                    Intent intentionVuePrincipaleClient = new Intent(getApplicationContext(), VuePrincipaleClient.class);
                                    intentionVuePrincipaleClient.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intentionVuePrincipaleClient.putExtra("nom", donnees.get("nom").toString());
                                    intentionVuePrincipaleClient.putExtra("mail", donnees.get("mail").toString());
                                    startActivity(intentionVuePrincipaleClient);
                                    break;
                                case "restaurant":
                                    Intent intentionVuePrincipaleRestaurant = new Intent(getApplicationContext(), VuePrincipaleRestaurant.class);
                                    intentionVuePrincipaleRestaurant.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intentionVuePrincipaleRestaurant.putExtra("nom", donnees.get("nom").toString());
                                    intentionVuePrincipaleRestaurant.putExtra("mail", donnees.get("mail").toString());
                                    startActivity(intentionVuePrincipaleRestaurant);
                                    break;
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
