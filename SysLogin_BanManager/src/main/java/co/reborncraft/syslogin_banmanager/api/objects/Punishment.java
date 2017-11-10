package co.reborncraft.syslogin_banmanager.api.objects;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.time.Instant;

public class Punishment {
	private final PunishmentTarget targetType;
	private final PunishmentType punishmentType;
	private final String target;
	private final String reason;
	private final long startTime;
	private final long endTime;
	private final String by;

	public Punishment(PunishmentTarget targetType, PunishmentType punishmentType, String target, String reason, long startTime, long endTime, String by) throws IllegalArgumentException {
		if (targetType != PunishmentTarget.USERNAME && punishmentType != PunishmentType.BAN) {
			throw new IllegalArgumentException("You cannot " + punishmentType + " an " + targetType + ".");
		}
		this.targetType = targetType;
		this.punishmentType = punishmentType;
		this.target = target;
		this.reason = reason;
		this.startTime = startTime;
		this.endTime = endTime;
		this.by = by;
	}

	@Override
	public String toString() {
		return SysLogin_BanManager.BANMANAGER_PREFIX +
				" \u00a7a" + by +
				" \u00a7dissued a " + punishmentType +
				" to (\u00a7a" + targetType + "\u00a7d) \u00a7a" + target +
				(endTime != 0 ? (" \u00a7dexpiring at \u00a7a" + Instant.ofEpochSecond(endTime)) : " \u00a7apermanently") +
				" \u00a7don \u00a7a" + Instant.ofEpochSecond(startTime) +
				" \u00a7dfor \u00a7a" + reason + "\u00a7d.";
	}

	public TextComponent[] toWarnTextComponents() {
		if (punishmentType == PunishmentType.WARN) {
			TextComponent[] textComps = new TextComponent[6];

			TextComponent blankline = new TextComponent("");
			TextComponent ruleComp = Utils.buildTextComponent("----------------------------------------------------", ChatColor.GOLD, false, false, false, true, false);

			textComps[0] = blankline;
			textComps[1] = ruleComp;
			textComps[2] = blankline;
			textComps[3] = Utils.parseIntoComp("\u00a7cYou've been warned by \u00a7b" + by + " \u00a7cfor \u00a7e" + reason);
			textComps[4] = blankline;
			textComps[5] = ruleComp;

			return textComps;
		}
		return new TextComponent[0];
	}

	public TextComponent toKickTextComponent() {
		if (punishmentType == PunishmentType.KICK) {
			final TextComponent ruleComp = new TextComponent("\n----------------------------------------------------\n\n");
			ruleComp.setColor(ChatColor.GOLD);
			ruleComp.setBold(false);
			ruleComp.setStrikethrough(true);

			final TextComponent textComp = new TextComponent("YOU (");
			textComp.setColor(ChatColor.RED);
			textComp.setBold(true);

			final TextComponent targetComp = new TextComponent(target);
			targetComp.setColor(ChatColor.AQUA);
			targetComp.setBold(false);
			textComp.addExtra(targetComp);

			textComp.addExtra(") HAVE BEEN KICKED\n");

			textComp.addExtra(ruleComp);

			textComp.addExtra("REASON: ");
			final TextComponent reasonComp = new TextComponent(reason + "\n");
			reasonComp.setColor(ChatColor.YELLOW);
			reasonComp.setBold(false);
			textComp.addExtra(reasonComp);

			textComp.addExtra(ruleComp);

			textComp.addExtra("KICKED BY: ");
			final TextComponent byComp = new TextComponent(by + "\n");
			byComp.setColor(ChatColor.AQUA);
			byComp.setBold(false);
			textComp.addExtra(byComp);

			textComp.addExtra("KICKED ON: ");
			final TextComponent startTimeComponent = new TextComponent(Instant.ofEpochSecond(startTime).toString() + "\n");
			startTimeComponent.setColor(ChatColor.LIGHT_PURPLE);
			startTimeComponent.setBold(false);
			textComp.addExtra(startTimeComponent);

			return textComp;
		}
		return null;
	}

	public TextComponent[] toMuteTextComponents() {
		if (punishmentType == PunishmentType.MUTE) {
			TextComponent[] textComps = new TextComponent[6];

			TextComponent blankline = new TextComponent("");
			TextComponent ruleComp = Utils.buildTextComponent("----------------------------------------------------", ChatColor.GOLD, false, false, false, true, false);

			textComps[0] = blankline;
			textComps[1] = ruleComp;
			textComps[2] = blankline;
			final long secs = endTime - (System.currentTimeMillis() / 1000);
			textComps[3] = Utils.parseIntoComp("\u00a7cYou've been muted by \u00a7b" + by + " \u00a7cfor \u00a7e" + reason + "\u00a7c. The mute will expire in " + Utils.secondsToString(secs));
			textComps[4] = blankline;
			textComps[5] = ruleComp;

			return textComps;
		}
		return new TextComponent[0];
	}

