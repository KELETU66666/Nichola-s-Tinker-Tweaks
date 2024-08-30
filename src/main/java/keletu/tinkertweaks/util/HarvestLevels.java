package keletu.tinkertweaks.util;

import static net.minecraft.util.text.TextFormatting.*;
import net.minecraft.util.text.translation.I18n;
import slimeknights.tconstruct.library.materials.Material;

import java.util.Map;

// strength of the tool-material. stone == strength of a stone pick etc.
public final class HarvestLevels {
    private HarvestLevels() {} // non-instantiable

    public static int _0_stone = 0;//stone
    public static int _1_flint = 1;//copper
    public static int _2_copper = 2;//iron
    public static int _3_iron = 3;//tin
    public static int _4_bronze = 4;//redstone
    public static int _5_diamond = 5;//obsidian
    public static int _6_obsidian = 6;//ardite
    public static int _7_ardite = 7;//cobalt
    public static int _8_cobalt = 8;//manyullym
    public static int _9_manyullym = 9;//manyullym+

    public static int max = 9;

    private static boolean vanilla = false;

    // needed if HarvestLevels module is deactivated to achieve vanilla mining levels
    public static void adjustToVanillaLevels()
    {
        _1_flint = 1;
        _2_copper = 1;
        _3_iron = 2;
        _4_bronze = 2;
        _5_diamond = 2;
        _6_obsidian = 3;
        _7_ardite = 4;
        _8_cobalt = 4;
        _9_manyullym = 5;

        max = 5;

        vanilla = true;
    }

    public static void updateHarvestLevelNames()
    {
        Map<Integer, String> names = slimeknights.tconstruct.library.utils.HarvestLevels.harvestLevelNames;

        if(vanilla)
        {
            names.put(0 , GRAY + I18n.translateToLocal("mininglevel.stone"));
            names.put(1 , DARK_RED + I18n.translateToLocal("mininglevel.iron"));
            names.put(2 , RED + I18n.translateToLocal("mininglevel.redstone"));
            names.put(3 , LIGHT_PURPLE + I18n.translateToLocal("mininglevel.obsidian"));
            names.put(4 , BLUE + I18n.translateToLocal("mininglevel.cobalt"));
            names.put(5 , DARK_PURPLE + I18n.translateToLocal("mininglevel.manyullyn"));
            names.put(6 , DARK_PURPLE + I18n.translateToLocal("mininglevel.manyullyn") + LIGHT_PURPLE + "+");
        }
        else {
            names.put(0, GRAY + I18n.translateToLocal("mininglevel.stone"));
            names.put(1, GOLD + I18n.translateToLocal("mininglevel.copper"));
            names.put(2, DARK_GRAY + I18n.translateToLocal("mininglevel.iron"));
            names.put(3, WHITE + I18n.translateToLocal("mininglevel.tin"));
            names.put(4, RED + I18n.translateToLocal("mininglevel.redstone"));
            names.put(5, LIGHT_PURPLE + I18n.translateToLocal("mininglevel.obsidian"));
            names.put(6, DARK_RED + I18n.translateToLocal("mininglevel.ardite"));
            names.put(7, DARK_AQUA + I18n.translateToLocal("mininglevel.cobalt"));
            names.put(8, DARK_PURPLE + I18n.translateToLocal("mininglevel.manyullyn"));
            names.put(9, DARK_PURPLE + I18n.translateToLocal("mininglevel.manyullyn") + LIGHT_PURPLE + "+");
        }
    }

    public static String getHarvestLevelName(int num)
    {
        return slimeknights.tconstruct.library.utils.HarvestLevels.getHarvestLevelName(num);
    }

    public static void setCustomHarvestLevelNames(Map<Integer, Material> mats)
    {
        for(Map.Entry<Integer, Material> mat : mats.entrySet())
            slimeknights.tconstruct.library.utils.HarvestLevels.harvestLevelNames.put(mat.getKey(), mat.getValue().getLocalizedNameColored() + mat.getValue().getLocalizedName());
    }
}