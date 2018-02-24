package canadiens.resto.vues;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import canadiens.resto.R;
import canadiens.resto.api.ActionsResultatAPI;
import canadiens.resto.api.RequeteAPI;
import canadiens.resto.api.TypeRequeteAPI;
import canadiens.resto.modeles.Restaurant;

public class FragmentGoogleMap extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback,
        OnMapReadyCallback {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final int CODE_REQUETE = 666;

    private OnFragmentInteractionListener mListener;

    private final String TAG = "GoogleMap : ";

    private GoogleMap googleMapCourante;

    private FusedLocationProviderClient serviceLocalisationClient;
    private LocationRequest requeteLocalisation;
    private LocationCallback miseAJourLocalisation;

    private RequeteAPI requeteAPI;

    private int rayonRestaurantProche = 15;

    private SharedPreferences preferencePartagees;

    private List<Pair<Restaurant, Marker>> restaurantsMarqueursAffiches; //ID du restaurant, Instance du marqueur

    public FragmentGoogleMap() {
        restaurantsMarqueursAffiches = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceLocalisationClient = LocationServices.getFusedLocationProviderClient(getContext());
        requeteAPI = new RequeteAPI();
        preferencePartagees = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    /**
     * Méthode appelé lors de la création de la vue
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate le layout pour ce fragment
        SupportMapFragment mapCourante;
        View vue = inflater.inflate(R.layout.fragment_google_map, container, false);
        mapCourante = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapCourante.getMapAsync(this);
        return vue;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " doit implémenter OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Méthode qui défini les paramètres de la localisation
     */
    protected void creerRequeteLocalisation() {
        requeteLocalisation = new LocationRequest();
        requeteLocalisation.setInterval(10000);
        requeteLocalisation.setFastestInterval(5000);
        requeteLocalisation.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Méthode appelée lorsque la carte est prête à être utilisée
     * @param googleMap
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMapCourante = googleMap;
        creerRequeteLocalisation();
        verifierLocalisationActivee(googleMap);
        
        //Demande la permission à l'utilisateur pour utiliser la localisation
        String permissions[] = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ActivityCompat.checkSelfPermission(getContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED) {
            activerToutLesServicesDeLocalisation();
        } else {
            requestPermissions(permissions, CODE_REQUETE);
        }

        //Lors du clique sur un marqueur, appel les deux fonctions en envoyant le marqueur courant. Et change sa vue en fonction des paramètres du marqueurs
        googleMapCourante.setInfoWindowAdapter(new InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                String[] tableauInformation = marker.getSnippet().split("/");

                LayoutInflater inflater = LayoutInflater.from(getContext());
                View vueMarqueur = inflater.inflate(R.layout.vue_detail_restaurant_marqueur, null, false);
                TextView champstexteCourant = vueMarqueur.findViewById(R.id.champs_texte_detail_marqueur);
                champstexteCourant.setText(marker.getTitle());
                TextView texteDescriptionDetailMarqueur = vueMarqueur.findViewById(R.id.champs_texte_description_detail_marqueur);
                if(tableauInformation[1].length() > 40) {
                    tableauInformation[1] = tableauInformation[1].substring(0, 40);
                    tableauInformation[1] += "...";
                }
                texteDescriptionDetailMarqueur.setText(tableauInformation[1]);
                return vueMarqueur;
            }
        });
        
        googleMapCourante.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                int idRestaurantSelectionné = Integer.parseInt(marker.getSnippet().split("/")[0]);
                FragmentDetailRestaurant fragmentDetailRestaurant  = new FragmentDetailRestaurant();
                Bundle argumentAPasser = new Bundle();
                argumentAPasser.putInt("idRestaurant", idRestaurantSelectionné);
                fragmentDetailRestaurant.setArguments(argumentAPasser);
                getFragmentManager().beginTransaction()
                        .replace(R.id.conteneur_principal_client, fragmentDetailRestaurant)
                        .commit();
            }
        });
        
        
    }

    /**
     * Méthode appelé après le résultat de la demande de permission
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CODE_REQUETE) {
            if (permissions.length == 2 &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    permissions[1].equals(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                activerToutLesServicesDeLocalisation();
            } else {
                Toast.makeText(getContext(), "L'application ne fonctionnera pas si vous n'acceptez pas les demandes de permissions", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Active tout les services de localisation nécéssaire pour la GoogleMap
     */
    @SuppressLint("MissingPermission")
    private void activerToutLesServicesDeLocalisation() {
        googleMapCourante.setMyLocationEnabled(true);

        //Ecouteur pour surcharger la méthode de clique du calque de localisation
        googleMapCourante.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public boolean onMyLocationButtonClick() {
                serviceLocalisationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location localisation) {
                        if(localisation != null) {
                            changerLocalisationCamera(localisation, 17);
                        }
                    }
                });
                return true;
            }
        });

        // Récupère la dernière localisation connu au démarrage de l'application
        serviceLocalisationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location localisation) {
                        if (localisation != null) {
                            changerLocalisationCamera(localisation, 17);
                            recupererRestaurantProche(localisation.getLatitude(), localisation.getLongitude());
                        }
                    }
                });

        //A chaque nouvelle mise à jour de la localisation, le code passe dans cette méthode
        miseAJourLocalisation = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult resultatLocalisation) {
                for (Location localisation : resultatLocalisation.getLocations()) {
                    recupererRestaurantProche(localisation.getLatitude(), localisation.getLongitude());
                }
            }
        };

        //Active la récupération régulière des données de localisation
        serviceLocalisationClient.requestLocationUpdates(requeteLocalisation, miseAJourLocalisation, null);
    }

    /**
     * Vérifie si la localisation est activée, si non, elle demande à l'utilisateur si il veut l'activer et le re-dirige vers les paramètres de localisation
     * @param googleMap
     */
    public void verifierLocalisationActivee(GoogleMap googleMap) {
        LocationManager gestionLocation = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gpsActive = false;
        boolean reseauActive = false;

        try {
            gpsActive = gestionLocation.isProviderEnabled(LocationManager.GPS_PROVIDER);
            reseauActive = gestionLocation.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception exception1) {
            Log.e(TAG, exception1.getMessage());
        }

        if(!gpsActive && !reseauActive) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setMessage(R.string.indication_fenetre_location);
            dialog.setPositiveButton(R.string.bouton_oui, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(R.string.bouton_non, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    getActivity().finish();
                }
            });
            dialog.show();
        }
    }


    /**
     * Méthode qui déplace la caméra vers une nouvelle localisation
     * @param nouvelleLocalisation
     */
    private void changerLocalisationCamera(Location nouvelleLocalisation, int niveauZoom) {
        CameraUpdate pointACentrer = CameraUpdateFactory.newLatLng(new LatLng(nouvelleLocalisation.getLatitude(), nouvelleLocalisation.getLongitude()));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(niveauZoom);
        googleMapCourante.moveCamera(pointACentrer);
        googleMapCourante.animateCamera(zoom);
    }

    /**
     * Affiche tout les restaurants contenu dans la liste sur la Google Map
     * @param listeRestaurant
     */
    private void ajouterPinRestaurant(List<Restaurant> listeRestaurant) {
        for(Restaurant restaurant : listeRestaurant) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(restaurant.getLatitude(), restaurant.getLongitude()));
            markerOptions.title(restaurant.getNom());
            markerOptions.snippet(restaurant.getIdRestaurant() + "/" + restaurant.getDescription());
            //On ajoute ce marqueur dans la liste des marqueurs affiches et sur la Google Map
            restaurantsMarqueursAffiches.add(new Pair<Restaurant, Marker>(
                    restaurant,
                    googleMapCourante.addMarker(markerOptions)
            ));
        }
    }

    /**
     * Récupère tout les restaurants depuis l'API en fonction de la position
     * @param latitude
     * @param longitude
     */
    private void recupererRestaurantProche(double latitude, double longitude) {
        JSONObject jsonDonnees = new JSONObject();
        try {
            rayonRestaurantProche = Integer.valueOf(preferencePartagees.getString("champs_texte_rayon","10"));
        } catch (Exception erreurConversionEnInt) {
            rayonRestaurantProche = 10;
        }
        if(rayonRestaurantProche <= 0)
            rayonRestaurantProche = 1;
        try {
            jsonDonnees.put("latitude", latitude);
            jsonDonnees.put("longitude", longitude);
            jsonDonnees.put("rayon", rayonRestaurantProche);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "recupererRestaurantProche: " + rayonRestaurantProche);
        RequeteAPI.effectuerRequete(TypeRequeteAPI.RESTAURANTS_PROCHES, jsonDonnees, new ActionsResultatAPI() {
            @Override
            public void quandErreur() {
                Log.e(TAG, "quandErreur: Erreur lors de la requête vers l'API");
            }

            @Override
            public void quandSucces(JSONObject donnees) throws JSONException {
                //Tableaux des restaurants
                List<Restaurant> nouveauxRestaurants = new ArrayList<>();
                List<Restaurant> autresRestaurants = new ArrayList<>();
                //On lit le tableau JSON des restaurants recus
                JSONArray tableauRestaurant = donnees.getJSONArray("restaurants");
                for(int i = 0; i < tableauRestaurant.length(); i++) {
                    JSONObject restaurantCourant = tableauRestaurant.getJSONObject(i);
                    String idRestaurant = restaurantCourant.getString("idRestaurant");
                    String nom = restaurantCourant.getString("nom");
                    double longitude = restaurantCourant.getDouble("longitude");
                    double latitude = restaurantCourant.getDouble("latitude");
                    String description = restaurantCourant.getString("description");
                    Restaurant restaurant = new Restaurant(idRestaurant, nom, longitude, latitude, description);
                    //On regarde si ce restaurant existe deja sur la carte
                    //Sinon on l'ajoute dans la liste des autres restaurants et on regarde si il a bougé depuis sa dernière position recue
                    if(!restaurantDejaSurGoogleMap(restaurant)){
                        nouveauxRestaurants.add(restaurant);
                    }
                    else{
                        //On regarde si le restaurant a bouge et si c'est le cas on déplace le marqueur
                        LatLng nouvelleLatitudeLongitude = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
                        Marker marqueurRestaurantActuel = recupererMarqueurAvecIdRestaurant(restaurant.getIdRestaurant());
                        if(!nouvelleLatitudeLongitude.equals(marqueurRestaurantActuel.getPosition())){
                            marqueurRestaurantActuel.setPosition(nouvelleLatitudeLongitude);
                        }
                        autresRestaurants.add(restaurant);
                    }
                }
                //On supprime les restaurants qui sont en dehors du rayon
                int i = 0;
                while(i < restaurantsMarqueursAffiches.size()){
                    //On récupère le restaurantMarqueur actuellement teste
                    Pair<Restaurant, Marker> restaurantMarqueurActuel = restaurantsMarqueursAffiches.get(i);
                    //On regarde si ce restaurantMarqueur est encore dans le rayon
                    boolean restaurantToujoursDansRayon = false;
                    for(Restaurant restaurant : autresRestaurants){
                        if(restaurant.getIdRestaurant().equals(restaurantMarqueurActuel.first.getIdRestaurant())){
                            restaurantToujoursDansRayon = true;
                            break;
                        }
                    }
                    //Si le restaurant est toujours dans le rayon on continue de parcourir la boucle
                    //Sinon on le supprime de la google map et de la liste
                    if(restaurantToujoursDansRayon){
                        i++;
                    }
                    else{
                        restaurantMarqueurActuel.second.remove(); //On remove le marqueur de la google map
                        restaurantsMarqueursAffiches.remove(i); //On supprime le marqueur de la liste
                    }
                }
                //On affiche les nouveaux restaurants
                ajouterPinRestaurant(nouveauxRestaurants);
            }
        });
    }

    private boolean restaurantDejaSurGoogleMap(Restaurant restaurant){
        return recupererMarqueurAvecIdRestaurant(restaurant.getIdRestaurant()) != null;
    }

    private Marker recupererMarqueurAvecIdRestaurant(String idRestaurant){
        for(Pair<Restaurant, Marker> restaurantMarqueur : restaurantsMarqueursAffiches){
            if(restaurantMarqueur.first.getIdRestaurant().equals(idRestaurant)){
                return restaurantMarqueur.second;
            }
        }
        return null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
