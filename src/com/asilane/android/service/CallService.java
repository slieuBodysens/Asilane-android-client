package com.asilane.android.service;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.content.Intent;
import android.net.Uri;

import com.asilane.android.MainActivity;
import com.asilane.core.AsilaneUtils;
import com.asilane.core.facade.history.HistoryTree;
import com.asilane.service.IService;

/**
 * @author walane
 * 
 */
public class CallService implements IService {

	private static final String APPELLE_LE = "appelle le .*";
	private static final String CALL_THE = "call the .*";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.asilane.service.Service#handleService(java.lang.String, com.asilane.recognition.Locale)
	 */
	@Override
	public String handleService(final String sentence, final Locale lang, final HistoryTree historyTree) {
		List<String> regexVars = null;

		// FRENCH
		if (lang == Locale.FRANCE) {
			if ((regexVars = AsilaneUtils.extractRegexVars(APPELLE_LE, sentence)) != null) {
				final String number = "tel:" + regexVars.get(0);
				final Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
				MainActivity.getInstance().startActivity(callIntent);
			}

			return "J'appelle...";
		}

		// ENGLISH
		if ((regexVars = AsilaneUtils.extractRegexVars(CALL_THE, sentence)) != null) {
			final String number = "tel:" + regexVars.get(0);
			final Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
			MainActivity.getInstance().startActivity(callIntent);
		}
		return "I'm calling...";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.asilane.service.IService#getCommands(com.asilane.recognition.Locale)
	 */
	@Override
	public Set<String> getCommands(final Locale lang) {
		final Set<String> set = new HashSet<String>();

		if (lang == Locale.FRANCE) {
			set.add(APPELLE_LE);
		} else {
			set.add(CALL_THE);
		}

		return set;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.asilane.service.IService#handleRecoveryService(java.lang.String, com.asilane.core.Locale)
	 */
	@Override
	public String handleRecoveryService(final String sentence, final Locale lang, final HistoryTree historyTree) {
		return null;
	}
}