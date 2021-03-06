package canadiens.resto.vues;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class VueInscriptionClient extends AppCompatActivity {

    protected EditText champNom;
    protected EditText champPrenom;
    protected EditText champTelephone;
    protected EditText champMail;
    protected EditText champMDP;

    protected Button boutonValider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vue_inscription_client);

        champNom = (EditText) findViewById(R.id.champ_nom_client);
        champPrenom = (EditText) findViewById(R.id.champ_prenom_client);
        champTelephone = (EditText) findViewById(R.id.champ_tel_client);
        champMail = (EditText) findViewById(R.id.champ_mail_client);
        champMDP = (EditText) findViewById(R.id.champ_mdp_client);

        boutonValider = (Button) findViewById(R.id.bouton_valider_client);

        ajouterEcouteur();

    }

    private void ajouterEcouteur() {
        boutonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    JSONObject parametres = new JSONObject();
                    parametres.put("nom", champNom.getText());
                    parametres.put("prenom", champPrenom.getText());
                    parametres.put("telephone", champTelephone.getText());
                    parametres.put("mail", champMail.getText());
                    parametres.put("motDePasse", champMDP.getText());

                    final DialogueChargement dialogueChargement = new DialogueChargement(VueInscriptionClient.this, "Inscription...");
                    dialogueChargement.show();

                    RequeteAPI.effectuerRequete(TypeRequeteAPI.INSCRIPTION_CLIENT, parametres, new ActionsResultatAPI() {
                        @Override
                        public void quandErreur() {
                            dialogueChargement.dismiss();
                            Toast.makeText(getApplicationContext(), "Erreur lors de l'inscription", Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void quandSucces(JSONObject donnees) throws JSONException {
                            dialogueChargement.dismiss();
                            Intent intentionClient = new Intent(getApplicationContext(), VuePrincipaleClient.class);
                            intentionClient.putExtra("token", donnees.get("token").toString());

                            // Récupère le token de l'utilisateur et le place dans la classe statique pour pouvoir le récupérer n'importe quand
                            String token = donnees.get("token").toString();
                            Token.definirToken(getApplicationContext(),token);
                            Token.definirType(getApplicationContext(), "client");

                            startActivity(intentionClient);
                        }
                    });
                }
                catch(JSONException e) { e.printStackTrace(); }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
