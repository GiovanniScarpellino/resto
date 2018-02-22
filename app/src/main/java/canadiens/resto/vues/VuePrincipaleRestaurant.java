package canadiens.resto.vues;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import canadiens.resto.R;

public class VuePrincipaleRestaurant extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private NavigationView navigationView;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vue_principale_restaurant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        token = getIntent().getStringExtra("token");
        System.out.println(token);

        changerDeFragment(TypeFragment.ReservationsRestaurant);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item_menu_parametres_restaurant, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
            case R.id.nav_reservations_restaurant:
                changerDeFragment(TypeFragment.ReservationsRestaurant);
                break;
            case R.id.nav_modification_profile_restaurant:
                changerDeFragment(TypeFragment.ModificationRestaurant);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changerDeFragment(TypeFragment fragment) {
        switch (fragment) {
            case ReservationsRestaurant:
                FragmentReservationsRestaurant fragmentReservationsRestaurant = new FragmentReservationsRestaurant();
                Bundle argumentAPasser = new Bundle();
                argumentAPasser.putString("token", token);
                fragmentReservationsRestaurant.setArguments(argumentAPasser);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.conteneur_principal_restaurant, fragmentReservationsRestaurant)
                        .commit();
                navigationView.setCheckedItem(R.id.nav_reservations_restaurant);
                break;
            case ModificationRestaurant:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.conteneur_principal_restaurant, new FragmentModificationRestaurant())
                        .commit();
                navigationView.setCheckedItem(R.id.nav_modification_profile_restaurant);
                break;
        }
    }
}