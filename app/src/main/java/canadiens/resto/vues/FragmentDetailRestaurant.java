package canadiens.resto.vues;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import canadiens.resto.R;
import canadiens.resto.api.ActionsResultatAPI;
import canadiens.resto.api.RequeteAPI;
import canadiens.resto.api.TypeRequeteAPI;
import canadiens.resto.modeles.Restaurant;

public class FragmentDetailRestaurant extends Fragment {

    private final String TAG = "Detail restaurant ";

    private Button boutonReserver;
    private TextView descriptionRestaurant;
    private int idRestaurant;

    public FragmentDetailRestaurant() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_restaurant, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boutonReserver = view.findViewById(R.id.bouton_reserver);
        boutonReserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO : redirection vers le page pour réserver
            }
        });
        descriptionRestaurant = view.findViewById(R.id.detail_restaurant);
        idRestaurant = getArguments().getInt("idRestaurant");
        System.out.println(idRestaurant);
        final JSONObject jsonDonnees = new JSONObject();
        try {
            jsonDonnees.put("idRestaurant", idRestaurant);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequeteAPI.effectuerRequete(TypeRequeteAPI.DETAILS_RESTAURANT, jsonDonnees, new ActionsResultatAPI() {
            @Override
            public void quandErreur() {
                System.out.println(jsonDonnees);
                Log.e(TAG, "erreur lors de la requète vers l'API !");
            }

            @Override
            public void quandSucces(JSONObject donnees) throws JSONException {
                System.out.println(donnees);
                JSONObject tableauRestaurant = donnees.getJSONObject("details");
                Restaurant restaurant = new Restaurant(
                        tableauRestaurant.getString("nom"),
                        tableauRestaurant.getString("adresse"),
                        tableauRestaurant.getString("telephone"),
                        tableauRestaurant.getString("mail"),
                        tableauRestaurant.getString("description")
                );
                String nom = restaurant.getNom();
                String adresse = restaurant.getAdresse();
                String telephone = restaurant.getTelephone();
                String mail = restaurant.getMail();
                String description = restaurant.getDescription();

                descriptionRestaurant.setText(
                        "Nom : " + nom + "\nAdresse : " + adresse + "\nTelephone : " + telephone + "\nMail : " + mail + "\nDescription : " +description
                );
            }
        });
    }
}
