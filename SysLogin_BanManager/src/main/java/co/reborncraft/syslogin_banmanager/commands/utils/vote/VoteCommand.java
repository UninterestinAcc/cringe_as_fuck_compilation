package co.reborncraft.syslogin_banmanager.commands.utils.vote;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.futures.vote.GiveVoteInformationFuture;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class VoteCommand extends Command {
	public VoteCommand() {
		super("vote", "", "voting");
	}

	@Override
	public void execute(CommandSender commandSender, String[] args) {
		if (commandSender instanceof ProxiedPlayer) {
			SysLogin_BanManager.getInstance().scheduleFuture(new GiveVoteInformationFuture(((ProxiedPlayer) commandSender), info -> {
				List<TextComponent> textComps = new ArrayList<>();

				textComps.add(Utils.buildTextComponent("----------------------------------------------------", ChatColor.GOLD, false, false, false, true, false));
				textComps.add(Utils.buildTextComponent("To vote, click one (or all) of the links below, put your name in the text box on the site, solve the captcha and click the vote button. Your reward will arrive within a minute.", ChatColor.GREEN));

				info.getVoteSites().entrySet()
						.stream()
						.sorted(Comparator.comparingInt(Map.Entry::getKey))
						.map(dat -> {
							TextComponent comp = Utils.buildTextComponent("    " + dat.getKey() + ") ", ChatColor.GOLD);
							TextComponent linkComp = Utils.buildTextComponent(dat.getValue(), ChatColor.YELLOW);
							linkComp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{Utils.buildTextComponent("Click to open link.", ChatColor.GOLD)}));
							linkComp.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, dat.getValue()));
							comp.addExtra(linkComp);
							return comp;
						})
						.forEach(textComps::add);
				long secs = info.getLastVoteTime() == null ? -1 : (System.currentTimeMillis() - info.getLastVoteTime().getTime()) / 1000;
				textComps.add(Utils.parseIntoComp("\u00a7aYou currently have \u00a7e" +
						info.getVotes() +
						" \u00a7avotes (all-time) and you " +
						(secs == -1 ? "never voted." : "last voted \u00a7e" + Utils.secondsToString(secs) + " \u00a7aago.")));

				textComps.forEach(((ProxiedPlayer) commandSender)::sendMessage);
				((ProxiedPlayer) commandSender).getServer().sendData("Vote", "TellVotingInformation".getBytes());
			}));
		} else {
			commandSender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_PLAYER_ONLY_ERROR));
		}
	}
}
