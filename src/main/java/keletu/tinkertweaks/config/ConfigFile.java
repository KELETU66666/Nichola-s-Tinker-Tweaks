package keletu.tinkertweaks.config;

import com.google.common.collect.Sets;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.mantle.config.AbstractConfigFile;
import slimeknights.mantle.configurate.objectmapping.Setting;
import slimeknights.mantle.configurate.objectmapping.serialize.ConfigSerializable;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import static slimeknights.tconstruct.tools.harvest.TinkerHarvestTools.*;

import java.io.File;
import java.util.*;

@ConfigSerializable
public class ConfigFile extends AbstractConfigFile {

    private final static int CONFIG_VERSION = 4;

    private String[] defaultModifiers = new String[]{"haste", "luck", "diamond", "reinforced", "soulbound", "mending_moss", "glowing"};
    private String[] allModifiers = new String[]{"aquadynamic", "hovering", "harvestwidth", "endspeed", "momentum", "superheat", "baconlicious", "soulbound", "reinforced", "sharp", "crumbling", "splintering", "crude2", "crude1",
            "stiff", "poisonous", "webbed", "harvestheight", "flammable", "coldblooded", "holy", "established", "luck", "unnatural", "smite", "glowing", "mending_moss", "haste", "jagged", "dense", "diamond", "shocking",
            "fiery", "heavy", "fractured", "enderfence", "hellish", "sharpness", "lightweight", "fins", "bane_of_arthopods", "splitting", "necrotic", "shulking", "insatiable", "prickly", "spiky", "petramor"};
    private static Map<Integer, String> defaultLevelTitles = new HashMap<Integer, String>() {{
        put(0, "Like new");
        put(1, "Clumsy");
        put(2, "Comfortable");
        put(3, "Accustomed");
        put(4, "Adept");
        put(5, "Expert");
        put(6, "Master");
        put(7, "Grandmaster");
        put(8, "Heroic");
        put(9, "Legendary");
        put(10, "Godlike");
        put(11, "Awesome");
        put(19, "MoxieGrrl");
        put(42, "boni");
        put(66, "Jadedcat");
        put(99, "Hacker");
    }};
    private static Map<Integer, String> defaultLevelupMessages = new HashMap<Integer, String>() {{
        put(2, "You begin to feel comfortable handling the %s");
        put(3, "You are now accustomed to the weight of the %s");
        put(4, "You have become adept at handling the %s");
        put(5, "You are now an expert at using the %s !");
        put(6, "You have mastered the %s!");
        put(7, "You have grandmastered the %s!");
        put(8, "You feel like you could fulfill mighty deeds with your %s!");
        put(9, "You and your %s are living legends!");
        put(10, "No god could stand in the way of you and your %s!");
        put(11, "Your %s is pure awesome.");
    }};
    private static Map<String, String> defaultModifierMessages = new HashMap<String, String>() {{
        put("aquadynamic", "Mine faster underwater! (\u00A79+Aquadynamic\u00A73)");
        put("autosmelt", "No furnace needed! (\u00A76+Autosmelt\u00A73)");
        put("bane_of_arthropods", "Spiders don't stand a chance! (\u00A71+Bane of Arthropods\u00A73)");
        put("beheading", "So many heads to take, so little time... (\u00A75+Beheading\u00A73)");
        put("diamond", "Harder, better, faster, stronger (\u00A7b+Diamond\u00A73)");
        put("emerald", "50 percent more durability (\u00A7a+Emerald\u00A73)");
        put("fiery", "Toasty! (\u00A76+Fiery\u00A73)");
        put("fins", "Water no longer inhibits projectile motion (\u00A79+Fins\u00A73)");
        put("glowing", "Happiness can be found, even in the darkest of times, if one only remembers to turn on the light. (\u00A7e+Glowing\u00A73)");
        put("haste", "Adding redstone to a tool seems to increase its speed. (\u00A74+Haste\u00A73)");
        put("knockback", "For when you need some personal space (\u00A77+Knockback\u00A73)");
        put("lightweight", "It is now 10 percent faster (\u00A7b+Lightweight\u00A73)");
        put("luck", "Increased chance of drops (\u00A79+Luck\u00A73)");
        put("mending_moss", "Your tool regenerates in sunlight (\u00A72+Mending\u00A73)");
        put("necrotic", "Lifesteal based on damage dealt (\u00A74+Necrotic\u00A73)");
        put("reinforced", "+10 percent chance to not use durability (\u00A70+Reinforced\u00A73)");
        put("sharp", "Quartz-honed edges deal extra damage (\u00A7f+Sharpness\u00A73)");
        put("shulking", "Levitate your foes, holding them in midair (\u00A75+Shulking\u00A73)");
        put("smite", "Obliterate the undead! (\u00A7e+Smite\u00A73)");
        put("soulbound", "It will follow you anywhere, even into the afterlife (\u00A78+Soulbound\u00A73)");
        put("splitting", "Chance to split into extra projectiles (\u00A7e+Splitting\u00A73)");
        put("stiff", "Blocking is effective against more powerful blows (\u00A77+Stiff\u00A73)");
        put("webbed", "Slow targets on hit (\u00A7f+Webbed\u00A73)");
        put("writable", "+1 Modifier (\u00A7f+Writable\u00A73)");
    }};


