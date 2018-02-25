package canadiens.resto.vues;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import canadiens.resto.R;
import canadiens.resto.api.ActionsResultatAPI;
import canadiens.resto.api.RequeteAPI;
import canadiens.resto.api.TypeRequeteAPI;
import canadiens.resto.assistants.Token;

public class FragmentDetailRestaurant extends Fragment implements
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private final String TAG = "Detail restaurant ";

    private Button boutonReserver;

    private TextView nomRestaurant;
    private TextView adresse;
    private TextView telephone;
    private TextView mail;
    private TextView description;

    private EditText champsHeure;
    private EditText champsDate;
    private EditText champsNombresPersonnes;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

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

        idRestaurant = getArguments().getInt("idRestaurant");

        nomRestaurant = view.findViewById(R.id.texte_nom_restaurant);
        adresse = view.findViewById(R.id.texte_adresse_restaurant);
        telephone = view.findViewById(R.id.texte_telephone_restaurant);
        mail = view.findViewById(R.id.texte_mail_restaurant);
        description = view.findViewById(R.id.texte_description_restaurant);

        datePickerDialog = new DatePickerDialog(getContext(), this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        timePickerDialog = new TimePickerDialog(getContext(), this, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);

        champsHeure = view.findViewById(R.id.heure_reservation);
        champsDate = view.findViewById(R.id.date_reservation);
        champsNombresPersonnes = view.findViewById(R.id.nombre_personne_reservation);

        champsHeure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
            }
        });

        champsDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        boutonReserver = view.findViewById(R.id.bouton_reserver);
        boutonReserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* --------------- Réservation dans un restaurant ---------------*/
                final JSONObject jsonDonnees = new JSONObject();
                try {
                    String jour = champsDate.getText().toString().split("/")[0];
                    String mois = champsDate.getText().toString().split("/")[1];
                    String annee = champsDate.getText().toString().split("/")[2];

                    jsonDonnees.put("date", annee+"-"+mois+"-"+jour);
                    jsonDonnees.put("heure", champsHeure.getText().toString());
                    jsonDonnees.put("nombrePersonnes", champsNombresPersonnes.getText().toString());
                    jsonDonnees.put("idRestaurant", idRestaurant);
                    jsonDonnees.put("token", Token.recupererToken(getContext()));

                    System.out.println(jsonDonnees);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequeteAPI.effectuerRequete(TypeRequeteAPI.RESERVATION, jsonDonnees, new ActionsResultatAPI() {
                    @Override
                    public void quandErreur() {
                        Log.e(TAG, "erreur lors de la requète vers l'API !");
                        Toast.makeText(getContext(), "Il y a eu un problème avec votre réservation...", Toast.LENGTH_LONG).show();
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void quandSucces(JSONObject donnees) throws JSONException {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.conteneur_principal_client, new FragmentReservationsClient())
                                .commit();
                        VuePrincipaleClient.navigationView.setCheckedItem(R.id.nav_reservations_client);
                    }
                });
                 /* ----------------------------------------------------------------------*/
            }
        });

        /* --------------- Récupération du détails du restaurant ---------------*/
        final JSONObject jsonDonnees = new JSONObject();
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

            @SuppressLint("SetTextI18n")
            @Override
            public void quandSucces(JSONObject donnees) throws JSONException {
                JSONObject restaurant = donnees.getJSONObject("details");

                nomRestaurant.setText(restaurant.getString("nom"));
                adresse.setText("Adresse : " + restaurant.getString("adresse"));
                telephone.setText("Téléphone : " + restaurant.getString("telephone"));
                mail.setText("Mail : " + restaurant.getString("mail"));
                description.setText(restaurant.getString("description"));
            }
        });
        /* ----------------------------------------------------------------------*/
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onDateSet(DatePicker datePicker, int annee, int mois, int jour) {
        champsDate.setText(String.format("%02d", jour) + "/" + String.format("%02d", mois + 1) + "/" + annee);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onTimeSet(TimePicker timePicker, int heure, int minute) {
        champsHeure.setText(String.format("%02d", heure) + ":" + String.format("%02d", minute));
    }
}
