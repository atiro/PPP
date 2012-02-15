package uk.org.tiro.android.PPP;

// Code taken from IBM Developerworks
// http://www.ibm.com/developerworks/opensource/library/x-android/

public enum LordsCommittee {
	DEBATE ("Debate"),
	ESTIMATED_RISING ("Estimated Rising Time"),
	LEGISLATION ("Legislation"),
	ORAL_QUESTIONS ("Oral Questions"),
	SHORT_DEBATE ("Short Debate"),
	STATEMENT ("Statement"),
	OTHER ("Other");

        private final String name;

        LordsCommittee(String name) {
                this.name = name;
        }

        public String toString() { return name; }

	public String toOrdinal() {
		return Integer.toString(this.ordinal());
	}
}
