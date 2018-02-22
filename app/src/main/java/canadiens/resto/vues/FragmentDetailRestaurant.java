package canadiens.resto.vues;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import canadiens.resto.R;
import canadiens.resto.api.ActionsResultatAPI;
import canadiens.resto.api.RequeteAPI;
import canadiens.resto.api.TypeRequeteAPI;
import canadiens.resto.modeles.Restaurant;

public class FragmentDetailRestaurant extends Fragment {

    private final String TAG = "Detail restaurant : ";

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

        descriptionRestaurant = view.findViewById(R.id.detail_restaurant);
        idRestaurant = getArguments().getInt("idRestaurant");
        JSONObject jsonDonnees = new JSONObject();
        try {
            jsonDonnees.put("idRestaurant", idRestaurant);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequeteAPI.effectuerRequete(TypeRequeteAPI.DETAILS_RESTAURANT, jsonDonnees, new ActionsResultatAPI() {
            @Override
            public void quandErreur() {
                Log.e(TAG, "erreur lors de la requète vers l'API !");
            }

            @Override
            public void quandSucces(JSONObject donnees) throws JSONException {
                JSONArray tableauRestaurant = donnees.getJSONArray("restaurants");
                JSONObject restaurantJSON = tableauRestaurant.getJSONObject(0);
                Restaurant restaurant = new Restaurant(
                        restaurantJSON.getString("nom"),
                        restaurantJSON.getString("adresse"),
                        restaurantJSON.getString("telephone"),
                        restaurantJSON.getString("mail"),
                        restaurantJSON.getString("description")
                );
                String nom = restaurant.getNom();
                String adresse = restaurant.getAdresse();
                String telephone = restaurant.getTelephone();
                String mail = restaurant.getMail();
                String description = restaurant.getDescription();

                descriptionRestaurant.setText(
                        "Nom :" + nom + "\n Adresse : " + adresse + "\n Telephone : " + telephone + "\n Mail : " + mail + "\n Description : " +description
                );
            }
        });
    }
}
