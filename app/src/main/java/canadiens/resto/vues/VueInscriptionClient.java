package canadiens.resto.vues;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;

import canadiens.resto.R;

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



    }
}
