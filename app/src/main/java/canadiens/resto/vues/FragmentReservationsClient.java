package canadiens.resto.vues;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import canadiens.resto.R;
import canadiens.resto.api.ActionsResultatAPI;
import canadiens.resto.api.RequeteAPI;
import canadiens.resto.api.TypeRequeteAPI;

/**
 * Created by nicog on 20/02/2018.
 */

public class FragmentReservationsClient extends Fragment {
    private static final String TOKEN = "a3b7620bde9f123ca56d52224a01faa2";

    private ListView listeReservations;
    private Button actionRafraichirReservations;

    public FragmentReservationsClient() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservations_client, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listeReservations = (ListView) view.findViewById(R.id.liste_reservations_client);
        actionRafraichirReservations = (Button) view.findViewById(R.id.action_rafraichir_reservations_client);

        actionRafraichirReservations.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                recupererEtAfficherClassement();
            }
        });

        recupererEtAfficherClassement();
    }

    /**
     * Envoie une requete a l'API pour récupérer les reservations du restaurant connecte
     * Si cela reussi, l'affichage est actualise pour afficher les informations
     * Sinon on affiche un toast indiquant qu'il y a eu une erreur
     */
    public void recupererEtAfficherClassement(){
        try {
            JSONObject parametres = new JSONObject();
            parametres.put("token", TOKEN);

            RequeteAPI.effectuerRequete(TypeRequeteAPI.RESERVATIONS_CLIENT, parametres, new ActionsResultatAPI() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void quandErreur() {
                    Toast.makeText(getContext(), "Impossible de récupérer les réservations", Toast.LENGTH_LONG).show();
                }

                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void quandSucces(JSONObject donnees) throws JSONException {
                    List<HashMap<String, String>> listeReservationsHashMap = new ArrayList<>();

                    JSONArray reservations = donnees.getJSONArray("reservations");
                    for (int i = 0; i < reservations.length(); i++) {
                        JSONObject reservationActuelle = reservations.getJSONObject(i);

                        HashMap<String, String> reservationHashMap = new HashMap<>();

                        reservationHashMap.put("date", reservationActuelle.getString("date"));
                        reservationHashMap.put("heure", reservationActuelle.getString("heure"));
                        reservationHashMap.put("nombrePersonnes", reservationActuelle.getString("nombrePersonnes") + " personnes");
                        reservationHashMap.put("nomRestaurant", reservationActuelle.getString("nomRestaurant"));
                        reservationHashMap.put("adresseRestaurant", reservationActuelle.getString("adresseRestaurant"));

                        listeReservationsHashMap.add(reservationHashMap);
                    }

                    SimpleAdapter adapteur = new SimpleAdapter(
                            getContext(),
                            listeReservationsHashMap,
                            R.layout.item_liste_reservations_client,
                            new String[] {"date", "heure", "nombrePersonnes", "nomRestaurant", "adresseRestaurant"},
                            new int[] {R.id.txt_date, R.id.txt_heure, R.id.txt_nombre_personnes, R.id.txt_nom_restaurant, R.id.txt_adresse_restaurant}
                    );

                    listeReservations.setAdapter(adapteur);
                }
            });
        }
        catch(Exception e){e.printStackTrace();}
    }
}
