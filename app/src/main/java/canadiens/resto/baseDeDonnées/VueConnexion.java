package canadiens.resto.baseDeDonnées;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import canadiens.resto.R;

public class VueConnexion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vue_connexion);

        Intent intentionVersHamburgerMenu = new Intent(VueConnexion.this, VuePrincipale.class);
        startActivity(intentionVersHamburgerMenu);
    }
}
