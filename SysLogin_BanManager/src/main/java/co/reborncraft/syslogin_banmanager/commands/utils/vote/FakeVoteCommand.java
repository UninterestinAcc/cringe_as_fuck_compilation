package co.reborncraft.syslogin_banmanager.commands.utils.vote;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.objects.Permissions;
import co.reborncraft.syslogin_banmanager.listeners.VoteListener;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class FakeVoteCommand extends Command {
	public FakeVoteCommand() {
		super("fakevote");
	}

	@Override
	public void execute(CommandSender commandSender, String[] args) {
		if (Permissions.permissed(commandSender, Permissions.DEBUG_COMMANDS)) {
			if (args.length >= 1) {
				VoteListener.trigger(args[0], "BungeeCord FakeVote from " + SysLogin_BanManager.getInstance().getServerVariables().get("server-id"));
				commandSender.sendMessage(Utils.buildTextComponent("Fakevote sent for ", ChatColor.GREEN, Utils.buildTextComponent(args[0], ChatColor.YELLOW)));
			} else {
				commandSender.sendMessage(Utils.buildTextComponent("Please specify playername.", ChatColor.RED));
			}
		} else {
			commandSender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.PERMISSION_ERROR));
		}
	}
}
