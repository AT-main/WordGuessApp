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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import tirzad.starunique.wordguess.data.WordContract.WordEntry;

public class ViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    WordCursorAdapter mCursorAdapter;
    static final String[] PROJECTION = {
            WordEntry._ID,
            WordEntry.COLUMN_WORD,
            WordEntry.COLUMN_SENTENCE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewActivity.this, AddEditActivity.class));
            }
        });

        ListView wordListView = (ListView) findViewById(R.id.lvItems);
        View emptyView = findViewById(R.id.empty_view);
        wordListView.setEmptyView(emptyView);

        mCursorAdapter = new WordCursorAdapter(this, null);
        wordListView.setAdapter(mCursorAdapter);

        wordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(ViewActivity.this, AddEditActivity.class);
                intent.setData(ContentUris.withAppendedId(WordEntry.CONTENT_URI, id));
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_insert_data:
                insertWord();
                return true;

            case R.id.action_delete_all_entries:
                showAllWordsDeletionConfirmation();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllWords() {
        getContentResolver().delete(WordEntry.CONTENT_URI, null, null);
    }

    private void insertWord() {
        ContentValues values = new ContentValues();
        values.put(WordEntry.COLUMN_WORD, "analysis");
        values.put(WordEntry.COLUMN_SENTENCE, "Further analysis of the data is needed.");
        Uri newUri = getContentResolver().insert(WordEntry.CONTENT_URI, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                WordEntry.CONTENT_URI,
                PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    private void showAllWordsDeletionConfirmation() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete all words in the database?")
                .setPositiveButton("DELETE ALL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAllWords();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialogInterface != null)
                            dialogInterface.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
