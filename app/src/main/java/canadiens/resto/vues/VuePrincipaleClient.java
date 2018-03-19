package canadiens.resto.vues;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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
import canadiens.resto.dialogues.DialogueChargement;

public class VuePrincipaleClient extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentGoogleMap.OnFragmentInteractionListener{

    public static NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vue_principale_client);
        Toolbar toolbar = findViewById(R.id.toolbar_client);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView menuHamburgerNom = navigationView.getHeaderView(0).findViewById(R.id.menu_hamburger_client_nom);
        TextView menuHamburgerMail = navigationView.getHeaderView(0).findViewById(R.id.menu_hamburger_client_mail);

        if(getIntent(). getStringExtra("nom") != null){
            Token.definirNom(this, getIntent().getStringExtra("nom"));
            Token.definirMail(this, getIntent().getStringExtra("mail"));
        }
        menuHamburgerNom.setText(Token.recupererNom(this));
        menuHamburgerMail.setText(Token.recupererMail(this));

        changerDeFragment(TypeFragment.GoogleMap);
    }

    /**
     * Lorsque l'utilisateur presse "retour" sur son téléphone, le dernier fragment chargé revient donc au premier plan
     */
    @Override
    public void onBackPressed() {
        Fragment fragmentMap = getSupportFragmentManager().findFragmentByTag(TypeFragment.GoogleMap+"");
        Fragment fragmentDetail = getSupportFragmentManager().findFragmentByTag(TypeFragment.DetailsRestaurant+"");
        Fragment fragmentPoint = getSupportFragmentManager().findFragmentByTag(TypeFragment.ModifierPointsClient+"");
        Fragment fragmentModificationClient = getSupportFragmentManager().findFragmentByTag(TypeFragment.ModificationClient+"");
        Fragment fragmentReservationsClient = getSupportFragmentManager().findFragmentByTag(TypeFragment.ReservationsClient+"");
        Fragment fragmentCodeFidelite = getSupportFragmentManager().findFragmentByTag(TypeFragment.CodeFidelite+"");

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            if (fragmentMap != null && fragmentMap.isVisible()){ //Retour quand on se trouve sur la GoogleMap
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Fermer l'application ?");
                builder.setMessage("Voulez vous fermer l'application ?");

                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
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
            }else{
                super.onBackPressed();
            }
        }

        if (fragmentPoint != null && fragmentPoint.isVisible()){ // Si on se trouve sur la fragment pour modifier les points d'un client un restaurant
            changerDeFragment(TypeFragment.GoogleMap);
        }

        if (fragmentDetail != null && fragmentDetail.isVisible()){ // Si on se trouve sur la fragment pour réserver un restaurant
            changerDeFragment(TypeFragment.GoogleMap);
        }

        //Actualiser le menu hamburger
        if(fragmentMap != null && fragmentMap.isVisible()){
            navigationView.setCheckedItem(R.id.nav_google_map);
        }else if(fragmentModificationClient != null && fragmentModificationClient.isVisible()){
            navigationView.setCheckedItem(R.id.nav_modification_profil_client);
        }else if(fragmentReservationsClient != null && fragmentReservationsClient.isVisible()){
            navigationView.setCheckedItem(R.id.nav_reservations_client);
        }else if(fragmentCodeFidelite != null && fragmentCodeFidelite.isVisible()){
            navigationView.setCheckedItem(R.id.nav_afficher_code_fidelite);
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
            case R.id.nav_afficher_code_fidelite :
                changerDeFragment(TypeFragment.CodeFidelite);
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
                    .replace(R.id.conteneur_principal_client, new FragmentGoogleMap(), TypeFragment.GoogleMap+"").addToBackStack("my_fragment")
                    .commit();
                navigationView.setCheckedItem(R.id.nav_google_map);
                break;
            case ModificationClient:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.conteneur_principal_client, new FragmentModificationClient(), TypeFragment.ModificationClient+"").addToBackStack("my_fragment")
                        .commit();
                navigationView.setCheckedItem(R.id.nav_modification_profil_client);
                break;
            case ReservationsClient:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.conteneur_principal_client, new FragmentReservationsClient(), TypeFragment.ReservationsClient+"").addToBackStack("my_fragment")
                        .commit();
                navigationView.setCheckedItem(R.id.nav_reservations_client);
                break;
            case CodeFidelite:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.conteneur_principal_client, new FragmentCodeFidelite(), TypeFragment.CodeFidelite+"").addToBackStack("my_fragment")
                        .commit();
                navigationView.setCheckedItem(R.id.nav_reservations_client);
                break;
        }
    }

    private void deconnecterClient() throws JSONException {
        JSONObject parametres = new JSONObject();
        parametres.put("token", Token.recupererToken(this));

        final DialogueChargement dialogueChargement = new DialogueChargement(this, "Déconnexion...");
        dialogueChargement.show();

        RequeteAPI.effectuerRequete(TypeRequeteAPI.DECONNEXION, parametres, new ActionsResultatAPI() {
            @Override
            public void quandErreur() {
                dialogueChargement.dismiss();
                Toast.makeText(VuePrincipaleClient.this, "Erreur lors de la déconnexion", Toast.LENGTH_LONG).show();
            }
            @Override
            public void quandSucces(JSONObject donnees) throws JSONException {
                dialogueChargement.dismiss();
                Token.definirToken(getApplicationContext(), "erreur");
                Intent intentionNaviguerVueConnexion = new Intent(VuePrincipaleClient.this, VueConnexion.class);
                startActivity(intentionNaviguerVueConnexion);
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