    @Setting
    General general = new General();
    @Setting
    ToolXP toolxp = new ToolXP();
    @Setting
    Modifier modifier = new Modifier();
    @Setting
    BonusStats bonusstats = new BonusStats();
    @Setting
    Messages messages = new Messages();
    @Setting
    Debugs debugs = new Debugs();
    @Setting
    Heads heads = new Heads();
    @Setting
    Items items = new Items();

    public ConfigFile() {
    }

    public ConfigFile(File file) {
        super(file);
    }

    @Override
    public int getConfigVersion() {
        return CONFIG_VERSION;
    }

    @Override
    public void insertDefaults() {
        clearNeedsSaving();
        // fill in defaults for missing entries
        TinkerRegistry.getAllModifiers().stream()
                .filter(mod -> Arrays.stream(defaultModifiers).anyMatch(it -> it.equals(mod.getIdentifier())))
                .forEach(mod -> modifier.modifiers.add(mod.getIdentifier()));
        // Fill in modifier messages:
        // messages.levelTitles.putAll(defaultLevelTitles);
        // Fill in levelup messages:
        // messages.levelupMessages.putAll(defaultLevelupMessages);
        // Fill in modifier messages:
        // messages.modifierMessages.putAll(defaultModifierMessages);

        TinkerRegistry.getTools().stream()
                .filter(tool -> !toolxp.baseXpForTool.containsKey(tool))
                .forEach(tool -> {
                    toolxp.baseXpForTool.put(tool, getDefaultXp(tool));

                    List<String> modifiers = new ArrayList<>();

                    Arrays.stream(allModifiers)
                            .filter(mod -> TinkerRegistry.getModifier(mod) != null)
                            .filter(mod -> {
                                try {
                                    ItemStack toolInstance = tool.getDefaultInstance();
                                    NBTTagCompound tag = TagUtil.getToolTag(toolInstance);
                                    tag.setInteger(Tags.FREE_MODIFIERS, 100);
                                    TagUtil.setToolTag(toolInstance, tag);
                                    System.out.print(mod);
                                    return TinkerRegistry.getModifier(mod).canApply(toolInstance, toolInstance);
                                } catch (TinkerGuiException e) {
                                    e.printStackTrace();
                                    return false;
                                }
                            })
                            .forEach(modifiers::add);

                    modifier.modifiersForTool.put(tool, modifiers);

                    setNeedsSaving();
                });


    }

    private int getDefaultXp(Item item) {
        Set<Item> aoeTools = Sets.newHashSet(hammer, excavator, lumberAxe);
        if (scythe != null) {
            aoeTools.add(scythe);
        }

        if (aoeTools.contains(item)) {
            return 9 * toolxp.defaultBaseXP;
        }
        return toolxp.defaultBaseXP;
    }

