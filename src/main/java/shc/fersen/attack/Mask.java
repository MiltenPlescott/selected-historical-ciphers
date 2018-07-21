/*
 * selected-historical-ciphers
 * 
 * Copyright (c) 2018, Milten Plescott. All rights reserved.
 * 
 * SPDX-License-Identifier:    BSD-3-Clause
 */

package shc.fersen.attack;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a mask for a single paragraph.
 *
 */
public class Mask {

	/**
	 * CT - cipherText<br>PT - plainText<br>ST - solvedText
	 */
	protected enum Encryption {
		CT, PT, ST
	}

	private List<Item> items;

	public Mask() {
	}

	// initialize mask to: every odd char -> CT,  every even char -> PT
	public Mask(int paragraphLength) {
		if (paragraphLength > 0) {
			this.items = new ArrayList<>(paragraphLength);
			for (int i = 0; i < paragraphLength; i++) {
				if (i % 2 == 0) {
					this.items.add(new Item(Encryption.CT, (i / 2) % FersenAttack.PSWD_LENGTH));
				}
				else {
					this.items.add(new Item(Encryption.PT, null));
				}
			}
		}
	}

	// from inclusive to the end
	public Mask getSubMask(int from) {
		return getSubMask(from, this.items.size());
	}

	// from inclusive, to exclusive
	public Mask getSubMask(int from, int to) {
		Mask mask = new Mask();
		mask.setItems(this.items.subList(from, to));
		return mask;
	}

	public int getLength() {
		return this.items.size();
	}

	// returns -1 if there is no such element
	public int getLastIndexOf(Encryption encryption) {
		int index = -1;
		for (int i = 0; i < this.items.size(); i++) {
			if (this.items.get(i).encryption == encryption) {
				index = i;
			}
		}
		return index;
	}

	// returns number of 'encryption' in 'this' starting at the index from (inclusive) going to the end
	public int getCount(Encryption encryption, int from) {
		int count = 0;
		for (int i = from; i < this.items.size(); i++) {
			if (this.items.get(i).encryption == encryption) {
				count++;
			}
		}
		return count;
	}

	// get index of nth char with encryption e
	public int getNthItemsIndex(int n, Encryption e) {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).encryption == e) {
				n--;
			}
			if (n == 0) {
				return i;
			}
		}
		return -1;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public class Item {

		private Encryption encryption;
		private Integer pswdIndex; // <0, pswdLength> OR null for PT

		public Item(Encryption encryption, Integer pswdIndex) {
			this.encryption = encryption;
			this.pswdIndex = pswdIndex;
		}

		public Encryption getEncryption() {
			return encryption;
		}

		public void setEncryption(Encryption encryption) {
			this.encryption = encryption;
		}

		public Integer getPswdIndex() {
			return pswdIndex;
		}

		public void setPswdIndex(Integer pswdIndex) {
			this.pswdIndex = pswdIndex;
		}

	}

}
