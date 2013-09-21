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
public class FindPlaceService implements IService {

	private static final String OU_SE_TROUVE = "o. se trouve .*";
	private static final String OU_SE_SITUE = "o. se situe .*";
	private static final String ITINERAIRE = ".*itinéraire de .* à .*";
	private static final String ITINERAIRE_ENTRE = ".*itinéraire entre .* et .*";
	private static final String WHERE_IS = "where is .*";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.asilane.service.Service#handleService(java.lang.String, com.asilane.recognition.Locale)
	 */
	@Override
	public String handleService(final String sentence, final Locale lang, final HistoryTree historyTree) {
		// Extract the place we are looking for
		List<String> regexVars = null;

		// FRENCH
		if (lang == Locale.FRANCE) {
			if ((regexVars = AsilaneUtils.extractRegexVars(OU_SE_TROUVE, sentence)) != null
					|| (regexVars = AsilaneUtils.extractRegexVars(OU_SE_SITUE, sentence)) != null) {
				return handleSearch(regexVars.get(0), lang);
			} else if ((regexVars = AsilaneUtils.extractRegexVars(ITINERAIRE, sentence)) != null
					|| (regexVars = AsilaneUtils.extractRegexVars(ITINERAIRE_ENTRE, sentence)) != null) {
				return handleSearch("from: " + regexVars.get(1) + " to: " + regexVars.get(2), lang);
			}
		}

		// ENGLISH
		if ((regexVars = AsilaneUtils.extractRegexVars(WHERE_IS, sentence)) != null) {
			return handleSearch(regexVars.get(0), lang);
		}

		// If no website provided
		if (lang == Locale.FRANCE) {
			return "Merci de spécifier un lieu.";
		}
		return "Please specify a place.";
	}

	/**
	 * Handle place search
	 * 
	 * @param place
	 * @param lang
	 * @return place location
	 */
	private String handleSearch(final String place, final Locale lang) {
		final Intent browserIntent = new Intent(Intent.ACTION_VIEW);
		browserIntent.setData(Uri.parse("https://maps.google.com/maps?q=" + place + "&hl="
				+ lang.toString().substring(0, 2)));
		MainActivity.getInstance().startActivity(browserIntent);

		if (lang == Locale.FRANCE) {
			if (place.contains("from")) {
				return "Voilà, je vous ai préparé l'itinéraire.";
			}
			return "Voici où se situe " + place + ".";
		}

		return "This is where " + place + " is.";
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
			set.add(OU_SE_TROUVE);
			set.add(OU_SE_SITUE);
			set.add(ITINERAIRE);
			set.add(ITINERAIRE_ENTRE);
		} else {
			set.add(WHERE_IS);
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
		List<String> regexVars = null;

		// FRENCH
		if (lang == Locale.FRANCE) {
			if ((regexVars = AsilaneUtils.extractRegexVars("et .*", sentence)) != null) {
				return handleSearch(regexVars.get(0), lang);
			}
		}

		// ENGLISH
		if ((regexVars = AsilaneUtils.extractRegexVars("and .*", sentence)) != null) {
			return handleSearch(regexVars.get(0), lang);
		}

		return null;
	}
}