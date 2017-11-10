package org.reborncraft.gtowny.chat;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatComponent {
	private String string = "";
	private String json = "{\"text\":\"\"";

	public enum ClickAction {
		OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND
	}

	public enum HoverAction {
		SHOW_TEXT, SHOW_ACHIEVEMENT
	}

	public ChatComponent append(String str) {
		json += "}, {" + textComponent(str);
		return this;
	}

	public ChatComponent append(ChatColor col) {
		json += "}, {" + textComponent(col + "");
		return this;
	}

	public ChatComponent setColour(ChatColor colour) {
		if (!json.endsWith(", \"color\":\"[a-z_]+\"")) {
			json += ", \"color\":\"" + (colour != ChatColor.MAGIC ? colour.asBungee().getName().toLowerCase() : "obfuscated") + "\"";
			string += colour;
		}
		return this;
	}

	private String textComponent(String str) {
		string += str;
		return "\"text\": \"" + sanitize(str) + "\"";
	}

	private String sanitize(String str) {
		return str.replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\");
	}

	public ChatComponent appendHoverable(String str, HoverAction action, String actionStr) {
		json += "}, {";
		json += textComponent(str);
		setHoverable(action, actionStr);
		return this;
	}

	public ChatComponent setHoverable(HoverAction action, String actionStr) {
		json += ", \"hoverEvent\":{";
		json += "\"action\":\"" + action.toString().toLowerCase() + "\",";
		json += "\"value\":\"" + sanitize(actionStr) + "\"";
		json += "}";
		return this;
	}

	public ChatComponent appendClickable(String str, ClickAction action, String actionStr) {
		json += "}, {";
		json += textComponent(str);
		setClickable(action, actionStr);
		return this;
	}

	public ChatComponent setClickable(ClickAction action, String actionStr) {
		json += ", \"clickEvent\":{";
		json += "\"action\":\"" + action.toString().toLowerCase() + "\",";
		json += "\"value\":\"" + sanitize(actionStr) + "\"";
		json += "}";
		return this;
	}

	public ChatComponent appendComponent(ChatComponent component) {
		json += "}, " + component.asExtraList();
		string += component.toString();
		return this;
	}

	@Deprecated
	public ChatComponent optimizeJson() { // Do the optimization proactively instead of this buggy fuckpile.
		String[] extraElements = this.json.split("\\}, \\{\"text\": \"");
		List<String> json = new ArrayList<>();
		json.add(extraElements[0]);
		extraElements = Arrays.copyOfRange(extraElements, 1, extraElements.length);
		String cache = "";
		String lastEvent = "\"";
		for (String element : extraElements) {
			String[] currentEvent = element.split("\", \"");
			if (currentEvent.length > 2) {
				currentEvent = new String[]{
						currentEvent[0],
						String.join("\", \"", Arrays.copyOfRange(currentEvent, 1, currentEvent.length))
				};
			}
			if (currentEvent.length == 2 && currentEvent[1].equals(lastEvent)) {
				cache += currentEvent[0];
			} else if (lastEvent.equals("\"") && currentEvent[0].endsWith("\"")) {
				cache += currentEvent[0].substring(0, currentEvent[0].length() - 1);
			} else {
				json.add(cache + (!lastEvent.equals("\"") ? "\", \"" + lastEvent : "\""));
				cache = currentEvent.length >= 1 ? currentEvent[0] : "";
				lastEvent = currentEvent.length == 2 ? currentEvent[1] : "";
			}
		}
		json.add(cache + (lastEvent.isEmpty() ? "" : "\", \"") + lastEvent);

		this.json = String.join("}, {\"text\": \"", json).replaceAll("\"\", \"", "\"").replaceAll("\\{\"text\":\"\"\\}, \\{", "{");
		return this;
	}

	public String asJson() {
		return ("{\"text\":\"\", \"extra\": [" + json + "}]}").replaceAll("&quot;", "\\\"");
	}

	public String asExtraList() { // Pop first(empty) component
		return json;
	}

	@Override
	public String toString() {
		return string;
	}
}
