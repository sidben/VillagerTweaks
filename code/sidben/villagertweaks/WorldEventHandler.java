package sidben.villagertweaks;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraft.entity.monster.EntityWitch;


public class WorldEventHandler {
	
	
	@ForgeSubscribe
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {

		if (event.entity instanceof EntityWitch) 
		{
			System.out.println("=================================================");
			System.out.println("WorldEventHandle.onEntityJoinWorldEvent()");
			System.out.println("	Event:      " + event.toString());
			System.out.println("	Side:       " + FMLCommonHandler.instance().getEffectiveSide());
			System.out.println("	Entity:     " + event.entity.getEntityName());
			System.out.println("    Coords:     " + event.entity.posX + ", " + event.entity.posY + ", " + event.entity.posZ);
			System.out.println("=================================================");
		}

	}
	

}
