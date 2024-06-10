package liketechnik.tinkertweaks;

import liketechnik.tinkertweaks.capability.CapabilityDamageXp;
import liketechnik.tinkertweaks.claybuckets.IguanaItems;
import liketechnik.tinkertweaks.config.ConfigSync;
import liketechnik.tinkertweaks.config.ConfigSyncPacket;
import liketechnik.tinkertweaks.level.modifier.ModToolHarvestLeveling;
import liketechnik.tinkertweaks.level.modifier.ModToolLeveling;
import liketechnik.tinkertweaks.level.modifier.ModMiningLevelBoost;
import liketechnik.tinkertweaks.mininglevel.HarvestLevelTweaks;
import liketechnik.tinkertweaks.mininglevel.TinkerMaterialTweaks;
import liketechnik.tinkertweaks.mininglevel.VanillaToolTipHandler;
import liketechnik.tinkertweaks.mobhead.IguanaMobHeads;
import liketechnik.tinkertweaks.util.HarvestLevels;
import liketechnik.tinkertweaks.util.Log;
import liketechnik.tinkertweaks.util.ModSupportHelper;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import slimeknights.mantle.network.NetworkWrapper;
import liketechnik.tinkertweaks.config.Config;
import liketechnik.tinkertweaks.debug.CommandLevelTool;
import liketechnik.tinkertweaks.debug.CommandModifierDump;

@Mod(modid = LiketechniksTinkerTweaks.MODID,
        version = LiketechniksTinkerTweaks.VERSION,
        name = "Liketechnik's Tinker Tweaks",
        dependencies = "required-after:forge@[14.21.1.2410,);"
                + "required-after:mantle@[1.12-1.3.1.21,);"
                + "required-after:tconstruct@[1.12-2.8,)",
        acceptedMinecraftVersions = "[1.12,1.13)"
)
public class LiketechniksTinkerTweaks {

    public static final String MODID = "liketechnikstinkertweaks";
    public static final String VERSION = "${version}";

    @SidedProxy(clientSide = "liketechnik.tinkertweaks.CommonProxy", serverSide = "liketechnik.tinkertweaks.CommonProxy")
    public static CommonProxy proxy;

    public static NetworkWrapper networkWrapper;

    public static ModToolLeveling modToolLeveling = new ModToolLeveling();
    public static ModToolHarvestLeveling modHarvestLeveling = new ModToolHarvestLeveling();
    private File modConfigurationDirectory;

    public static Set<Item> toolWhitelist = new HashSet<Item>();

