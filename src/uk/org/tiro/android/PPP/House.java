package uk.org.tiro.android.PPP;

// Code taken from IBM Developerworks
// http://www.ibm.com/developerworks/opensource/library/x-android/

public enum House {
        COMMONS ("House of Commons"),
        LORDS ("House of Lords"),
	BOTH ("Both Houses"),
	NEITHER ("Neither House");

        private final String name;

        House(String name) {
                this.name = name;
        }

        public String toString() { return name; }

	public String toOrdinal() {
		return Integer.toString(this.ordinal());
	}
}
