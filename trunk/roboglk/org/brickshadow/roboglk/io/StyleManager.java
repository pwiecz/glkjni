/* This file is a part of roboglk.
 * Copyright (c) 2009 Edward McCardell
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.brickshadow.roboglk.io;


import org.brickshadow.roboglk.GlkStyle;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.TextAppearanceSpan;


public class StyleManager {
	
	/* TODO: remember, when I implement style hint modifications,
	 * to synchronize them on this.
	 */
	
	public static class StyleSpan {
		public final CharacterStyle characterStyle;
		public final int start;
		
		public StyleSpan(CharacterStyle characterStyle, int start) {
			this.characterStyle = characterStyle;
			this.start = start;
		}
	}
	
	public static StyleSpan[] getNullSpans() {
		return new StyleSpan[] { null, null };
	}
	
	public static int getIntBgColor(StyleSpan[] spans) {
		StyleSpan bgSpan = spans[Style.BG_SPAN];
		if (bgSpan == null) {
			return 0;
		}
		return
			((BackgroundColorSpan) bgSpan.characterStyle).getBackgroundColor();
	}
	
	public static int getIntTextSize(StyleSpan[] spans) {
		StyleSpan textSpan = spans[Style.TEXT_SPAN];
		if (textSpan == null) {
			return 0;
		}
		return
			((TextAppearanceSpan) textSpan.characterStyle).getTextSize();
	}
	
	/**
	 * Translates a Glk style into a {@code StyleSpans} object.
	 */
	public static class Style {
		public static int TEXT_SPAN = 0;
		public static int BG_SPAN = 1;
		
		private final String family;
		private final int style;
		private final int size;
		private final int foreColor;
		private final int backColor;
		
		private ColorStateList foreState;
		private ColorStateList backState;
		
		public Style(String family, int style, int size,
				int foreColor, int backColor) {

			if (backColor == 0) {
				throw new IllegalArgumentException();
			}
			this.family = family;
			this.style = style;
			this.size = size;
			this.foreColor = foreColor;
			this.backColor = backColor;
			
			foreState = ColorStateList.valueOf(foreColor);
			backState = ColorStateList.valueOf(backColor);
		}
		
		public StyleSpan[] getSpans(boolean isReverse, int textLen) {
			return new StyleSpan[] {
					new StyleSpan(
							new TextAppearanceSpan(
									family,
									style,
									size,
									(isReverse ? backState : foreState),
									null),
							textLen),
					new StyleSpan(
							new BackgroundColorSpan(
									(isReverse ? foreColor : backColor)),
							textLen)
			};
		}
	}
	
	private static int NUM_STYLES = GlkStyle.User2 + 1;
	
	private final Style[] styles = new Style[NUM_STYLES];
	
	/** Temporary default constructor */
	public StyleManager() {
		/* For now, all styles are black on white. */
		int black = 0xFF000000;
		int white = 0xFFFFFFFF;
		
		for (int style = 0; style < NUM_STYLES; style++) {
			String family =
				(style == GlkStyle.Preformatted
						? "Courier" : "Helvetica");
			int typeface =
				(style == GlkStyle.Subheader
						? Typeface.BOLD : Typeface.NORMAL);
			styles[style] = new Style(
					family,
					typeface,
					12,
					black,
					white);
		}
	}
	
	public int measureStyle(int style, int hint) {
		throw new StyleMeasurementException();
	}
	
	/**
	 * Returns whether two styles are visibly distinct. It will not be
	 * called from Glk unless the styles are not equal.
	 */
	public boolean distinguishStyles(int style1, int style2) {
		if (style1 >= NUM_STYLES || style2 >= NUM_STYLES) {
			return false;
		}
		if (style1 == GlkStyle.Preformatted
				|| style1 == GlkStyle.Subheader
				|| style2 == GlkStyle.Preformatted
				|| style2 == GlkStyle.Subheader) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the new spans that need to be applied to change to the
	 * given style. Normally this is only
	 */
	public StyleSpan[] getSpans(int style, int textLen, boolean isReverse,
			StyleSpan[] currentSpans) {		

		StyleSpan[] newSpans = styles[style].getSpans(isReverse, textLen);
		
		/* Chances are that the TEXT_SPAN will be different (since you
		 * are supposed to call this only after making sure the new style
		 * is visibly distinct), so for now we don't bother eliminating
		 * a redundant TEXT_SPAN.
		 */
		
		int newBgColor = getIntBgColor(newSpans);
		int oldBgColor = getIntBgColor(currentSpans);
		if (newBgColor == oldBgColor) {
			newSpans[Style.BG_SPAN] = null;
		}

		return newSpans;
	}

	public void applyStyle(int style, boolean isReverse,
			StyleSpan[] currentSpans, int baseBgColor, Spannable text) {

		int textLen = text.length();
		StyleSpan[] newSpans = getSpans(style, textLen, isReverse,
				currentSpans);
		int numSpans = newSpans.length;
		for (int s = 0; s < numSpans; s++) {
			StyleSpan newSpan = newSpans[s];
			StyleSpan oldSpan = currentSpans[s];
			
			if (newSpan != null) {
				if (oldSpan != null) {
					text.setSpan(oldSpan.characterStyle, oldSpan.start,
							textLen, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				currentSpans[s] = newSpan;
				
				if (s == Style.BG_SPAN) {
					if (getIntBgColor(newSpans) == baseBgColor) {
						continue;
					}
				}
				
				text.setSpan(newSpan.characterStyle, textLen, textLen,
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			}
		}
	}
}
