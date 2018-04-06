package com.icys.party;

import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.icys.party.listener.onJoin;
import com.icys.party.listener.onQuit;
import com.icys.party.party.PartyEntity;
import com.icys.party.party.PartyManager;
import com.icys.party.player.PlayerManager;
import com.icys.party.type.ReasonJoin;
import com.icys.party.type.ReasonLeave;

public class Party extends JavaPlugin {
	
	private static Party instance;
	
	private PlayerManager playerManager;
	private PartyManager partyManager;
	
	@Override
	public void onEnable() {
		
		instance = this;
		handle();
		
		log("enable!");
		
	}
	
	@Override
	public void onDisable() {
		
		log("disable!");
		
	}
	
	private void handle() {
		playerManager = new PlayerManager(this);
		partyManager = new PartyManager(this);
		
		PluginManager plma = this.getServer().getPluginManager();
		plma.registerEvents(new onJoin(this), this);
		plma.registerEvents(new onQuit(this), this);
	}
	
	/**
	 * Call PartyManager that run on this plugin
	 * @return PartyManager instance
	 */
	public PartyManager getPartyManager() {
		return partyManager;
	}
	
	/**
	 * Call PlayerManager that run on this plugin
	 * @return PlayerManager instance
	 */
	public PlayerManager getPlayerManager() {
		return playerManager;
	}
	
