package com.icys.party.player;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.icys.party.Party;
import com.icys.party.event.PlayerJoinPartyEvent;
import com.icys.party.event.PlayerLeftPartyEvent;
import com.icys.party.party.PartyEntity;
import com.icys.party.task.InviteTask;
import com.icys.party.type.ReasonJoin;
import com.icys.party.type.ReasonLeave;

import net.md_5.bungee.api.ChatColor;

public class PlayerManager {
	
	private Party plugin;
	
	private HashMap<UUID, PartyEntity> listPlayerParty;
	private HashMap<UUID, PlayerEntity> playerPartyStatus;
	
	public PlayerManager(Party instance) {
		plugin = instance;
		
		listPlayerParty = new HashMap<UUID, PartyEntity>();
		playerPartyStatus = new HashMap<UUID, PlayerEntity>();
	}
	
	/**
	 * Using to initial player by put data into hashmap
	 */
	public void init(Player player) {
		UUID uuid = player.getUniqueId();
		PlayerEntity entity = new PlayerEntity(uuid);
		
		playerPartyStatus.put(uuid, entity);
	}
	
	/**
	 * Check player have data in hashmap
	 * @return Is player have initial data
	 */
	public boolean alreadyInit(Player player) {
		UUID uuid = player.getUniqueId();
		if(playerPartyStatus.containsKey(uuid)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Get player party
	 * @param uuid UUID of player
	 * @return Player's party
	 */
	public PartyEntity getParty(UUID uuid) {
		if(!listPlayerParty.containsKey(uuid)) {
			//listPlayerParty.put(uuid, "none");
		}
		return listPlayerParty.get(uuid);
	}
	
	/**
	 * Get player party
	 * @param player Player
	 * @return Player's party
	 */
	public PartyEntity getParty(Player player) {
		UUID uuid = player.getUniqueId();
		return getParty(uuid);
	}
	
	/**
	 * Get PlayerEntity of player
	 * @param uuid UUID of player
	 * @return Player's PlayerEntity
	 */
	public PlayerEntity getEntity(UUID uuid) {
		if(playerPartyStatus.containsKey(uuid)) {
			return playerPartyStatus.get(uuid);
		} else {
			PlayerEntity entity = new PlayerEntity(uuid);
			playerPartyStatus.put(uuid, entity);
			return entity;
		}
	}
	
	/**
	 * Get PlayerEntity of player
	 * @param player Player
	 * @return Player's PlayerEntity
	 */
	public PlayerEntity getEntity(Player player) {
		UUID uuid = player.getUniqueId();
		return getEntity(uuid);
	}
	
	/**
	 * Sending invite to target
	 * @param target Player who has been invited
	 * @param invited Player who invite another
	 * @param party Target party
	 */
	public void sendInvite(Player target, Player invited, PartyEntity party) {		
		PlayerEntity entity = getEntity(target);
		entity.giveInvite(invited.getUniqueId(), new InviteTask(this.plugin, target, party));
	}
	
	/**
	 * Player accept invite
	 * @param player Player
	 * @return Is success
	 */
	public boolean acceptInvite(Player player) {
		UUID uuid = player.getUniqueId();
		PlayerEntity entity = getEntity(uuid);
		
		UUID target = entity.whoInvited();
		PartyEntity party = listPlayerParty.get(target);
		
		if(party.getMembers() < party.getMaxMember()) {
			entity.acceptInvite(party);
			joinParty(player, party, ReasonJoin.Invited);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Player cancel invite
	 * @param player Player
	 */
	public void cancelInvite(Player player) {
		UUID uuid = player.getUniqueId();
		PlayerEntity entity = getEntity(uuid);
		
		entity.cancelInvite();
	}
	
	/**
	 * Check player has invite
	 * @param player Player
	 * @return has invite
	 */
	public boolean hasInvite(Player player) {
		PlayerEntity entity = getEntity(player);
		if(entity.whoInvited() != null) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Player left party
	 * @param player Player
	 * @param reason Reason left
	 * @return is success
	 */
	public boolean leaveParty(Player player, ReasonLeave reason) {
		if(hasParty(player)) {
			
			PartyEntity party = getParty(player);
			
			/* Initial left party event */
			PlayerLeftPartyEvent event = new PlayerLeftPartyEvent(player, party, reason);
			Bukkit.getPluginManager().callEvent(event);
			if(!event.isCancelled()) {
				if(party.getMembers() - 1 <= 0) {
					this.plugin.getPartyManager().destroyParty(party.getName());
					player.sendMessage(ChatColor.RED + party.getName() + " destroy!");
				}
				party.removeMember(player.getUniqueId());
				listPlayerParty.remove(player.getUniqueId());
				
				switch(reason) {
				case Kick:
					event.getParty().sendMessage(ChatColor.RED + player.getName() + " left the party! (Kicking)");
					break;
				case Quit:
					event.getParty().sendMessage(ChatColor.RED + player.getName() + " left the party! (Quiting)");
					break;
				case Leave:
					event.getParty().sendMessage(ChatColor.RED + player.getName() + " left the party! (Lefting)");
					break;
				case Banned:
					event.getParty().sendMessage(ChatColor.RED + player.getName() + " left the party! (Banned)");
					break;
				default:
					event.getParty().sendMessage(ChatColor.RED + player.getName() + " left the party!");
					break;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Player joining party
	 * @param player Player
	 * @param party Party
	 * @param reason Reason join
	 */
	public void joinParty(Player player, PartyEntity party, ReasonJoin reason) {
		if(!hasParty(player)) {
			/* Initial join party event */
			PlayerJoinPartyEvent event = new PlayerJoinPartyEvent(player, party, reason);
			Bukkit.getPluginManager().callEvent(event);
			
			if(!event.isCancelled()) {
				if(!party.getMember().contains(player.getUniqueId())) {
					UUID uuid = player.getUniqueId();
					listPlayerParty.put(uuid, party);
				
					party.addMember(uuid);
					
					switch(reason) {
					case Created:
						event.getParty().sendMessage(ChatColor.GREEN + player.getName() + " join the party! (Creating)");
						break;
					case Force:
						event.getParty().sendMessage(ChatColor.GREEN + player.getName() + " join the party! (Forceing)");
						break;
					case Invited:
						event.getParty().sendMessage(ChatColor.GREEN + player.getName() + " join the party! (Inviting)");
						break;
					case Join:
						event.getParty().sendMessage(ChatColor.GREEN + player.getName() + " join the party! (Joining)");
						break;
					case Queue:
						event.getParty().sendMessage(ChatColor.GREEN + player.getName() + " join the party! (Queueing)");
						break;
					default:
						event.getParty().sendMessage(ChatColor.GREEN + player.getName() + " join the party!");
						break;
					}
				}
			}
		} else {
			player.sendMessage(ChatColor.RED + "You already have a party!");
		}
	}
	
	/**
	 * Check if player has party
	 * @param player Player
	 * @return has party
	 */
	public boolean hasParty(Player player) {
		return hasParty(player.getUniqueId());
	}
	
	/**
	 * Check if player has party
	 * @param uuid UUID's player
	 * @return has party
	 */
	public boolean hasParty(UUID uuid) {
		if(!listPlayerParty.containsKey(uuid)) {
			return false;
		} else {
			return true;
		}
	}
	
	public HashMap<UUID, PartyEntity> listPlayerDebug() {
		return listPlayerParty;
	}
}
