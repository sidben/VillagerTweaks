package sidben.villagertweaks;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;


/*
 * Ref: ZeroLevels/ChickenShed
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
		

		if (event.entity instanceof EntityZombie) 
		{
			EntityZombie zombie = (EntityZombie) event.entity;
			
			// Only adult villager zombies will be affected
			if (zombie.isChild() || !zombie.isVillager()) return;


			// This zombie will drop an emerald?
			// boolean haveEmerald = false; 
			
			EntityItem item = new EntityItem(zombie.worldObj, zombie.posX, zombie.posY, zombie.posZ);
			ItemStack stack = new ItemStack(Item.emerald.itemID, 1, 0);
			item.getDataWatcher().updateObject(10, stack);
			event.drops.add(item);
			
			
			/*
			for (EntityItem item : event.drops) 
			{
				if(item != null) 
				{
					// The watchable object 10 is the itemstack of the item entity
					ItemStack originalStack = item.getDataWatcher().getWatchableObjectItemStack(10);
					ItemStack stack = originalStack.copy();
					if(stack != null && stack.itemID == Item.feather.itemID) {
						stack.stackSize = MathHelper.getRandomIntegerInRange(item.worldObj.rand, 1, 1);
						item.getDataWatcher().updateObject(10, stack); // Update the object with the new stack
						setFeather = true; // A feather was found
					}
				}
			}

			if(!setFeather && 1 > 0) { // If a feather wasn't found, it adds one, if the minimum isn't 0 already that is
				EntityItem item = new EntityItem(chicken.worldObj, chicken.posX, chicken.posY, chicken.posZ);
				int stackSize = MathHelper.getRandomIntegerInRange(item.worldObj.rand, 1, 1);
				ItemStack stack = new ItemStack(Item.feather.itemID, stackSize, 0);
				item.getDataWatcher().updateObject(10, stack);
				event.drops.add(item);
			}
			*/
		}
	}
	
}
