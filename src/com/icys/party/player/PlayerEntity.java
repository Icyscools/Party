package com.icys.party.player;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.icys.party.Party;
import com.icys.party.party.PartyEntity;
import com.icys.party.task.InviteTask;

public class PlayerEntity {
	
	private UUID uuid;
	private PartyEntity party;
	private UUID invite;
	private InviteTask task;
	
	public PlayerEntity(UUID uuid) {
		this.uuid = uuid;
		this.invite = null;
		party = new PartyEntity(Bukkit.getPlayer(this.uuid).getName(), this.uuid);
	}

	public PlayerEntity(Player player) {
		this.uuid = player.getUniqueId();
		this.invite = null;
		party = new PartyEntity(Bukkit.getPlayer(this.uuid).getName(), this.uuid);
	}
	
	public UUID whoInvited() {
		return this.invite;
	}
	
	public void giveInvite(UUID invite, InviteTask task) {
		this.invite = invite;
		this.task = task;
	}
	
	public boolean hasInvited() {
		return this.invite != null ? true : false;
	}
	
	public void acceptInvite(PartyEntity party) {
		this.party = party;
		cancelInvite();
	}
	
	public void cancelInvite() {
		this.invite = null;
		this.task.cancel();
	}
	
}
