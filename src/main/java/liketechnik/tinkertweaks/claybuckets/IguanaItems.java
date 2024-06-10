package liketechnik.tinkertweaks.claybuckets;

import liketechnik.tinkertweaks.LiketechniksTinkerTweaks;
import liketechnik.tinkertweaks.claybuckets.proxy.ItemsCommonProxy;
import liketechnik.tinkertweaks.reference.Reference;
import liketechnik.tinkertweaks.util.Log;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class IguanaItems {
    public static Item clayBucketUnfired;
    public static ItemClayBucket clayBucketFired;

    @SidedProxy(clientSide = "liketechnik.tinkertweaks.claybuckets.proxy.ItemsClientProxy", serverSide = "liketechnik.tinkertweaks.claybuckets.proxy.ItemsCommonProxy")
    public static ItemsCommonProxy proxy;
    public static void preInit(FMLPreInitializationEvent event)
    {
        Log.debug("Adding Items");
        // unfired clay bucket is a regular item
        clayBucketUnfired = new Item().setTranslationKey(Reference.prefix("clay_bucket_unfired")).setRegistryName("clay_bucket_unfired").setMaxStackSize(16).setCreativeTab(CreativeTabs.MISC);
        clayBucketFired = new ItemClayBucket();
        ForgeRegistries.ITEMS.register(clayBucketUnfired);
        ForgeRegistries.ITEMS.register(clayBucketFired);

        // add recipes
        ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(LiketechniksTinkerTweaks.MODID, "claybucketunfired"), new ItemStack(clayBucketUnfired), "c c", " c ", 'c', new ItemStack(Items.CLAY_BALL)).setRegistryName(LiketechniksTinkerTweaks.MODID, "claybucketunfired"));
        GameRegistry.addSmelting(clayBucketUnfired, new ItemStack(clayBucketFired), 0.0F);

        proxy.registerModels();

        Log.debug("Added Items");
    }

    public static void postInit(FMLPostInitializationEvent event)
    {
        if(TinkerSmeltery.castCreationFluids == null)
            return;

        MinecraftForge.EVENT_BUS.register(clayBucketFired);
    }
}