package shc.fersen.attack;

public enum EveryOtherCharacter {

	// Fersen cipher: odd ~ CT, even ~ PT
	EVEN(1), ODD(0); // first odd  element in an array has index 0
	// first even element in an array has index 1
	// I do realize that 1 is not an even number
	public final int startingIndex;

	private EveryOtherCharacter(int startingIndex) {
		this.startingIndex = startingIndex;
	}

	public EveryOtherCharacter getTheOtherOne() {
		if (this == ODD) {
			return EVEN;
		}
		else {
			return ODD;
		}
	}

}
