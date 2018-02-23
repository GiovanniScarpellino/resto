package canadiens.resto.vues;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import canadiens.resto.R;
import canadiens.resto.api.ActionsResultatAPI;
import canadiens.resto.api.RequeteAPI;
import canadiens.resto.api.TypeRequeteAPI;
import canadiens.resto.assistants.Token;

public class FragmentModificationRestaurant extends Fragment {
    private EditText champNom;
    private EditText champDescription;
    private EditText champAdresse;
    private EditText champTelephone;
    private EditText champMail;
    private EditText champMotDePasse;
    private Button actionValiderModifications;

    public FragmentModificationRestaurant() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_modification_restaurant, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        champNom = (EditText) view.findViewById(R.id.champ_nom_restaurant);
        champDescription = (EditText) view.findViewById(R.id.champ_description_restaurant);
        champAdresse = (EditText) view.findViewById(R.id.champ_adresse_restaurant);
        champTelephone = (EditText) view.findViewById(R.id.champ_telephone_restaurant);
        champMail = (EditText) view.findViewById(R.id.champ_mail_restaurant);
        champMotDePasse = (EditText) view.findViewById(R.id.champ_mot_de_passe_restaurant);
        actionValiderModifications = (Button) view.findViewById(R.id.action_valider_modifications_restaurant);

        actionValiderModifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    modifierRestaurant();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void modifierRestaurant() throws JSONException {
        JSONObject parametres = new JSONObject();
        parametres.put("nom", champNom.getText().toString());
        parametres.put("description", champDescription.getText().toString());
        parametres.put("adresse", champAdresse.getText().toString());
        parametres.put("telephone", champTelephone.getText().toString());
        parametres.put("mail", champMail.getText().toString());
        parametres.put("motDePasse", champMotDePasse.getText().toString());
        parametres.put("token", Token.recupererToken());

        RequeteAPI.effectuerRequete(TypeRequeteAPI.MODIFICATION_RESTAURANT, parametres, new ActionsResultatAPI() {
            @Override
            public void quandErreur() {
                Toast.makeText(getContext(),"Impossible de modifier le restaurant", Toast.LENGTH_LONG).show();
            }
            @Override
            public void quandSucces(JSONObject donnees) throws JSONException {
                Toast.makeText(getContext(),"Restaurant modifié !", Toast.LENGTH_LONG).show();
            }
        });
    }
}
