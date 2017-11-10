package co.reborncraft.syslogin_banmanager.api.responses.states;

public enum LoginState {
	DATABASE_ERROR("\u00a7cDatabase error."),
	SUCCESSFUL_LOGIN("\u00a7aWelcome back, you logged in successfully."),
	BAD_PASSWORD("\u00a7cThe password is incorrect."),
	BAD_USERNAME("\u00a7cUsername capitalization mismatched."),
	BAD_IP("\u00a7cYou have IP protection enabled, please log in from the designated IP address(es).");

	private final String message;

	LoginState(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
