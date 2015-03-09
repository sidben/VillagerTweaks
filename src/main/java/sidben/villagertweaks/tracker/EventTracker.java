package sidben.villagertweaks.tracker;

import net.minecraft.util.Vec3i;



public class EventTracker {
    
    private Vec3i _position;
    private String _customName;
    private Object _specialInfo;
    private int _tickAdded;
    private int _entityID;

    
    // Position where the event happened
    public Vec3i getPosition()
    {
        return _position;
    }

    // Does the event involves something with a custom name? Store it here.
    public String getCustomName()
    {
        return _customName;
    }

    // Does the event has extra information? Use this generic object.
    public Object getObject()
    {
        return _specialInfo;
    }

    // Returns the entity ID being tracked, if any.
    public int getEntityID()
    {
        return _entityID;
    }

    // Returns the tick in which this entry was created (Tick of Birth).
    public int getTOB()
    {
        return _tickAdded;
    }


    
    
    public void expireNow() {
        this._tickAdded = -1;
    }
    
    
    public EventTracker(int entityID, Vec3i pos, String customName, Object extraInfo, int expire) {
        this._entityID = entityID;
        this._customName = customName;
        this._position = pos;
        this._specialInfo = extraInfo;
        this._tickAdded = expire;
    }

    /*
    public EventTracker(int entityID, String customName, Object extraInfo, int expire) {
        this._entityID = entityID;
        this._customName = customName;
        this._position = null;
        this._specialInfo = extraInfo;
        this._expire = expire;
    }
    */


    
    @Override 
    public String toString() {
        StringBuilder r = new StringBuilder();
        
        r.append("Entity ID = " + this.getEntityID());
        r.append(", ");
        r.append("Position = ");
        if (this.getPosition() == null) {
            r.append("NULL");
        }
        else {
            r.append(this.getPosition().toString());
        }
        r.append(", ");
        r.append("Tick of Birth = " + this.getTOB());
        r.append(", ");
        r.append("Custom Name = " + this.getCustomName());
        r.append(", ");
        r.append("Extra Info = ");
        if (this.getObject() == null) {
            r.append("NULL");
        }
        else {
            r.append(this.getObject().getClass().getName());
            r.append(":");
            r.append(this.getObject().toString());
        }
        
        return r.toString();
    }
    
}