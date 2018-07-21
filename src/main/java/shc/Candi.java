/*
 * selected-historical-ciphers
 * 
 * Copyright (c) 2018, Milten Plescott. All rights reserved.
 * 
 * SPDX-License-Identifier:    BSD-3-Clause
 */

package shc;

import shc.fersen.attack.Mask;

@Deprecated
public class Candi {

	private final String currentPartWhole;
	private final Mask maskCurrentPart;
	private final String strToEval;
	private final String generatedString;

	// strToEval = generatedString plugged into currentPartWhole
	// everyone coming here has score of strToEval = 1.0
	public Candi(String currentPartWhole, Mask maskCurrentPart, String strToEval, String generatedString) {
		this.currentPartWhole = currentPartWhole;
		this.maskCurrentPart = maskCurrentPart;
		this.strToEval = strToEval;
		this.generatedString = generatedString;
	}

	public String getCurrentPartWhole() {
		return currentPartWhole;
	}

	public Mask getMaskCurrentPart() {
		return maskCurrentPart;
	}

	public String getStrToEval() {
		return strToEval;
	}

	public String getGeneratedString() {
		return generatedString;
	}

}
