package io.github.loldatsec.mcplugins.haloplus.game;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

public class GameTeam {

	public GameTeamEnum team;
	public int score = 0;
	public Map<Player, Integer> members = new HashMap<Player, Integer>();
	public Team scoreboardTeam = null;

	public GameTeam(GameTeamEnum team) {
		this.team = team;
	}

	@SuppressWarnings("deprecation")
	public void setScoreboardTeam(Team team) {
		scoreboardTeam = team;
		scoreboardTeam.setAllowFriendlyFire(false);
		scoreboardTeam.setPrefix(GameTeamEnum.getColour(this.team) + "");
		scoreboardTeam.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
		scoreboardTeam.setCanSeeFriendlyInvisibles(true);
		for (Player p : members.keySet()) {
			scoreboardTeam.addPlayer(p.getPlayer());
		}
	}

	@SuppressWarnings("deprecation")
	public void updateScoreboard() {
		for (OfflinePlayer p : scoreboardTeam.getPlayers()) {
			if (!p.isOnline() || !members.containsKey(p.getPlayer())) {
				scoreboardTeam.removePlayer(p);
			}
		}
		for (Player p : members.keySet()) {
			if (!scoreboardTeam.hasPlayer(p)) {
				scoreboardTeam.addPlayer(p);
			}
		}
	}
}
