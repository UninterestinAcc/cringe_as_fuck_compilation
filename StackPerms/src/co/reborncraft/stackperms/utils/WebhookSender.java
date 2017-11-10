package co.reborncraft.stackperms.utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/*
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
*/

public class WebhookSender {
	public static void sendGroupUpdate(CommandSender by, OfflinePlayer target, boolean isAdding, String group) {
	}

	public static void flushSendBuffer() {
	}

	/*
	private static Map<String, List<Map<String, Object>>> dataMap = new HashMap<>();

	public static void sendGroupUpdate(CommandSender by, OfflinePlayer target, boolean isAdding, String group) {
		List<Map<String, Object>> mapList;
		if (dataMap.containsKey("embeds")) {
			mapList = dataMap.get("embeds");
		} else {
			mapList = new ArrayList<>();
			dataMap.put("embeds", mapList);
		}
		Map<String, Object> embedMap = new HashMap<>();

		embedMap.put("color", isAdding ? 65280 : 16711680);

		embedMap.put("description", "**+** *" + group + "*");

		Map<String, Object> authorMap = new HashMap<>();
		authorMap.put("name", target.getName() + " has been " + (isAdding ? "promoted" : "demoted") + " on " + Bukkit.getServerName());
		authorMap.put("icon_url", "https://minotar.net/helm/" + target.getName() + "/128.png");
		embedMap.put("author", authorMap);

		Map<String, Object> footerMap = new HashMap<>();
		footerMap.put("text", "By " + by.getName());
		footerMap.put("icon_url", "https://minotar.net/helm/" + by.getName() + "/128.png");
		embedMap.put("footer", footerMap);

		mapList.add(embedMap);
	}

	public static void flushSendBuffer() {
		URL webhookURL = null;
		try {
			FileConfiguration spc = StackPerms.getInstance().getConfig();
			if (spc.contains("discord-webhook") && spc.isString("discord-webhook")) {
				webhookURL = new URL(spc.getString("discord-webhook"));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (webhookURL == null) {
			return;
		}
		if (dataMap.size() >= 1) {
			JSONObject json = new JSONObject(dataMap);
			dataMap.clear();
			try {
				HttpResponse<JsonNode> resp = Unirest.post(webhookURL.toString())
						.header("Content-Type", "application/json")
						.body(json)
						.asJson();
				if (resp.getStatus() != 204) {
					StackPerms.getInstance().getLogger().severe("Error occurred while trying to post webhook to discord: HTTP/" + resp.getStatus() + " " + resp.getStatusText() + "\n" + resp.getBody());
				}
			} catch (UnirestException e) {
				e.printStackTrace();
			}
		}
	}
	*/
}
