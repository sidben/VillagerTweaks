package sidben.villagertweaks.handler;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class PlayerEventHandler
{

    // OBS: This method is called whenever a player interacts with something (right-click)
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onEntityInteractEvent(EntityInteractEvent event)
    {

        // check if the player right-clicked a villager
        if (event.target instanceof EntityVillager) {

            if (ConfigurationHandler.canNameVillagers) {

                // Check if the player is holding a Name Tag
                final ItemStack item = event.entityPlayer.inventory.getCurrentItem();
                if (item != null && item.getItem() == Items.name_tag) {

                    // Check if the tag have a name and the player is sneaking
                    if (item.hasDisplayName() && event.entityPlayer.isSneaking()) {

                        // Set the name to that villager
                        final EntityLiving villager = (EntityLiving) event.target;
                        villager.setCustomNameTag(item.getDisplayName());

                        // Consumes the item if not in Creative
                        if (!event.entityPlayer.capabilities.isCreativeMode) {
                            --item.stackSize;
                        }

                        // Cancel the regular event (trade GUI)
                        event.setCanceled(true);
                    }

                }

            }

        }

    }

}
