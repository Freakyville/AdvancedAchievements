package com.hm.achievement.listener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import com.hm.achievement.AdvancedAchievements;
import com.hm.achievement.db.DatabasePools;

public class AchieveConsumeListener implements Listener {

	private AdvancedAchievements plugin;

	public AchieveConsumeListener(AdvancedAchievements plugin) {

		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {

		Player player = event.getPlayer();
		if (plugin.isRestrictCreative() && player.getGameMode() == GameMode.CREATIVE || plugin.isInExludedWorld(player))
			return;

		String configAchievement;

		if (event.getItem().getType().name().equals("POTION")
				&& player.hasPermission("achievement.count.consumedpotions")) {
			int consumedPotions;
			if (!DatabasePools.getConsumedPotionsHashMap().containsKey(player.getUniqueId().toString()))
				consumedPotions = plugin.getDb().getNormalAchievementAmount(player, "consumedpotions") + 1;
			else
				consumedPotions = DatabasePools.getConsumedPotionsHashMap().get(player.getUniqueId().toString()) + 1;

			DatabasePools.getConsumedPotionsHashMap().put(player.getUniqueId().toString(), consumedPotions);
			configAchievement = "ConsumedPotions." + consumedPotions;
		} else if (player.hasPermission("achievement.count.eatenitems")) {
			int eatenItems;
			if (!DatabasePools.getEatenItemsHashMap().containsKey(player.getUniqueId().toString()))
				eatenItems = plugin.getDb().getNormalAchievementAmount(player, "eatenitems") + 1;
			else
				eatenItems = DatabasePools.getEatenItemsHashMap().get(player.getUniqueId().toString()) + 1;

			DatabasePools.getEatenItemsHashMap().put(player.getUniqueId().toString(), eatenItems);
			configAchievement = "EatenItems." + eatenItems;
		} else
			return;

		if (plugin.getReward().checkAchievement(configAchievement)) {

			plugin.getAchievementDisplay().displayAchievement(player, configAchievement);
			plugin.getDb().registerAchievement(player, plugin.getPluginConfig().getString(configAchievement + ".Name"),
					plugin.getPluginConfig().getString(configAchievement + ".Message"));
			plugin.getReward().checkConfig(player, configAchievement);
		}
	}
}
