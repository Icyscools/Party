package com.icys.party.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.icys.party.Party;
import com.icys.party.party.PartyEntity;
import com.icys.party.party.PartyManager;
import com.icys.party.player.PlayerManager;
import com.icys.party.type.ReasonLeave;

import net.md_5.bungee.api.ChatColor;

public class onQuit implements Listener {
	
	private Party plugin;
	private PlayerManager playerManager;
	private PartyManager partyManager;
	
	public onQuit(Party instance) {
		this.plugin = instance;
		this.playerManager = plugin.getPlayerManager();
		this.partyManager = plugin.getPartyManager();
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(this.playerManager.hasParty(player)) {
			PartyEntity party = this.playerManager.getParty(player);
			this.playerManager.leaveParty(player, ReasonLeave.Quit);
		}
	}

}
