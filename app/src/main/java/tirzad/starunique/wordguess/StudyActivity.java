package tirzad.starunique.wordguess;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.security.SecureRandom;

import tirzad.starunique.wordguess.data.SentenceHandling;
import tirzad.starunique.wordguess.data.WordContract;
import tirzad.starunique.wordguess.data.WordDbHelper;

public class StudyActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    WordDbHelper mWordDbHelper = new WordDbHelper(this);
    TextView mTextViewWord;
    TextView mTextViewSentence;
    int mRandomWordIndex;
    private boolean mIsLoaderCreated = false;
    private String mWord;
    private String mSentence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

//        loadANewSentence();

        mTextViewWord = (TextView) findViewById(R.id.textview_word);
        mTextViewSentence = (TextView) findViewById(R.id.textview_sentence);

        Button skipButton = (Button) findViewById(R.id.skip_button);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                loadANewSentence();
//                onCreateLoader(22, null);
                getLoaderManager().restartLoader(22, null, StudyActivity.this);
            }
        });

        getLoaderManager().initLoader(22, null, this);
    }

    private void loadANewSentence() {
        SQLiteDatabase database = mWordDbHelper.getReadableDatabase();

        Cursor cursor = database.query(WordContract.WordEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor.moveToNext()) {
            SecureRandom randomNumbers = new SecureRandom();
            int randomWord = randomNumbers.nextInt(cursor.getCount());
            cursor.moveToPosition(randomWord);

            final TextView textViewWord = (TextView) findViewById(R.id.textview_word);
            textViewWord.setText(String.valueOf(cursor.getString(1)));

            final TextView textViewSentence = (TextView) findViewById(R.id.textview_sentence);
            textViewSentence.setText(SentenceHandling.changeToStars(String.valueOf(cursor.getString(2))));

            cursor.close();

            Button hintButton = (Button) findViewById(R.id.hint_button);
            hintButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textViewSentence.setText(
                            SentenceHandling.giveHintWord(String.valueOf(textViewSentence.getText())));
                }
            });

            final EditText guessedWord = (EditText) findViewById(R.id.guessed_word_edit_text);
            Button checkButton = (Button) findViewById(R.id.check_button);
            checkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textViewSentence.setText(
                            SentenceHandling.checkGuessedWord(String.valueOf(textViewSentence.getText()),
                                    guessedWord.getText().toString().trim()));
                    guessedWord.setText("");
                }
            });
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v("TEST", "Creating loader");
        mIsLoaderCreated = true;
        return new CursorLoader(this,
                WordContract.WordEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        Log.v("TEST", "Load Finished");


        if (cursor.moveToFirst()) {
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor =  sharedPreferences.edit();

            if (mIsLoaderCreated) {
                SecureRandom randomNumbers = new SecureRandom();
                mRandomWordIndex = randomNumbers.nextInt(cursor.getCount());

                editor.putInt("Random_Word", mRandomWordIndex);
                editor.apply();
            }

            cursor.moveToPosition(sharedPreferences.getInt("Random_Word",2));

            mWord = String.valueOf(cursor.getString(1));
            mSentence = SentenceHandling.changeToStars(String.valueOf(cursor.getString(2)));
            mTextViewWord.setText(mWord);
            mTextViewSentence.setText(mSentence);
//            cursor.close();

            Button hintButton = (Button) findViewById(R.id.hint_button);
            hintButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTextViewSentence.setText(
                            SentenceHandling.giveHintWord(String.valueOf(mTextViewSentence.getText())));
                }
            });

            final EditText guessedWord = (EditText) findViewById(R.id.guessed_word_edit_text);
            Button checkButton = (Button) findViewById(R.id.check_button);
            checkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTextViewSentence.setText(
                            SentenceHandling.checkGuessedWord(String.valueOf(mTextViewSentence.getText()),
                                    guessedWord.getText().toString().trim()));
                    guessedWord.setText("");
                }
            });
            mIsLoaderCreated = false;

        }
        mTextViewWord.setText(mWord);
        mTextViewSentence.setText(mSentence);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
