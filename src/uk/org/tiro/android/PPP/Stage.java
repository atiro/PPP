package uk.org.tiro.android.PPP;

public enum Stage {
	FIRSTREADING ( "1st\nReading" ),
	SECONDREADING ( "2nd\nReading" ),
	COMMITTEE ( "Committee\nStage" ),
	REPORT ( "Report\nStage" ),
	THIRDREADING ( "3rd\nReading" ),
	PROGRAMME ( "Programme\nMotion" ),
	ROYAL ( "Royal\nAscent" ),
	EXAMINERS ( "Examiners" ),
	CONSIDERATION ("Consideration\nof\nAmendments"),
	READING ("Second\nReading\nCommittee" ),
	CARRY ( "Carry-over\nmotion" ),
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

