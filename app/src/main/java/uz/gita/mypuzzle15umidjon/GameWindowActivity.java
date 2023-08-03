package uz.gita.mypuzzle15umidjon;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;


public class GameWindowActivity extends AppCompatActivity {
    private MediaPlayer click;
    private MediaPlayer backgroundMusic;
    private TextView textSteps;
    private ImageView soundControl;
    private Chronometer chronometer;
    private boolean running;
    private boolean backPlaying;
    private Button[][] buttons;
    private ArrayList<Integer> numbers;
    private Coordinate emptyGrid;
    private int steps;
    public static final String RECORDS = "records";
    public static final String RECORD1 = "record1";
    public static final String RECORD2 = "record2";
    public static final String RECORD3 = "record3";
    public SharedPreferences records;
    public static final String gameStateKey = "GAME_STATE";
    public SharedPreferences gameState;
    private long chronometer1;
    private boolean chronometer1bool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadViews();
        loadData();
        gameState = getSharedPreferences(gameStateKey, MODE_PRIVATE);
        if (gameState.getString("gridState", null) == null) {
            dataToScreen();
        } else {
            restoreGameState();
        }
    }

    private void loadViews() {

        textSteps = findViewById(R.id.text_score);
        soundControl = findViewById(R.id.sound_control);

        click = MediaPlayer.create(this, R.raw.click_sound);
        backgroundMusic = MediaPlayer.create(this, R.raw.background_music);
        backgroundMusic.setLooping(true);

        findViewById(R.id.finish).setOnClickListener(v -> finish());
        findViewById(R.id.restart).setOnClickListener(v -> restart());

        final ViewGroup group = findViewById(R.id.container);
        final int count = group.getChildCount();
        buttons = new Button[4][4];

        for (int i = 0; i < count; i++) {
            final Button button = (Button) group.getChildAt(i);
            final int x = i / 4;
            final int y = i % 4;
            button.setOnClickListener(v -> onItemClick(button, x, y));
            buttons[x][y] = button;
        }
        emptyGrid = new Coordinate(3, 3);
    }

    private void loadData() {
        numbers = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            numbers.add(i);
        }
    }

    private void dataToScreen() {
        steps = 0;
        textSteps.setText("0");
        Collections.shuffle(numbers);

        if (!backPlaying) {
            backgroundMusic.start();
            backPlaying = true;
        }
        soundControl.setOnClickListener(v -> {
            if (backPlaying) {
                backgroundMusic.pause();
                backPlaying = false;
                soundControl.setImageResource(R.drawable.ic_baseline_volume_off_24);
            } else {
                backgroundMusic.start();
                backPlaying = true;
                soundControl.setImageResource(R.drawable.ic_baseline_volume_up_24);
            }
        });

        Log.d("TTT", backPlaying + "");

        chronometer = findViewById(R.id.chronometer);

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
        running = false;


        buttons[emptyGrid.x][emptyGrid.y].setVisibility(View.VISIBLE);
        emptyGrid.x = 3;
        emptyGrid.y = 3;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                final int index = 4 * i + j;
                if (index < 15) {
                    int num = numbers.get(index);
                    buttons[i][j].setText(String.valueOf(num));
                } else {
                    buttons[i][j].setText("");
                    buttons[i][j].setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void onItemClick(Button button, int x, int y) {
        final int dx = Math.abs(emptyGrid.x - x);
        final int dy = Math.abs(emptyGrid.y - y);
        if (dx + dy == 1) {
            if (!running) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                running = true;
                chronometer1bool = true;
            }

            click.start();
            textSteps.setText(String.valueOf(++steps));

            final String text = button.getText().toString();
            button.setText("");
            button.setVisibility(View.INVISIBLE);

            final Button temp = buttons[emptyGrid.x][emptyGrid.y];
            temp.setText(text);
            temp.setVisibility(View.VISIBLE);

            emptyGrid.x = x;
            emptyGrid.y = y;
            if (hasWon()) {
                CustomDialog customDialog = new CustomDialog(this);
                customDialog.getWindow().setBackgroundDrawableResource((android.R.color.transparent));
                customDialog.create();
                customDialog.setData("Winner", "You win the game\nDo you want to go back to main menu?");
                customDialog.setOnPositiveClickListener(this::finish);
                customDialog.show();
                if (running) {
                    chronometer.stop();
                    running = false;
                }
                saveRecord(steps);
                restart();
            }
        }
    }

    private boolean hasWon() {
        if (emptyGrid.x != 3 || emptyGrid.y != 3) return false;
        for (int i = 0; i < 15; i++) {
            final int y = i / 4;
            final int x = i % 4;
            final String text = buttons[y][x].getText().toString();
            if (!text.equals(String.valueOf(i + 1))) return false;
        }
        return true;
    }

    private void saveRecord(int steps) {
        records = getSharedPreferences(RECORDS, MODE_PRIVATE);
        SharedPreferences.Editor editor = records.edit();

        int record1 = Integer.parseInt(records.getString(RECORD1, "0"));
        int record2 = Integer.parseInt(records.getString(RECORD2, "0"));
        int record3 = Integer.parseInt(records.getString(RECORD3, "0"));

        if (steps < record1 || record1 == 0) {
            editor.putString(RECORD3, String.valueOf(record2));
            editor.putString(RECORD2, String.valueOf(record1));
            editor.putString(RECORD1, String.valueOf(steps));
        } else if (steps < record2 || record2 == 0) {
            editor.putString(RECORD3, String.valueOf(record2));
            editor.putString(RECORD2, String.valueOf(steps));
        } else if (steps < record3 || record3 == 0) {
            editor.putString(RECORD3, String.valueOf(steps));
        }
        editor.apply();

    }

    private void restart() {
        dataToScreen();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong("chronometer", chronometer.getBase());
        outState.putBoolean("running", running);
        outState.putInt("steps", steps);

        outState.putBoolean("backPlaying", backPlaying);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                if (buttons[i][j].getVisibility() == View.VISIBLE)
                    sb.append(buttons[i][j].getText().toString()).append("/");
                else
                    sb.append("0").append("/");
            }
        }
        outState.putString("gridState", sb.toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        running = savedInstanceState.getBoolean("running");
        chronometer.setBase(savedInstanceState.getLong("chronometer"));
        if (running) {
            chronometer.start();
        } else
            chronometer.setBase(SystemClock.elapsedRealtime());
        steps = savedInstanceState.getInt("steps");

        backPlaying = savedInstanceState.getBoolean("backPlaying");
        if (backPlaying) {
            backgroundMusic.start();
            soundControl.setImageResource(R.drawable.ic_baseline_volume_up_24);
        } else {
            backgroundMusic.pause();
            soundControl.setImageResource(R.drawable.ic_baseline_volume_off_24);
        }

        String[] gridState = savedInstanceState.getString("gridState").split("/");
        int index = 0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (gridState[index].equals("0")) {
                    emptyGrid.x = i;
                    emptyGrid.y = j;
                    buttons[i][j].setVisibility(View.INVISIBLE);
                } else {
                    buttons[i][j].setText(gridState[index]);
                    buttons[i][j].setVisibility(View.VISIBLE);
                }
                index++;
            }
        }
        textSteps.setText(String.valueOf(steps));
    }

    private void restoreGameState() {
        dataToScreen();

        running = gameState.getBoolean("running", false);

        if (running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - gameState.getLong("chronometer", 0));
            chronometer.start();
        }
        steps = gameState.getInt("steps", 0);
        textSteps.setText(String.valueOf(steps));

        String[] gridState = gameState.getString("gridState", null).split("/");
        int index = 0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (gridState[index].equals("0")) {
                    emptyGrid.x = i;
                    emptyGrid.y = j;
                    buttons[i][j].setVisibility(View.INVISIBLE);
                } else {
                    buttons[i][j].setText(gridState[index]);
                    buttons[i][j].setVisibility(View.VISIBLE);
                }
                index++;
            }
        }
    }

    private void saveGameState() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                if (buttons[i][j].getVisibility() == View.VISIBLE) {
                    sb.append(buttons[i][j].getText().toString()).append("/");
                } else {
                    sb.append("0").append("/");
                }
            }
        }
        gameState = getSharedPreferences(gameStateKey, MODE_PRIVATE);
        SharedPreferences.Editor editor = gameState.edit();

        editor.putInt("steps", steps);
        editor.putBoolean("running", running);
        editor.putLong("chronometer", SystemClock.elapsedRealtime() - chronometer.getBase());
        editor.putString("gridState", sb.toString()).apply();
    }

    @Override
    protected void onResume() {
        if (chronometer1bool) {
            chronometer.setBase(SystemClock.elapsedRealtime() - chronometer1);
            chronometer.start();
        }

        if (backPlaying) {
            backgroundMusic.start();
            backPlaying = true;
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (chronometer1bool) {
            chronometer1 = SystemClock.elapsedRealtime() - chronometer.getBase();
        }

        backgroundMusic.pause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveGameState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        click.release();
        click = null;

        backgroundMusic.release();
        backgroundMusic = null;
    }
}