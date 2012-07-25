package org.melonbrew.fe;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.melonbrew.fe.command.CommandType;
import org.melonbrew.fe.command.SubCommand;
import org.melonbrew.fe.command.commands.*;

public class FeCommand implements CommandExecutor {
	private final Fe plugin;
	
	private final List<SubCommand> commands;
	
	public FeCommand(Fe plugin){
		this.plugin = plugin;
		
		commands = new ArrayList<SubCommand>();
		
		commands.add(new BalanceCommand(plugin));
		commands.add(new ReloadCommand(plugin));
		commands.add(new SendCommand(plugin));
		commands.add(new TopCommand(plugin));
		commands.add(new CreateCommand(plugin));
		commands.add(new RemoveCommand(plugin));
		commands.add(new SetCommand(plugin));
		commands.add(new GrantCommand(plugin));
		commands.add(new HelpCommand(plugin, this));
	}
	
	public List<SubCommand> getCommands(){
		return commands;
	}
	
	private SubCommand getCommand(String name){
		for (SubCommand command : commands){
			String[] aliases = command.getName().split(",");
			
			for (String alias : aliases){
				if (alias.equalsIgnoreCase(name)){
					return command;
				}
			}
		}
		
		return null;
	}
	
	private String[] merge(String[]... arrays) {
		int arraySize = 0;
		
		for (String[] array : arrays) {
			arraySize += array.length;
		}
		
		String[] result = new String[arraySize];
		
		int j = 0;
		
		for (String[] array : arrays) {
			for (String string : array){
				result[j++] = string;
			}
		}
		
		return result;
	}
	
	private void sendDefaultCommand(CommandSender sender, Command cmd, String[] args){
		String command;
		
		if (sender instanceof Player){
			command = "balance";
		}else {
			command = "help";
		}
		
		onCommand(sender, cmd, null, merge(new String[]{command}, args));
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (args.length < 1){
			sendDefaultCommand(sender, cmd, args);
			
			return true;
		}
		
		SubCommand command = getCommand(args[0]);
		
		if (command == null){
			sendDefaultCommand(sender, cmd, args);
			
			return true;
		}
		
		boolean console = !(sender instanceof Player);
		
		if (console && args.length < 2 && command.getCommandType() == CommandType.CONSOLE_WITH_ARGUMENTS){
			sender.sendMessage(ChatColor.RED + "That command needs arguments.");
			
			return true;
		}
		
		if (console && command.getCommandType() == CommandType.PLAYER){
			sender.sendMessage(ChatColor.RED + Phrase.COMMAND_NOT_CONSOLE.parse(command.getFirstName()));
			
			return true;
		}
		
		if (!sender.hasPermission(command.getPermission())){
			sender.sendMessage(ChatColor.RED + Phrase.NO_PERMISSION_FOR_COMMAND.parse());
			
			return true;
		}
		
        String[] realArgs = new String[args.length - 1];

        for (int i = 1; i < args.length; i++){
                realArgs[i - 1] = args[i];
        }
		
		if (!command.onCommand(sender, cmd, realArgs)){
			sender.sendMessage(plugin.getMessagePrefix() + "Try " + ChatColor.GOLD + "/" + cmd.getName() + " " + command.getUsage());
		}
		
		return true;
	}
	
	public String parse(SubCommand command){
		ChatColor commandColor = ChatColor.GOLD;
		
		ChatColor operatorsColor = ChatColor.DARK_GRAY;
		
		ChatColor argumentColor = ChatColor.YELLOW;
		
		String finalMessage = commandColor + "/fe";
		
		if (!command.getFirstName().equalsIgnoreCase("balance")){
			 finalMessage += " " + command.getFirstName() + " ";
		}
		
		String[] split = command.getUsage().split(" ");
		
		if (split[0].equalsIgnoreCase(command.getFirstName())){
			for (int i = 1; i < split.length; i++){
				finalMessage += parseArg(split[i], operatorsColor, argumentColor) + " ";
			}
			
			finalMessage = finalMessage.substring(0, finalMessage.length() - 1);
		}else {
			finalMessage += " " + parseArg(split[0], operatorsColor, argumentColor) + " ";
			
			finalMessage = finalMessage.substring(0, finalMessage.length() - 1);
		}
		
		return finalMessage;
	}
	
	private String parseArg(String argument, ChatColor operatorsColor, ChatColor argumentColor){
		String operator = argument.substring(0, 1);
		
		argument = argument.substring(1, argument.length());
		
		String reverse;
		
		if (operator.equals("[")){
			reverse = "]";
		}else {
			reverse = ")";
		}
		
		argument = argument.substring(0, argument.length() - 1);
		
		argument = operatorsColor + operator + argumentColor + argument + operatorsColor + reverse;
		
		return argument;
	}
}