package co.reborncraft.syslogin_banmanager.commands.utils.staff;

import co.reborncraft.syslogin_banmanager.SysLogin_BanManager;
import co.reborncraft.syslogin_banmanager.api.futures.management.PushStaffFuture;
import co.reborncraft.syslogin_banmanager.api.objects.Permissions;
import co.reborncraft.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class UpdateStaffsCommand extends Command {
	public UpdateStaffsCommand() {
		super("updatestaffs", "", "updatestaff");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (Permissions.permissed(sender, Permissions.MANAGE)) {
			if (args.length >= 2) {
				String targetStaff = args[1];
				if (args[0].equalsIgnoreCase("demote")) {
					if (SysLogin_BanManager.getInstance().isStaff(targetStaff)) {
						SysLogin_BanManager.getInstance().scheduleFuture(new PushStaffFuture(sender, targetStaff, 0, true, success -> {
							sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_UPDATE_STAFF_SUCCESS));
							SysLogin_BanManager.getInstance().requestPullStaff();
						}));
					} else {
						sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_UPDATE_STAFF_NOT_STAFF));
					}
				} else if (args[0].equalsIgnoreCase("promote")) {
					if (SysLogin_BanManager.getInstance().isStaff(targetStaff)) {
						sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_UPDATE_STAFF_ALREADY_STAFF));
					} else {
						SysLogin_BanManager.getInstance().scheduleFuture(new PushStaffFuture(sender, targetStaff, 0, false, success -> {
							sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_UPDATE_STAFF_SUCCESS));
							SysLogin_BanManager.getInstance().requestPullStaff();
						}));
					}
				} else if (args[0].equalsIgnoreCase("check")) {
					if (SysLogin_BanManager.getInstance().isStaff(targetStaff)) {
						sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_UPDATE_STAFF_CHECK_MESSAGE.replaceAll("\\{name}", targetStaff)));
						sender.sendMessage(Permissions.toChatRepresentation(SysLogin_BanManager.getInstance().getStaffPermissions(targetStaff)));
					} else {
						sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_UPDATE_STAFF_NOT_STAFF));
					}
				} else if (args.length >= 3) {
					try {
						int targetOrder = Integer.parseUnsignedInt(args[2]);
						Permissions perm = Permissions.fromOrder(targetOrder);
						if (perm != null) {
							if (SysLogin_BanManager.getInstance().isStaff(targetStaff)) {
								long updatePermission = SysLogin_BanManager.getInstance().getStaffPermissions(targetStaff);
								if (args[0].equalsIgnoreCase("revoke")) {
									if (Permissions.has(perm, updatePermission)) {
										updatePermission = Permissions.removePermission(perm, updatePermission);
										SysLogin_BanManager.getInstance().scheduleFuture(new PushStaffFuture(sender, targetStaff, updatePermission, false, success -> {
											sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_UPDATE_STAFF_SUCCESS));
											SysLogin_BanManager.getInstance().requestPullStaff();
										}));
									} else {
										sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_UPDATE_STAFF_PERM_NONEXISTANT));
									}
								} else if (args[0].equalsIgnoreCase("grant")) {
									if (Permissions.has(perm, updatePermission)) {
										sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_UPDATE_STAFF_PERM_ALREADYEXISTS));
									} else {
										updatePermission = Permissions.addPermission(perm, updatePermission);
										SysLogin_BanManager.getInstance().scheduleFuture(new PushStaffFuture(sender, targetStaff, updatePermission, false, success -> {
											sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_UPDATE_STAFF_SUCCESS));
											SysLogin_BanManager.getInstance().requestPullStaff();
										}));
									}
								} else {
									sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_UPDATE_STAFF_MESSAGE));
								}
							} else {
								sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_UPDATE_STAFF_NOT_STAFF));
							}
						} else {
							sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_UPDATE_STAFF_PERM_INPUT_REQ_NUMBER));
							sender.sendMessage(Permissions.toChatRepresentation(-1));
						}
					} catch (NumberFormatException e) {
						sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_UPDATE_STAFF_PERM_INPUT_REQ_NUMBER));
						sender.sendMessage(Permissions.toChatRepresentation(-1));
					}
				} else {
					sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_UPDATE_STAFF_MESSAGE));
				}
			} else {
				sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.SL_UPDATE_STAFF_MESSAGE));
			}
		} else {
			sender.sendMessage(Utils.parseIntoComp(SysLogin_BanManager.PERMISSION_ERROR));
		}
	}
}
