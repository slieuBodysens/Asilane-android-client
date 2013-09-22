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

	private static final String ENVOI_UN_SMS = "envoi.* un (sms|message)";
	private static final String SEND_A_SMS = "send a (sms|message)";

	private static final String ENVOI_UN_SMS_A = "envoi.* un (sms|message) à.*";
	private static final String SEND_A_SMS_TO = "send a (sms|message) to.*";

	private static final String ENVOI_UN_SMS_A_EN_DISANT = "envoi.* un (sms|message) à .* en.* disant .*";
	private static final String SEND_A_SMS_TO_AND_SAY = "send a (sms|message) to .* and say .*";

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
			// With dest and message
			if ((regexVars = AsilaneUtils.extractRegexVars(ENVOI_UN_SMS_A_EN_DISANT, sentence)) != null) {
				sms(regexVars.get(2), regexVars.get(4));
				return "Ok, je vous prépare l'envoi d'un sms.";
			}

			// With dest
			else if ((regexVars = AsilaneUtils.extractRegexVars(ENVOI_UN_SMS_A, sentence)) != null) {
				sms(regexVars.get(2), "");
				return "Ok, je vous prépare l'envoi d'un sms.";
			}

			// Blank sms
			sms("", "");
			return "Ok, je vous prépare l'envoi d'un sms.";
		}

		// ENGLISH

		// With dest
		if ((regexVars = AsilaneUtils.extractRegexVars(SEND_A_SMS_TO, sentence)) != null) {
			sms(regexVars.get(1), "");
			return "Ok, i send a sms.";
		}
		// With dest and message
		else if ((regexVars = AsilaneUtils.extractRegexVars(SEND_A_SMS_TO_AND_SAY, sentence)) != null) {
			sms(regexVars.get(1), regexVars.get(2));
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
		final Set<String> set = new HashSet<String>();

		if (lang == Locale.FRANCE) {
			set.add(ENVOI_UN_SMS);
			set.add(ENVOI_UN_SMS_A);
			set.add(ENVOI_UN_SMS_A_EN_DISANT);
		} else {
			set.add(SEND_A_SMS);
			set.add(SEND_A_SMS_TO);
			set.add(SEND_A_SMS_TO_AND_SAY);
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