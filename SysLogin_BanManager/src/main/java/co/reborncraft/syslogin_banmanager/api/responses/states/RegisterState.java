package co.reborncraft.syslogin_banmanager.api.responses.states;

public enum RegisterState {
	DATABASE_ERROR("\u00a7cDatabase error."),
	SUCCESSFUL_REGISTER("\u00a7aWelcome to Reborncraft, you registered successfully."),
	ALT_DETECTED("\u00a7cPlease ask staff to register more accounts.");

	private final String message;

	RegisterState(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
