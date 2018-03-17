package canadiens.resto.vues;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import canadiens.resto.R;
import canadiens.resto.api.ActionsResultatAPI;
import canadiens.resto.api.RequeteAPI;
import canadiens.resto.api.TypeRequeteAPI;
import canadiens.resto.assistants.Token;
import canadiens.resto.dialogues.DialogueChargement;

public class FragmentModificationPointClient extends Fragment {

    private TextView textPointClient;
    private EditText champPointsClient;

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button boutonValiderModification;

    public FragmentModificationPointClient() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_modification_point_client, container, false);
    }

    @Override
    public void onViewCreated(final View vue, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(vue, savedInstanceState);

        final VuePrincipaleRestaurant vuePrincipaleRestaurant = (VuePrincipaleRestaurant)getActivity();

        final int points = getArguments().getInt("points");
        final String code = getArguments().getString("code");
        textPointClient = vue.findViewById(R.id.text_point_client);
        champPointsClient = vue.findViewById(R.id.champ_modification_point_client);

        radioGroup = vue.findViewById(R.id.radio_groupe_points);
        radioButton = vue.findViewById(radioGroup.getCheckedRadioButtonId());
        boutonValiderModification = vue.findViewById(R.id.bouton_valider_modification_point_client);

        textPointClient.setText("Le client possède " + points + " points. \nEntrez le nombre de points à ajouter/enlever :");

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButton = vue.findViewById(radioGroup.getCheckedRadioButtonId());
            }
        });

        boutonValiderModification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                int nbPoints = Integer.parseInt(champPointsClient.getText().toString());
                if(radioButton.getText().equals("Enlever"))
                    nbPoints -= nbPoints * 2;

                JSONObject jsonDonnees = new JSONObject();

                try {
                    jsonDonnees.put("codeFidelite", code);
                    jsonDonnees.put("token", Token.recupererToken(view.getContext()));
                    jsonDonnees.put("points", nbPoints);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final DialogueChargement dialogueChargement = new DialogueChargement(getContext(), "Modification des points...");
                dialogueChargement.show();

                RequeteAPI.effectuerRequete(TypeRequeteAPI.MODIFICATION_POINTS_FIDELITE, jsonDonnees, new ActionsResultatAPI() {
                    @Override
                    public void quandErreur() {
                        dialogueChargement.dismiss();
                        Toast.makeText(view.getContext(), "Impossible de modifier les points du client", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void quandSucces(JSONObject donnees) throws JSONException {
                        dialogueChargement.dismiss();
                        getFragmentManager().beginTransaction()
                                .replace(R.id.conteneur_principal_restaurant, new FragmentReservationsRestaurant())
                                .commit();
                        vuePrincipaleRestaurant.getNavigationView().setCheckedItem(R.id.nav_reservations_restaurant);
                    }
                });


            }
        });


    }
}
