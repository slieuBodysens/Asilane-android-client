package com.asilane.android.service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.asilane.android.AsilaneAndroidUtils;
import com.asilane.core.AsilaneUtils;
import com.asilane.core.facade.history.HistoryTree;
import com.asilane.service.IService;

/**
 * @author walane
 * 
 */
public class SaveWhatSayingService implements IService {

	private static final String ENREGISTRE_CE_QUE_JE_DIS = ".*registr. ce que je dis.*";
	private static final String SAVE_WHAT_I_SAY = ".*save what i say.*";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.asilane.service.Service#handleService(java.lang.String, com.asilane.recognition.Locale)
	 */
	@Override
	public String handleService(final String sentence, final Locale lang, final HistoryTree historyTree) {
		// FRENCH
		if (lang == Locale.FRANCE) {
			AsilaneAndroidUtils.setClipboardContents(AsilaneUtils.extractRegexVars(ENREGISTRE_CE_QUE_JE_DIS, sentence)
					.get(1));
			return "Voilà, j'ai enregistré ce que vous avez dit dans le presse-papier.";
		}

		// ENGLISH
		AsilaneAndroidUtils.setClipboardContents(AsilaneUtils.extractRegexVars(SAVE_WHAT_I_SAY, sentence).get(1));
		return "It's done, i have save it in the clipboard.";
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
			set.add(ENREGISTRE_CE_QUE_JE_DIS);
		} else {
			set.add(SAVE_WHAT_I_SAY);
		}

		return set;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.asilane.service.IService#handleRecoveryService(java.lang.String, com.asilane.core.Locale)
	 */
	@Override
	public String handleRecoveryService(final String sentence, final Locale lang) {
		return null;
	}
}