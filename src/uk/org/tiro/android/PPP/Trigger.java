package uk.org.tiro.android.PPP;

// Code taken from IBM Developerworks
// http://www.ibm.com/developerworks/opensource/library/x-android/

public enum Trigger {
	ACT ("Act"),
	BILL ("Bill"),
	SI ("Stat. Instru."),
	DSI ("Draft Stat. Instu."),
	SELECT ("Select Committee"),
	MAIN ("Debate"),
	GENERAL ("General Committee"),
	WESTMINSTER ("Westminster Hall"),
	GRAND ("Grand Committee"),
	REPORT ("Report");


        private final String name;

        Trigger(String name) {
                this.name = name;
        }

        public String toString() { return name; }

	public String toOrdinal() {
		return Integer.toString(this.ordinal());
	}
}
