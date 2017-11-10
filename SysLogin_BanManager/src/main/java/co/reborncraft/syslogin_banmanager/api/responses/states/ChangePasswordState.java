package co.reborncraft.syslogin_banmanager.api.responses.states;

public enum ChangePasswordState {
	DATABASE_ERROR("\u00a7cDatabase error."),
	SUCCESSFUL_CHANGE("\u00a7aYour password has been successfully changed!"),
	SAME_PASSWORD("\u00a7cYour new password is identical to the old one. Aborting.");

	private final String message;

	ChangePasswordState(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
