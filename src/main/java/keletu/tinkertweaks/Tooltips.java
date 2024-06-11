package keletu.tinkertweaks;

import keletu.tinkertweaks.config.Config;
import keletu.tinkertweaks.level.ToolHarvestLevelNBT;
import keletu.tinkertweaks.level.ToolLevelNBT;
import keletu.tinkertweaks.util.HarvestLevels;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import slimeknights.tconstruct.library.client.CustomFontColor;
import slimeknights.tconstruct.library.utils.TinkerUtil;

import java.awt.*;
import java.util.List;

// utility class for constructing tooltip
public final class Tooltips {

    private Tooltips() {
    } // non-instantiable

    public static void addTooltips(ItemStack itemStack, List<String> tooltips) {
        NBTTagCompound tag = TinkerUtil.getModifierTag(itemStack, KeletuTinkerTweaks.modToolLeveling.getModifierIdentifier());
        if (!tag.isEmpty()) {
            ToolLevelNBT data = new ToolLevelNBT(tag);
            if (Config.canLevelUp(data.level)) {
                tooltips.add(1, getXpToolTip(data.xp, KeletuTinkerTweaks.modToolLeveling.getXpForLevelup(data.level, itemStack)));
            }
            tooltips.add(1, getLevelTooltip(data.level));
        }

        NBTTagCompound tag1 = TinkerUtil.getModifierTag(itemStack, KeletuTinkerTweaks.modHarvestLeveling.getModifierIdentifier());
        if (!tag1.isEmpty()) {
            ToolHarvestLevelNBT data = new ToolHarvestLevelNBT(tag1);
            // xp if not boosted
            if (data.isBoosted)
                tooltips.add(2, getBoostedTooltip());
            else
                tooltips.add(2, getBoostXpToolTip(data.bxp, KeletuTinkerTweaks.modHarvestLeveling.getXpForLevelup(data.level, itemStack)));
                // otherwise boosted message

        }
    }

    public static String getBoostXpString(int xp, int xpNeeded) {
        return getXpString(xp, xpNeeded);
    }

    private static String getBoostXpToolTip(int xp, int xpNeeded) {
        return String.format("%s: %s", I18n.translateToLocal("tooltip.level.miningxp"), getXpString(xp, xpNeeded));
    }

    public static String getMiningLevelTooltip(int hLevel) {
        return String.format("%s: %s", I18n.translateToLocal("tooltip.level.mininglevel"), HarvestLevels.getHarvestLevelName(hLevel));
    }

    public static String getBoostedTooltip() {
        return String.format("%s: \u00a76%s", I18n.translateToLocal("tooltip.level.miningxp"), I18n.translateToLocal("tooltip.level.boosted"));
    }

    private static String getXpToolTip(int xp, int xpNeeded) {
        return String.format("%s: %s", I18n.translateToLocal("tooltip.xp"), getXpString(xp, xpNeeded));
    }

    private static String getXpString(int xp, int xpNeeded) {
        return TextFormatting.WHITE + String.format("%d / %d", xp, xpNeeded);
        //float xpPercentage = (float)xp / (float)xpNeeded * 100f;
        //return String.format("%.2f", xpPercentage) + "%"
    }

    public static String getInfoString(String base, TextFormatting baseColor, String info, TextFormatting infoColor) {
        return getInfoString(base, baseColor, info, infoColor.toString());
    }

    public static String getInfoString(String base, TextFormatting baseColor, String info, String infoColor) {
        return String.format("%s%s %s(%s%s%s)", baseColor, base, baseColor, infoColor, info, baseColor);
    }

    private static String getLevelTooltip(int level) {
        return String.format("%s: %s", I18n.translateToLocal("tooltip.level"), getLevelString(level));
    }

    public static String getLevelString(int level) {
        return getLevelColor(level) + getRawLevelString(level) + TextFormatting.RESET;
    }

    private static String getRawLevelString(int level) {
        if (level <= 0) {
            return "";
        }

        // try a basic translated string
        if (I18n.canTranslate("tooltip.level." + level)) {
            return I18n.translateToLocal("tooltip.level." + level);
        }

        // ok. try to find a modulo
        int i = 1;
        while (I18n.canTranslate("tooltip.level." + i)) {
            i++;
        }

        // get the modulo'd string
        String str = I18n.translateToLocal("tooltip.level." + (level % i));
        // and add +s!
        for (int j = level / i; j > 0; j--) {
            str += '+';
        }

        return str;
    }

    private static String getLevelColor(int level) {
        float hue = (0.277777f * level);
        hue = hue - (int) hue;
        return CustomFontColor.encodeColor(Color.HSBtoRGB(hue, 0.75f, 0.8f));
        /* Old colors
        switch (level%12)
        {
            case 0: return TextFormatting.GRAY.toString();
            case 1: return TextFormatting.DARK_RED.toString();
            case 2: return TextFormatting.GOLD.toString();
            case 3: return TextFormatting.YELLOW.toString();
            case 4: return TextFormatting.DARK_GREEN.toString();
            case 5: return TextFormatting.DARK_AQUA.toString();
            case 6: return TextFormatting.LIGHT_PURPLE.toString();
            case 7: return TextFormatting.WHITE.toString();
            case 8: return TextFormatting.RED.toString();
            case 9: return TextFormatting.DARK_PURPLE.toString();
            case 10:return TextFormatting.AQUA.toString();
            case 11:return TextFormatting.GREEN.toString();
            default: return "";
        }*/
    }
}
