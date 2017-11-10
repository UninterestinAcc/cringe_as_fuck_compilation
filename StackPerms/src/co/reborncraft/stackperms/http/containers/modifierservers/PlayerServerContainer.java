package co.reborncraft.stackperms.http.containers.modifierservers;

import co.reborncraft.stackperms.StackPerms;
import co.reborncraft.stackperms.http.StackPermsHTTPServerlet;
import co.reborncraft.stackperms.api.Group;
import co.reborncraft.stackperms.api.SavePlayer;
import co.reborncraft.stackperms.api.structure.Named;
import co.reborncraft.stackperms.http.RequestProcessor;
import co.reborncraft.stackperms.http.SecuredHttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerServerContainer extends SecuredHttpHandler {
	public PlayerServerContainer(StackPermsHTTPServerlet stackPermsHTTPServerlet) {
		super(stackPermsHTTPServerlet);
	}

	@Override
	public String processRequest(HttpExchange exch, final String requestURI) throws IOException {
		String out = "<a href=\"/\">Go Back</a><br/>";
		if (requestURI.startsWith("/players")) {
			Map.Entry<String, Boolean> ret = RequestProcessor.typeRoot(exch, SavePlayer.class);
			if (ret.getValue()) {
				return null;
			}
			out += ret.getKey();
			out += "<form method=\"post\" action=\"/players\">";
			for (String playerName : StackPerms.getInterface().getPlayers().stream().map(Named::getName).sorted(String::compareTo).collect(Collectors.toList())) {
				out += "<input type=\"checkbox\" name=\"SavePlayer." + playerName + "\"><a href=\"/player/" + playerName + "\">" + playerName + "</a><br/>";
			}
			out += "<input type=\"submit\" name=\"action\" value=\"Delete Selected\"/><br/><hr/><br/>" +
					"<input type=\"text\" name=\"name\"/><input type=\"submit\" name=\"action\" value=\"Create New\"/>";
		} else if (requestURI.matches("^/player/(\\w{3,64})")) {
			RequestProcessor.typeAndVarName(exch, SavePlayer.class);
			String playerName = requestURI.replaceAll("^/player/(\\w{3,64})", "$1");
			Optional<SavePlayer> savePlayer;
			List<Group> defaultGroupsOfPlayer = new ArrayList<>();
			List<Group> explicitGroupsOfPlayer = new ArrayList<>();
			savePlayer = Optional.of(StackPerms.getInterface().getOrCreateSavePlayer(playerName));
			savePlayer.ifPresent(sp -> {
				explicitGroupsOfPlayer.addAll(sp.getExplicitGroups());
				defaultGroupsOfPlayer.addAll(sp.getDefaultGroups());
			});
			boolean savePlayerIsPresent = savePlayer.isPresent();
			String prefix = ifNullThenMakeEmpty(savePlayerIsPresent ? savePlayer.get().getPrefix() : "");
			String tabPrefix = ifNullThenMakeEmpty(savePlayerIsPresent ? savePlayer.get().getTabPrefix() : "");
			String suffix = ifNullThenMakeEmpty(savePlayerIsPresent ? savePlayer.get().getSuffix() : "");

			out += "<h1>" + playerName.toUpperCase() + "</h1><form method=\"post\" action=\"/player/" + playerName + "\">";
			out += "Prefix <input type=\"text\" name=\"prefix\" value=\"" + prefix + "\"/><br/>";
			out += "Tab Prefix <input type=\"text\" name=\"tabPrefix\" value=\"" + tabPrefix + "\"/><br/>";
			out += "Suffix <input type=\"text\" name=\"suffix\" value=\"" + suffix + "\"/><br/>";
			out += "<input type=\"submit\" name=\"action\" value=\"Set Information\"/><br/><hr/><br/>";
			out += "</form><form method=\"post\" action=\"/player/" + playerName + "\">" +
					"<div style=\"background-color: #afa\">" +
					"<h2>Add group</h2>" +
					"<select name=\"addGroup\">";
			for (Group group : StackPerms.getInterface().getDeclaredGroups().stream().sorted((g1, g2) -> String.CASE_INSENSITIVE_ORDER.compare(g1.getName(), g2.getName())).collect(Collectors.toList())) {
				if (!explicitGroupsOfPlayer.contains(group) && !defaultGroupsOfPlayer.contains(group)) {
					String gn = group.getName();
					out += "<option value=\"" + gn + "\">" + gn + "</option>";
				}
			}
			out += "</select><input type=\"submit\" name=\"action\" value=\"Add Group\"/>";
			out += "</div></form><br/><hr/><br/>" +
					"<form method=\"post\" action=\"/player/" + playerName + "\">" +
					"<div style=\"background-color: #faa\">";
			out += "<h2>Remove group</h2>" +
					"<select name=\"removeGroup\">";
			for (Group group : explicitGroupsOfPlayer.stream().sorted((g1, g2) -> String.CASE_INSENSITIVE_ORDER.compare(g1.getName(), g2.getName())).collect(Collectors.toList())) {
				String gn = group.getName();
				out += "<option value=\"" + gn + "\">" + gn + "</option>";
			}
			out += "</select><br/><i>The player has the following default groups: <b>";
			out += defaultGroupsOfPlayer.stream().sorted((g1, g2) -> String.CASE_INSENSITIVE_ORDER.compare(g1.getName(), g2.getName())).map(Named::getName).collect(Collectors.joining(", "));
			out += "</b>.</i><br/><input type=\"submit\" name=\"action\" value=\"Remove Group\"/>";
			out += "</div></form>" +
					"<form method=\"post\" action=\"/player/" + playerName + "\">" +
					"<div style=\"background-color: #afa\">" +
					"<h2>Add Permissions</h2>" +
					"Add Permissions<br/>" +
					"<textarea name=\"addPermissions\" style=\"width: 300px; height: 600px\"></textarea><br/>" +
					"<input type=\"submit\" name=\"action\" value=\"Add Permissions\"/>";
			out += "</div></form><br/><hr/><br/>" +
					"<form method=\"post\" action=\"/player/" + playerName + "\">" +
					"<div style=\"background-color: #faa\">" +
					"<h2>Delete Permissions</h2>";
			if (savePlayerIsPresent) {
				List<String> perms = StackPerms.getInterface().serializePermissions(savePlayer.get().getExplicitPermissions());
				perms.sort(Comparator.naturalOrder());
				if (perms.isEmpty()) {
					out += "<i>The player has no explicit permissions.</i>";
				} else {
					for (String perm : perms) {
						out += "<input type=\"checkbox\" name=\"Permission." + perm + "\">" + perm + "</input>";
					}
				}
			}
			out += "<br/><input type=\"submit\" name=\"action\" value=\"Delete Permissions\"/><br/>" +
					"</div></form><br/><hr/><br/>" +
					"<div style=\"background-color: #faa\">" +
					"<h2>Delete this player</h2>" +
					"<form method=\"post\" action=\"/players\">" +
					"<input style=\"visibility: hidden\" type=\"checkbox\" name=\"SavePlayer." + playerName + "\" checked/>" +
					"<input style=\"visibility: hidden\" type=\"text\" name=\"action\" value=\"Delete Selected\"/><br/>" +
					"<input type=\"submit\" name=\"action\" value=\"Delete\"/>" +
					"</div>";
		} else {
			return "<b>Invalid player name.</b>";
		}
		return out + "</form>";
	}
}
