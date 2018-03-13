package canadiens.resto.vues;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import canadiens.resto.R;
import canadiens.resto.api.ActionsResultatAPI;
import canadiens.resto.api.RequeteAPI;
import canadiens.resto.api.TypeRequeteAPI;
import canadiens.resto.assistants.Token;

import static android.content.ContentValues.TAG;


public class FragmentCodeFidelite extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String token;
    private String codeFidelite;

    public FragmentCodeFidelite() {
    }

    public static FragmentCodeFidelite newInstance(String param1, String param2) {
        FragmentCodeFidelite fragment = new FragmentCodeFidelite();
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
        token = Token.recupererToken(getContext());
        JSONObject parametresAEnvoyer = new JSONObject();
        try {
            parametresAEnvoyer.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequeteAPI.effectuerRequete(TypeRequeteAPI.RECUPERATION_CODE_FIDELITE, parametresAEnvoyer, new ActionsResultatAPI() {
            @Override
            public void quandErreur() {
                Log.d(TAG, "quandErreur: Erreur lors de la récupération du code fidélité");
            }

            @Override
            public void quandSucces(JSONObject donnees) throws JSONException {
                codeFidelite = donnees.getString("code");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_code_fidelite, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
