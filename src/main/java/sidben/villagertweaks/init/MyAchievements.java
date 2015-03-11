package sidben.villagertweaks.init;

//import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import sidben.villagertweaks.reference.Reference;


/*
 * How to get the achievements:
 * 
 * 
 * NameVillager
 * ===================================================
 * In game: Use a name tag to give any villager a custom name.
 * 
 * In code: Listen to the player event [EntityInteractEvent] and trigger
 * right after the naming code.
 * 
 * 
 * 
 * CureVillager
 * ===================================================
 * In game: Cure a zombie villager. The achievement triggers after
 * the villager converts. 
 * 
 * In code: Listen to the player event [EntityInteractEvent] to find
 * when a player right-clicks a villager with a golden apple. If that
 * zombie can be cured, adds the zombie ID and the player ID to a list
 *  for future check. (ServerInfoTracker.startedCuringZombie)
 * 
 * It also listen to the [EntityJoinWorldEvent] event and trigger
 * right after copying data from the cured zombie to the new
 * villager, using [ServerInfoTracker.triggerZombieCuredAchievement].
 * 
 * That code will check the list of zombies that were clicked by players
 * and give the achievement to the player that started the cure, if s/he
 * can be found. If the player logs off before the conversion s/he won't 
 * get the achievement, since the list isn't persisted anywhere.
 * 
 * 
 * 
 * InfectVillager
 * ===================================================
 * In game: Cure a zombie villager and let it be infected again shortly
 * after (up to 10 minutes after the cure).
 * 
 * In code: Listen to the [EntityJoinWorldEvent] event and to find when a
 * villager that was just cured joins the world and add that villager ID
 * to a list for future check, using [ServerInfoTracker.endedCuringZombie].
 * Must happen BEFORE [ServerInfoTracker.triggerZombieCuredAchievement]
 * since that method clears the list we need to check.
 * 
 * It will also listen to the [EntityJoinWorldEvent] event and to find when 
 * a zombie villager joins the world and trigger right after copying data from
 * the infected villager to the zombie, using [ServerInfoTracker.triggerVillagerInfectedAchievement].
 * 
 * 
 * 
 * Enchant Pumpkin
 * ===================================================
 * Not implemented.
 * 
 * 
 * 
 * Snow Golem
 * ===================================================
 * Not implemented.
 * 
 * 
 * 
 * Super Golem
 * ===================================================
 * Not implemented.
 * 
 * 
 * 
 */
public class MyAchievements
{


    public static Achievement NameVillager;
    public static Achievement CureVillager;
    public static Achievement InfectVillager;
    public static Achievement EnchantPumpkin;
    public static Achievement SnowGolem;
    public static Achievement SuperGolem;

    static AchievementPage    ModPage;



    public static void register()
    {
        NameVillager = new Achievement(Reference.ModID + ".name_villager", "name_villager", 2, -3, Items.name_tag, null);
        CureVillager = new Achievement(Reference.ModID + ".cure_villager", "cure_villager", 1, 0, Items.golden_apple, null);
        InfectVillager = new Achievement(Reference.ModID + ".infect_villager", "infect_villager", 3, 0, Items.rotten_flesh, CureVillager);
        /*
        EnchantPumpkin = new Achievement(Reference.ModID + ".enchant_pumpkin", "enchant_pumpkin", -3, -2, Blocks.pumpkin, null);
        SnowGolem = new Achievement(Reference.ModID + ".snowman", "snowman", -3, 2, Items.snowball, EnchantPumpkin);
        SuperGolem = new Achievement(Reference.ModID + ".super_golem", "super_golem", -5, 0, Blocks.iron_block, EnchantPumpkin).setSpecial();

        ModPage = new AchievementPage(Reference.ModName, NameVillager, CureVillager, InfectVillager, EnchantPumpkin, SnowGolem, SuperGolem);
        */
        
        /* 
         * Need to register the stats or else the achievement won't be saved. Doing this also allow 
         * the achievement to be given by the /achievement command.
         * (thanks TinkersConstruct's GitHub repo!)
         */
        NameVillager.registerStat();
        CureVillager.registerStat();
        InfectVillager.registerStat();

        
        ModPage = new AchievementPage(Reference.ModName, NameVillager, CureVillager, InfectVillager);
        AchievementPage.registerAchievementPage(ModPage);
    }



}