package canadiens.resto.dialogues;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import canadiens.resto.R;

public class ChargementDialogue extends Dialog {

    private TextView texteDialogue;
    private View vue;

    public ChargementDialogue(@NonNull Context context, String texte) {
        super(context);
        vue = View.inflate(getContext(), R.layout.dialogue_chargement, null);
        texteDialogue = vue.findViewById(R.id.text_chargement);
        texteDialogue.setText(texte);
    }

    public ChargementDialogue(@NonNull Context context) {
        this(context, "Chargement...");
    }

    public void setTexte(String texte) {
        texteDialogue.setText(texte);
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setContentView(vue);
    }
}
