package canadiens.resto.vues;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import canadiens.resto.R;
import canadiens.resto.api.ActionsResultatAPI;
import canadiens.resto.api.RequeteAPI;
import canadiens.resto.api.TypeRequeteAPI;
import canadiens.resto.assistants.ChangerOrientationVueQRCode;
import canadiens.resto.assistants.Token;
import canadiens.resto.dialogues.DialogueChargement;

public class VuePrincipaleRestaurant extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vue_principale_restaurant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_restaurant);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView menuHamburgerNom = navigationView.getHeaderView(0).findViewById(R.id.menu_hamburger_restaurant_nom);
        TextView menuHamburgerMail = navigationView.getHeaderView(0).findViewById(R.id.menu_hamburger_restaurant_mail);

        menuHamburgerNom.setText(getIntent().getStringExtra("nom"));
        menuHamburgerMail.setText(getIntent().getStringExtra("mail"));

        changerDeFragment(TypeFragment.ReservationsRestaurant);
    }

    /**
     * Lorsque l'utilisateur presse "retour" sur son téléphone, le dernier fragment chargé revient donc au premier plan
     */
    @Override
    public void onBackPressed() {
        android.support.v4.app.Fragment monFragmentReservations = getSupportFragmentManager().findFragmentByTag("RESERVATIONS");
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            if (monFragmentReservations != null && monFragmentReservations.isVisible()){ //Retour quand on se trouve sur les réservations
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Se déconnecter");
                builder.setMessage("Sûr de vous ?");

                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish(); // Déconnexion, arrive pas à quitter l'appli
                    }
                });

                builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else { // Retour quand on est sur un autre fragment que Réservations
                super.onBackPressed();
            }
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_reservations_restaurant:
                changerDeFragment(TypeFragment.ReservationsRestaurant);
                break;
            case R.id.nav_modification_profil_restaurant:
                changerDeFragment(TypeFragment.ModificationRestaurant);
                break;
            case R.id.nav_deconnexion_restaurant:
                try {
                    deconnecterRestaurant();
                }
                catch(JSONException e){
                    Log.e("Deconnexion", "JSONException lors de la déconnexion");
                }
                break;
            case R.id.nav_scanner_qr_code:
                IntentIntegrator intention = new IntentIntegrator(this);
                intention.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                intention.setPrompt("Scanner le code QR");
                intention.setCameraId(0);
                intention.setCaptureActivity(ChangerOrientationVueQRCode.class);
                intention.setOrientationLocked(true);
                intention.setBeepEnabled(false);
                intention.initiateScan();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changerDeFragment(TypeFragment fragment) {
        switch (fragment) {
            case ReservationsRestaurant:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.conteneur_principal_restaurant, new FragmentReservationsRestaurant(), "RESERVATIONS").addToBackStack("my_fragment")
                        .commit();
                navigationView.setCheckedItem(R.id.nav_reservations_restaurant);
                break;
            case ModificationRestaurant:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.conteneur_principal_restaurant, new FragmentModificationRestaurant()).addToBackStack("my_fragment")
                        .commit();
                navigationView.setCheckedItem(R.id.nav_modification_profil_restaurant);
                break;
        }
    }

    private void deconnecterRestaurant() throws JSONException {
        JSONObject parametres = new JSONObject();
        parametres.put("token", Token.recupererToken(this));

        final DialogueChargement dialogueChargement = new DialogueChargement(this, "Déconnexion...");
        dialogueChargement.show();

        RequeteAPI.effectuerRequete(TypeRequeteAPI.DECONNEXION, parametres, new ActionsResultatAPI() {
            @Override
            public void quandErreur() {
                dialogueChargement.dismiss();
                Toast.makeText(VuePrincipaleRestaurant.this, "Erreur lors de la déconnexion", Toast.LENGTH_LONG).show();
            }
            @Override
            public void quandSucces(JSONObject donnees) throws JSONException {
                dialogueChargement.dismiss();
                Token.definirToken(getApplicationContext(), "erreur");
                Intent intentionNaviguerVueConnexion = new Intent(VuePrincipaleRestaurant.this, VueConnexion.class);
                startActivity(intentionNaviguerVueConnexion);
            }
        });
    }

    @Override
    protected void onActivityResult(int codeRequete, int codeResultat, Intent donnees) {
        final IntentResult resultat = IntentIntegrator.parseActivityResult(codeRequete, codeResultat, donnees);
        if (resultat != null) {
            if (resultat.getContents() == null) {
                Toast.makeText(this, "Vous avez annuler le scan...", Toast.LENGTH_LONG).show();
            } else {
                JSONObject jsonDonnees = new JSONObject();

                try {
                    jsonDonnees.put("codeFidelite", resultat.getContents());
                    jsonDonnees.put("token", Token.recupererToken(VuePrincipaleRestaurant.this));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final DialogueChargement dialogueChargement = new DialogueChargement(this, "Validation du code...");
                dialogueChargement.show();

                RequeteAPI.effectuerRequete(TypeRequeteAPI.VERIFICATION_CODE_FIDELITE, jsonDonnees, new ActionsResultatAPI() {
                    @Override
                    public void quandErreur() {
                        dialogueChargement.dismiss();
                        Toast.makeText(VuePrincipaleRestaurant.this, "Le code n'est pas valide !", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void quandSucces(JSONObject donnees) throws JSONException {
                        dialogueChargement.dismiss();
                        FragmentModificationPointClient fragmentModificationPointClient = new FragmentModificationPointClient();
                        Bundle argumentAPasser = new Bundle();
                        argumentAPasser.putInt("points", donnees.getInt("points"));
                        argumentAPasser.putString("code", resultat.getContents());
                        fragmentModificationPointClient.setArguments(argumentAPasser);
                        getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.conteneur_principal_restaurant, fragmentModificationPointClient,"POINT")
                                    .commit();
                    }
                });
            }
        } else {
            super.onActivityResult(codeRequete, codeResultat, donnees);
        }
    }
}
