package com.game.simonsaysgame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    ArrayList<Integer> simonInput = new ArrayList<Integer>(); //Stores the Random Simon Inputs
    ArrayList<Integer> playerInput = new ArrayList<Integer>(); //Stores all Player Inputs

    int max = 4, min = 1; //Random Boundaries
    private int level = 1;
    int playerLocation, playBackLocation;
    int currentScore, highScore;
    public Button startButton, redButton, greenButton, yellowButton, blueButton;
    boolean blinkPause;
    boolean gameStart;
    boolean timerStarted;
    boolean playerTurn;
    AlertDialog.Builder builder;
    MediaPlayer sucesssound, clicksound, startsound, bgmusic;
    TextView highScoreText, currentScoreText, leveltextviews;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        startButton = findViewById(R.id.startButton);
        redButton = findViewById(R.id.redButton);
        yellowButton = findViewById(R.id.yellowButton);
        greenButton = findViewById(R.id.greenButton);
        blueButton = findViewById(R.id.blueButton);
        startButton.setOnClickListener(StartButton);
        redButton.setOnClickListener(RedButton);
        yellowButton.setOnClickListener(YellowButton);
        blueButton.setOnClickListener(BlueButton);
        greenButton.setOnClickListener(GreenButton);
        highScoreText = findViewById(R.id.textView4);
        currentScoreText = findViewById(R.id.textView3);
        leveltextviews = findViewById(R.id.leveltextviews);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        highScoreText.setText("" + sharedPref.getInt("savedHighScore", 0));
        yellowButton.setVisibility(View.INVISIBLE);
        greenButton.setVisibility(View.INVISIBLE);
        blueButton.setVisibility(View.INVISIBLE);
        redButton.setVisibility(View.INVISIBLE);
        builder = new AlertDialog.Builder(this);
        sucesssound = MediaPlayer.create(this, R.raw.coin);
        clicksound = MediaPlayer.create(this, R.raw.click);
        startsound = MediaPlayer.create(this, R.raw.start);
        bgmusic = MediaPlayer.create(this, R.raw.bgmusic);
        TextView leveltextviews = findViewById(R.id.leveltextviews);

        ImageView opensound = findViewById(R.id.opensound);
        ImageView mutesound = findViewById(R.id.mutesound);
        opensound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opensound.setVisibility(View.INVISIBLE);
                mutesound.setVisibility(View.VISIBLE);
                bgmusic.setVolume(0.0f, 0.0f);
            }
        });

        mutesound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mutesound.setVisibility(View.INVISIBLE);
                opensound.setVisibility(View.VISIBLE);
                bgmusic.setVolume(1.0f, 1.0f);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the MediaPlayer object when the activity is destroyed
        bgmusic.release();
        bgmusic = null;
    }

    //Start the Game
    Button.OnClickListener StartButton = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!gameStart) //Start Game and Clear Old Game Data
            {
                level = 1;
                currentScore = 0; // Reset the current score to 0
                playerLocation = 0;
                playBackLocation = 0;
                simonInput.clear();
                playerInput.clear();
                timerStarted = false;
                playerTurn = false;
                blinkPause = false;
                SimonSays();
                bgmusic.start();
                bgmusic.setLooping(true);
                startButton.setVisibility(View.INVISIBLE);
                startsound.start();
                gameStart = true;
            } else {
                bgmusic.start();
            }
        }
    };

    //Red = 1
    Button.OnClickListener RedButton = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (gameStart && playerTurn) {
                playerInput.add(1);
                clicksound.start();
                PlayerTurn();
            }
        }
    };

    //Yellow = 2
    Button.OnClickListener YellowButton = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (gameStart && playerTurn) {
                playerInput.add(2);
                clicksound.start();
                PlayerTurn();
            }
        }
    };

    //Green = 3
    Button.OnClickListener GreenButton = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (gameStart && playerTurn) {
                playerInput.add(3);
                clicksound.start();
                PlayerTurn();
            }
        }
    };

    //Blue = 4
    Button.OnClickListener BlueButton = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (gameStart && playerTurn) {
                playerInput.add(4);
                clicksound.start();
                PlayerTurn();
            }
        }
    };

    public void showNameDialog() {
        final EditText input = new EditText(this);
        input.setCompoundDrawablesWithIntrinsicBounds(R.drawable.user, 0, 0, 0);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Congratulations you did it!")
                .setMessage("You reached a score of 25 above!" + "\n" + "Enter your name :")
                .setView(input)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = input.getText().toString();
                        HighScoreDbHelper dbHelper = new HighScoreDbHelper(MainActivity.this);
                        boolean success = dbHelper.addHighScore(name, currentScore);
                        if (success) {
                            Toast.makeText(MainActivity.this, "Your Score is saved!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to save the score!", Toast.LENGTH_SHORT).show();
                        }

                        // Show the high scores
                        Cursor cursor = dbHelper.getAllHighScores();
                        ArrayList<String> scoreList = new ArrayList<>();
                        while (cursor.moveToNext()) {
                            String score = cursor.getString(
                                    cursor.getColumnIndexOrThrow(HighScoreDbHelper.HighScoreEntry.COLUMN_NAME_SCORE));
                            String name1 = cursor.getString(
                                    cursor.getColumnIndexOrThrow(HighScoreDbHelper.HighScoreEntry.COLUMN_NAME_NAME));
                            scoreList.add(name1 + "'s Scores - " + score);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                MainActivity.this,
                                android.R.layout.simple_list_item_1,
                                scoreList);
                        ListView scoreListView = new ListView(MainActivity.this);
                        scoreListView.setAdapter(adapter);
                        AlertDialog scoreDialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Player's High Scores")
                                .setView(scoreListView)
                                .setPositiveButton("OK", null)
                                .create();
                        scoreDialog.show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    public void PlayerTurn() {
        playBackLocation = 0;

        //If the player pressed the wrong button
        if (playerInput.get(playerLocation) != simonInput.get(playerLocation)) {
            if (currentScore >= 25) {
                // show name dialog if the score is 25 or more
                showNameDialog();
            }

            if (currentScore > highScore) {
                // update high score and save it to shared preferences
                highScore = currentScore;
                highScoreText.setText("" + highScore);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("savedHighScore", highScore);
                editor.commit();
                currentScoreText.setText("0");
            }
            Toast.makeText(MainActivity.this, "Game Over :(", Toast.LENGTH_LONG).show();
            yellowButton.setVisibility(View.INVISIBLE);
            greenButton.setVisibility(View.INVISIBLE);
            blueButton.setVisibility(View.INVISIBLE);
            redButton.setVisibility(View.INVISIBLE);
            sucesssound.start();
            bgmusic.pause();
            startButton.setText("Play Again!");
            startButton.setVisibility(View.VISIBLE);
            currentScoreText.setText("0");
            gameStart = false;
        }

        //If the player pressed the right button
        if (playerInput.get(playerLocation) == simonInput.get(playerLocation)) {
            playerLocation++;
        }

        //Make it Simon's Turn
        if (playerLocation >= simonInput.size()) {
            currentScore++;
            currentScoreText.setText("" + currentScore);

            // check if the score meets the requirement to level up
            if (currentScore == 10 && level == 1) {
                level = 2;
                max = 9;
                leveltextviews.setText("Level 2");
            } else if (currentScore == 20 && level == 2) {
                level = 3;
                max = 16;
                leveltextviews.setText("Level 3");
            } else if (currentScore == 30 && level == 3) {
                level = 4;
                max = 25;
                leveltextviews.setText("Level 4");
            } else if (currentScore == 40 && level == 4) {
                level = 5;
                max = 36;
                leveltextviews.setText("Level 5");
            }

            playerTurn = false;
            playBackLocation = 0;
            blinkPause = false;
            playerInput.clear();
            SimonSays();
        }
    }

    //Simon's Turn
    public void SimonSays() {
        //Show Player the Current Pattern + New Step
        if (!playerTurn) {
            int numberOfItems = (int) Math.pow(level + 1, 2); //Calculate the number of items to display
            simonInput.clear();
            for (int i = 0; i < numberOfItems; i++) {
                simonInput.add((int) (Math.random() * (max - min + 1) + min)); //Create a new Step - Number Between 1 and 4
            }

            timerStarted = false;

            CreateTimer();

            String levelText = "Level " + level;
            leveltextviews.setText(levelText);
        }
    }

    //Button Flashing
    public void CreateTimer()
    {
        //Start the Timer if One Hasn't
        if (timerStarted)
        {
            timer.cancel();
            timerStarted = false;
        }
        else {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask()
            {
                @Override
                public void run()
                {
                    runOnUiThread(new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            yellowButton.setVisibility(View.INVISIBLE);
                            greenButton.setVisibility(View.INVISIBLE);
                            blueButton.setVisibility(View.INVISIBLE);
                            redButton.setVisibility(View.INVISIBLE);

                            //Turn on a button, unless its pausing in between blinks
                            if (!blinkPause)
                            {
                                switch (simonInput.get(playBackLocation))
                                {
                                    case 1: //Red
                                        redButton.setVisibility(View.VISIBLE);
                                        break;

                                    case 2: //Yellow
                                        yellowButton.setVisibility(View.VISIBLE);
                                        break;

                                    case 3: //Green
                                        greenButton.setVisibility(View.VISIBLE);
                                        break;

                                    case 4: //Blue
                                        blueButton.setVisibility(View.VISIBLE);
                                        break;
                                }
                            }

                            //Go to the next step if this is not a blink pause
                            if (!blinkPause)
                            {
                                playBackLocation++;
                            }

                            //If the play back variable is at the end of the list switch the turn to the player
                            if (playBackLocation >= simonInput.size() && blinkPause)
                            {
                                yellowButton.setVisibility(View.VISIBLE);
                                greenButton.setVisibility(View.VISIBLE);
                                blueButton.setVisibility(View.VISIBLE);
                                redButton.setVisibility(View.VISIBLE);
                                playerLocation = 0;
                                playerTurn = true;
                                timer.cancel();
                                return;

                            }

                            blinkPause = !blinkPause;
                        }
                    });
                }
            }, 500, 500);
            timerStarted = true;
        }
    }
}