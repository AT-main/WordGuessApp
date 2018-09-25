package tirzad.starunique.wordguess;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import tirzad.starunique.wordguess.data.WordContract;

public class AddEditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mWordEditText;
    private EditText mSentenceEditText;
    private String mSelection;
    private String[] mSelectionArgs;
    private Uri mCurrentWordUri;

    private boolean mWordHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mWordHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Intent intent = getIntent();
        mCurrentWordUri = intent.getData();
        if (mCurrentWordUri != null) {
            setTitle("Edit Word");
            mSelection = WordContract.WordEntry._ID + "=?";
            mSelectionArgs = new String[]{String.valueOf(ContentUris.parseId(mCurrentWordUri))};

            getLoaderManager().initLoader(1, null, this);
        } else {
            setTitle("Add Word");
            invalidateOptionsMenu();
        }

        mWordEditText = (EditText) findViewById(R.id.edit_word);
        mSentenceEditText = (EditText) findViewById(R.id.edit_sentence);

        mWordEditText.setOnTouchListener(mTouchListener);
        mSentenceEditText.setOnTouchListener(mTouchListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_save:
                saveWord();
                return true;

            case R.id.action_delete_word:
                showDeleteConfirmationDialog();
                return true;

            // R.id.home wouldn't trigger an error but you need android before that to work
            case android.R.id.home:

                if (!mWordHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddEditActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonOnClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                NavUtils.navigateUpFromSameTask(AddEditActivity.this);
                                finish();
                            }
                        };

                showUnsavedChangesDialog(discardButtonOnClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteWord() {

        if (mCurrentWordUri != null) {
            getContentResolver().delete(
                    /*WordContract.WordEntry.CONTENT_URI*/ mCurrentWordUri,
                    null/*mSelection*/,
                    null/*mSelectionArgs*/);
        }
        finish();
    }

    private void saveWord() {

        String wordString = mWordEditText.getText().toString().trim();
        String sentenceString = mSentenceEditText.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(WordContract.WordEntry.COLUMN_WORD, wordString);
        values.put(WordContract.WordEntry.COLUMN_SENTENCE, sentenceString);

        try {
            if (mCurrentWordUri != null) {
                getContentResolver().update(mCurrentWordUri, values,
                        mSelection, mSelectionArgs);
            } else {
                Uri newUri = getContentResolver().insert(WordContract.WordEntry.CONTENT_URI, values);
                if (newUri == null) {
                    Toast.makeText(this, "Insertion failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Word Saved", Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                WordContract.WordEntry._ID,
                WordContract.WordEntry.COLUMN_WORD,
                WordContract.WordEntry.COLUMN_SENTENCE};

        return new CursorLoader(this,
                WordContract.WordEntry.CONTENT_URI /*mCurrentWordUri*/,
                projection,
                mSelection,
                mSelectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToNext()) {
//            cursor.moveToNext();
            mWordEditText.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow(WordContract.WordEntry.COLUMN_WORD)));
            mSentenceEditText.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow(WordContract.WordEntry.COLUMN_SENTENCE)));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mWordEditText.setText("");
        mSentenceEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?")
                .setPositiveButton("DISCARD", discardButtonClickListener)
                .setNegativeButton("KEEP EDITING", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialogInterface != null) {
                            dialogInterface.dismiss();
                        }
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {

        if (!mWordHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonOnClickListener
                = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };

        showUnsavedChangesDialog(discardButtonOnClickListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentWordUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_word);
            menuItem.setVisible(false);
        }

        return true;
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete Word?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteWord();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialogInterface != null) {
                            dialogInterface.dismiss();
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
