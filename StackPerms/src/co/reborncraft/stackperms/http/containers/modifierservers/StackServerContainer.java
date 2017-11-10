package co.reborncraft.stackperms.http.containers.modifierservers;

import co.reborncraft.stackperms.StackPerms;
import co.reborncraft.stackperms.http.StackPermsHTTPServerlet;
import co.reborncraft.stackperms.api.Group;
import co.reborncraft.stackperms.api.Stack;
import co.reborncraft.stackperms.api.structure.Named;
import co.reborncraft.stackperms.http.RequestProcessor;
import co.reborncraft.stackperms.http.SecuredHttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StackServerContainer extends SecuredHttpHandler {
	public StackServerContainer(StackPermsHTTPServerlet stackPermsHTTPServerlet) {
		super(stackPermsHTTPServerlet);
	}

	@Override
	public String processRequest(HttpExchange exch, String requestURI) throws IOException {
		String out = "<a href=\"/\">Go Back</a><br/>";
		if (requestURI.startsWith("/stacks")) {
			Map.Entry<String, Boolean> ret = RequestProcessor.typeRoot(exch, co.reborncraft.stackperms.api.Stack.class);
			if (ret.getValue()) {
				return null;
			}
			out += ret.getKey();
			out += "<form method=\"post\" action=\"/stacks\">";
			for (String stackName : StackPerms.getInterface().getDeclaredStacks().stream().map(Named::getName).sorted(String::compareTo).collect(Collectors.toList())) {
				out += "<input type=\"checkbox\" name=\"Stack." + stackName + "\"><a href=\"/stack/" + stackName + "\">" + stackName + "</a><br/>";
			}
			out += "<input type=\"submit\" name=\"action\" value=\"Delete Selected\"/><br/><hr/><br/>" +
					"<input type=\"text\" name=\"name\"/><input type=\"submit\" name=\"action\" value=\"Create New\"/>";
		} else if (requestURI.matches("^/stack/(\\w{3,64})")) {
			RequestProcessor.typeAndVarName(exch, co.reborncraft.stackperms.api.Stack.class);
			String stackName = requestURI.replaceAll("^/stack/(\\w{3,64})", "$1");
			out += "<h1>" + stackName.toUpperCase() + "</h1><form method=\"post\" action=\"/stack/" + stackName + "\">";
			out += "Default Group <select name=\"defaultGroup\">";
			Optional<Stack> stack = StackPerms.getInterface().getStackByName(stackName);
			if (stack.isPresent()) {
				Optional<Group> defGroup = stack.get().getDefaultGroup();
				boolean defGroupIsPresent = defGroup.isPresent();
				out += "<option value=\"Group.x\" " + (defGroupIsPresent ? "" : "selected") + ">None</option>";
				for (Group group : stack.get().getGroups().stream().sorted((g1, g2) -> String.CASE_INSENSITIVE_ORDER.compare(g1.getName(), g2.getName())).collect(Collectors.toList())) {
					out += "<option value=\"Group." + group.getName() + "\"" +
							(defGroupIsPresent && defGroup.get() == group ? " selected" : "")
							+ ">" + group.getName() + "</option>";
				}
			} else {
				out += "<option value=\"Group.x\" selected>None</option>";
			}
			out += "</select><br/>";
			out += "Weight <input type=\"number\" name=\"weight\" value=\"" + (stack.isPresent() ? stack.get().getWeight() : "") + "\"/><br/>";
			List<Group> groupsInStack = stack.isPresent() ? stack.get().getGroups() : Collections.emptyList();
			if (stack.isPresent()) {
				out += "<h3>Change Group Order</h3>";
				if (groupsInStack.size() >= 1) {
					for (Group group : groupsInStack) {
						out += "<input type=\"number\" name=\"Group." + group.getName() + "\" value=\"" + group.getRank() + "\">" + group.getName() + "</input><br/>";
					}
				} else {
					out += "<i>No groups in stack.</i><br/>";
				}
			}
			out += "<input type=\"submit\" name=\"action\" value=\"Set Information\"/><br/><hr/><br/>";
			out += "</form><form method=\"post\" action=\"/stack/" + stackName + "\">" +
					"<div style=\"background-color: #afa\">" +
					"<h2>Add group</h2>" +
					"<select name=\"addGroup\">";
			for (Group group : StackPerms.getInterface().getDeclaredGroups().stream().sorted((g1, g2) -> String.CASE_INSENSITIVE_ORDER.compare(g1.getName(), g2.getName())).collect(Collectors.toList())) {
				if (!groupsInStack.contains(group)) {
					String gn = group.getName();
					out += "<option value=\"" + gn + "\">" + gn + "</option>";
				}
			}
			out += "</select><input type=\"submit\" name=\"action\" value=\"Add Group\"/>";
			out += "</div></form><br/><hr/><br/>" +
					"<form method=\"post\" action=\"/stack/" + stackName + "\">" +
					"<div style=\"background-color: #faa\">";
			out += "<h2>Remove group</h2>" +
					"<select name=\"removeGroup\">";
			for (Group group : groupsInStack.stream().sorted((g1, g2) -> String.CASE_INSENSITIVE_ORDER.compare(g1.getName(), g2.getName())).collect(Collectors.toList())) {
				String gn = group.getName();
				out += "<option value=\"" + gn + "\">" + gn + "</option>";
			}
			out += "</select><input type=\"submit\" name=\"action\" value=\"Remove Group\"/>";
			out += "</div></form><br/><hr/><br/>" +
					"<div style=\"background-color: #faa\">" +
					"<h2>Delete this stack</h2>" +
					"<form method=\"post\" action=\"/stacks\">" +
					"<input style=\"visibility: hidden\" type=\"checkbox\" name=\"Stack." + stackName + "\" checked/>" +
					"<input style=\"visibility: hidden\" type=\"text\" name=\"action\" value=\"Delete Selected\"/><br/>" +
					"<input type=\"submit\" value=\"Delete\"/>" +
					"</div>";
		} else {
			return "<b>Invalid stack name.</b>";
		}
		return out + "</form>";
	}
}
