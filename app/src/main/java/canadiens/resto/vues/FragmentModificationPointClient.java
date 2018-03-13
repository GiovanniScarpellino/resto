package canadiens.resto.vues;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import canadiens.resto.R;
import canadiens.resto.api.ActionsResultatAPI;
import canadiens.resto.api.RequeteAPI;
import canadiens.resto.api.TypeRequeteAPI;
import canadiens.resto.assistants.Token;

public class FragmentModificationPointClient extends Fragment {

    private TextView textPointClient;
    private EditText champtPointClient;
    private Button boutonValiderModification;

    public FragmentModificationPointClient() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_modification_point_client, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int points = getArguments().getInt("points");
        final String code = getArguments().getString("code");
        textPointClient = (TextView) view.findViewById(R.id.text_point_client);
        champtPointClient = (EditText) view.findViewById(R.id.champ_modification_point_client);
        boutonValiderModification = (Button) view.findViewById(R.id.bouton_valider_modification_point_client);

        textPointClient.setText("Le client possède " + points + " points. \nEntrez le nombre de points à ajouter :");

        boutonValiderModification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                int nbPoints = Integer.parseInt(champtPointClient.getText().toString());
                JSONObject jsonDonnees = new JSONObject();

                try {
                    jsonDonnees.put("codeFidelite", code);
                    jsonDonnees.put("token", Token.recupererToken(view.getContext()));
                    jsonDonnees.put("points", points);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequeteAPI.effectuerRequete(TypeRequeteAPI.MODIFICATION_POINTS_FIDELITE, jsonDonnees, new ActionsResultatAPI() {
                    @Override
                    public void quandErreur() {
                        Toast.makeText(view.getContext(), "Erreure lors de la modification des points !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void quandSucces(JSONObject donnees) throws JSONException {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.conteneur_principal_restaurant, new FragmentReservationsRestaurant())
                                .commit();
                        VuePrincipaleClient.navigationView.setCheckedItem(R.id.nav_reservations_restaurant);
                    }
                });


            }
        });


    }
}
