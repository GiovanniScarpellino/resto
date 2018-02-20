package canadiens.resto.vues;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import canadiens.resto.R;
import canadiens.resto.api.ActionsResultatAPI;
import canadiens.resto.api.RequeteAPI;
import canadiens.resto.api.TypeRequeteAPI;
import canadiens.resto.modeles.Restaurant;

public class GoogleMapFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback,
        OnMapReadyCallback {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final int CODE_REQUETE = 666;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private final String TAG = "GoogleMap : ";

    private GoogleMap googleMapCourante;

    private FusedLocationProviderClient serviceLocalisationClient;
    private LocationRequest requeteLocalisation;
    private LocationCallback miseAJourLocalisation;

    private RequeteAPI requeteAPI;

    public GoogleMapFragment() {

    }

    /**
     * Méthode pour créer une nouvelle instance de GoogleMap avec des paramètres
     * @param param1
     * @param param2
     * @return
     */
    public static GoogleMapFragment newInstance(String param1, String param2) {
        GoogleMapFragment fragment = new GoogleMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        serviceLocalisationClient = LocationServices.getFusedLocationProviderClient(getContext());
        requeteAPI = new RequeteAPI();
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


        Restaurant restaurant1 = new Restaurant("1", "pizza", "1 rue machin", 47.05, 5.8167, "03", "mail", "mdp", "description1");
        Restaurant restaurant2 = new Restaurant("1", "crepe", "1 rue machin", 47.15, 5.8167, "03", "mail", "mdp", "description2");
        List<Restaurant> listeRestaurant = new ArrayList<>();
        listeRestaurant.add(restaurant1);
        listeRestaurant.add(restaurant2);
        afficherPinRestaurant(listeRestaurant);

        //Lors du clique sur un marqueur, appel les deux fonctions en envoyant le marqueur courant. Et change sa vue en fonction des paramètres du marqueurs
        googleMapCourante.setInfoWindowAdapter(new InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View vueMarqueur = inflater.inflate(R.layout.vue_detail_restaurant_marqueur, null, false);
                TextView champstexteCourant = vueMarqueur.findViewById(R.id.champs_texte_detail_marqueur);
                champstexteCourant.setText(marker.getTitle());
                return vueMarqueur;
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
                    Log.d(TAG, "onLocationResult: " + localisation.getLongitude());
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

    private void afficherPinRestaurant(List<Restaurant> listeRestaurant) {
        for(Restaurant restaurant : listeRestaurant) {
            googleMapCourante.addMarker(new MarkerOptions()
                    .position(new LatLng(restaurant.getLongitude(), restaurant.getLatitude())))
                    .setTitle(restaurant.getNom());
        }
    }

    private void recupererRestaurantProche(double latitude, double longitude) {
        JSONObject jsonDonnees = new JSONObject();
        try {
            jsonDonnees.put("latitude", latitude);
            jsonDonnees.put("longitude", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequeteAPI.effectuerRequete(TypeRequeteAPI.CONNEXION, jsonDonnees, new ActionsResultatAPI() {
            @Override
            public void quandErreur() {
                Toast.makeText(getContext(), "Erreur synchronisation avec l'API, veuillez patienter", Toast.LENGTH_LONG).show();
            }

            @Override
            public void quandSucces(JSONObject donnees) throws JSONException {

            }
        });
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
