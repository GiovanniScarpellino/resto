package canadiens.resto.dialogues;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import canadiens.resto.R;

public class ChargementDialogue extends Dialog {

    public ChargementDialogue(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setContentView(R.layout.dialogue_chargement);
    }
}
