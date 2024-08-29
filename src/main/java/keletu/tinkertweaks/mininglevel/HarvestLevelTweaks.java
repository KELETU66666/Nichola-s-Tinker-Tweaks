package keletu.tinkertweaks.mininglevel;

import keletu.tinkertweaks.config.Config;
import keletu.tinkertweaks.util.HarvestLevels;
import keletu.tinkertweaks.util.Log;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Used to modify the harvest levels of all known/findable tools and blocks. Vanilla and modded.
 * Has to be used with the Tinker Tool Tweaks or you'll be very unhappy with unmineable blocks.
 */
public final class HarvestLevelTweaks {
    private HarvestLevelTweaks() {
    } // non-instantiable

    public static void modifyHarvestLevels() {
        Log.debug("Modifying HarvestLevel of blocks and items");

        modifyOredictBlocks();
        modifyVanillaBlocks();

        modifyTools();

        Log.debug("Finished modifying HarvestLevel of blocks and items");
    }

    private static void modifyVanillaBlocks() {
        // ensure that the forgehooks are in place
        new ForgeHooks(); // this ensures that the static initializer of ForgeHooks is called already. Otherwise it overwrites our Harvestlevel changes.
        // see ForgeHooks.initTools()

        Blocks.IRON_ORE.setHarvestLevel("pickaxe", HarvestLevels._2_copper);
        Blocks.IRON_BLOCK.setHarvestLevel("pickaxe", HarvestLevels._2_copper);
        Blocks.IRON_BARS.setHarvestLevel("pickaxe", HarvestLevels._2_copper);
        Blocks.LAPIS_ORE.setHarvestLevel("pickaxe", HarvestLevels._2_copper);
        Blocks.LAPIS_BLOCK.setHarvestLevel("pickaxe", HarvestLevels._2_copper);

        Blocks.GOLD_ORE.setHarvestLevel("pickaxe", HarvestLevels._3_iron);
        Blocks.GOLD_BLOCK.setHarvestLevel("pickaxe", HarvestLevels._3_iron);
        Blocks.REDSTONE_ORE.setHarvestLevel("pickaxe", HarvestLevels._3_iron);
        Blocks.LIT_REDSTONE_ORE.setHarvestLevel("pickaxe", HarvestLevels._3_iron);

        Blocks.DIAMOND_ORE.setHarvestLevel("pickaxe", HarvestLevels._4_bronze); // yes, diamond requires diamond level. good thing there's bronze/steel ;)
        Blocks.DIAMOND_BLOCK.setHarvestLevel("pickaxe", HarvestLevels._4_bronze);
        Blocks.EMERALD_ORE.setHarvestLevel("pickaxe", HarvestLevels._4_bronze);
        Blocks.EMERALD_BLOCK.setHarvestLevel("pickaxe", HarvestLevels._4_bronze);

        Blocks.OBSIDIAN.setHarvestLevel("pickaxe", HarvestLevels._5_diamond);

        Blocks.ENCHANTING_TABLE.setHarvestLevel("pickaxe", HarvestLevels._5_diamond);

        if (Config.shouldProcessHarvestLevelLog())
            Log.debug("Modified vanilla blocks");
    }

    private static void modifyOredictBlocks() {
        //String[][][] lists = new String[][][] {oreDictLevels, oreDictLevelsMetallurgyFantasy, oreDictLevelsMetallurgyNether, oreDictLevelsMetallurgyEnd};
        //for(String[][] odll : lists)
        for (int i = 0; i < allOreDicLevels.length; ++i)
            for (String materialName : allOreDicLevels[i]) {
                modifyOredictBlock(materialName, i);
            }

        // metal-blocks
        if (Config.shouldProcessHarvestLevelLog())
            Log.debug("Modified oredicted blocks");
    }

    public static void modifyOredictBlock(String orePostfix, int hlvl) {
        for (String prefix : oreDictPrefixes)
            for (ItemStack oreStack : OreDictionary.getOres(prefix + orePostfix))
                modifyBlock(oreStack, hlvl);
    }

    public static void modifyBlock(ItemStack stack, int harvestLevel) {
        Block block = Block.getBlockFromItem(stack.getItem());

        int meta = stack.getItemDamage();
        Integer[] metas;
        if (meta == OreDictionary.WILDCARD_VALUE)
            metas = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        else
            metas = new Integer[]{meta};

        for (int m : metas) {
            try {
                if (Config.shouldProcessHarvestLevelLog()) {
                    Log.debug(String.format("Changed Harvest Level of %s from %d to %d", stack.getTranslationKey(), block.getHarvestLevel(block.getStateFromMeta(m)), harvestLevel));
                }

                // gravelore gets shovel level instead of pickaxe.
                //if (block instanceof GravelOre)
                //    block.setHarvestLevel("shovel", harvestLevel, block.getStateFromMeta(m));
                //else
                block.setHarvestLevel("pickaxe", harvestLevel, block.getStateFromMeta(m));

                if (Config.shouldProcessOverrideChangesLog() && Loader.instance().isInState(LoaderState.POSTINITIALIZATION))
                    Log.info(String.format("Block Override: Changed Harvest Level of %s to %d", stack.getTranslationKey(), harvestLevel));
            } catch (Exception e) {
                // exception can occur if stuff does weird things metadatas
            }
        }
    }

