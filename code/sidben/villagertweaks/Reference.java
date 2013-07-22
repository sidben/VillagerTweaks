package sidben.villagertweaks;


/*
 * Inspired by EE3 from Pahimar
 */
public class Reference {

    // --- Forces debug mode always on. THIS SHOULD NEVER BE 'TRUE' ON RELEASE BUILDS!!!1!1
    public static final boolean ForceDebug         = false;

    // --- Mod basic info
    public static final String  ModID              = "villagertweaks";
    public static final String  ModName            = "Villager Tweaks";
    public static final String  ModVersion         = "1.0";
    public static final String  Channel            = "chVLTweaks";

    public static final String  ResourcesNamespace = Reference.ModID;

    public static final String  ServerProxyClass   = "sidben.villagertweaks.CommonProxy";
    public static final String  ClientProxyClass   = "sidben.villagertweaks.ClientProxy";

}