    private static void findToolsFromConfig() {
        Log.debug("Setting up whitelist/blacklist for allowed tools");

        // cycle through all items
        for (ResourceLocation identifier : Item.REGISTRY.getKeys()) {
            Item item = Item.REGISTRY.getObject(identifier);
            // do we care about this item?
            if (!(item instanceof ItemTool || item instanceof ItemHoe || item instanceof ItemSword || item instanceof ItemBow))
                continue;

            String mod = identifier.toString().split(":")[0]; // should always be non-null... I think

            // whitelist
            if (Config.excludedToolsIsWhitelist()) {
                // on the whitelist?
                //if (Config.excludedModTools().contains(mod) || Config.excludedTools().contains(identifier))
                //    toolWhitelist.add(item);
            }
            // blacklist
            else {
                //if (!Config.excludedModTools().contains(mod) && !Config.excludedTools().contains(identifier))
                //    toolWhitelist.add(item);
            }
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Log.init(event.getModLog());

        modConfigurationDirectory = event.getModConfigurationDirectory();
        networkWrapper = new NetworkWrapper("tinkerlevel" + ":sync");
        networkWrapper.registerPacketClient(ConfigSyncPacket.class);

        HarvestLevels.updateHarvestLevelNames();

        CapabilityDamageXp.register();

        MinecraftForge.EVENT_BUS.register(this);
        IguanaItems.preInit(event);
        IguanaMobHeads.preInit(event);
    }

    @SubscribeEvent
    protected void registerTools(RegistryEvent.Register<Item> event) {
        Config.INSTANCE.load(new File(modConfigurationDirectory, "LiketechniksTinkerTweaks.cfg"));
        Config.INSTANCE.save();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // mobhead modifiers for mining boost
        registerBoostModifiers();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        TinkerMaterialTweaks.modifyToolMaterials();
        HarvestLevelTweaks.modifyHarvestLevels();
        MinecraftForge.EVENT_BUS.register(new VanillaToolTipHandler());

        IguanaItems.postInit(event);
        IguanaMobHeads.postInit(event);
        //if(Config.changeDiamondModifier)
        //  changeDurabilityModifiers();

        MinecraftForge.EVENT_BUS.register(liketechnik.tinkertweaks.EventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(EntityXpHandler.INSTANCE);
        if (event.getSide().isServer()) {
            MinecraftForge.EVENT_BUS.register(new ConfigSync());
        }
    }

    private void registerBoostModifiers()
    {
        // zombie head
        new ModMiningLevelBoost("zombiehead", getVanillaMobHead(2), HarvestLevels._2_copper);
        // skeleton skull
        new ModMiningLevelBoost("skeletonhead", getVanillaMobHead(0), HarvestLevels._3_iron);
        // creeper head
        new ModMiningLevelBoost("creeperhead", getVanillaMobHead(4), HarvestLevels._5_diamond);

      //  if(IguanaTweaksTConstruct.pulsar.isPulseLoaded(Reference.PULSE_MOBHEADS)) {
            // pigman head
            new ModMiningLevelBoost("zombiepigmanhead", getIguanaMobHead(1), HarvestLevels._5_diamond);
            // blaze head
            new ModMiningLevelBoost("blazehead", getIguanaMobHead(2), HarvestLevels._6_obsidian);
            // blizz head
            if(ModSupportHelper.ThermalFoundation)
                new ModMiningLevelBoost("blizzhead", getIguanaMobHead(3), HarvestLevels._6_obsidian);
            // enderman head
            new ModMiningLevelBoost("endermanhead", getIguanaMobHead(0), HarvestLevels._7_ardite);
       // }

        // wither head
        new ModMiningLevelBoost("witherskeletonhead", getVanillaMobHead(1), HarvestLevels._8_cobalt);
        // netherstar
        new ModMiningLevelBoost("netherstar", getVanillaMobHead(5), HarvestLevels._9_manyullym);

        // rendering code
        //ToolCore[] tools = new ToolCore[] { TinkerHarvestTools.pickaxe, TinkerHarvestTools.hammer };
        //int[] modifierIds = new int[] { 20, 21, 22, 23, 24, 25, 26, 27, 28 };
        //String[] renderNames = new String[] { "zombiehead", "skeletonskull", "creeperhead", "zombiepigmanhead", "blazehead", "blizzhead", "endermanhead", "witherskeletonskull", "netherstar" };

        //for (ToolCore tool : tools)
        //    for (int index = 0; index < modifierIds.length; ++index)
        //        TinkerRegistryClient.addEffectRenderMapping(tool, modifierIds[index], Reference.RESOURCE, renderNames[index], true);
    }
    
    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandLevelTool());
        event.registerServerCommand(new CommandModifierDump());
    }

    private ItemStack getVanillaMobHead(int meta)
    {
        return new ItemStack(Items.SKULL, 1, meta);
    }

    private ItemStack getIguanaMobHead(int meta)
    {
        return new ItemStack(IguanaMobHeads.skullItem, 1, meta);
    }

    //private void changeDurabilityModifiers()
    //    {
    //        // deactivate mininglevel increase by tcon
    //        PHConstruct.miningLevelIncrease = false;
    //
    //        Log.debug("Adding Diamond/Emerald Modifiers for Mining Levels");
    //        TinkerRegistry.registerModifier(new ModBonusMiningLevel(new ItemStack[] {new ItemStack(Items.DIAMOND) }, "Diamond"));
    //        TinkerRegistry.registerModifier(new ModBonusMiningLevel(new ItemStack[] {new ItemStack(Items.EMERALD) }, "Emerald"));
    //    }
}
