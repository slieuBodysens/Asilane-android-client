package com.asilane;

import android.view.View;
import android.view.View.OnClickListener;

public class Controller implements OnClickListener {
	private final MainActivity activity;

	public Controller(final MainActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onClick(final View v) {
		// // Voice call
		// if (CaptureState.PROCESSING_AUDIO.equals(activity.getAsilane().getRecordingState())) {
		// final String iaResponse = activity.getAsilane().closeRecordAndHandleSentence();
		// } else {
		// activity.getAsilane().beginRecord();
		// }

		activity.getResponseField().setText(
				activity.getFacade().handleSentence(activity.getManualEditText().getText().toString(),
						java.util.Locale.FRANCE));
	}
}