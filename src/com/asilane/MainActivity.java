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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.asilane.core.facade.Facade;

public class MainActivity extends Activity {
	private Facade facade;
	private TextToSpeech tts;

	protected static final int RESULT_SPEECH = 1;

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
						speak(response);
						;
					}
				});
			}
			break;
		}

		}
	}

	private void speak(final String txt) {
		tts.setLanguage(Locale.FRANCE);
		tts.speak(txt, TextToSpeech.QUEUE_FLUSH, null);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		facade = new Facade();

		final ImageButton button = (ImageButton) findViewById(R.id.speakButton);
		((EditText) findViewById(R.id.response)).setKeyListener(null);

		// button.setOnClickListener(new Controller(this));
		button.setOnClickListener(new View.OnClickListener() {

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

	public TextView getResponseField() {
		return (TextView) findViewById(R.id.response);
	}

	public EditText getManualEditText() {
		return (EditText) findViewById(R.id.manualEditText);
	}
}