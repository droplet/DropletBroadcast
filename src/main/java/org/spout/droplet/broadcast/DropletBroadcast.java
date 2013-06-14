/*
 * This file is part of DropletBroadcast.
 *
 * Copyright (c) 2012 Spout LLC <http://www.spout.org/>
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
package org.spout.droplet.broadcast;

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

import org.spout.droplet.broadcast.command.DropletCommand;
import org.spout.droplet.broadcast.config.DropletConfig;

public class DropletBroadcast extends CommonPlugin {
	private DropletConfig config;
	private Server server;

	@Override
	public void onDisable() {
		server.getScheduler().cancelTasks(this);
		getLogger().log(Level.INFO, "disabled");
	}

	@Override
	public void onEnable() {
		try {
			config.load();
		} catch (ConfigurationException e) {
			getLogger().log(Level.WARNING, "Error loading DropletBroadcast configuration: ", e);
		}

		// Register commands
		CommandRegistrationsFactory<Class<?>> commandRegFactory = new AnnotatedCommandRegistrationFactory(new SimpleInjector(this), new SimpleAnnotatedCommandExecutorFactory());
		server.getRootCommand().addSubCommands(this, DropletCommand.class, commandRegFactory);

		server.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				Player[] onlinePlayers = server.getOnlinePlayers();
				// Do not run the task if no players are online or they are no messages.
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
		server = (Server) getEngine();
		config = new DropletConfig(getDataFolder());
	}

	public DropletConfig getConfig() {
		return config;
	}
}
