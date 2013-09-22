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
public class YouTubeService implements IService {

	private static final String VIDEO = "(video|vidéo) .*";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.asilane.service.Service#handleService(java.lang.String, com.asilane.recognition.Locale)
	 */
	@Override
	public String handleService(final String sentence, final Locale lang, final HistoryTree historyTree) {
		List<String> regexVars = null;

		if ((regexVars = AsilaneUtils.extractRegexVars(VIDEO, sentence)) != null) {
			return handleSearch(regexVars.get(1), lang);
		}

		// If no website provided
		if (lang == Locale.FRANCE) {
			return "Merci de spécifier un nom de vidéo.";
		}
		return "Please specify a video name.";
	}

	private String handleSearch(final String term, final Locale lang) {
		final Intent browserIntent = new Intent(Intent.ACTION_VIEW);
		browserIntent.setData(Uri.parse("https://duckduckgo.com/?q=!%20site:youtube.com%2Fwatch%20" + term));
		MainActivity.getInstance().startActivity(browserIntent);

		if (lang == Locale.FRANCE) {
			return "C'est parti.";
		} else {
			return "Let's go.";
		}
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
			set.add(VIDEO);
		} else {
			set.add(VIDEO);
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