	/**
	 * Call Party main class
	 * @return Party instance
	 */
	public static Party getInstance() {
		return instance;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("party")) {
				if(args.length == 0) {
					player.sendMessage("===== Party =====");
					player.sendMessage("/party create");
					player.sendMessage("/party join <party's name>");
					player.sendMessage("/party status");
					player.sendMessage("/party leave");
					player.sendMessage("/party list");
					player.sendMessage("/party invite <player>");
					player.sendMessage("/party accept");
				} else if(args.length >= 1) {
					PartyEntity party;
					switch(args[0]) {
						case "create":
							if(!playerManager.hasParty(player)){
								party = partyManager.createParty(player.getName(), player);
								playerManager.joinParty(player, party, ReasonJoin.Created);
								
								player.sendMessage(ChatColor.GREEN + "You create a " + ChatColor.BOLD + party.getName() + ChatColor.RESET + "" + ChatColor.GREEN + " party!");
							} else {
								player.sendMessage(ChatColor.RED + "You have a party. Please leave first!");
							}
							break;
						case "join":
							if(args.length == 2) {
								String partyName = args[1];
								if(partyManager.isExist(partyName)) {
									party = partyManager.getParty(partyName);
									if(!playerManager.hasParty(player)){
										playerManager.joinParty(player, party, ReasonJoin.Join);
										player.sendMessage("Join " + ChatColor.ITALIC + Bukkit.getPlayer(party.getLeader()).getName() + "'s" + ChatColor.RESET + " party!");
									} else if(party.getName() == playerManager.getParty(player).getName()) {
										player.sendMessage(ChatColor.RED + "You're currently a member of this party!");
									} else {
										player.sendMessage(ChatColor.RED + "You have a party. Please leave first!");
									}
								} else {
									player.sendMessage(ChatColor.RED + "Party not found!");
								}
							} else {
								player.sendMessage(ChatColor.RED + "/party join " + ChatColor.BOLD + "<party's name>");
							}
							break;
						case "status":
							if(playerManager.hasParty(player)) {
								party = playerManager.getParty(player);
								if(!party.isEmpty()) {
									player.sendMessage("=== " + party.getName() + " party Status ===");
									player.sendMessage(ChatColor.DARK_GREEN + party.getType() + " party");
									player.sendMessage(ChatColor.GREEN + "Max player: " + party.getMaxMember());
									player.sendMessage("Members");
									player.sendMessage("- " + ChatColor.BOLD + "" + ChatColor.ITALIC + Bukkit.getOfflinePlayer(party.getLeader()).getName() + ChatColor.GRAY + " (Leader)");
									for(UUID uuid : party.getMember()) {
										if(uuid != party.getLeader()) {
											player.sendMessage("- " + ChatColor.ITALIC + Bukkit.getOfflinePlayer(uuid).getName());
										} else {
											continue;
										}
									}
								}
							} else {
								player.sendMessage(ChatColor.RED + "You don't have any party!");
							}
							break;
						case "leave":
							if(playerManager.hasParty(player)) {
								party = playerManager.getParty(player);
								playerManager.leaveParty(player, ReasonLeave.Leave);
								player.sendMessage(ChatColor.RED + "You have left the " + ChatColor.BOLD + party.getName() + "" + ChatColor.RESET + "" + ChatColor.RED + " party");
							} else {
								player.sendMessage(ChatColor.RED + "You don't have a party");
							}
							break;
						case "list":
							player.sendMessage("==== " + ChatColor.BOLD + "List of Parties" + ChatColor.RESET + " ====");
							if(!partyManager.getListParties().isEmpty()) {
								for(String str : partyManager.getListParties()) {
									player.sendMessage("- " + str);
								}
							} else {
								player.sendMessage(ChatColor.ITALIC + "There aren't any party");
							}
							break;
						case "invite":
							if(playerManager.hasParty(player)) {
								party = playerManager.getParty(player);
								if(party.getLeader() == player.getUniqueId()) {
									if(party.getMembers() < party.getMaxMember()) {
										if(args.length == 2) {
											String targetName = args[1];
											if(Bukkit.getPlayer(targetName) != null) {
												Player target = Bukkit.getPlayer(targetName);
												if(player == target) {
													player.sendMessage(ChatColor.RED + "You can't invite yourself! " + ChatColor.GRAY + "(It's seem you're feeling lonely)");
												} else if(playerManager.hasParty(player.getUniqueId())) {
													playerManager.sendInvite(target, player, party);
													player.sendMessage(ChatColor.GREEN + "You invite " + ChatColor.RESET + "" + ChatColor.ITALIC 
															+ targetName + ChatColor.RESET + "" + ChatColor.GREEN + " to join party!");
													target.sendMessage(ChatColor.ITALIC + targetName + ChatColor.RESET + "" 
															+ ChatColor.GREEN + " invite you to join their party!");
													target.sendMessage(ChatColor.GREEN + "/party accept -- to " + ChatColor.BOLD + "accept");
												}
											} else {
												player.sendMessage(ChatColor.RED + "Player not found!");
											}
										} else {
											player.sendMessage(ChatColor.RED + "/party invite " + ChatColor.BOLD + "<player>");
										}
									} else {
										player.sendMessage(ChatColor.RED + "You can't invite players! Party is full");
									}
								} else {
									player.sendMessage(ChatColor.RED + "You aren't leader!");
								}
							} else {
								player.sendMessage(ChatColor.RED + "You don't have any party! Create it first");
							}
							break;
						case "accept":
							if(playerManager.hasInvite(player)) {
								if(playerManager.acceptInvite(player)) {
									party = playerManager.getParty(player);
									player.sendMessage("Join " + ChatColor.ITALIC + Bukkit.getPlayer(party.getLeader()).getName() + "'s" + ChatColor.RESET + " party!");
								} else {
									player.sendMessage(ChatColor.RED + "You can't join that party! Party full");
								}
							} else {
								player.sendMessage(ChatColor.RED + "You don't get any invite");
							}
							break;
						case "max":
							if(playerManager.hasParty(player)) {
								party = playerManager.getParty(player);
								if(party.getLeader() == player.getUniqueId()) {
									if(args.length == 2) {
										party.setMaxMember(Integer.parseInt(args[1]));
										player.sendMessage(ChatColor.GREEN + "Update maximum member on " + party.getName() + " party!");
									} else {
										player.sendMessage(ChatColor.RED + "/party max " + ChatColor.BOLD + "<amount>");
									}
								} else {
									player.sendMessage(ChatColor.RED + "You aren't leader!");
								}
							} else {
								player.sendMessage(ChatColor.RED + "You don't have any party! Create it first");
							}
							break;
						case "kick":
							if(playerManager.hasParty(player)) {
								party = playerManager.getParty(player);
								if(party.getLeader() == player.getUniqueId()) {
									if(args.length == 2) {
										String targetName = args[1];
										if(Bukkit.getPlayer(targetName) != null) {
											Player target = Bukkit.getPlayer(targetName);
											if(player == target) {
												player.sendMessage(ChatColor.RED + "You can't kick yourself!");
											} else {
												playerManager.leaveParty(target, ReasonLeave.Kick);
												target.sendMessage(ChatColor.RED + "You have been kick from the " + ChatColor.BOLD + party.getName() + "" + ChatColor.RESET + "" + ChatColor.RED + " party");
												player.sendMessage(ChatColor.RED + "You have kick " + target.getName() + " from the party!");
												party.sendMessage(ChatColor.RED + player.getName() + " left the party! (Kicking)");
											}
										} else {
											player.sendMessage(ChatColor.RED + "Player not found!");
										}
									} else {
										player.sendMessage(ChatColor.RED + "/party kick " + ChatColor.BOLD + "<player>");
									}
								} else {
									player.sendMessage(ChatColor.RED + "You aren't leader!");
								}
							} else {
								player.sendMessage(ChatColor.RED + "You don't have any party!");
							}
							break;
						case "debug":
							if(playerManager.getEntity(player).whoInvited() != null) {
								player.sendMessage("1");
								player.sendMessage("uuid invite = " + playerManager.getEntity(player).whoInvited());
							} else {
								player.sendMessage("0");
							}
							player.sendMessage(ChatColor.GRAY + "" + partyManager.getParties().toString());
							player.sendMessage("" + playerManager.hasParty(player));
							if(playerManager.hasParty(player)) {
								player.sendMessage(ChatColor.GRAY + playerManager.getParty(player).getMember().toString());
								player.sendMessage(playerManager.getParty(player).getLeader().toString());
							} else {
								player.sendMessage(ChatColor.GRAY + playerManager.listPlayerDebug().toString());
							}
							break;
						default:
							player.sendMessage("===== " + ChatColor.BOLD + "Party" + ChatColor.RESET + " =====");
							player.sendMessage("/party create");
							player.sendMessage("/party join <party's name>");
							player.sendMessage("/party status");
							player.sendMessage("/party leave");
							player.sendMessage("/party list");
							player.sendMessage("/party invite <player>");
							player.sendMessage("/party accept");
							break;
					}
				}
			}
		}
		return true;
	}
	
	public void log(String msg) {
		getServer().getLogger().log(Level.INFO, "[Party] " + msg);
	}
	
}
