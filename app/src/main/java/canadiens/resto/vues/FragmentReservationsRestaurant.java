package canadiens.resto.vues;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import canadiens.resto.assistants.Token;
import canadiens.resto.dialogues.DialogueChargement;

public class FragmentReservationsRestaurant extends Fragment {
    private ListView listeReservations;
    private Button actionRafraichirReservations;

    public FragmentReservationsRestaurant() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservations_restaurant, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listeReservations = (ListView) view.findViewById(R.id.liste_reservations_restaurant);
        actionRafraichirReservations = (Button) view.findViewById(R.id.action_rafraichir_reservations_restaurant);

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
            parametres.put("token", Token.recupererToken(getContext()));

            final DialogueChargement dialogueChargement = new DialogueChargement(getContext(), "Chargement des réservations...");
            dialogueChargement.show();

            RequeteAPI.effectuerRequete(TypeRequeteAPI.RESERVATIONS_RESTAURANT, parametres, new ActionsResultatAPI() {
                @Override
                public void quandErreur() {
                    dialogueChargement.dismiss();
                    Toast.makeText(getContext(), "Impossible de récupérer les réservations", Toast.LENGTH_LONG).show();
                }

                @Override
                public void quandSucces(JSONObject donnees) throws JSONException {
                    List<HashMap<String, String>> listeReservationsHashMap = new ArrayList<>();

                    JSONArray reservations = donnees.getJSONArray("reservations");
                    for (int i = 0; i < reservations.length(); i++) {
                        JSONObject reservationActuelle = reservations.getJSONObject(i);

                        HashMap<String, String> reservationHashMap = new HashMap<>();

                        reservationHashMap.put("heure", reservationActuelle.getString("heure"));
                        reservationHashMap.put("nombrePersonnes", reservationActuelle.getString("nombrePersonnes") + " personnes");
                        reservationHashMap.put("nomClient", reservationActuelle.getString("nomClient"));
                        reservationHashMap.put("telephoneClient", reservationActuelle.getString("telephoneClient"));

                        listeReservationsHashMap.add(reservationHashMap);
                    }

                    SimpleAdapter adapteur = new SimpleAdapter(
                            getContext(),
                            listeReservationsHashMap,
                            R.layout.item_liste_reservations_restaurant,
                            new String[] {"heure", "nombrePersonnes", "nomClient", "telephoneClient"},
                            new int[] {R.id.txt_heure, R.id.txt_nombre_personnes, R.id.txt_nom_client, R.id.txt_telephone_client}
                    );

                    listeReservations.setAdapter(adapteur);

                    //On cache le dialogue de chargement à la fin
                    dialogueChargement.dismiss();
                }
            });
        }
        catch(Exception e){e.printStackTrace();}
    }
}
