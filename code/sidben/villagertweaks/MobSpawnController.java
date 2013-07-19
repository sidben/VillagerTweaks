package sidben.villagertweaks;

import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.PotentialSpawns;


public class MobSpawnController {

	
	@ForgeSubscribe
	public void getPotentialSpawns(PotentialSpawns e)
	{
		List<SpawnListEntry> mobsList = e.list;
		BiomeGenBase biome = e.world.getBiomeGenForCoords(e.x, e.z);
		
		
		// Debug
		System.out.println("=================================================");
		System.out.println("MobSpawnController.getPotentialSpawns()");
		System.out.println("	Side:       " + FMLCommonHandler.instance().getEffectiveSide());
		System.out.println("	Time:       " + e.world.getWorldTime());
		System.out.println("	Rain:       " + e.world.isRaining());
		System.out.println("	Moon Phase: " + e.world.getMoonPhase());
		System.out.println("	List size:  " + e.list.size());
		System.out.println("    Coords:     " + e.x + ", " + e.y + ", " + e.z);
		System.out.println("	Biome:      " + biome.biomeID + " / " + biome.biomeName);
		System.out.println("");
		System.out.println("  Lista:");
		for(int i = 0; i < mobsList.size(); i++)
		{
			System.out.println("	#" + i + ": " + mobsList.get(i).entityClass.toString());
		}
		System.out.println("=================================================");


	}
	
}
