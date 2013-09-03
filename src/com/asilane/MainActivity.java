package com.asilane;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.asilane.core.facade.Facade;

public class MainActivity extends Activity {
	private Facade facade;
	private TextToSpeech tts;

	protected static final int RESULT_SPEECH = 1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		facade = new Facade();
		tts = new TextToSpeech(this, null);

		// Set non editable text field
		((EditText) findViewById(R.id.response)).setKeyListener(null);

		// Adding a listener to the manual button
		((Button) findViewById(R.id.manualButton)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final String response = facade.handleSentence(getManualEditText().getText().toString(), Locale.FRANCE);
				getResponseField().setText(response);
				textToSpeech(response);
			}
		});

		// Adding a istener to the speak recognition button
		final ImageButton speakButton = (ImageButton) findViewById(R.id.speakButton);
		speakButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				// Stop speeching when user speak
				if (tts != null) {
					tts.stop();
					tts.shutdown();
				}

				final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

				try {
					startActivityForResult(intent, RESULT_SPEECH);
				} catch (final ActivityNotFoundException a) {
					final Toast t = Toast.makeText(getApplicationContext(),
							"Opps! Your device doesn't support Speech to Text", Toast.LENGTH_SHORT);
					t.show();
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {

		case RESULT_SPEECH: {
			if (resultCode == RESULT_OK && null != data) {
				final ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				final String response = getFacade().handleSentence(text.get(0), Locale.FRANCE);
				getResponseField().setText(response);
				tts = new TextToSpeech(this, new OnInitListener() {
					@Override
					public void onInit(final int status) {
						textToSpeech(response);
						;
					}
				});
			}
		}
			break;
		}
	}

	/**
	 * Speech a text
	 * 
	 * @param textToSpeech
	 */
	private void textToSpeech(final String textToSpeech) {
		tts.setLanguage(Locale.FRANCE);
		tts.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * @return Asilane
	 */
	public Facade getFacade() {
		return facade;
	}

	/**
	 * @return the response field
	 */
	public TextView getResponseField() {
		return (TextView) findViewById(R.id.response);
	}

	/**
	 * @return the manual EditText
	 */
	public EditText getManualEditText() {
		return (EditText) findViewById(R.id.manualEditText);
	}
}