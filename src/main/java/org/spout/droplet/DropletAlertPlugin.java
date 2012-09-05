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
package org.spout.droplet;

import java.util.logging.Level;

import org.spout.api.Server;
import org.spout.api.chat.ChatArguments;
import org.spout.api.command.CommandRegistrationsFactory;
import org.spout.api.command.annotated.AnnotatedCommandRegistrationFactory;
import org.spout.api.command.annotated.SimpleAnnotatedCommandExecutorFactory;
import org.spout.api.command.annotated.SimpleInjector;
import org.spout.api.entity.Player;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.plugin.CommonPlugin;
import org.spout.api.scheduler.TaskPriority;

import org.spout.droplet.command.DropletCommand;
import org.spout.droplet.config.DropletConfig;

public class DropletAlertPlugin extends CommonPlugin {
	private DropletConfig config;

	@Override
	public void onDisable() {
		getEngine().getScheduler().cancelTasks(this);
		getLogger().log(Level.INFO, "disabled");
	}

	@Override
	public void onEnable() {
		try {
			config.load();
		} catch (ConfigurationException e) {
			getLogger().log(Level.WARNING, "Error loading DropletAlert configuration: ", e);
		}

		//Register commands
		CommandRegistrationsFactory<Class<?>> commandRegFactory = new AnnotatedCommandRegistrationFactory(new SimpleInjector(this), new SimpleAnnotatedCommandExecutorFactory());
		getEngine().getRootCommand().addSubCommands(this, DropletCommand.class, commandRegFactory);

		getEngine().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				Player[] onlinePlayers = ((Server) getEngine()).getOnlinePlayers();
				//Do not run the task if no players are online or they are no messages.
				if (DropletConfig.MESSAGES.getStringList().size() == 0 || onlinePlayers.length == 0) {
					return;
				}
				for (Player plr : onlinePlayers) {
					for (String str : DropletConfig.MESSAGES.getStringList()) {
						plr.sendMessage(new ChatArguments(str));
					}
				}
			}
		}, DropletConfig.DELAY.getLong() * 20, DropletConfig.REPEAT.getLong() * 20, TaskPriority.LOW);

		getLogger().log(Level.INFO, "b" + getDescription().getVersion() + " enabled");
	}

	@Override
	public void onLoad() {
		config = new DropletConfig(getDataFolder());
	}

	public DropletConfig getConfig() {
		return config;
	}
}