	public TextComponent toBanTextComponent() {
		if (punishmentType == PunishmentType.BAN) {
			final TextComponent ruleComp = new TextComponent("\n----------------------------------------------------\n\n");
			ruleComp.setColor(ChatColor.GOLD);
			ruleComp.setBold(false);
			ruleComp.setStrikethrough(true);

			final TextComponent textComp = new TextComponent("YOU (");
			textComp.setColor(ChatColor.RED);
			textComp.setBold(true);

			final TextComponent targetComp = new TextComponent((targetType == PunishmentTarget.ISP ? "AS" + target + ": " + Utils.loadASNName("as" + target) : target));
			targetComp.setColor(ChatColor.AQUA);
			targetComp.setBold(false);
			textComp.addExtra(targetComp);

			textComp.addExtra(") HAVE BEEN " + (targetType != PunishmentTarget.USERNAME ? targetType + " " : "") + "BANNED\n");

			textComp.addExtra(ruleComp);

			textComp.addExtra("REASON: ");
			final TextComponent reasonComp = new TextComponent(reason + "\n");
			reasonComp.setColor(ChatColor.YELLOW);
			reasonComp.setBold(false);
			textComp.addExtra(reasonComp);

			textComp.addExtra(ruleComp);

			textComp.addExtra("BANNED BY: ");
			final TextComponent byComp = new TextComponent(by + "\n");
			byComp.setColor(ChatColor.AQUA);
			byComp.setBold(false);
			textComp.addExtra(byComp);

			textComp.addExtra("BANNED ON: ");
			final TextComponent startTimeComponent = new TextComponent((startTime <= 0 ? "undefined" : Instant.ofEpochSecond(startTime).toString()) + "\n");
			startTimeComponent.setColor(ChatColor.LIGHT_PURPLE);
			startTimeComponent.setBold(false);
			textComp.addExtra(startTimeComponent);

			textComp.addExtra("BANNED UNTIL: ");
			final TextComponent endTimeComponent = new TextComponent((endTime <= 0 ? "PERMANENT" : Instant.ofEpochSecond(endTime).toString()) + "\n");
			endTimeComponent.setColor(ChatColor.LIGHT_PURPLE);
			endTimeComponent.setBold(false);
			textComp.addExtra(endTimeComponent);

			if (endTime > 0) {
				textComp.addExtra("TIME REMAINING: ");
				long secs = endTime - (System.currentTimeMillis() / 1000);
				textComp.addExtra(Utils.buildTextComponent(Utils.secondsToString(secs) + "\n", ChatColor.LIGHT_PURPLE, Utils.TernaryState.FALSE, Utils.TernaryState.NEITHER, Utils.TernaryState.NEITHER, Utils.TernaryState.NEITHER, Utils.TernaryState.NEITHER));
			}

			textComp.addExtra(ruleComp);

			textComp.addExtra("APPEAL ON: ");
			final TextComponent appealComp = new TextComponent("https://discord.reborncraft.co");
			appealComp.setColor(ChatColor.YELLOW);
			appealComp.setBold(false);
			textComp.addExtra(appealComp);

			return textComp;
		}
		return null;
	}

	public PunishmentTarget getTargetType() {
		return targetType;
	}

	public PunishmentType getPunishmentType() {
		return punishmentType;
	}

	public String getTarget() {
		return target;
	}

	public String getReason() {
		return reason;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public String getBy() {
		return by;
	}

	public enum PunishmentTarget {
		USERNAME("User", "USERNAME"), IP("IP", "IP"), ISP("ISP", "ASN");

		private final String dbPrefix;
		private final String dbIdentifier;

		PunishmentTarget(String dbPrefix, String dbIdentifier) {
			this.dbPrefix = dbPrefix;
			this.dbIdentifier = dbIdentifier;
		}

		public String getDbPrefix() {
			return dbPrefix;
		}

		public String getDbIdentifier() {
			return dbIdentifier;
		}
	}

	public enum PunishmentType {
		BAN("Bans"), MUTE("Mutes"), WARN("Warns"), KICK(null);
		private final String dbSuffix;

		PunishmentType(String dbPrefix) {
			this.dbSuffix = dbPrefix;
		}

		public String getDbSuffix() {
			return dbSuffix;
		}

		@Override
		public String toString() {
			return this.name();
		}
	}
}
