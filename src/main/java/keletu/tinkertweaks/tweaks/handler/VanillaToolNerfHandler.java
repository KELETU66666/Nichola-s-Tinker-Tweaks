package keletu.tinkertweaks.tweaks.handler;

import keletu.tinkertweaks.KeletuTinkerTweaks;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class VanillaToolNerfHandler {
    @SubscribeEvent
    public void breakSpeed(PlayerEvent.BreakSpeed event)
    {
        if(event.getEntityPlayer() == null)
            return;

        ItemStack itemStack = event.getEntityPlayer().getHeldItemMainhand();
        if(itemStack == ItemStack.EMPTY)
            return;

        if(isUselessTool(itemStack.getItem()))
            event.setNewSpeed(0);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onItemToolTip(ItemTooltipEvent event) {
        if (event.getEntityPlayer() == null)
            return;

        if(isUselessTool(event.getItemStack().getItem())) {
            event.getToolTip().add(TextFormatting.DARK_RED + I18n.format("tooltip.uselessTool1"));
            event.getToolTip().add(TextFormatting.DARK_RED + I18n.format("tooltip.uselessTool2"));
        }
    }

    public static boolean isUselessTool(Item item)
    {
        if(item == null)
            return false;

        if(KeletuTinkerTweaks.toolWhitelist.contains(item))
            return false;

        if(item instanceof ItemTool)
            return true;

        return false;
    }
}