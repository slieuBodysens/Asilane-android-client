package com.asilane;

import android.view.View;
import android.view.View.OnClickListener;

import com.asilane.core.Language;

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

		activity.getText().setText(
				activity.getFacade().handleSentence(activity.getEditText().getText().toString(), Language.french));
	}
}