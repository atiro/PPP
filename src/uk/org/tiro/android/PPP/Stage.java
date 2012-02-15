package uk.org.tiro.android.PPP;

public enum Stage {
	FIRSTREADING ( "1st Reading" ),
	SECONDREADING ( "2nd Reading" ),
	COMMITTEE ( "Committee Stage" ),
	REPORT ( "Report Stage" ),
	THIRDREADING ( "3rd Reading" ),
	PROGRAMME ( "Programme Motion" ),
	ROYAL ( "Royal Ascent" ),
	EXAMINERS ( "Examiners" ),
	CONSIDERATION ("Consideration of Amendments"),
	READING ("Second Reading Committee" ),
	UNKNOWN ("Unknown");


	private final String name;

	Stage(String name) {
		this.name = name;
	}

	public String toString() { return name; }

        public String toOrdinal() {
                return Integer.toString(this.ordinal());
        }

}

