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
import canadiens.resto.dialogues.DialogueChargement;

public class FragmentModificationClient extends Fragment {
    private static final String TOKEN = "2548fb4657f078ba30e40afd1da2ec13";

    protected EditText champNom;
    protected EditText champPrenom;
    protected EditText champTelephone;
    protected EditText champMail;
    protected EditText champMDP;

    protected Button boutonValider;

    public FragmentModificationClient() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_modification_client, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        champNom = (EditText) view.findViewById(R.id.champ_nom_client_modif);
        champPrenom = (EditText) view.findViewById(R.id.champ_prenom_client_modif);
        champTelephone = (EditText) view.findViewById(R.id.champ_tel_client_modif);
        champMail = (EditText) view.findViewById(R.id.champ_mail_client_modif);
        champMDP = (EditText) view.findViewById(R.id.champ_mdp_client_modif);

        boutonValider = (Button) view.findViewById(R.id.bouton_valider_client_modif);

        boutonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    modifierClient();
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void modifierClient() throws JSONException{
        JSONObject parametres = new JSONObject();
        parametres.put("nom", champNom.getText().toString());
        parametres.put("prenom", champPrenom.getText().toString());
        parametres.put("telephone", champTelephone.getText().toString());
        parametres.put("mail", champMail.getText().toString());
        parametres.put("motDePasse", champMDP.getText().toString());
        parametres.put("token", TOKEN);

        final DialogueChargement dialogueChargement = new DialogueChargement(getContext(), "Modification du profil...");
        dialogueChargement.show();

        RequeteAPI.effectuerRequete(TypeRequeteAPI.MODIFICATION_CLIENT, parametres, new ActionsResultatAPI() {
            @Override
            public void quandErreur() {
                dialogueChargement.dismiss();
                Toast.makeText(getContext(),"Impossible de modifier le profil", Toast.LENGTH_LONG).show();
            }

            @Override
            public void quandSucces(JSONObject donnees) throws JSONException {
                dialogueChargement.dismiss();
                Toast.makeText(getContext(),"Profil modifié !", Toast.LENGTH_LONG).show();
            }
        });
    }
}
