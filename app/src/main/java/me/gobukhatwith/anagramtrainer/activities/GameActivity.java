package me.gobukhatwith.anagramtrainer.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import me.gobukhatwith.anagramtrainer.R;
import me.gobukhatwith.anagramtrainer.anagram.Word;

public class GameActivity extends AppCompatActivity {
    private ArrayList<Word> words;
    private Word currentWord;

    private Button checkAnswerButton;
    private Button hintButton;
    private Button newAnagramButton;

    private Integer fromLevel;
    private Integer toLevel;
    private Integer shuffleIntensity;

    private EditText answerEditText;
    private TextView anagramTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setLevels();

        answerEditText = findViewById(R.id.game_answer_edittext);
        anagramTextView = findViewById(R.id.game_anagram_textview);

        checkAnswerButton = findViewById(R.id.game_check_answer_button);
        hintButton = findViewById(R.id.game_hint_button);
        newAnagramButton = findViewById(R.id.game_new_anagram_button);

        InputStream wordsStream = getBaseContext().getResources().openRawResource(R.raw.words);
        words = new ArrayList<>();

        Scanner scanner = new Scanner(wordsStream).useDelimiter("\\n");

        int i = 1;
        while (scanner.hasNext()) {
            Word word = new Word(scanner.nextLine(), i, shuffleIntensity);
            if (word.getDifficulty() >= (double) fromLevel &&  word.getDifficulty() <= (double) toLevel) {
                words.add(word);
            }
            i++;
        }

        Collections.sort(words, new Comparator<Word>() {
            @Override
            public int compare(Word o1, Word o2) {
                return (int) (o1.getDifficulty() * 1000 - o2.getDifficulty() * 1000);
            }
        });
//
//        Toast.makeText(this, String.valueOf(words.get(words.size() - 1).getDifficulty()) + "\n" +
//                String.valueOf(words.get(words.size() - 2).getDifficulty()) + "\n" +
//                String.valueOf(words.get(0).getDifficulty())
//                , Toast.LENGTH_SHORT).show();

        getNewAnagram();

        checkAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answer = answerEditText.getText().toString();
                if (currentWord.isCorrect(answer)) {
                    Toast.makeText(GameActivity.this, "YISSS, YOU ARE RIGHT", Toast.LENGTH_SHORT).show();
                    answerEditText.setText("");
                    getNewAnagram();
                } else {
                    Toast.makeText(GameActivity.this, "Nope, try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        newAnagramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GameActivity.this, "Loooh " + currentWord.getWord(), Toast.LENGTH_SHORT).show();
                getNewAnagram();
            }
        });

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerEditText.setText(currentWord.hint());
                answerEditText.setSelection(answerEditText.getText().length());
            }
        });
    }

    private void getNewAnagram() {
        SecureRandom random = new SecureRandom();
        if (words.size() == 0) {
            Toast.makeText(this, "Ебать ты долбаеб братишка, настройки поправь", Toast.LENGTH_SHORT).show();
            return;
        }
        Integer position = Math.abs(random.nextInt()) % words.size();
        currentWord = words.get(position);
        anagramTextView.setText(words.get(position).getAnagram());
    }

    private void setLevels() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String levelFromPref = prefs.getString("level_from", null);
        fromLevel = levelFromPref == null ? 3 : Integer.valueOf(levelFromPref);

        String levelToPref = prefs.getString("level_to", null);
        toLevel = levelToPref == null ? 10 : Integer.valueOf(levelToPref);

        String shuffleIntensityPref = prefs.getString("shuffle_intensity", null);
        shuffleIntensity = shuffleIntensityPref == null ? 50 : Integer.valueOf(shuffleIntensityPref);
    }
}
