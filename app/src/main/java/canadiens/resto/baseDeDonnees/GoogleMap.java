package canadiens.resto.baseDeDonnees;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import canadiens.resto.R;


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
    private SupportMapFragment mapCourante;

    private final String TAG = "ERREUR : ";

    public GoogleMap() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GoogleMap.
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
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Méthode appelé lorsque là carte est prête
     * @param googleMap
     */
    @Override
    public void onMapReady(com.google.android.gms.maps.GoogleMap googleMap) {
        verifierLocalisation(googleMap);
    }

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
            // notify user
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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
