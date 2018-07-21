/*
 * selected-historical-ciphers
 * 
 * Copyright (c) 2018, Milten Plescott. All rights reserved.
 * 
 * SPDX-License-Identifier:    BSD-3-Clause
 */

package shc.fersen.attack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Candidate {

	private final String candidateWhole;
	private final String candidateCT;
	private final String candidatePT;
	private final String appropriatePartFromOrigTextWhole;
	private final String appropriatePartFromOrigTextCT;
	private final String appropriatePartFromOrigTextPT;
	private final Double score; // candidates after being split retain their old score
	private Double avgScore; // candidates after being split retain their old score
	private int pswdStartIndex;	// <0 ; pswdLengthGuess - 1> index of the character of password corresponding to the candidateCT.charAt(0)
	// (indexo of password character that was used to encrypt the very first character of candidateCT)

	// candidateWhole and appropriatePartFromOrigTextWhole are assumed to have same length
	public Candidate(String candidateWhole, String appropriatePartFromOrigTextWhole, int pswdStartIndex, Double score) {
		this.candidateWhole = candidateWhole;
		this.candidateCT = Helper.getEveryOtherChar(candidateWhole, EveryOtherCharacter.ODD);
		this.candidatePT = Helper.getEveryOtherChar(candidateWhole, EveryOtherCharacter.EVEN);

		this.appropriatePartFromOrigTextWhole = appropriatePartFromOrigTextWhole;
		this.appropriatePartFromOrigTextCT = Helper.getEveryOtherChar(appropriatePartFromOrigTextWhole, EveryOtherCharacter.ODD);
		this.appropriatePartFromOrigTextPT = Helper.getEveryOtherChar(appropriatePartFromOrigTextWhole, EveryOtherCharacter.EVEN);

		this.pswdStartIndex = pswdStartIndex;
		this.score = score;
	}

	public Candidate leftPartCandidate(int indexPartCTToRemove) {
		if (indexPartCTToRemove > 0) {
			return new Candidate(this.candidateWhole.substring(0, 2 * indexPartCTToRemove), this.appropriatePartFromOrigTextWhole.substring(0, 2 * indexPartCTToRemove), this.pswdStartIndex, this.score);
		}
		else {
			return null;
		}
	}

	public Candidate rightPartCandidate(int indexPartCTToRemove) {
		if (indexPartCTToRemove < (this.candidateCT.length() - 1)) {
			return new Candidate(this.candidateWhole.substring(2 * (indexPartCTToRemove + 1)), this.appropriatePartFromOrigTextWhole.substring(2 * (indexPartCTToRemove + 1)), this.pswdStartIndex + indexPartCTToRemove + 1, this.score);
		}
		else {
			return null;
		}
	}

	// indexPartCTToRemove = <0 ; oldCandidate.candidateCT.length - 1>
	// splitCandidate('LeRoIpEnSe', 0) returns -> [null ; 'RoIpEnSe']
	// splitCandidate('LeRoIpEnSe', 2) returns -> ['LeRo' ; 'EnSe']
	public List<Candidate> splitCandidate(int indexPartCTToRemove) {
		assert indexPartCTToRemove >= 0 && indexPartCTToRemove < this.candidateCT.length() : "index to split candidate at is out of range";

		List<Candidate> newCandidates = new ArrayList<>(2);
		newCandidates.add(leftPartCandidate(indexPartCTToRemove)); // left
		newCandidates.add(rightPartCandidate(indexPartCTToRemove)); // right

		return newCandidates;
	}

	// checks for the same PT, CT characters (table cannot have both characters same in one cell)
	public boolean hasSamePTCT() {
		for (int i = 0; i < candidateCT.length(); i++) {
			if (candidateCT.charAt(i) == appropriatePartFromOrigTextCT.charAt(i)) {
				return true;
			}
		}
		return false;
	}

	// key   -> appropriatePartFromOrigTextWhole
	// value -> list of all FersenAttackCandidates, that have the key
	public static Map<String, List<Candidate>> getMapCandidates(List<Candidate> listCandidates) {
		Map<String, List<Candidate>> map = new HashMap<>();
		for (Candidate candidate : listCandidates) {
			List<Candidate> listValue = map.get(candidate.appropriatePartFromOrigTextWhole);
			if (listValue == null) { // map does not have candidate.appropriatePartFromOrigTextWhole key yet
				listValue = new ArrayList<>();
				listValue.add(candidate);
				map.put(candidate.appropriatePartFromOrigTextWhole, listValue);
			}
			else { // map already contains key
				listValue.add(candidate);
				map.put(candidate.appropriatePartFromOrigTextWhole, listValue);
			}
		}
		return map;
	}

	public int getPswdStartIndex() {
		return pswdStartIndex;
	}

	public void setPswdStartIndex(int pswdStartIndex) {
		this.pswdStartIndex = pswdStartIndex;
	}

	public Double getScore() {
		return score;
	}

	public Double getAvgScore() {
		return avgScore;
	}

	public void setAvgScore(Double avgScore) {
		this.avgScore = avgScore;
	}

	public String getCandidateWhole() {
		return candidateWhole;
	}

	public String getCandidateCT() {
		return candidateCT;
	}

	public String getCandidatePT() {
		return candidatePT;
	}

	public String getAppropriatePartFromOrigTextWhole() {
		return appropriatePartFromOrigTextWhole;
	}

	public String getAppropriatePartFromOrigTextCT() {
		return appropriatePartFromOrigTextCT;
	}

	public String getAppropriatePartFromOrigTextPT() {
		return appropriatePartFromOrigTextPT;
	}

	@Override
	public String toString() {
		return "candidate: " + candidateWhole + ", score: " + score;
	}

	public String toStringWhole() {
		return "whole text candidate: " + candidateWhole + ", original: " + appropriatePartFromOrigTextWhole + ", score: " + score;
	}

	public String toStringOnlyCT() {
		return "CT candidate: " + candidateCT + ", original: " + appropriatePartFromOrigTextCT + ", score: " + score;
	}

	public String toStringOnlyPT() {
		return "PT candidate: " + candidatePT + ", original: " + appropriatePartFromOrigTextPT + ", score: " + score;
	}

}
