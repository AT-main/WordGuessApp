package tirzad.starunique.wordguess;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import tirzad.starunique.wordguess.data.WordContract;

/**
 * Created by StarUnique on 18/09/2017.
 */

public class WordCursorAdapter extends CursorAdapter {


    public WordCursorAdapter(Context context, Cursor c/*, int flags*/) {
        super(context, c, 0/*flags*/);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView lvWord = (TextView) view.findViewById(R.id.lv_word);
        TextView lvSentence = (TextView) view.findViewById(R.id.lv_sentence);

        lvWord.setText(cursor.getString(cursor.getColumnIndexOrThrow(WordContract.WordEntry.COLUMN_WORD)));
        lvSentence.setText(cursor.getString(cursor.getColumnIndexOrThrow(WordContract.WordEntry.COLUMN_SENTENCE)));


    }
}
