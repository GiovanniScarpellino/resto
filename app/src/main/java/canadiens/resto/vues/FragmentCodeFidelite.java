package canadiens.resto.vues;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import canadiens.resto.R;
import canadiens.resto.api.ActionsResultatAPI;
import canadiens.resto.api.RequeteAPI;
import canadiens.resto.api.TypeRequeteAPI;
import canadiens.resto.assistants.Token;
import canadiens.resto.dialogues.ChargementDialogue;

import static android.content.ContentValues.TAG;

public class FragmentCodeFidelite extends Fragment {
    private String token;
    private String codeFidelite;
    private ImageView imageQrCode;
    private ChargementDialogue chargementDialogue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chargementDialogue = new ChargementDialogue(getContext());
        chargementDialogue.setTexte("Obtention du code...");
        token = Token.recupererToken(getContext());
        JSONObject parametresAEnvoyer = new JSONObject();
        try {
            parametresAEnvoyer.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        chargementDialogue.show();
        RequeteAPI.effectuerRequete(TypeRequeteAPI.RECUPERATION_CODE_FIDELITE, parametresAEnvoyer, new ActionsResultatAPI() {
            @Override
            public void quandErreur() {
                Toast.makeText(getContext(), "Erreur lors de la récupération du code fidélité", Toast.LENGTH_LONG).show();
            }

            @Override
            public void quandSucces(JSONObject donnees) throws JSONException {
                chargementDialogue.setTexte("Génération du QRCode...");
                codeFidelite = donnees.getString("code");
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    System.out.println(imageQrCode.getWidth() + " " + imageQrCode.getHeight());
                    BitMatrix bitMatrix = multiFormatWriter.encode(codeFidelite, BarcodeFormat.QR_CODE, imageQrCode.getWidth(), imageQrCode.getHeight());
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    imageQrCode.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                chargementDialogue.dismiss();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vue = inflater.inflate(R.layout.fragment_code_fidelite, container, false);
        imageQrCode = vue.findViewById(R.id.qrcode_img);
        return vue;
    }
}
