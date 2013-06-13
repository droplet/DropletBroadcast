/*
 * This file is part of DropletBroadcast.
 *
 * Copyright (c) 2012, Spout LLC <http://www.spout.org/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.spout.droplet.broadcast.command;

import org.spout.api.Spout;
import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.Permissible;
import org.spout.api.exception.CommandException;
import org.spout.api.exception.ConfigurationException;
import org.spout.droplet.broadcast.DropletBroadcast;
import org.spout.droplet.broadcast.config.DropletConfig;

import java.util.logging.Level;

/**
 * All subcommands go in this class.
 * <p/>
 * Equates to /droplet reload or /droplet addmessage
 */
public class DropletCommands {
	private final DropletBroadcast plugin;

	public DropletCommands(DropletBroadcast instance) {
		plugin = instance;
	}

	@Command(aliases = {"reload"}, usage = "", desc = "Reload the droplet configuration.")
	@Permissible("droplet.command.reload")
	public void reload(CommandSource source, CommandArguments args) throws CommandException {
		// Load the config again.
		try {
			plugin.getConfig().load();
		} catch (ConfigurationException e) {
			Spout.getLogger().log(Level.WARNING, "Error saving DropletBroadcast configuration: ", e);
		}
	}

	@Command(aliases = {"addmessage", "addm"}, usage = "<message>", desc = "Add a new message to show to players.")
	@Permissible("droplet.command.addmessage")
	public void addmessage(CommandSource source, CommandArguments args) throws CommandException {
        String message = args.getString(0);
		//ChatArguments chat = ChatArguments.fromString(message);
		// Make sure we don't add the same message twice...
		for (String s : DropletConfig.MESSAGES.getStringList()) {
			if (s.equalsIgnoreCase(message)) {
				source.sendMessage("\"" + message + "\" has already been added.");
				break;
			}
		}

		DropletConfig.MESSAGES.getStringList().add(message);
		source.sendMessage("\"" + message + "\" was added to the messages list.");
	}

	@Command(aliases = {"removemessage", "remme"}, usage = "<message>", desc = "Removes a message from the messages available.")
	@Permissible("droplet.command.removemessage")
	public void removemessage(CommandSource source, CommandArguments args) throws CommandException {
        String message = args.getString(0);
		// Search for the message to be removed.
		for (String s : DropletConfig.MESSAGES.getStringList()) {
			if (s.equalsIgnoreCase(message)) {
				DropletConfig.MESSAGES.getStringList().remove(message);
				source.sendMessage("\"" + message + "\" was removed from the messages list.");
				return;
			}
		}
		// No messages found :'(
		source.sendMessage("\"" + message + "\" was not found in the messages list!");
	}
}
