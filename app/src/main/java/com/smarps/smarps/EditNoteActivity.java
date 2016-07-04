package com.smarps.smarps;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditNoteActivity extends ActionBarActivity {
	
	private Note note;
	private EditText titleEditText;
	private EditText contentEditText;
	private String postTitle;
	private String postContent;
	private Button deleteNoteButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_edit_note);
		
		final Intent intent = this.getIntent();
        final Intent intent2 = new Intent(this, MainActivity.class);
		
		titleEditText = (EditText) findViewById(R.id.noteTitle);
		contentEditText = (EditText) findViewById(R.id.noteContent);
		
		if (intent.getExtras() != null) {
			note = new Note(intent.getStringExtra("noteId"), intent.getStringExtra("noteTitle"), intent.getStringExtra("noteContent"));
			
			titleEditText.setText(note.getTitle());
			contentEditText.setText(note.getContent());
		}
		
		deleteNoteButton = (Button)findViewById(R.id.deleteNote);
		deleteNoteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(
                        EditNoteActivity.this);
                alert.setTitle("Alert!!");
                alert.setMessage("Are you sure to delete this smarps");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNote();
                        dialog.dismiss();
                        startActivity(intent2);
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_note, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		saveNote();

		return super.onOptionsItemSelected(item);
	}


	
	private void saveNote() {
		
		postTitle = titleEditText.getText().toString();
		postContent = contentEditText.getText().toString();
		
		postTitle = postTitle.trim();
		postContent = postContent.trim();
		
		// If user doesn't enter a title or content, do nothing
		// If user enters title, but no content, save
		// If user enters content with no title, give warning
		// If user enters both title and content, save
		
		if (!postTitle.isEmpty()) {
			
			// Check if post is being created or edited
			
			if (note == null) {
				// create new post
				
				final ParseObject post = new ParseObject("Post");
				post.put("title", postTitle);
				post.put("content", postContent);
				post.put("author", ParseUser.getCurrentUser());
				setProgressBarIndeterminateVisibility(true);
				post.saveInBackground(new SaveCallback() {
		            public void done(ParseException e) {
		            	setProgressBarIndeterminateVisibility(false);
		                if (e == null) {
		                    // Saved successfully.
		                	note = new Note(post.getObjectId(), postTitle, postContent);
		                	Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
		                } else {
		                    // The save failed.
		                	Toast.makeText(getApplicationContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
		                    Log.d(getClass().getSimpleName(), "User update error: " + e);
		                }
		            }
		        });
				
			}
			else {
				// update post
				
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
				 
				// Retrieve the object by id
				query.getInBackground(note.getId(), new GetCallback<ParseObject>() {
				  public void done(ParseObject post, ParseException e) {
				    if (e == null) {
				      // Now let's update it with some new data.
				    	post.put("title", postTitle);
						post.put("content", postContent);
						setProgressBarIndeterminateVisibility(true);
						post.saveInBackground(new SaveCallback() {
				            public void done(ParseException e) {
				            	setProgressBarIndeterminateVisibility(false);
				                if (e == null) {
				                    // Saved successfully.
				                	Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
				                } else {
				                    // The save failed.
				                	Toast.makeText(getApplicationContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
				                    Log.d(getClass().getSimpleName(), "User update error: " + e);
				                }
				            }
				        });
				    }
				  }
				});
			}
		} 
		else if (postTitle.isEmpty() && !postContent.isEmpty()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(EditNoteActivity.this);
			builder.setMessage(R.string.edit_error_message)
				.setTitle(R.string.edit_error_title)
				.setPositiveButton(android.R.string.ok, null);
			AlertDialog dialog = builder.create();
			dialog.show();			
		}
	}

    private void deleteNote() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");

        // Retrieve the object by id
        query.getInBackground(note.getId(), new GetCallback<ParseObject>() {
            public void done(ParseObject post, ParseException e) {
                if (e == null) {
                    setProgressBarIndeterminateVisibility(true);
                    post.deleteInBackground(new DeleteCallback() {
                        public void done(ParseException e) {
                            setProgressBarIndeterminateVisibility(false);
                            if (e == null) {
                                // deleted successfully.
                                Toast.makeText(getApplicationContext(), "deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                // The delete failed.
                                Toast.makeText(getApplicationContext(), "Failed to delete", Toast.LENGTH_SHORT).show();
                                Log.d(getClass().getSimpleName(), "User delete error: " + e);
                            }
                        }
                    });
                }
            }
        });
    }
}