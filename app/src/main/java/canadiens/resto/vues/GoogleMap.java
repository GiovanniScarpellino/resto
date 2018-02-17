package canadiens.resto.vues;

import android.Manifest;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import canadiens.resto.R;
import canadiens.resto.modeles.Restaurant;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GoogleMap.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GoogleMap#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoogleMap extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback,
        OnMapReadyCallback {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private final String TAG = "GoogleMap : ";

    private com.google.android.gms.maps.GoogleMap googleMapCourante;

    private FusedLocationProviderClient serviceLocalisationClient;
    private LocationRequest requeteLocalisation;
    private LocationCallback miseAJourLocalisation;


    public GoogleMap() {

    }

    /**
     * Méthode pour créer une nouvelle instance de GoogleMap avec des paramètres
     * @param param1
     * @param param2
     * @return
     */
    public static GoogleMap newInstance(String param1, String param2) {
        GoogleMap fragment = new GoogleMap();
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
        // Inflate the layout for this fragment
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
     * Méthode appelée lorsque la carte est prête à être utilisée
     * @param googleMap
     */
    @Override
    public void onMapReady(final com.google.android.gms.maps.GoogleMap googleMap) {
        googleMapCourante = googleMap;
        creerRequeteLocalisation();
        verifierLocalisation(googleMap);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);

        // Récupère la dernière localisation connu au démarrage de l'application
        serviceLocalisationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location localisation) {
                        if (localisation != null) {
                            changerLocalisationCamera(localisation, 15);
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
     * Méthode qui défini les paramètres de la localisation
     */
    protected void creerRequeteLocalisation() {
        requeteLocalisation = new LocationRequest();
        requeteLocalisation.setInterval(10000);
        requeteLocalisation.setFastestInterval(5000);
        requeteLocalisation.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Vérifie si la localisation est activée, si non, elle demande à l'utilisateur si il veut l'activer et le re-dirige vers les paramètres de localisation
     * @param googleMap
     */
    public void verifierLocalisation(com.google.android.gms.maps.GoogleMap googleMap) {
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
