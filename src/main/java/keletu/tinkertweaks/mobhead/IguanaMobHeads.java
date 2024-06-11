package keletu.tinkertweaks.mobhead;

import keletu.tinkertweaks.mobhead.blocks.IguanaSkullBlock;
import keletu.tinkertweaks.mobhead.handlers.MobHeadHandler;
import keletu.tinkertweaks.mobhead.items.IguanaSkull;
import keletu.tinkertweaks.mobhead.items.Wearable;
import keletu.tinkertweaks.mobhead.proxy.MobHeadCommonProxy;
import keletu.tinkertweaks.mobhead.tilenetities.IguanaSkullTileEntity;
import keletu.tinkertweaks.util.Log;
import keletu.tinkertweaks.util.ModSupportHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Adds additional MobHeads and controls MobHead dropping.
 */

public class IguanaMobHeads {
    public static Item skullItem;
    public static Block skullBlock;
    public static Item wearables; // secret thing

    @SidedProxy(clientSide = "keletu.tinkertweaks.mobhead.proxy.MobHeadClientProxy", serverSide = "keletu.tinkertweaks.mobhead.proxy.MobHeadCommonProxy")
    public static MobHeadCommonProxy proxy;

    public static void preInit(FMLPreInitializationEvent event)
    {

        if(ModSupportHelper.ThermalFoundation)
            integrateThermalExpansion();

        skullItem = new IguanaSkull();
        ForgeRegistries.ITEMS.register(skullItem);

        skullBlock = new IguanaSkullBlock();
        ForgeRegistries.BLOCKS.register(skullBlock);
        GameRegistry.registerTileEntity(IguanaSkullTileEntity.class, "skullTE");

        // psssssst!
        wearables = new Wearable();
        ForgeRegistries.ITEMS.register(wearables);
        proxy.registerModels();
        proxy.initialize();
    }

    public static void postInit(FMLPostInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new MobHeadHandler());

        proxy.postInit();

        // :>
        //TODO
        //ChestGenHooks.addItem(ChestGenHooks.VILLAGE_BLACKSMITH, new WeightedRandomChestContent(new ItemStack(wearables, 1, 0), 0, 1, 1));
        //ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_LIBRARY, new WeightedRandomChestContent(new ItemStack(wearables, 1, 3), 0, 1, 1));
    }

    private static void integrateThermalExpansion()
    {
        Log.debug("Adding Blizz head");
        IguanaSkull.addHead(3, "blizz", "skull_blizz");
    }
}