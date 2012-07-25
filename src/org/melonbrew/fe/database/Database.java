package org.melonbrew.fe.database;

import java.util.List;

import org.melonbrew.fe.Fe;

public abstract class Database {
	private final Fe plugin;
	
	public Database(Fe plugin){
		this.plugin = plugin;
	}
	
	public abstract boolean init();
	
	public abstract List<Account> getTopAccounts();
	
	public abstract double loadAccountMoney(String name);
	
	protected abstract void saveAccount(String name, double money);
	
	public abstract void removeAccount(String name);
	
	public abstract void close();
	
	public Account getAccount(String name){
		double money = loadAccountMoney(name);
		
		if (money == -1){
			return null;
		}else {
			Account account = new Account(name, this);
			
			account.setMoney(money);
			
			return account;
		}
	}
	
	public Account createAccount(String name){
		Account exists = getAccount(name);
		
		if (exists != null){
			return exists;
		}
		
		Account account = new Account(name, this);
		
		account.setMoney(plugin.getAPI().getDefaultHoldings());
		
		return account;
	}
	
	public boolean accountExists(String name){
		return getAccount(name) != null;
	}
}