package hu.blogspot.limarapeksege.activity;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.util.handlers.note.NoteHandler;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class NotePad extends Activity {

	private EditText noteField;
	private String noteText;
	private String recipeName;
	NoteHandler noteHandler;
	Bundle getName;

	// private Button confirmButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_note_pad);
		noteHandler = new NoteHandler(NotePad.this);

		noteField = (EditText) findViewById(R.id.notePadBody);

		Bundle getName = getIntent().getExtras();
		recipeName = getName.getString("name").toString();
		setTitle(recipeName + " jegyzet");

		File noteFile = new File(Environment.getExternalStorageDirectory()
				+ "/LimaraPéksége/FavoriteRecipes/Notes", recipeName + ".txt");
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
			noteText = noteField.getText().toString();
			Log.w("LimaraPéksége", noteText);

			if (noteHandler.saveNote(noteText, recipeName)) {
				Toast.makeText(this, R.string.note_saved, Toast.LENGTH_LONG)
						.show();
			} else {
				Toast.makeText(this, R.string.common_error, Toast.LENGTH_LONG)
						.show();
			}
		} else if (getString(R.string.menu_delete) == item.getTitle()) {
			if (noteHandler.deleteNote(recipeName)) {
				Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_LONG)
						.show();
			} else {
				Toast.makeText(this, R.string.common_error, Toast.LENGTH_LONG)
						.show();
			}
			onBackPressed();
		}

		return super.onOptionsItemSelected(item);
	}

}
