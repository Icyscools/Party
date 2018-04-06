package com.icys.party.party;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class PartyEntity {
	
	private List<UUID> 	listMembers;
	private List<UUID>	invitedList;
	private int			maxPlayer;
	private String		type;
	private UUID 		leader;
	private String		name;
	
	public PartyEntity(String name, UUID leader) {
		
		this.name = name;
		this.leader = leader;
		this.listMembers = new ArrayList<UUID>();
		this.maxPlayer = 4;
		this.type = "public";
		
		//working in updatePlayerParty()
		//this.listMembers.add(leader);
		
	}
	
	public PartyEntity(String name) {
		
		this.name = name;
		this.leader = null;
		this.listMembers = new ArrayList<UUID>();
		this.maxPlayer = 4;
		this.type = "public";
		
	}
	
	/**
	 * Getting name of party
	 * @return name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Add player to party by uuid
	 * @param uuid Player's UUID
	 */
	public void addMember(UUID uuid) {
		this.listMembers.add(uuid);
	}
	
	/**
	 * Remove player by party by uuid
	 * @param uuid Player's UUID
	 */
	public void removeMember(UUID uuid) {
		if(this.listMembers.contains(uuid)) {
			this.listMembers.remove(uuid);
			if(getLeader() == uuid) {
				Iterator<UUID> iter = this.listMembers.iterator();
				while(iter.hasNext()) {
					this.leader = iter.next();
					break;
				}
			}
			
			/*
			if(getLeader() == uuid) {
				for(UUID u : this.listMembers) {
					if(u == getLeader()) {
						continue;
					} else {
						this.leader = u;
						break;
					}
				}
				Bukkit.getPlayer(this.leader).sendMessage(ChatColor.GREEN + "You're now leader!");
			}
			
			//iter remove
			Iterator<UUID> iter = this.listMembers.iterator();
			while(iter.hasNext()) {
				UUID _u = iter.next();
				if(_u == uuid) {
					iter.remove();
				}
			}
			*/
		}
	}
	
	/**
	 * Setting player to leader of party by uuid
	 * @param uuid Player's UUID
	 */
	public void setLeader(UUID uuid) {
		Bukkit.getPlayer(this.leader).sendMessage("You're no longer a leader");
		
		this.leader = uuid;
		Bukkit.getPlayer(this.leader).sendMessage("You're now a leader");
	}
	
	/**
	 * Setting player to leader of party
	 * @param player Player
	 */
	public void setLeader(Player player) {
		Bukkit.getPlayer(this.leader).sendMessage("You're no longer a leader");
		
		this.leader = player.getUniqueId();
		player.sendMessage("You're now a leader");
	}
	
	/**
	 * Getting Leader
	 * @return UUID of leader
	 */
	public UUID getLeader() {
		return this.leader;
	}
	
	/**
	 * Getting list of party members
	 * @return List of members 
	 */
	public List<UUID> getMember() {
		return this.listMembers;
	}
	
	/**
	 * Getting amount of party members
	 * @return Amount of party members
	 */
	public int getMembers() {
		return this.listMembers.size();
	}
	
	/**
	 * Setting max amount of party member that can hold
	 * @param m Max amount
	 */
	public void setMaxMember(int m) {
		this.maxPlayer = m;
	}

	/**
	 * Getting max amount of party member that can hold
	 * @return Max amount of party member that can hold
	 */
	public int getMaxMember() {
		return this.maxPlayer;
	}
	
	/**
	 * Getting type of party (public or private)
	 * @return Type of party
	 */
	public String getType() {
		return this.type;
	}
	
	/**
	 * Check if party is empty
	 * @return Party empty
	 */
	public boolean isEmpty() {
		return listMembers.isEmpty();
	}

	/**
	 * Sending message to all party member
	 * @param str String
	 */
	public void sendMessage(String str) {
		for(UUID uuid : listMembers) {
			if(Bukkit.getOnlinePlayers().contains(Bukkit.getOfflinePlayer(uuid))) {
				Bukkit.getPlayer(uuid).sendMessage(str);
			}
		}
	}

}
