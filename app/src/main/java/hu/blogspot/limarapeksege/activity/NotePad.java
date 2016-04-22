package hu.blogspot.limarapeksege.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.util.AnalyticsTracker;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.handlers.note.NoteHandler;

public class NotePad extends Activity {

    private EditText noteField;
    private String recipeName;
    NoteHandler noteHandler;
    private AnalyticsTracker trackerApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_note_pad);

        trackerApp = (AnalyticsTracker) getApplication();
        trackerApp.sendScreen(getString(R.string.analytics_screen_note));


        noteHandler = new NoteHandler(NotePad.this);

        noteField = (EditText) findViewById(R.id.notePadBody);

        Bundle getName = getIntent().getExtras();
        recipeName = getName.getString("name");
        setTitle(recipeName + " jegyzet");

        File noteFile = new File(Environment.getExternalStorageDirectory()
                + GlobalStaticVariables.NOTES_PATH, recipeName + ".txt");
        if (noteFile.exists()) {
            noteField.setText(noteHandler.readNote(noteFile));
        } else {
            try {
                noteFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.note_pad, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (getString(R.string.menu_save) == item.getTitle()) {
            String noteText = noteField.getText().toString();
            Log.w(GlobalStaticVariables.LOG_TAG, noteText);

            if (noteHandler.saveNote(noteText, recipeName)) {
                Toast.makeText(this, R.string.note_saved, Toast.LENGTH_LONG)
                        .show();
                trackerApp.sendTrackerEvent(getString(R.string.analytics_category_note), getString(R.string.analytics_note_saved));
            } else {
                Toast.makeText(this, R.string.common_error, Toast.LENGTH_LONG)
                        .show();
                trackerApp.sendTrackerEvent(getString(R.string.analytics_category_note), getString(R.string.common_error));
            }
        } else if (getString(R.string.menu_delete) == item.getTitle()) {
            if (noteHandler.deleteNote(recipeName)) {
                Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_LONG)
                        .show();
                trackerApp.sendTrackerEvent(getString(R.string.analytics_category_note), getString(R.string.analytics_note_deleted));
            } else {
                Toast.makeText(this, R.string.common_error, Toast.LENGTH_LONG)
                        .show();
                trackerApp.sendTrackerEvent(getString(R.string.analytics_category_note), getString(R.string.common_error));
            }
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

}
