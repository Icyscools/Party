package com.icys.party.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.icys.party.Party;

public class onJoin implements Listener {
	
	private Party plugin;
	
	public onJoin(Party instance) {
		this.plugin = instance;
	}
	
	@EventHandler
	public void onJoining(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		if(!this.plugin.getPlayerManager().alreadyInit(player)) {
			this.plugin.getPlayerManager().init(player);
		}
	}

}
