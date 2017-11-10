package co.reborncraft.syslogin_banmanager.api.objects;

import net.md_5.bungee.api.chat.TextComponent;

public class ExpiredPunishment extends Punishment {
	private final String liftedBy;

	public ExpiredPunishment(PunishmentTarget targetType, PunishmentType punishmentType, String target, String reason, String liftedBy, long startTime, long endTime, String by) throws IllegalArgumentException {
		super(targetType, punishmentType, target, reason, startTime, endTime, by);
		this.liftedBy = liftedBy == null ? "Expired" : liftedBy;
	}

	public String getLiftedBy() {
		return liftedBy;
	}

	public TextComponent toInformationComp() { // TODO When there is userinfo
		return null;
	}

	@Override
	public TextComponent toBanTextComponent() throws IllegalArgumentException {
		throw new IllegalArgumentException("Punishment has already officially expired.");
	}

	@Override
	public TextComponent[] toMuteTextComponents() throws IllegalArgumentException {
		throw new IllegalArgumentException("Punishment has already officially expired.");
	}
}
