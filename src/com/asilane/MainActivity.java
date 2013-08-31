package com.asilane;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.asilane.core.facade.Facade;

public class MainActivity extends Activity {
	private Facade facade;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		facade = new Facade();

		// Capture our button from layout
		final Button button = (Button) findViewById(R.id.button1);
		// Register the onClick listener with the implementation above
		button.setOnClickListener(new Controller(this));
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

	public TextView getText() {
		return (TextView) findViewById(R.id.textView1);
	}

	public EditText getEditText() {
		return (EditText) findViewById(R.id.editText1);
	}
}