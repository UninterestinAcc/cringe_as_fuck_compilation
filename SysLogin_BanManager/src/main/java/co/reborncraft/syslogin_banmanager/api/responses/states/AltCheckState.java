package co.reborncraft.syslogin_banmanager.api.responses.states;

public enum AltCheckState {
	DATABASE_ERROR("\u00a7cDatabase error."),
	ALTS_FOUND(""),
	ALTS_NOT_FOUND("\u00a7cSorry, no alts found for that user.");

	private final String message;

	AltCheckState(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
