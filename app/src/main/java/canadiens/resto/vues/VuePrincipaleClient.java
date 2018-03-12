package canadiens.resto.vues;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import canadiens.resto.R;
import canadiens.resto.api.ActionsResultatAPI;
import canadiens.resto.api.RequeteAPI;
import canadiens.resto.api.TypeRequeteAPI;
import canadiens.resto.assistants.Token;
import canadiens.resto.dialogues.ChargementDialogue;

public class VuePrincipaleClient extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentGoogleMap.OnFragmentInteractionListener {

    public static NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vue_principale_client);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_client);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView menuHamburgerNom = navigationView.getHeaderView(0).findViewById(R.id.menu_hamburger_client_nom);
        TextView menuHamburgerMail = navigationView.getHeaderView(0).findViewById(R.id.menu_hamburger_client_mail);

        menuHamburgerNom.setText(getIntent().getStringExtra("nom"));
        menuHamburgerMail.setText(getIntent().getStringExtra("mail"));

        changerDeFragment(TypeFragment.GoogleMap);
    }

    /**
     * Lorsque l'utilisateur presse "retour" sur son téléphone, le dernier fragment chargé revient donc au premier plan
     */
    @Override
    public void onBackPressed() {
        android.support.v4.app.Fragment monFragment = getSupportFragmentManager().findFragmentByTag("MAP");
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            if (monFragment != null && monFragment.isVisible()){ //Retour quand on se trouve sur la GoogleMap
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
            } else { // Retour quand on est sur un autre fragment que GoogleMap
                System.out.println("retour normal");
                super.onBackPressed();
            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item_menu_parametres_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_setting_clients) {
            Intent intentionVersParametre = new Intent(VuePrincipaleClient.this, VueParametreClient.class);
            startActivity(intentionVersParametre);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_google_map:
                changerDeFragment(TypeFragment.GoogleMap);
                break;
            case R.id.nav_reservations_client:
                changerDeFragment(TypeFragment.ReservationsClient);
                break;
            case R.id.nav_modification_profil_client:
                changerDeFragment(TypeFragment.ModificationClient);
                break;
            case R.id.nav_deconnexion_client:
                try {
                    deconnecterClient();
                }
                catch(JSONException e){
                    Log.e("Deconnexion", "JSONException lors de la déconnexion");
                }
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changerDeFragment(TypeFragment fragment) {
        switch (fragment) {
            case GoogleMap:
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.conteneur_principal_client, new FragmentGoogleMap(), "MAP").addToBackStack("fragment_google_map")
                    .commit();
                navigationView.setCheckedItem(R.id.nav_google_map);
                break;
            case ModificationClient:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.conteneur_principal_client, new FragmentModificationClient()).addToBackStack("fragment_modification_client")
                        .commit();
                navigationView.setCheckedItem(R.id.nav_modification_profil_client);
                break;
            case ReservationsClient:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.conteneur_principal_client, new FragmentReservationsClient()).addToBackStack("fragment_reservations_client")
                        .commit();
                navigationView.setCheckedItem(R.id.nav_reservations_client);
                break;
        }
    }

    private void deconnecterClient() throws JSONException {
        JSONObject parametres = new JSONObject();
        parametres.put("token", Token.recupererToken(this));

        final ChargementDialogue dialogueChargement = new ChargementDialogue(this, "Déconnexion...");
        dialogueChargement.show();

        RequeteAPI.effectuerRequete(TypeRequeteAPI.DECONNEXION, parametres, new ActionsResultatAPI() {
            @Override
            public void quandErreur() {
                dialogueChargement.dismiss();
                Toast.makeText(VuePrincipaleClient.this, "Erreur lors de la déconnexion, veuillez réessayer...", Toast.LENGTH_LONG).show();
            }
            @Override
            public void quandSucces(JSONObject donnees) throws JSONException {
                Token.definirToken(getApplicationContext(), "erreur");
                dialogueChargement.dismiss();
                Intent intentionNaviguerVueConnexion = new Intent(VuePrincipaleClient.this, VueConnexion.class);
                startActivity(intentionNaviguerVueConnexion);
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
