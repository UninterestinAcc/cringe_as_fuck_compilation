package co.reborncraft.stackperms.api.structure;

public interface Fixable {
	/**
	 * Gets the chat prefix of this group
	 */
	String getPrefix();

	/**
	 * Sets the chat prefix of this group
	 */
	void setPrefix(String prefix);

	/**
	 * Gets the chat suffix of this group
	 */
	String getSuffix();

	/**
	 * Sets the chat suffix of this group
	 */
	void setSuffix(String suffix);

	/**
	 * Gets the tab prefix of this group (limited to 16 characters INCLUDING FORMATTING CODES)
	 */
	String getTabPrefix();

	/**
	 * Sets the tab prefix of this group.
	 *
	 * @throws IllegalArgumentException If the tab prefix's length is > 16
	 */
	void setTabPrefix(String tabPrefix);
}
