package canadiens.resto.vues;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

public class FragmentVerificationCodeClient extends Fragment {

    private static final String TAG = "Verification code ";

    private EditText champCodeClient;
    private Button boutonVerificationCodeClient;


    public FragmentVerificationCodeClient() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verification_code_client, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        champCodeClient = (EditText) view.findViewById(R.id.champ_verification_code_client);
        boutonVerificationCodeClient = (Button) view.findViewById(R.id.bouton_valider_verif_code);

        boutonVerificationCodeClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = champCodeClient.getText().toString();
                if(code.isEmpty()){
                    Toast.makeText(view.getContext(), "Veuillez entrer un code", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject jsonDonnees = new JSONObject();

                    try {
                        String token = Token.recupererToken(view.getContext());
                        jsonDonnees.put("codeFidelite", code);
                        jsonDonnees.put("token", token);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    RequeteAPI.effectuerRequete(TypeRequeteAPI.VERIFICATION_CODE_FIDELITE, jsonDonnees, new ActionsResultatAPI() {
                        @Override
                        public void quandErreur() {
                            Log.e(TAG, "erreur lors de la requète vers l'API !");
                            Toast.makeText(getContext(), "Le code n'est pas valide !", Toast.LENGTH_LONG).show();
                    }

                        @Override
                        public void quandSucces(JSONObject donnees) throws JSONException {
                            Toast.makeText(getContext(), "Le code est valide !", Toast.LENGTH_LONG).show();
                        }
                    });


                }
            }
        });

    }
}
