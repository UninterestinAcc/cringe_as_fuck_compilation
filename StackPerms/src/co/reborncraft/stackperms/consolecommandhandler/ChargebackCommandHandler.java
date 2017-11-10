package co.reborncraft.stackperms.consolecommandhandler;

import co.reborncraft.stackperms.api.SavePlayer;
import co.reborncraft.stackperms.utils.WebhookSender;
import org.bukkit.command.CommandSender;

public class ChargebackCommandHandler implements PermissionsManagementCommand {
	@Override
	public void execute(CommandSender sender, SavePlayer sp, String[] args) {
		WebhookSender.sendGroupUpdate(sender, sp.getPlayer(), false, "All groups");
		sp.clearCustomData();
		sender.sendMessage("Removed all custom data of " + sp.getName());
	}
}
