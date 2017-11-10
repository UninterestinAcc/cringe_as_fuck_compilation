package co.reborncraft.stackperms.consolecommandhandler;

import co.reborncraft.stackperms.StackPerms;
import co.reborncraft.stackperms.api.Group;
import co.reborncraft.stackperms.api.SavePlayer;
import co.reborncraft.stackperms.utils.WebhookSender;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public class PromoteCommandHandler implements PermissionsManagementCommand {
	@Override
	public void execute(CommandSender sender, SavePlayer sp, String[] args) {
		if (args.length >= 1) {
			for (String groupName : args) {
				Optional<Group> group = StackPerms.getInterface().getGroupByName(groupName);
				group.ifPresent(g -> {
					if (sp.promoteIfPossible(g)) {
						WebhookSender.sendGroupUpdate(sender, sp.getPlayer(), true, g.getName());
						sender.sendMessage(sp.getName() + " was added to group " + g.getName());
					} else {
						sender.sendMessage(sp.getName() + " already has a group that is higher ranking than " + g.getName());
					}
				});
				if (!group.isPresent()) {
					sender.sendMessage("Group by the name of " + groupName + " was not found.");
				}
			}
		} else {
			sender.sendMessage("\u00a74\u00a7lError! \u00a77At least 1 group needs to be specified.");
		}
	}
}
