package sidben.villagertweaks.tracker;

import java.util.ArrayList;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3i;
import sidben.villagertweaks.common.ExtendedVillagerZombie;
import sidben.villagertweaks.helper.GolemEnchantment;
import sidben.villagertweaks.helper.MagicHelper;


// TODO: add inheritance, so I can test the type of tracker and don't rely on trust


public class EventTracker
{

    private final Vec3i  _position;
    private final String _customName;
    private final Object _specialInfo;
    private int          _tickAdded;
    private final int    _entityID;


    /**
     * Position where the event happened
     * 
     */
    public Vec3i getPosition()
    {
        return _position;
    }

    /**
     * Does the event involves something with a custom name? Store it here.
     * 
     */
    public String getCustomName()
    {
        return _customName;
    }

    /**
     * Does the event has extra information? Use this generic object.
     * 
     */
    public Object getObject()
    {
        return _specialInfo;
    }

    /**
     * Returns the entity ID being tracked, if any.
     * 
     */
    public int getEntityID()
    {
        return _entityID;
    }

    /**
     * Sets the tick in which this entry was created (Tick of Birth).
     * 
     */
    public void setTOB(int tick)
    {
        this._tickAdded = tick;
    }

    /**
     * Gets the tick in which this entry was created (Tick of Birth).
     * 
     */
    public int getTOB()
    {
        return _tickAdded;
    }

    /**
     * Forces this object to "expire" so it won't be used again.
     * 
     */
    public void expireNow()
    {
        this._tickAdded = -1;
    }



    private EventTracker(int entityID, Vec3i pos, String customName, Object extraInfo) {
        this._entityID = entityID;
        this._customName = customName;
        this._position = pos;
        this._specialInfo = extraInfo;
        this._tickAdded = 0;
    }

    public EventTracker(EntityVillager villager) {
        this(villager.getEntityId(), villager.getPosition(), villager.getCustomNameTag(), new Object[] { villager.getProfession(), villager.isChild() });
    }

    public EventTracker(EntityZombie zombie, ExtendedVillagerZombie properties) {
        this(zombie.getEntityId(), zombie.getPosition(), zombie.getCustomNameTag(), properties);
    }

    public EventTracker(EntityGolem golem) {
        this(golem.getEntityId(), golem.getPosition(), "", null);
        // NOTE: custom info will be applied by the pumpkin, no need to track
        // anything but the golem ID and position. Pumpkin code will find the entity
        // and get whatever information it needs.
    }

    public EventTracker(ItemStack pumpkin, Vec3i pos) {
        this(0, pos, pumpkin.getDisplayName(), MagicHelper.getEnchantmentIds(pumpkin));
    }



    /**
     * Updates a zombie entity with the villager info this object is tracking.
     * 
     */
    public void updateZombie(EntityZombie zombie, ExtendedVillagerZombie properties)
    {

        // Note: I must trust that this object actually contain a villager info. If not, the cast below will fail.
        final Object[] extraInfo = (Object[]) this.getObject();
        final int profession = (Integer) extraInfo[0];
        final boolean isBaby = (Boolean) extraInfo[1];

        // Custom name
        if (this.getCustomName() != "") {
            zombie.setCustomNameTag(this.getCustomName());
            zombie.enablePersistence();
        }

        // Adult or child
        zombie.setChild(isBaby);

        // Profession
        if (profession >= 0 && profession <= 4) {   // vanilla professions
            properties.setProfession(profession);
        } else {
            properties.setProfession(-1);           // vanilla zombie villager
        }

    }


    /**
     * Updates a villager entity with the zombie info this object is tracking.
     * 
     * @param villager
     */
    public void updateVillager(EntityVillager villager)
    {

        // Note: I must trust that this object actually contain a zombie info. If not, the cast below will fail.
        final ExtendedVillagerZombie properties = (ExtendedVillagerZombie) this.getObject();

        // Custom name
        if (this.getCustomName() != "") {
            villager.setCustomNameTag(this.getCustomName());
        }

        // Profession
        if (properties.getProfession() >= 0 && properties.getProfession() <= 4) {   // vanilla professions
            villager.setProfession(properties.getProfession());
        }

    }
    
    
    
    /**
     * Creates a new pumpkin ItemStack with the info this object is tracking.
     * 
     */
    public ItemStack getPumpkin()
    {

        // Note: I must trust that this object actually contain a pumpkin info.
        final int[] enchantIds = (int[]) this.getObject();
        
        // Enchantment IDs
        ArrayList<GolemEnchantment> enchantedList = GolemEnchantment.convertToEnchantList(enchantIds);
        ItemStack pumpkin = MagicHelper.getEnchantedPumpkin(enchantedList);

        // Custom name
        if (this.getCustomName() != "") {
            pumpkin.setStackDisplayName(this.getCustomName());
        }


        return pumpkin;
    }



    @Override
    public String toString()
    {
        final StringBuilder r = new StringBuilder();

        r.append("Entity ID = ");
        r.append(this.getEntityID());
        r.append(", Position = ");
        if (this.getPosition() == null) {
            r.append("NULL");
        } else {
            r.append(this.getPosition().toString());
        }
        r.append(", Tick of Birth = ");
        r.append(this.getTOB());
        r.append(", Custom Name = ");
        r.append(this.getCustomName());
        r.append(", Extra Info = ");
        if (this.getObject() == null) {
            r.append("NULL");
        } else {
            r.append(this.getObject().getClass().getName());
            r.append(":");
            r.append(this.getObject().toString());
        }

        return r.toString();
    }



}