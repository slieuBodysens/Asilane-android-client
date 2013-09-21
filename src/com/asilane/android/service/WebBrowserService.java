package com.asilane.android.service;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.SearchManager;
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
public class WebBrowserService implements IService {

	private static final String GO_ON = "(go on|find) .*";
	private static final String VA_SUR = "(va sur|trouve) .*";
	private static final String GIVE_ME_INFO_ON = "give me info.* on.*";
	private static final String SEARCH_INFO_ON = "search info.* on.*";
	private static final String INFO_SUR = ".*info.* sur .*";

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
			if ((regexVars = AsilaneUtils.extractRegexVars(VA_SUR, sentence)) != null) {
				return handleSearch(regexVars.get(1), lang, true);
			} else if ((regexVars = AsilaneUtils.extractRegexVars(INFO_SUR, sentence)) != null) {
				return handleSearch(regexVars.get(2), lang, false);
			}
		}

		// ENGLISH
		if ((regexVars = AsilaneUtils.extractRegexVars(GO_ON, sentence)) != null) {
			return handleSearch(regexVars.get(1), lang, true);
		} else if ((regexVars = AsilaneUtils.extractRegexVars(SEARCH_INFO_ON, sentence)) != null) {
			return handleSearch(regexVars.get(1), lang, false);
		}

		// If no website provided
		if (lang == Locale.FRANCE) {
			return "Merci de spécifier un site Web";
		}
		return "Please specify a website.";
	}

	private String handleSearch(final String term, final Locale lang, final boolean directBrowsing) {
		// Direct Browsing : find and load the website corresponding to the term
		if (directBrowsing) {
			final Intent browserIntent = new Intent(Intent.ACTION_VIEW);
			browserIntent.setData(Uri.parse("https://duckduckgo.com/?q=!%20" + term));
			MainActivity.getInstance().startActivity(browserIntent);

			if (lang == Locale.FRANCE) {
				return "Ok, je vais sur " + term;
			}
			return "Ok i'm going on " + term;
		}
		// Normal search : search on SearchManager the term
		else {
			final Intent searchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
			searchIntent.putExtra(SearchManager.QUERY, term);
			MainActivity.getInstance().startActivity(searchIntent);

			if (lang == Locale.FRANCE) {
				return "Ok, je cherche des informations sur " + term + ".";
			}
			return "Ok, i'm looking for informations on " + term + ".";
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
			set.add(VA_SUR);
			set.add(INFO_SUR);
		} else {
			set.add(GO_ON);
			set.add(SEARCH_INFO_ON);
			set.add(GIVE_ME_INFO_ON);
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