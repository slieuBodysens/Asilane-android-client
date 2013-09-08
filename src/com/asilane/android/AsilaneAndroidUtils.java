package com.asilane.android;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;

/**
 * Some good things used in the android application
 * 
 * @author walane
 */
public class AsilaneAndroidUtils {

	/**
	 * Place a String in the android clipboard
	 * 
	 * @param astring
	 */
	public static void setClipboardContents(final String string) {
		final ClipboardManager clipboard = (ClipboardManager) MainActivity.getInstance().getSystemService(
				Activity.CLIPBOARD_SERVICE);
		clipboard.setPrimaryClip(ClipData.newPlainText("label", string));
	}
}