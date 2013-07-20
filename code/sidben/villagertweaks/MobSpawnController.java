package sidben.villagertweaks;

import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.PotentialSpawns;


public class MobSpawnController {


	

	
	@ForgeSubscribe
	public void getPotentialSpawns(PotentialSpawns event)
	{
		
		if (ConfigLoader.witchBonusSpawn) {
	
			List<SpawnListEntry> mobsList = event.list;
			BiomeGenBase biome = event.world.getBiomeGenForCoords(event.x, event.z);
			
			// Witch spawn custom rules
			int witchBiome = BiomeGenBase.swampland.biomeID;
			int witchMoon = 4;			// New moon - To debug in-game use "/time set 109000"
			int witchMinY = 62;
			int witchMaxY = 71;
			int witchListIndex = -1;
			
			
			if (event.type == EnumCreatureType.monster && biome.biomeID == witchBiome && event.y >= witchMinY && event.y <= witchMaxY) 
			{
	
				// Finds out if there is a witch in the list
				for(int i = 0; i < mobsList.size(); i++) {
					if (mobsList.get(i).entityClass.equals(EntityWitch.class)) 
					{
						witchListIndex = i;
						break;
					}
				}
	
				System.out.println("	have witch? = " + (witchListIndex >= 0));
				if (witchListIndex >= 0) {
					System.out.println("	Chance = " + mobsList.get(witchListIndex).itemWeight);
				}
				
				
				// If starts/stops raining, update the chance
				if (witchListIndex >= 0) {
					if (event.world.isRaining() && mobsList.get(witchListIndex).itemWeight != ConfigLoader.witchChanceRain) {
						mobsList.get(witchListIndex).itemWeight = ConfigLoader.witchChanceRain;
						ModVillagerTweaks.logDebugInfo("    Updating witch spawn chance - rain started");
					}
					if (!event.world.isRaining() && mobsList.get(witchListIndex).itemWeight != ConfigLoader.witchChanceBasic) {
						mobsList.get(witchListIndex).itemWeight = ConfigLoader.witchChanceBasic;
						ModVillagerTweaks.logDebugInfo("    Updating witch spawn chance - rain stopped");
					}
				}
				
				
				
				if (event.world.getMoonPhase() == witchMoon && witchListIndex < 0) 
				{
					/*
					 * All requirements filled, add Witches to the possible mobs spawn.
					 * 
					 * Signature: SpawnListEntry(entityClass, weightedProb, min, max)
					 */
					int witchChance = event.world.isRaining() ? ConfigLoader.witchChanceRain : ConfigLoader.witchChanceBasic;		// 2x more likely to spawn on raining nights
					mobsList.add(new SpawnListEntry(EntityWitch.class, witchChance, 1, 3));
	
					// Debug
					ModVillagerTweaks.logDebugInfo("    Adding witches on " + event.x + "/" + event.z + " (weight " + witchChance + ")");
				}
				
				else if (event.world.getMoonPhase() != witchMoon && witchListIndex >= 0 && witchListIndex < mobsList.size())
				{
					/*
					 * No longer able to spawn witches, remove them
					 */
					mobsList.remove(witchListIndex);
					
					// Debug
					ModVillagerTweaks.logDebugInfo("    Removing witches from " + event.x + "/" + event.z);
				}
			
				
				
	
			}
		
		}

	}
	
}
