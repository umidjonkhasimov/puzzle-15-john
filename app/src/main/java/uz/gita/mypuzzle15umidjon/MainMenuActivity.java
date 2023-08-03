package uz.gita.mypuzzle15umidjon;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        findViewById(R.id.startGame).setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, GameWindowActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.records).setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, RecordsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.about).setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.exit).setOnClickListener(v -> {
            CustomDialog customDialog = new CustomDialog(this);
            customDialog.getWindow().setBackgroundDrawableResource((android.R.color.transparent));
            customDialog.create();
            customDialog.setOnPositiveClickListener(this::finish);
            customDialog.setData("Are you sure?", "Do you really want to exit?");
            customDialog.show();
        });
    }

    @Override
    public void onBackPressed() {
        CustomDialog customDialog = new CustomDialog(this);
        customDialog.getWindow().setBackgroundDrawableResource((android.R.color.transparent));
        customDialog.create();
        customDialog.setOnPositiveClickListener(this::finish);
        customDialog.setData("Are you sure?", "Do you really want to exit?");
        customDialog.show();
    }
}