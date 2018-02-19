package canadiens.resto.vues;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
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

/**
 * Created by nicog on 16/02/2018.
 */

public class VueInscriptionClient extends AppCompatActivity {

    protected EditText champNom;
    protected EditText champPrenom;
    protected EditText champTelephone;
    protected EditText champMail;
    protected EditText champMDP;

    protected Button boutonValider;
    protected Button boutonRetour;

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
        boutonRetour = (Button) findViewById(R.id.bouton_retour_client);

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

                    RequeteAPI.effectuerRequete(TypeRequeteAPI.INSCRIPTION_CLIENT, parametres, new ActionsResultatAPI() {
                        @Override
                        public void quandErreur() {
                            Toast.makeText(getApplicationContext(), "ERREUR", Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void quandSucces(JSONObject donnees) throws JSONException {
                            Toast.makeText(getApplicationContext(), donnees.get("token").toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                catch(JSONException e) { e.printStackTrace(); }
            }
        });

        boutonRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
