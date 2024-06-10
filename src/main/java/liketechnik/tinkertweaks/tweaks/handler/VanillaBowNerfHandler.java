package liketechnik.tinkertweaks.tweaks.handler;

import liketechnik.tinkertweaks.LiketechniksTinkerTweaks;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class VanillaBowNerfHandler {
    @SubscribeEvent
    public void onArrowNock(ArrowNockEvent event)
    {
        if(event.getEntityPlayer() == null)
            return;

        if(event.getResult() == null)
            return;

        if(isUselessBow(event.getBow().getItem()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onItemToolTip(ItemTooltipEvent event) {
        if (event.getEntityPlayer() == null)
            return;

        if(isUselessBow(event.getItemStack().getItem())) {
            event.getToolTip().add(TextFormatting.DARK_RED + I18n.format("tooltip.uselessBow1"));
            event.getToolTip().add(TextFormatting.DARK_RED + I18n.format("tooltip.uselessTool2"));
        }
    }

    public static boolean isUselessBow(Item item)
    {
        if(item == null)
            return false;

        if(LiketechniksTinkerTweaks.toolWhitelist.contains(item))
            return false;

        if(item instanceof ItemBow)
            return true;

        return false;
    }
}