    private static String[] defaultExcludedTools = new String[]{
            // botania
            "botania:manasteel_axe",
            "botania:manasteel_pick",
            "botania:manasteel_shovel",
            // Flaxbeards Steam Power
            "Steamcraft:axeGildedGold",
            "Steamcraft:pickGildedGold",
            "Steamcraft:shovelGildedGold",
            "Steamcraft:axeBrass",
            "Steamcraft:pickBrass",
            "Steamcraft:shovelBrass",
            // IC2
            "IC2:itemToolBronzeAxe",
            "IC2:itemToolBronzePickaxe",
            "IC2:itemToolBronzeSpade",
            // Railcraft
            "Railcraft:tool.steel.axe",
            "Railcraft:tool.steel.pickaxe",
            "Railcraft:tool.steel.shovel"
    };
    private static String[] defaultExcludedHoes = new String[]{
            "Steamcraft:hoeGildedGold",
            "Steamcraft:hoeBrass",
            "IC2:itemToolBronzeHoe",
            "Railcraft:tool.steel.hoe"
    };
    private static String[] defaultExcludedSwords = new String[]{
            "botania:manasteel_sword",
            "steamcraft:swordGildedGold",
            "Steamcraft:swordBrass",
            "ThermalExpansion:tool.battleWrenchInvar",
            "IC2:itemToolBronzeSword",
            "Railcraft:tool.steel.sword"
    };
    private static String[] defaultExcludedBows = new String[]{

    };

    private static String[] defaultAllowMod = new String[]{
            "minecraft",
            "Metallurgy",
            "Natura",
            "BiomesOPlenty",
            "ProjRed|Exploration",
            "appliedenergistics2",
            "MekanismTool",
            "ThermalFoundation"
    };

    @ConfigSerializable
    static class General {
        @Setting(comment = "Changes the amount of modifier slots a newly built tool gets (default: 3).")
        public int newToolMinModifiers = 0;

        @Setting(comment = "Maximum achievable levels. If set to 0 or lower there is no upper limit")
        public int maximumLevels = -1;

        @Setting(comment = "If set to true, you get a random modifier on level up (default: true).")
        public boolean bonusRandomModifier = true;

        @Setting(comment = "If set to true, you get an extra free modifier slot on level up (default: false).")
        public boolean bonusModifierSlot = false;

        @Setting(comment = "If set to true, your tools' base stats increase on level up (default: false).")
        public boolean bonusStats = false;

        @Setting(comment = "Change durability of all tool materials (in percent)")
        public int durabilityPercentage = 80;
        @Setting(comment = "Change mining speed of all tool materials (in percent)")
        public int miningSpeedPercentage = 1;

        @Setting(comment = "Does mobhead upgrade requires modifier")
        public boolean mobHeadRequiresModifier = false;

        @Setting(comment = "levelingPickaxeBoost")
        public boolean levelingPickaxeBoost = true;

        @Setting(comment = "Makes all non-TConstruct tools mine nothing")
        public boolean nerfVanillaTools = true;
        @Setting(comment = "Makes all non-TConstruct hoes to not be able to hoe ground. Use the Mattock.")
        public boolean nerfVanillaHoes = false;
        @Setting(comment = "Makes all non-TConstruct swords useless. Like whacking enemies with a stick.")
        public boolean nerfVanillaSwords = false;
        @Setting(comment = "Makes all non-TConstruct bows useless. You suddenly forgot how to use a bow.")
        public boolean nerfVanillaBows = false;
        @Setting(comment = "Change the type of the exclusion.\n'blacklist' means the listed tools are made unusable.\n'whitelist' means ALL tools except the listed ones are unusable.")
        public boolean excludedToolsIsWhitelist = false;
    }

    @ConfigSerializable
    static class ToolXP {
        @Setting(comment = "Base XP used when no more specific entry is present for the tool")
        public int defaultBaseXP = 500;

        @Setting(comment = "Base XP for each of the listed tools")
        public Map<Item, Integer> baseXpForTool = new HashMap<>();

        @Setting(comment = "How much the XP-per-Level is multiplied by each time the tool levels up")
        public float levelMultiplier = 2f;
    }

    @ConfigSerializable
    static class Modifier {
        @Setting(comment = "Modifiers used when no more specific entry is present for the tool.")
        public List<String> modifiers = new ArrayList<>();

        @Setting(comment = "Modifiers for each of the listed tools")
        public Map<Item, List<String>> modifiersForTool = new HashMap<>();
    }

