package canadiens.resto.vues;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import canadiens.resto.R;

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
                //TODO
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
