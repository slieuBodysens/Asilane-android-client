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
public class SMSService implements IService {

	private final Set<String> commands = new HashSet<String>();

	private static final String ENVOI_UN_SMS = ".*(sms|message)$";
	private static final String SEND_A_SMS = ".*(sms|message)$";

	private static final String ENVOI_UN_SMS_AU = ".*(sms|message) au (\\d{2} \\d{2} \\d{2} \\d{2} \\d{2})$";
	private static final String SEND_A_SMS_TO = ".*(sms|message) to (\\d{2} \\d{2} \\d{2} \\d{2} \\d{2})$";

	private static final String ENVOI_UN_SMS_AU_EN_DISANT = ".*(sms|message) au (\\d{2} \\d{2} \\d{2} \\d{2} \\d{2}|\\d{2}) .*";
	private static final String SEND_A_SMS_TO_AND_SAY = ".*(sms|message) to (\\d{2} \\d{2} \\d{2} \\d{2} \\d{2}|\\d{2}) .*";

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
			// With dest
			if ((regexVars = AsilaneUtils.extractRegexVars(ENVOI_UN_SMS_AU, sentence)) != null) {
				sms(regexVars.get(2), null);
				return "Ok, je vous prépare l'envoi d'un sms.";
			}

			// With dest and message
			if ((regexVars = AsilaneUtils.extractRegexVars(ENVOI_UN_SMS_AU_EN_DISANT, sentence)) != null) {
				sms(regexVars.get(2), regexVars.get(3));
				return "Ok, je vous prépare l'envoi d'un sms.";
			}

			// Blank sms
			sms("", "");
			return "Ok, je vous prépare l'envoi d'un sms.";
		}

		// ENGLISH

		if ((regexVars = AsilaneUtils.extractRegexVars(SEND_A_SMS_TO, sentence)) != null) {
			sms(regexVars.get(2), null);
			return "Ok, i send a sms.";
		}

		// With dest and message
		if ((regexVars = AsilaneUtils.extractRegexVars(SEND_A_SMS_TO_AND_SAY, sentence)) != null) {
			sms(regexVars.get(2), regexVars.get(3));
			return "Ok, i send a sms.";
		}

		// Blank sms
		sms("", "");
		return "Ok.";

	}

	/**
	 * Send a sms
	 * 
	 * @param dest
	 * @param message
	 */
	private void sms(final String dest, final String message) {
		final Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + dest));
		intent.putExtra("sms_body", message);

		MainActivity.getInstance().startActivity(intent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.asilane.service.IService#getCommands(com.asilane.recognition.Locale)
	 */
	@Override
	public Set<String> getCommands(final Locale lang) {
		if (commands.isEmpty()) {
			if (lang == Locale.FRANCE) {
				commands.add(ENVOI_UN_SMS);
				commands.add(ENVOI_UN_SMS_AU);
				commands.add(ENVOI_UN_SMS_AU_EN_DISANT);
			} else {
				commands.add(SEND_A_SMS);
				commands.add(SEND_A_SMS_TO);
				commands.add(SEND_A_SMS_TO_AND_SAY);
			}
		}

		return commands;
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