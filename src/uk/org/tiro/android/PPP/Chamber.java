package uk.org.tiro.android.PPP;

// Code taken from IBM Developerworks
// http://www.ibm.com/developerworks/opensource/library/x-android/

public enum Chamber {
        MAIN ("Main Chamber"),
	SELECT ("Select Committee"),
	WESTMINSTER ("Westminster Hall"),
	GRAND ("Grand Committee"),
	GENERAL ("General Committee"),
	OTHER ("Other");

        private final String name;

        Chamber(String name) {
                this.name = name;
        }

        public String toString() { return name; }

	public String toOrdinal() {
		return Integer.toString(this.ordinal());
	}
}
