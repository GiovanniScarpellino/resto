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

public class VueInscriptionRestaurant extends AppCompatActivity {

    protected EditText champNom;
    protected EditText champAdresse;
    protected EditText champLatitude;
    protected EditText champLongitude;
    protected EditText champTel;
    protected EditText champMail;
    protected EditText champDescription;
    protected EditText champMDP;

    protected Button btnValider;
    protected Button btnRetour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vue_inscription_restaurant);

        champNom  = (EditText) findViewById(R.id.champ_nom);
        champAdresse  = (EditText) findViewById(R.id.champ_adresse);
        champLatitude  = (EditText) findViewById(R.id.champ_latitude);
        champLongitude  = (EditText) findViewById(R.id.champ_longitude);
        champTel  = (EditText) findViewById(R.id.champ_telephone);
        champMail  = (EditText) findViewById(R.id.champ_mail);
        champDescription  = (EditText) findViewById(R.id.champ_description);
        champMDP  = (EditText) findViewById(R.id.champ_mdp);
        btnValider = (Button) findViewById(R.id.action_valider);
        btnRetour = (Button) findViewById(R.id.action_retour);

        ajouterEcouteur();

    }


    /**
     * Ajoute les écouteur aux boutons
     */
    private void ajouterEcouteur() {
        btnValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    JSONObject parametres = new JSONObject();
                    parametres.put("nom", champNom.getText());
                    parametres.put("description", champDescription.getText());
                    parametres.put("adresse", champAdresse.getText());
                    parametres.put("latitude", champLatitude.getText());
                    parametres.put("longitude", champLongitude.getText());
                    parametres.put("telephone", champTel.getText());
                    parametres.put("mail", champMail.getText());
                    parametres.put("motDePasse", champMDP.getText());

                    RequeteAPI.effectuerRequete(TypeRequeteAPI.INSCRIPTION_RESTAURANT, parametres, new ActionsResultatAPI() {
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

        btnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    finish();
                }
        });
    }
}
