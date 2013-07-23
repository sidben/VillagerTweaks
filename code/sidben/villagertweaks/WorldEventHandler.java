package sidben.villagertweaks;


import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;



public class WorldEventHandler {

    @ForgeSubscribe
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {

        /*
         * //if (event.entity instanceof EntityWitch) //{
         * System.out.println("================================================="
         * ); System.out.println("WorldEventHandle.onEntityJoinWorldEvent()");
         * System.out.println("	Event:      " + event.toString());
         * System.out.println("	Side:       " +
         * FMLCommonHandler.instance().getEffectiveSide());
         * System.out.println("	Entity:     " + event.entity.getEntityName());
         * System.out.println("    Coords:     " + event.entity.posX + ", " +
         * event.entity.posY + ", " + event.entity.posZ);
         * System.out.println("================================================="
         * ); //}
         */
        
        if (event.entity instanceof EntityVillager) {
            EntityVillager villager = (EntityVillager) event.entity;
            int taskFireworksPriority = 8;
            
            // Debug
            System.out.println("Adding AI task to villager on " + event.entity.posX + ", " + event.entity.posY + ", " + event.entity.posZ);
            System.out.println(" Side:       " + FMLCommonHandler.instance().getEffectiveSide());

            villager.tasks.addTask(taskFireworksPriority, new EntityAILaunchFireworks(villager));
        }

    }

}
