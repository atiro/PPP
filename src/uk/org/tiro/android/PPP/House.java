package uk.org.tiro.android.PPP;

// Code taken from IBM Developerworks
// http://www.ibm.com/developerworks/opensource/library/x-android/

public enum House {
        COMMONS ("House of Commons", "Commons"),
        LORDS ("House of Lords", "Lords"),
	BOTH ("Both Houses", "Both"),
	NEITHER ("Neither House", "");

        private final String name;
        private final String short_name;

        House(String name, String short_name) {
                this.name = name;
		this.short_name = short_name;
        }

        public String toString() { return name; }
        public String toShort() { return short_name; }

	public String toOrdinal() {
		return Integer.toString(this.ordinal());
	}
}
