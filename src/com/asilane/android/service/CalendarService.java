package com.asilane.android.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

import com.asilane.android.MainActivity;
import com.asilane.core.AsilaneUtils;
import com.asilane.core.facade.history.HistoryTree;
import com.asilane.service.IService;

/**
 * @author walane
 * 
 */
public class CalendarService implements IService {

	private static final String REVEILLE_MOI = "réveil.*moi (demain|aujourd'hui) à .*h.*";
	private static final String RDV = ".*rendez.*vous (demain|aujourd'hui) à .*h.*";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.asilane.service.Service#handleService(java.lang.String, com.asilane.recognition.Locale)
	 */
	@Override
	public String handleService(final String sentence, final Locale lang, final HistoryTree historyTree) {
		List<String> regexVars = null;
		final Calendar cal = new GregorianCalendar();

		try {
			// FRENCH
			if (lang == Locale.FRANCE) {
				// With dest and message
				if ((regexVars = AsilaneUtils.extractRegexVars(REVEILLE_MOI, sentence)) != null) {
					cal.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(regexVars.get(2)));
					cal.set(GregorianCalendar.MINUTE, Integer.parseInt(regexVars.get(3)));
					addCalendarEvent("Réveil", cal.getTime(), null, regexVars.get(1).equals("demain"));

					return "Très bien je vous réveille " + regexVars.get(1) + " à " + regexVars.get(2) + "h"
							+ regexVars.get(3);
				} else if ((regexVars = AsilaneUtils.extractRegexVars(RDV, sentence)) != null) {
					cal.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(regexVars.get(3)));
					cal.set(GregorianCalendar.MINUTE, Integer.parseInt(regexVars.get(4)));
					addCalendarEvent("Rendez-vous", cal.getTime(), null, regexVars.get(1).equals("demain"));

					return "Très bien je vous avertirai " + regexVars.get(2) + " à " + regexVars.get(3) + "h"
							+ regexVars.get(4);
				}
			}

			// ENGLISH

			// With dest
			return "";

		} catch (final NumberFormatException e) {
			if (lang == Locale.FRANCE) {
				return "Je n'ai pas bien compris à quel heure vous souhaitez prévoir un évènement.";
			}
			return "I don't understand verywell the hour of the event.";
		}
	}

	/**
	 * Add an event in the Android's calendar
	 * 
	 * @param title
	 * @param beginDate
	 * @param endDate
	 * @param tomorrow
	 */
	@SuppressLint("InlinedApi")
	private void addCalendarEvent(final String title, final Date beginDate, Date endDate, final boolean tomorrow) {
		// If the event is tomorrow, we add 12 hours
		final long tomorrowValue = (tomorrow) ? 43200000 : 0;
		beginDate.setTime(beginDate.getTime() + tomorrowValue * 2);

		// If there is no endDate, it means beginDate + 5min
		if (endDate == null) {
			endDate = new Date(beginDate.getTime() + 300000);
		} else {
			endDate.setTime(endDate.getTime() + tomorrowValue);
		}

		final Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra(Events.TITLE, title);
		intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginDate.getTime());
		intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDate.getTime());
		intent.putExtra(Events.ALL_DAY, false);// periodicity
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
			set.add(REVEILLE_MOI);
			set.add(RDV);
		} else {
		}

		return set;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.asilane.service.IService#handleRecoveryService(java.lang.String, java.util.Locale)
	 */
	@Override
	public String handleRecoveryService(final String sentence, final Locale lang, final HistoryTree historyTree) {
		return null;
	}
}