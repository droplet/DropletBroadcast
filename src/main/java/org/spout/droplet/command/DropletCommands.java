/*
 * This file is part of DropletAlert.
 *
 * Copyright (c) 2012, SpoutDev <http://www.spout.org/>
 * DropletAlert is licensed under the SpoutDev License Version 1.
 *
 * DropletAlert is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * DropletAlert is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.droplet.command;

import java.util.logging.Level;

import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.exception.CommandException;
import org.spout.api.exception.ConfigurationException;

import org.spout.droplet.DropletAlertPlugin;
import org.spout.droplet.config.DropletConfig;

/**
 * All subcommands go in this class.
 *
 * Equates to /droplet reload or /droplet addmessage
 */
public class DropletCommands {
	private final DropletAlertPlugin plugin;

	public DropletCommands(DropletAlertPlugin instance) {
		plugin = instance;
	}

	@Command(aliases = {"reload"}, usage = "", desc = "Reload the droplet configuration.")
	@CommandPermissions("droplet.command.reload")
	public void reload(CommandContext args, CommandSource source) throws CommandException {
		//load the config again.
		try {
			plugin.getConfig().load();
		} catch (ConfigurationException e) {
			Spout.getLogger().log(Level.WARNING, "Error saving DropletAlertPlugin configuration: ", e);
		}
	}

	@Command(aliases = {"addmessage", "addm"}, usage = "<message>", desc = "Add a new message to show to players.")
	@CommandPermissions("droplet.command.addmessage")
	public void addmessage(CommandContext args, CommandSource source) {
		String message = args.getString(0);
		ChatArguments chat = ChatArguments.fromString(message);
		//Make sure we don't add the same message twice...
		for (String s : DropletConfig.MESSAGES.getStringList()) {
			if (s.equalsIgnoreCase(message)) {
				source.sendMessage(new ChatArguments("\"", chat, "\" has already been added."));
				break;
			}
		}

		DropletConfig.MESSAGES.getStringList().add(message);
		source.sendMessage(new ChatArguments("\"", chat, "\" was added to the messages list."));
	}

	@Command(aliases = {"removemessage", "remme"}, usage = "<message>", desc = "Removes a message from the messages available.")
	@CommandPermissions("droplet.command.removemessage")
	public void removemessage(CommandContext args, CommandSource source) {
		String message = args.getString(0);
		ChatArguments chat = ChatArguments.fromString(message);
		//Search for the message to be removed.
		for (String s : DropletConfig.MESSAGES.getStringList()) {
			if (s.equalsIgnoreCase(message)) {
				DropletConfig.MESSAGES.getStringList().remove(message);
				source.sendMessage(new ChatArguments("\"", chat, "\" was removed from the messages list."));
				return;
			}
		}
		//No messages found :'(
		source.sendMessage(new ChatArguments("\"", chat, "\" was not found in the messages list!"));
	}
}
