package com.icys.party.task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.icys.party.Party;
import com.icys.party.party.PartyEntity;

import net.md_5.bungee.api.ChatColor;

public class InviteTask extends BukkitRunnable {
	
	private Party plugin;
	private Player player;
	private PartyEntity party;

	public InviteTask(Party instance, Player target, PartyEntity party) {
		this.plugin = instance;
		this.player = target;
		this.party = party;
		
		this.runTaskLater(this.plugin, 5000);
	}
	
	@Override
	public void run() {
		if(this.plugin.getPlayerManager().getEntity(this.player).whoInvited() != null) {
			this.plugin.getPlayerManager().cancelInvite(this.player);
			this.player.sendMessage(ChatColor.GRAY + "Invite timeout");
		}
		this.cancel();
	}

}
