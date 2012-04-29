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
package org.spout.droplet;

import java.util.logging.Level;

import org.spout.api.Spout;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.player.Player;
import org.spout.api.plugin.CommonPlugin;

public class DropletAlert extends CommonPlugin {
	private DropletConfig config;

	@Override
	public void onLoad() {
		config = new DropletConfig(getDataFolder());
	}

	@Override
	public void onEnable() {
		try {
			config.load();
		} catch (ConfigurationException e) {
			getLogger().log(Level.WARNING, "Error loading DropletAlert configuration: ", e);
		}
		Spout.getEngine().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				//Do not run the task if no players are online or they are no messages.
				if (DropletConfig.MESSAGES.getStringList().size() == 0 || Spout.getEngine().getOnlinePlayers().length == 0) {
					return;
				}
				for (Player plr : Spout.getEngine().getOnlinePlayers()) {
					for (String str : DropletConfig.MESSAGES.getStringList()) {
						plr.sendMessage(str);
					}
				}
			}
		}, DropletConfig.DELAY.getLong(), DropletConfig.REPEAT.getLong());
	}

	@Override
	public void onDisable() {
		try {
			config.save();
		} catch (ConfigurationException e) {
			getLogger().log(Level.WARNING, "Error saving DropletAlert configuration: ", e);
		}
		Spout.getEngine().getScheduler().cancelTasks(this);
	}
}
