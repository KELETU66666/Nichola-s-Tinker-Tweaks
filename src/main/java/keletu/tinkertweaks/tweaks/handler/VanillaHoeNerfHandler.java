package keletu.tinkertweaks.tweaks.handler;

import keletu.tinkertweaks.KeletuTinkerTweaks;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class VanillaHoeNerfHandler {
    @SubscribeEvent
    public void onHoeBlock(UseHoeEvent event)
    {
        // don't modify hoeing without tool (from machines, if they even send an event.)
        if(event.getCurrent() == ItemStack.EMPTY)
            return;

        if(isUselessHoe(event.getCurrent().getItem()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onItemToolTip(ItemTooltipEvent event) {
        if (event.getEntityPlayer() == null)
            return;

        if(isUselessHoe(event.getItemStack().getItem())) {
            event.getToolTip().add(TextFormatting.DARK_RED + I18n.translateToLocal("tooltip.uselessHoe1"));
            event.getToolTip().add(TextFormatting.DARK_RED + I18n.translateToLocal("tooltip.uselessTool2"));
        }
    }

    public static boolean isUselessHoe(Item item)
    {
        if(item == null)
            return false;

        if(KeletuTinkerTweaks.toolWhitelist.contains(item))
            return false;

        if(item instanceof ItemHoe)
            return true;

        return false;
    }
}