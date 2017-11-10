package co.reborncraft.stackperms.http;

import co.reborncraft.stackperms.StackPerms;
import co.reborncraft.stackperms.api.Group;
import co.reborncraft.stackperms.api.SavePlayer;
import co.reborncraft.stackperms.api.Stack;
import co.reborncraft.stackperms.api.StackPermsInterface;
import co.reborncraft.stackperms.api.structure.Groupable;
import co.reborncraft.stackperms.api.structure.PermissionManagementType;
import co.reborncraft.stackperms.impl.GroupImpl;
import co.reborncraft.stackperms.impl.StackImpl;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RequestProcessor {
	public static Map.Entry<String, Boolean> typeAndVarName(HttpExchange exch, Class<? extends PermissionManagementType> managementType) {
		final String typeStr = managementType.getSimpleName();
		final StackPermsInterface spi = StackPerms.getInterface();
		String saveableName = exch.getRequestURI().toString().replaceAll("^/(stack|group|player)/(\\w{3,64})$", "$2");
		if (exch.getRequestMethod().equalsIgnoreCase("POST")) {
			Map<String, List<String>> postContent = StackPermsHTTPServerlet.readPOSTUriIntoMap(exch.getRequestBody());
			if (postContent.containsKey("action")) {
				String action = postContent.get("action").get(0).toLowerCase();
				switch (typeStr) {
					case "Stack": {
						Optional<Stack> existingStackGet = spi.getStackByName(saveableName);
						Stack stack = existingStackGet.orElseGet(() -> new StackImpl(saveableName));
						switch (action) {
							case "set information": {
								if (postContent.containsKey("defaultGroup")) {
									String groupName = postContent.get("defaultGroup").get(0);
									if (groupName.length() > 6) {
										stack.setDefaultGroup(spi.getGroupByName(groupName.substring(6)).orElseGet(() -> null));
										StackPerms.getInterface().getPlayers().forEach(Groupable::cleanGroupStacking);
									}
								}
								if (postContent.containsKey("weight")) {
									try {
										stack.setWeight(Double.parseDouble(postContent.get("weight").get(0)));
									} catch (NumberFormatException ignored) {
									}
								}
								for (String group : postContent.entrySet().stream()
										.filter(e -> e.getKey().startsWith("Group."))
										.map(e -> e.getKey().substring(6))
										.collect(Collectors.toSet())
										) {
									spi.getGroupByName(group).ifPresent(g -> {
										try {
											g.setRank(Double.parseDouble(postContent.get("Group." + group).get(0)));
											g.save();
										} catch (NumberFormatException ignored) {
										}
									});
								}
								break;
							}
							case "add group": {
								if (postContent.containsKey("addGroup")) {
									String group = postContent.get("addGroup").get(0);
									spi.getGroupByName(group).ifPresent(g -> {
										g.setStack(stack);
										g.save();
									});
								}
								break;
							}
							case "remove group": {
								if (postContent.containsKey("removeGroup")) {
									String group = postContent.get("removeGroup").get(0);
									spi.getGroupByName(group).ifPresent(g -> {
										g.setStack(null);
										g.save();
									});
								}
								break;
							}
						}
						stack.save();
						break;
					}
					case "Group": {
						Optional<Group> existingGroupGet = spi.getGroupByName(saveableName);
						Group group = existingGroupGet.orElseGet(() -> new GroupImpl(saveableName));
						switch (action) {
							case "set information": {
								if (postContent.containsKey("prefix")) {
									String prefix = postContent.get("prefix").get(0);
									if (prefix != null && !prefix.isEmpty()) {
										group.setPrefix(prefix);
									} else {
										group.setPrefix(null);
									}
								}
								if (postContent.containsKey("tabPrefix")) {
									String tabPrefix = postContent.get("tabPrefix").get(0);
									if (tabPrefix != null && !tabPrefix.isEmpty()) {
										group.setTabPrefix(tabPrefix);
									} else {
										group.setTabPrefix(null);
									}
								}
								if (postContent.containsKey("suffix")) {
									String suffix = postContent.get("suffix").get(0);
									if (suffix != null && !suffix.isEmpty()) {
										group.setSuffix(suffix);
									} else {
										group.setSuffix(null);
									}
								}
								break;
							}
							case "add permissions": {
								if (postContent.containsKey("addPermissions")) {
									String[] addThese = postContent.get("addPermissions").get(0).split("(,\\s*)|(\\s+)");
									group.addPermissions(Arrays.stream(addThese));
								}
								break;
							}
							case "delete permissions": {
								group.removePermissions(postContent.entrySet().stream()
										.filter(e -> e.getKey().startsWith("Permission.") && e.getValue().get(0).equalsIgnoreCase("on"))
										.map(e -> e.getKey().substring(11)));
								break;
							}
						}
						break;
					}
					case "SavePlayer": {
						SavePlayer savePlayer = StackPerms.getInterface().getOrCreateSavePlayer(saveableName);
						switch (action) {
							case "set information": {
								if (postContent.containsKey("prefix")) {
									String prefix = postContent.get("prefix").get(0);
									if (prefix != null && !prefix.isEmpty()) {
										savePlayer.setPrefix(prefix);
									} else {
										savePlayer.setPrefix(null);
									}
								}
								if (postContent.containsKey("tabPrefix")) {
									String tabPrefix = postContent.get("tabPrefix").get(0);
									if (tabPrefix != null && !tabPrefix.isEmpty()) {
										savePlayer.setTabPrefix(tabPrefix);
									} else {
										savePlayer.setTabPrefix(null);
									}
								}
								if (postContent.containsKey("suffix")) {
									String suffix = postContent.get("suffix").get(0);
									if (suffix != null && !suffix.isEmpty()) {
										savePlayer.setSuffix(suffix);
									} else {
										savePlayer.setSuffix(null);
									}
								}
								break;
							}
							case "add group": {
								if (postContent.containsKey("addGroup")) {
									String group = postContent.get("addGroup").get(0);
									spi.getGroupByName(group).ifPresent(savePlayer::addGroup);
								}
								break;
							}
							case "remove group": {
								if (postContent.containsKey("removeGroup")) {
									String group = postContent.get("removeGroup").get(0);
									spi.getGroupByName(group).ifPresent(savePlayer::removeGroup);
								}
								break;
							}
							case "add permissions": {
								if (postContent.containsKey("addPermissions")) {
									String[] addThese = postContent.get("addPermissions").get(0).split("(,\\s*)|(\\s+)");
									savePlayer.addPermissions(Arrays.stream(addThese));
								}
								break;
							}
							case "delete permissions": {
								savePlayer.removePermissions(postContent.entrySet().stream()
										.filter(e -> e.getKey().startsWith("Permission.") && e.getValue().get(0).equalsIgnoreCase("on"))
										.map(e -> e.getKey().substring(11)));
								break;
							}
						}
						break;
					}
				}
			}
		}
		return new AbstractMap.SimpleEntry<>("", false);
	}

	public static Map.Entry<String, Boolean> typeRoot(HttpExchange exch, Class<? extends PermissionManagementType> managementType) {
		final String typeStr = managementType.getSimpleName();
		final StackPermsInterface spi = StackPerms.getInterface();
		if (exch.getRequestMethod().equalsIgnoreCase("POST")) {
			Map<String, List<String>> postContent = StackPermsHTTPServerlet.readPOSTUriIntoMap(exch.getRequestBody());
			if (postContent.containsKey("action")) {
				switch (postContent.get("action").get(0).toLowerCase()) {
					case "delete selected": {
						List<String> delete = postContent.entrySet().stream()
								.filter(e -> e.getKey().startsWith(typeStr + ".") && e.getValue().get(0).equalsIgnoreCase("on"))
								.map(e -> e.getKey().replaceAll("^" + typeStr + "\\.", ""))
								.collect(Collectors.toList());
						switch (typeStr) {
							case "Stack": {
								spi.deleteStacksIfPresent(delete);
								break;
							}
							case "Group": {
								spi.deleteGroupsIfPresent(delete);
								break;
							}
							case "SavePlayer": {
								spi.deletePlayersIfPresent(delete);
								break;
							}
						}
						spi.deletePlayersIfPresent(delete);
						return new AbstractMap.SimpleEntry<>("<b>" + delete.size() + " " + typeStr + "(s) have been deleted if they were present.</b><br/>", false);
					}
					case "create new": {
						if (postContent.containsKey("name")) {
							String nameOfNew = postContent.get("name").get(0);
							if (nameOfNew.matches("\\w{3,64}")) {
								exch.getResponseHeaders().add("Location", "/" + (typeStr.equalsIgnoreCase("SavePlayer") ? "player" : typeStr.toLowerCase()) + "/" + nameOfNew);
								try {
									exch.sendResponseHeaders(301, 0);
									exch.getResponseBody().close();
									exch.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
								return new AbstractMap.SimpleEntry<>("", true);
							} else {
								return new AbstractMap.SimpleEntry<>("<b>Invalid name, must be alphanumeric (underscores allowed) and 3-64 characters long.</b><br/>", false);
							}
						}
						break;
					}
				}
			}
		}
		return new AbstractMap.SimpleEntry<>("", false);
	}
}
