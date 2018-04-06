package com.icys.party.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.icys.party.party.PartyEntity;
import com.icys.party.type.ReasonJoin;

public class PlayerJoinPartyEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();
	private Player player;
	private ReasonJoin reason;
	private PartyEntity party;
	private boolean isCancelled;

    public PlayerJoinPartyEvent(Player player, PartyEntity party, ReasonJoin reason) {
    	this.player = player;
    	this.reason = reason;
    	this.party = party;
    	this.isCancelled = false;
    }
    
    public Player getPlayer() {
    	return this.player;
    }
    
    public PartyEntity getParty() {
    	return this.party;
    }
    
    public ReasonJoin getReason() {
    	return this.reason;
    }
    
    public boolean isCancelled() {
    	return this.isCancelled;
    }
    
    public void setCancelled(boolean isCancelled) {
    	this.isCancelled = isCancelled;
    }
    
	@Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
