package com.icys.party.party;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.icys.party.Party;

public class PartyManager {
	
	private Party plugin;
	
	private HashMap<String, PartyEntity> parties;
	private List<String>				 listParties;
	
	public PartyManager(Party instance) {
		plugin = instance;
		
		parties = new HashMap<String, PartyEntity>();
		listParties = new ArrayList<String>();
	}
	
	/**
	 * Check if party is exist 
	 * @param name Party name
	 * @return is exist
	 */
	public boolean isExist(String name) {
		if(!parties.containsKey(name)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Getting party by name
	 * @param name Party name
	 * @return PartyEntity
	 */
	public PartyEntity getParty(String name) {
		return parties.get(name);
	}
	
	/**
	 * Creating party
	 * @param name Party name
	 * @return PartyEntity
	 */
	public PartyEntity createParty(String name) {
		PartyEntity party = new PartyEntity(name);
		if(parties.containsKey(name)) {
			destroyParty(name);
		}
		
		parties.put(name, party);
		listParties.add(name);
		return party;
	}
	
	/**
	 * Creating party with leader
	 * @param name Party name
	 * @param leader UUID of leader
	 * @return PartyEntity
	 */
	public PartyEntity createParty(String name, UUID leader) {
		PartyEntity party = new PartyEntity(name, leader);
		if(parties.containsKey(name)) {
			destroyParty(name);
		}
		
		parties.put(name, party);
		listParties.add(name);
		return party;
	}
	
	/**
	 * Creating party with leader
	 * @param name Party name
	 * @param leader Player leader
	 * @return PartyEntity
	 */
	public PartyEntity createParty(String name, Player leader) {
		PartyEntity party = new PartyEntity(name, leader.getUniqueId());
		if(parties.containsKey(name)) {
			destroyParty(name);
		}
		
		parties.put(name, party);
		listParties.add(name);
		return party;
	}
	
	/**
	 * Creating empty party (No Members/Leader)
	 * Only show name in public
	 * @param name Party name
	 */
	public void createEmptyParty(String name) {
		PartyEntity party = new PartyEntity(name);
		if(parties.containsKey(name)) {
			destroyParty(name);
		}
		
		listParties.add(name);
		parties.put(name, party);
	}
	
	/**
	 * Getting party name list
	 * @return Party list
	 */
	public List<String> getListParties() {
		return listParties;
	}
	
	/**
	 * Getting PartyEntity list
	 * @return PartyEntity list
	 */
	public HashMap<String, PartyEntity> getParties() {
		return parties;
	}
	
	/**
	 * Remove party by name
	 * @param name Party name
	 */
	public void destroyParty(String name) {
		parties.remove(name);
		//string remove
		Iterator<String> iter = listParties.iterator();
		while(iter.hasNext()) {
			String str = iter.next();
			if(str == name) {
				iter.remove();
			}
		}
	}
	
}