    @ConfigSerializable
    static class BonusStats {
        @Setting(comment = "Adds to base damage. Applied each levelup. Does nothing if bonusStats is false. Default = 1.0")
        public float damageBonus = 1.0f;

        @Setting(comment = "Multiplies base durability. Applied each levelup. Does nothing if bonusStats is false. Default = 1.1")
        public float durabilityMultiplier = 1.1f;

        @Setting(comment = "Adds to base mining speed. Applied each levelup. Does nothing if bonusStats is false. Default = 0.5")
        public float miningSpeedBonus = 0.5f;

        @Setting(comment = "Multiplies base attack speed. Applied each levelup. Does nothing if bonusStats is false. Default = 1.05")
        public float attackSpeedMultiplier = 1.05f;

        @Setting(comment = "Adds to base draw speed. Only affects bows and crossbows. Applied each levelup. Does nothing if bonusStats is false. Default = 0.5")
        public float drawSpeedBonus = 0.5f;

        @Setting(comment = "Adds to base projectile speed. Only affects bows and crossbows. Applied each levelup. Does nothing if bonusStats is false. Default = 0.5")
        public float projectileSpeedBonus = 0.5f;
    }

    @ConfigSerializable
    static class Messages {
        @Setting(comment = "Use level titles from this config file instead of hard-coded values? (This is the part of the tooltip that says your skill with the tool is Clumsy or Accustomed or Legendary, etc)")
        public boolean configLevelTitles = true;
        @Setting(comment = "Level titles go here. The level itself will be used when it does not have a specific title.")
        public Map<Integer, String> levelTitles = new HashMap<>(defaultLevelTitles);
        // public Map<Integer, String> levelTitles = defaultLevelTitles;
        // levelTitles.putAll(defaultLevelTitles);

        @Setting(comment = "Use levelup messages from this config file instead of hard-coded values? (This is the message printed to the chat telling you what level your tool has reached)")
        public boolean configLevelupMessages = true;
        @Setting(comment = "Levelup messages go here. The '%s' will be replaced with the name of your tool.")
        public Map<Integer, String> levelupMessages = new HashMap<>(defaultLevelupMessages);
        // public Map<Integer, String> levelupMessages = defaultLevelupMessages;
        // levelupMessages.putAll(defaultLevelupMessages);
        @Setting(comment = "Generic Levelup message to fall back on:")
        public String genericLevelupMessage = "Your %s has reached level %d";

        @Setting(comment = "Use modifier messages from this config file instead of hard-coded values? (This is the message printed to the chat telling you what random modifier you got)")
        public boolean configModifierMessages = true;
        @Setting(comment = "Modifier messages go here. Look up Minecraft color codes if you are confused by this symbol: \u00A7")
        public Map<String, String> modifierMessages = new HashMap<>(defaultModifierMessages);
        // public Map<String, String> modifierMessages = defaultModifierMessages;
        // modifierMessages.putAll(defaultModifierMessages);
        @Setting(comment = "Generic modifier message to fall back on:")
        public String genericModifierMessage = "Your tool has gained a new modifier!";

        @Setting(comment = "Message to play in chat when a tool's stats increase (only applicible if the BonusStats module is enabled; empty by default.):")
        public String genericStatsUpMessage = "";
    }

    @ConfigSerializable
    static class Heads {
        @Setting(comment = "Base percentage for a head to drop")
        public int baseHeadDropChance = 5;

        @Setting(comment = "Percentage added to base percentage per level of Beheading modifier")
        public int beheadingHeadDropChance = 2;

    }

    @ConfigSerializable
    static class Items {
        @Setting(comment = "Can clay bucket fill hot fluid")
        public boolean bucketHotFluids = true;

    }

    @ConfigSerializable
    static class Debugs {
        @Setting(comment = "Logs when the harvest level of a block is changed")
        public boolean logHarvestLevelChanges = true;

        @Setting(comment = "Logs when the mining level of a (non-tinker) tool is changed")
        public boolean logMiningLevelChanges = true;

        @Setting(comment = "Logs every single thing done by the Override module. Use at your own risk. ;)")
        public boolean logOverrideChanges = true;
    }
}
