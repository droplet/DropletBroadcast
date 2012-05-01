/*
 * This file is part of DropletAlert (http://www.spout.org/).
 *
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

import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.NestedCommand;
import org.spout.api.exception.CommandException;

import org.spout.droplet.DropletAlertPlugin;

/**
 * This is our parent command (the first one in the sequence of commands)
 *
 * Equates to: /droplet
 */
public class DropletCommand {
	private final DropletAlertPlugin plugin;

	/**
	 * We must pass in an instance of our plugin's object for the annotation to register under the factory.
	 * @param instance
	 */
	public DropletCommand(DropletAlertPlugin instance) {
		plugin = instance;
	}

	//This is the command. Will detail all the options later.
	@Command(aliases = {"droplet"}, usage = "", desc = "Access droplet commands", min = 1, max = 1)
	//This is the class with all subcommands under /droplet
	@NestedCommand(DropletCommands.class)
	public void droplet(CommandContext args, CommandSource source) throws CommandException {
		//Droplet does nothing
	}
}
