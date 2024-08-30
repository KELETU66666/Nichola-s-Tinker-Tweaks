package keletu.tinkertweaks.tweaks.handler;

import keletu.tinkertweaks.KeletuTinkerTweaks;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class VanillaBowNerfHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onArrowNock(PlayerInteractEvent.RightClickItem event)
    {
        if(event.getEntityPlayer() == null)
            return;

        if(event.getResult() == null)
            return;

        if(isUselessBow(event.getItemStack().getItem()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onItemToolTip(ItemTooltipEvent event) {
        if (event.getEntityPlayer() == null)
            return;

        if(isUselessBow(event.getItemStack().getItem())) {
            event.getToolTip().add(TextFormatting.DARK_RED + I18n.translateToLocal("tooltip.uselessBow1"));
            event.getToolTip().add(TextFormatting.DARK_RED + I18n.translateToLocal("tooltip.uselessTool2"));
        }
    }

    public static boolean isUselessBow(Item item)
    {
        if(item == null)
            return false;

        if(KeletuTinkerTweaks.toolWhitelist.contains(item))
            return false;

        if(item instanceof ItemBow)
            return true;

        return false;
    }
}