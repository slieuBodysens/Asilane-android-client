package com.asilane.android;

import java.util.ArrayList;
import java.util.List;
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

import com.asilane.R;
import com.asilane.android.service.CalendarService;
import com.asilane.android.service.CallService;
import com.asilane.android.service.FindPlaceService;
import com.asilane.android.service.MailService;
import com.asilane.android.service.SMSService;
import com.asilane.android.service.SaveWhatSayingService;
import com.asilane.android.service.WebBrowserService;
import com.asilane.android.service.YouTubeService;
import com.asilane.core.facade.Facade;
import com.asilane.core.facade.NoServiceFoundException;
import com.asilane.core.facade.ServiceDispatcher;
import com.asilane.service.AsilaneDialogService;
import com.asilane.service.AsilaneIdentityService;
import com.asilane.service.DateService;
import com.asilane.service.FortyTwoService;
import com.asilane.service.HelloService;
import com.asilane.service.IPService;
import com.asilane.service.IService;
import com.asilane.service.InsultService;
import com.asilane.service.RepeatService;
import com.asilane.service.WeatherForecastService;
import com.asilane.service.WikipediaService;

public class MainActivity extends Activity {
	private Facade facade;
	private TextToSpeech tts;
	private final Locale lang = Locale.FRANCE;
	private static Activity INSTANCE;

	protected static final int RESULT_SPEECH = 1;

	/**
	 * Get Activity Instance
	 * 
	 * @return Activity Instance
	 */
	public static Activity getInstance() {
		return INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialize INSTANCE, facade, text to speech and load services in the dispatcher
		INSTANCE = this;
		facade = new Facade();
		tts = new TextToSpeech(this, null);
		ServiceDispatcher.getInstance(lang).setServices(getAllServices());

		// Set non editable text field
		((EditText) findViewById(R.id.response)).setKeyListener(null);

		// Adding a listener to the manual button
		((Button) findViewById(R.id.manualButton)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {

				try {
					final String response = facade.handleSentence(getManualEditText().getText().toString(), lang);
					getResponseField().setText(response);
					tts = new TextToSpeech(MainActivity.getInstance(), new OnInitListener() {
						@Override
						public void onInit(final int status) {
							textToSpeech(response);
							;
						}
					});
				} catch (final NoServiceFoundException e) {
					final String response = e.getMessage();
					getResponseField().setText(response + "\n\"" + getManualEditText().getText() + "\"");
					tts = new TextToSpeech(MainActivity.getInstance(), new OnInitListener() {
						@Override
						public void onInit(final int status) {
							textToSpeech(response);
							;
						}
					});
				}

			}
		});

		// Adding a listener to the speak recognition button
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
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, lang);
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
		if (data != null) {
			final ArrayList<String> textSpeeched = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			try {
				// Speech the response
				final String response = getFacade().handleSentence(textSpeeched.get(0), Locale.FRANCE);
				getResponseField().setText(response);
				tts = new TextToSpeech(this, new OnInitListener() {
					@Override
					public void onInit(final int status) {
						textToSpeech(response);
						;
					}
				});
			} catch (final NoServiceFoundException e) {
				// If nothing understood -> display the original request
				getResponseField().setText(e.getMessage() + "\n\"" + textSpeeched.get(0) + "\"");

				// Speech the error message
				tts = new TextToSpeech(this, new OnInitListener() {
					@Override
					public void onInit(final int status) {
						textToSpeech(e.getMessage());
						;
					}
				});
			}
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
	 * Get all services<br>
	 * This is a custom method to choose which services will be implemented in the Android version
	 * 
	 * @return All services
	 */
	public List<IService> getAllServices() {
		final List<IService> allServices = new ArrayList<IService>();

		allServices.add(new SaveWhatSayingService());
		allServices.add(new YouTubeService());
		allServices.add(new AsilaneIdentityService());
		allServices.add(new FortyTwoService());
		allServices.add(new WeatherForecastService());
		allServices.add(new WebBrowserService());
		allServices.add(new AsilaneDialogService());
		allServices.add(new DateService());
		allServices.add(new IPService());
		allServices.add(new WikipediaService());
		allServices.add(new FindPlaceService());
		allServices.add(new MailService());
		allServices.add(new RepeatService());
		allServices.add(new HelloService());
		allServices.add(new CallService());
		allServices.add(new SMSService());
		allServices.add(new CalendarService());
		allServices.add(new InsultService());

		return allServices;
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