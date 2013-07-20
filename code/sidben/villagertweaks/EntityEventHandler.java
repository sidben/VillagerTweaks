package sidben.villagertweaks;

import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;


/*
 * Initial Reference: ZeroLevels/ChickenShed
 * https://github.com/ZeroLevels/ChickenShed/blob/4214365b5087c46377ace54c33cdbfe80c9aa77c/vazkii/chickenshed/ChickenShed.java
 */

public class EntityEventHandler {

	
	@ForgeSubscribe
	public void onLivingDeath(LivingDeathEvent e) {
	}


	
	@ForgeSubscribe
	public void onLivingDrops(LivingDropsEvent event) {

		// Break the method if the world is a client
		if (event.entity.worldObj.isRemote) return;
		

		// It's a zombie!
		if (event.entity instanceof EntityZombie) 
		{
			boolean haveIngot = false;
			int emeralds = 0;
			int targetEmeraldChance = 40 + (11 * event.lootingLevel);		// Chance to get an emerald, should range from 40% to 73%
			ItemStack droppedItem;
			EntityZombie zombie = (EntityZombie) event.entity;
			

			
			// Only adult villager zombies will be affected, and only if killed by players
			if (zombie.isChild() || !zombie.isVillager() || !event.recentlyHit) return;
			

			// Gets the current dropped items
			for(int i = 0; i < event.drops.size(); i++) {
				droppedItem = event.drops.get(i).getEntityItem(); 
				if (droppedItem != null) {
					if (droppedItem.itemID == Item.ingotIron.itemID) haveIngot = true;
				}
			}
			


			// Never drops emeralds when dropping iron ingot
			if (!haveIngot)		
			{
				// Will this zombie drop an emerald?
				Random rand = new Random();
				int chanceEmerald = rand.nextInt(100) + 1;
				if (chanceEmerald <= targetEmeraldChance) emeralds = 1;		// drop 1 emerald, yay!

				// Debug
				ModVillagerTweaks.logDebugInfo("    Drop Chance: " + chanceEmerald + "/" + targetEmeraldChance + " = " + emeralds);
			}

			// Adds the emeralds to the drop list
			if (emeralds > 0)
			{
				EntityItem item = new EntityItem(zombie.worldObj, zombie.posX, zombie.posY, zombie.posZ);
				ItemStack stack = new ItemStack(Item.emerald.itemID, emeralds, 0);
				item.getDataWatcher().updateObject(10, stack);
				event.drops.add(item);
			}
			
		}
	}
	
}
