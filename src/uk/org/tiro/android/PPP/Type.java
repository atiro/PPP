package uk.org.tiro.android.PPP;

public enum Type {

	GOVERNMENT ( "Government Bill" ),
	CONSOLIDATION ( "Consolidation Bill" ),
	PMB_BALLOT ( "Private Members Bill (Balloted)" ),
	PMB_TEN ("Private Members Bill (Ten Minute Rule)" ),
	PMB_PRESENTATION ("Private Members Bill (Presentation)" ),
	PMB_LORDS ("Private Members Bill (House of Lords)" ),
	PRIVATE  ("Private Members Bill" ),
	HL ( "House of Lords Bill" ),
	HYBRID ("Hybrid Bill"),
	UNKNOWN ("Unknown Bill");

	private final String name;

	Type(String name) {
		this.name = name;
	}

	public String toString() { return name; }

        public String toOrdinal() {
                return Integer.toString(this.ordinal());
        }

}