    private static void modifyTools() {
        ItemStack tmp = new ItemStack(Items.STICK); // we need one as argument, it's never actually accessed...
        // search for all items that have pickaxe harvestability
        for (Item o : ForgeRegistries.ITEMS) {
            // cycle through all toolclasses. usually this'll either be pickaxe, shovel or axe. But mods could add items with multiple.
            for (String toolClass : o.getToolClasses(tmp)) {
                // adapt harvest levels
                int old = o.getHarvestLevel(tmp, toolClass, null, null);
                // wood/gold tool unchanged
                if (old <= 0)
                    continue;

                int hlvl = getUpdatedHarvestLevel(old);

                o.setHarvestLevel(toolClass, hlvl);
                break;

                //if (Config.shouldProcessMiningLevelLog())
                //    Log.debug(String.format("Changed Harvest Level for %s of %s from %d to %d", toolClass, o.getTranslationKey(), old, hlvl));
//
            }

        }

        if (Config.shouldProcessMiningLevelLog())
            Log.debug("Modified tools");
    }

    public static int getUpdatedHarvestLevel(int old) {
        switch (old) {
            // stone tool: nerfed to wood level
            case 1:
                return HarvestLevels._0_stone;
            // iron tool
            case 2:
                return HarvestLevels._3_iron;
            // diamond tool
            case 3:
                return HarvestLevels._5_diamond;
            // default... we just increase it?
            default:
                return old + 2;
        }
    }


    // todo: expose this to config. But I'm too lazy for such a minor thing. Just call me to add another string...
    public static final String[] oreDictPrefixes = {
            "ore", "denseore", "oreNether", "denseoreNether", "block", "stone", "brick", "orePoor"
    };

    // HarvestLevels
    public static final String[][] oreDictLevels = {
            // 0: Stone
            {},
            // 1: Flint
            {"Copper", "Coal", "Tetrahedrite", "Aluminum", "Aluminium", "NaturalAluminum", "AluminumBrass", "Shard", "Bauxite", "Zinc"},
            // 2: Copper
            {"Iron", "Pyrite", "Silver", "Lapis"},
            // 3: Iron
            {"Tin", "Cassiterite", "Gold", "Lead", "Redstone", "Steel", "Galena", "Nickel", "Invar", "Electrum", "Sphalerite", "Osmium"},
            // 4: Bronze
            {"Diamond", "Emerald", "Ruby", "Sapphire", "Amethyst", "Cinnabar", "GreenSapphire", "BlackGranite", "RedGranite", "Manganese"},
            // 5: Redstone/Diamond
            {"Obsidian", "Tungstate", "Sodalite", "Quartz", "CertusQuartz", "SkyStone"},
            // 6: Obsidian/Alumite
            {"Ardite", "Uranium", "Olivine", "Sheldonite", "Platinum", "Yellorite"},
            // 7: Ardite
            {"Cobalt", "Iridium", "Cooperite", "Titanium"},
            // 8: Cobalt
            {"Manyullyn"},
            // 9: Manyullyn (empty)
            {}
    };

    public static final String[][] oreDictLevelsMetallurgyFantasy = {
            // 0: Stone
            {"Prometheum", "DeepIron"},
            // 1: Flint
            {"Infuscolium"},
            // 2: Copper
            {"Oureclase"},
            // 3: Iron
            {"AstralSilver"},
            // 4: Bronze
            {"Carmot"},
            // 5: Redstone/Diamond
            {"Mithril"},
            // 6: Obsidian/Alumite
            {"Rubracium"},
            // 7: Ardite
            {"Orichalcum"},
            // 8: Cobalt
            {"Adamantine"},
            // 9: Manyullyn
            {"Atlarus"}
    };

    public static final String[][] oreDictLevelsMetallurgyNether = {
            // 0: Stone
            {},
            // 1: Flint
            {},
            // 2: Copper
            {"Lemurite", "Ignatius"},
            // 3: Iron
            {"ShadowIron"},
            // 4: Bronze
            {"Midasium", "Vyroxeres"},
            // 5: Redstone/Diamond
            {"Ceruclase"},
            // 6: Obsidian/Alumite
            {"Alduorite"},
            // 7: Ardite
            {"Kalendrite"},
            // 8: Cobalt
            {"Vulcanite"},
            // 9: Manyullyn
            {"Sanguinite"}
    };

    public static final String[][] oreDictLevelsMetallurgyEnd = {
            // 0: Stone
            {},
            // 1: Flint
            {},
            // 2: Copper
            {},
            // 3: Iron
            {},
            // 4: Bronze
            {},
            // 5: Redstone/Diamond
            {"Eximite"},
            // 6: Obsidian/Alumite
            {"Meutoite"},
            // 7: Ardite
            {},
            // 8: Cobalt
            {},
            // 9: Manyullyn
            {}
    };

    public static String[][] allOreDicLevels;

    static {
        String[][][] lists = new String[][][]{oreDictLevels, oreDictLevelsMetallurgyFantasy, oreDictLevelsMetallurgyNether, oreDictLevelsMetallurgyEnd};
        allOreDicLevels = new String[oreDictLevels.length][];
        for (int i = 0; i < 10; i++) {
            int size = 0;
            for (String[][] list : lists) size += list.length;

            allOreDicLevels[i] = new String[size];
            int j = 0;
            for (String[][] list : lists)
                for (String entry : list[i])
                    allOreDicLevels[i][j++] = entry;
        }
    }
}