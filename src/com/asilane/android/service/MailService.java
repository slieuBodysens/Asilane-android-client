package com.asilane.android.service;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.content.Intent;

import com.asilane.android.MainActivity;
import com.asilane.core.AsilaneUtils;
import com.asilane.core.facade.history.HistoryTree;
import com.asilane.service.IService;

/**
 * @author walane
 * 
 */
public class MailService implements IService {

	private final Set<String> commands = new HashSet<String>();

	private static final String ENVOI_UN_MAIL = "envoi.* un .*mail";
	private static final String SEND_A_MAIL = "send a .*mail";

	private static final String ENVOI_UN_MAIL_A = "envoi.* un .*mail à.*";
	private static final String SEND_A_MAIL_TO = "send a .*mail to.*";

	private static final String ENVOI_UN_MAIL_A_EN_DISANT = "envoi.* un .*mail à .* en.* disant .*";
	private static final String SEND_A_MAIL_TO_AND_SAY = "send a .*mail to .* and say .*";

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
			if ((regexVars = AsilaneUtils.extractRegexVars(ENVOI_UN_MAIL_A_EN_DISANT, sentence)) != null) {
				final String dest = textToEmailAddress(regexVars.get(2), lang);
				mail(dest, "", regexVars.get(4));
				return "Ok, je vous prépare l'envoi d'un email à " + dest;
			}

			// With dest
			else if ((regexVars = AsilaneUtils.extractRegexVars(ENVOI_UN_MAIL_A, sentence)) != null) {
				final String dest = textToEmailAddress(regexVars.get(2), lang);
				mail(dest, "", "");
				return "Ok, je vous prépare l'envoi d'un email à " + dest;
			}

			// Blank mail
			mail("", "", "");
			return "Ok, je vous prépare l'envoi d'un email.";
		}

		// ENGLISH

		// With dest and message
		if ((regexVars = AsilaneUtils.extractRegexVars(SEND_A_MAIL_TO_AND_SAY, sentence)) != null) {
			final String dest = textToEmailAddress(regexVars.get(1), lang);
			mail(dest, "", regexVars.get(2));
			return "Ok, i send a mail to " + dest;
		}
		// With dest
		else if ((regexVars = AsilaneUtils.extractRegexVars(SEND_A_MAIL_TO, sentence)) != null) {
			final String dest = textToEmailAddress(regexVars.get(1), lang);
			mail(dest, "", "");
			return "Ok, i send a mail to " + dest;
		}

		// Blank mail
		mail("", "", "");
		return "Ok.";

	}

	/**
	 * Transform a text into a valid email address
	 * 
	 * @param text
	 * @return the valid email address corresponding to the text
	 */
	private String textToEmailAddress(final String text, final Locale lang) {
		if (lang == Locale.FRANCE) {
			return text.replace("arobase", "@").replace("point", ".").replace(" ", "");
		}
		return text.replace("arobas", "@").replace("dot", ".").replace(" ", "");
	}

	/**
	 * Send a mail
	 * 
	 * @param dest
	 * @param subject
	 * @param message
	 */
	private void mail(final String dest, final String subject, final String message) {
		final Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, new String[] { dest });
		email.putExtra(Intent.EXTRA_SUBJECT, subject);
		email.putExtra(Intent.EXTRA_TEXT, message);
		email.setType("message/rfc822");

		MainActivity.getInstance().startActivity(Intent.createChooser(email, "Choose an Email client :"));
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
				commands.add(ENVOI_UN_MAIL);
				commands.add(ENVOI_UN_MAIL_A);
				commands.add(ENVOI_UN_MAIL_A_EN_DISANT);
			} else {
				commands.add(SEND_A_MAIL);
				commands.add(SEND_A_MAIL_TO);
				commands.add(SEND_A_MAIL_TO_AND_SAY);
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