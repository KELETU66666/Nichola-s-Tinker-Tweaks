package keletu.tinkertweaks.tweaks.handler;

import keletu.tinkertweaks.KeletuTinkerTweaks;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ListIterator;

public class VanillaSwordNerfHandler {
    @SubscribeEvent
    public void onHurt(LivingHurtEvent event)
    {
        if (!(event.getSource().damageType.equals("player")))
            return;

        // only players
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer))
            return;
        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();

        // the tool
        ItemStack stack = player.getHeldItemMainhand();
        if(stack == ItemStack.EMPTY)
            return;

        if(isUselessWeapon(stack.getItem()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onItemToolTip(ItemTooltipEvent event) {
        if (event.getEntityPlayer() == null)
            return;

        if(isUselessWeapon(event.getItemStack().getItem())) {
            // remove +dmg stuff
            ListIterator<String> iter = event.getToolTip().listIterator();
            //while(iter.hasNext())
            //{
            //    if(iter.next().startsWith(LevelingToolTipHandler.plusPrefix))
            //        iter.remove();
            //}
            event.getToolTip().add(TextFormatting.DARK_RED + I18n.format("tooltip.uselessWeapon1"));
            event.getToolTip().add(TextFormatting.DARK_RED + I18n.format("tooltip.uselessTool2"));
        }
    }

    public static boolean isUselessWeapon(Item item)
    {
        if(item == null)
            return false;

        if(KeletuTinkerTweaks.toolWhitelist.contains(item))
            return false;

        if(item instanceof ItemSword)
            return true;

        return false;
    }
}