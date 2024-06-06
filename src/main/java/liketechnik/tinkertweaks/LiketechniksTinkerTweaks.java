package liketechnik.tinkertweaks;

import liketechnik.tinkertweaks.capability.CapabilityDamageXp;
import liketechnik.tinkertweaks.config.ConfigSync;
import liketechnik.tinkertweaks.config.ConfigSyncPacket;
import liketechnik.tinkertweaks.mininglevel.HarvestLevelTweaks;
import liketechnik.tinkertweaks.mininglevel.TinkerMaterialTweaks;
import liketechnik.tinkertweaks.mininglevel.VanillaToolTipHandler;
import liketechnik.tinkertweaks.util.HarvestLevels;
import liketechnik.tinkertweaks.util.Log;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
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
    }

    @SubscribeEvent
    protected void registerTools(RegistryEvent.Register<Item> event) {
        Config.INSTANCE.load(new File(modConfigurationDirectory, "LiketechniksTinkerTweaks.cfg"));
        Config.INSTANCE.save();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        TinkerMaterialTweaks.modifyToolMaterials();
        HarvestLevelTweaks.modifyHarvestLevels();
        MinecraftForge.EVENT_BUS.register(new VanillaToolTipHandler());

        //if(Config.changeDiamondModifier)
        //  changeDurabilityModifiers();

        MinecraftForge.EVENT_BUS.register(liketechnik.tinkertweaks.EventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(EntityXpHandler.INSTANCE);
        if (event.getSide().isServer()) {
            MinecraftForge.EVENT_BUS.register(new ConfigSync());
        }
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandLevelTool());
        event.registerServerCommand(new CommandModifierDump());
    }

    //private void changeDurabilityModifiers()
    //    {
    //        // deactivate mininglevel increase by tcon
    //        PHConstruct.miningLevelIncrease = false;
    //
    //        Log.debug("Adding Diamond/Emerald Modifiers for Mining Levels");
    //        ModifyBuilder.registerModifier(new ModBonusMiningLevel(new ItemStack[] {new ItemStack(Items.DIAMOND) }, "Diamond"));
    //        ModifyBuilder.registerModifier(new ModBonusMiningLevel(new ItemStack[] {new ItemStack(Items.EMERALD) }, "Emerald"));
    //    }
}
