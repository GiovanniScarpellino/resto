package canadiens.resto.vues;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import canadiens.resto.R;
import canadiens.resto.modeles.Restaurant;

public class FragmentDetailRestaurant extends Fragment {

    TextView descriptionRestaurant;
    Restaurant restaurant;

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

        // TODO : Résupérer l'instance du restaurant

        descriptionRestaurant = view.findViewById(R.id.detail_restaurant);
        descriptionRestaurant.setText(restaurant.getDescription());
    }
}
