package uz.gita.mypuzzle15umidjon;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class RecordsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        TextView firstPlace = findViewById(R.id.firstPlace);
        TextView secondPlace = findViewById(R.id.secondPlace);
        TextView thirdPlace = findViewById(R.id.thirdPlace);

        SharedPreferences records = getSharedPreferences("records", MODE_PRIVATE);

        if (records != null) {
            firstPlace.setText(records.getString("record1", "0"));
            secondPlace.setText(records.getString("record2", "0"));
            thirdPlace.setText(records.getString("record3", "0"));
        }
    }
}