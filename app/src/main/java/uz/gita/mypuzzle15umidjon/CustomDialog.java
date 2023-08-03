package uz.gita.mypuzzle15umidjon;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;


public class CustomDialog extends AlertDialog {
    private OnPositiveClickListener onPositiveClickListener;
    TextView title;
    TextView body;


    public CustomDialog(Context context) {
        super(context);

    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        setCancelable(false);
        AppCompatButton buttonYes = findViewById(R.id.buttonYes);
        AppCompatButton buttonNo = findViewById(R.id.buttonNo);
        title = findViewById(R.id.titleText);
        body = findViewById(R.id.bodyText);

        buttonYes.setOnClickListener(v -> {
            if (hasListener()) {
                onPositiveClickListener.onClick();
                dismiss();
            }
        });

        buttonNo.setOnClickListener(v -> dismiss());
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    private boolean hasListener() {
        return onPositiveClickListener != null;
    }

    public CustomDialog setOnPositiveClickListener(OnPositiveClickListener onPositiveClickListener) {
        this.onPositiveClickListener = onPositiveClickListener;
        return this;
    }

    public interface OnPositiveClickListener {
        void onClick();
    }

    public void setData(String title, String body) {
        if (this.title != null && this.body != null) {
            this.title.setText(title);
            this.body.setText(body);
        }
    }
}