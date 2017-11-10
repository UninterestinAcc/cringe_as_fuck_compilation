package co.reborncraft.stackperms.http.containers.modifierservers;

import co.reborncraft.stackperms.StackPerms;
import co.reborncraft.stackperms.http.StackPermsHTTPServerlet;
import co.reborncraft.stackperms.api.Group;
import co.reborncraft.stackperms.api.structure.Named;
import co.reborncraft.stackperms.http.RequestProcessor;
import co.reborncraft.stackperms.http.SecuredHttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class GroupServerContainer extends SecuredHttpHandler {
	public GroupServerContainer(StackPermsHTTPServerlet stackPermsHTTPServerlet) {
		super(stackPermsHTTPServerlet);
	}

	@Override
	public String processRequest(HttpExchange exch, final String requestURI) throws IOException {
		String out = "<a href=\"/\">Go Back</a><br/>";
		if (requestURI.startsWith("/groups")) {
			Map.Entry<String, Boolean> ret = RequestProcessor.typeRoot(exch, Group.class);
			if (ret.getValue()) {
				return null;
			}
			out += ret.getKey();
			out += "<form method=\"post\" action=\"/groups\">";
			for (String groupName : StackPerms.getInterface().getDeclaredGroups().stream().map(Named::getName).sorted(String::compareTo).collect(Collectors.toList())) {
				out += "<input type=\"checkbox\" name=\"Group." + groupName + "\"><a href=\"/group/" + groupName + "\">" + groupName + "</a><br/>";
			}
			out += "<input type=\"submit\" name=\"action\" value=\"Delete Selected\"/><br/><hr/><br/>" +
					"<input type=\"text\" name=\"name\"/><input type=\"submit\" name=\"action\" value=\"Create New\"/>";
		} else if (requestURI.matches("^/group/(\\w{3,64})")) {
			RequestProcessor.typeAndVarName(exch, Group.class);
			String groupName = requestURI.replaceAll("^/group/(\\w{3,64})", "$1");
			Optional<Group> group = StackPerms.getInterface().getGroupByName(groupName);
			boolean groupIsPresent = group.isPresent();
			String prefix = ifNullThenMakeEmpty(groupIsPresent ? group.get().getPrefix() : "");
			String tabPrefix = ifNullThenMakeEmpty(groupIsPresent ? group.get().getTabPrefix() : "");
			String suffix = ifNullThenMakeEmpty(groupIsPresent ? group.get().getSuffix() : "");

			out += "<h1>" + groupName.toUpperCase() + "</h1><form method=\"post\" action=\"/group/" + groupName + "\">";
			out += "Prefix <input type=\"text\" name=\"prefix\" value=\"" + prefix + "\"/><br/>";
			out += "Tab Prefix <input type=\"text\" name=\"tabPrefix\" value=\"" + tabPrefix + "\"/><br/>";
			out += "Suffix <input type=\"text\" name=\"suffix\" value=\"" + suffix + "\"/><br/>";
			out += "<input type=\"submit\" name=\"action\" value=\"Set Information\"/>" +
					"</form><br/><hr/><br/>" +
					"<form method=\"post\" action=\"/group/" + groupName + "\">" +
					"<div style=\"background-color: #afa\">" +
					"<h2>Add Permissions</h2>" +
					"Add Permissions<br/>" +
					"<textarea name=\"addPermissions\" style=\"width: 300px; height: 600px\"></textarea><br/>" +
					"<input type=\"submit\" name=\"action\" value=\"Add Permissions\"/>";
			out += "</div></form><br/><hr/><br/>" +
					"<form method=\"post\" action=\"/group/" + groupName + "\">" +
					"<div style=\"background-color: #faa\">" +
					"<h2>Delete Permissions</h2>";
			if (groupIsPresent) {
				List<String> perms = StackPerms.getInterface().serializePermissions(group.get().getPermissions());
				perms.sort(Comparator.naturalOrder());
				for (String perm : perms) {
					out += "<input type=\"checkbox\" name=\"Permission." + perm + "\">" + perm + "</input>";
				}
			}
			out += "<br/><input type=\"submit\" name=\"action\" value=\"Delete Permissions\"/><br/>" +
					"</div></form><br/><hr/><br/>" +
					"<div style=\"background-color: #faa\">" +
					"<h2>Delete this group</h2>" +
					"<form method=\"post\" action=\"/groups\">" +
					"<input style=\"visibility: hidden\" type=\"checkbox\" name=\"Group." + groupName + "\" checked/>" +
					"<input style=\"visibility: hidden\" type=\"text\" name=\"action\" value=\"Delete Selected\"/><br/>" +
					"<input type=\"submit\" value=\"Delete\"/>" +
					"</div>";
		} else {
			return "<b>Invalid group name.</b>";
		}
		return out + "</form>";
	}
}
