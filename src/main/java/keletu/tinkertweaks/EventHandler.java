package keletu.tinkertweaks;

import com.google.common.collect.Lists;
import keletu.tinkertweaks.config.Config;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.library.events.TinkerToolEvent;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.tools.tools.Pickaxe;

import java.util.List;

public final class EventHandler {

    public static EventHandler INSTANCE = new EventHandler();

    @SubscribeEvent
    public void onToolBuild(TinkerToolEvent.OnItemBuilding event) {
        // we build a dummy tool tag to get the base modifier amount, unchanged by traits
        List<Material> materials = Lists.newArrayList();
        for (int i = 0; i < event.tool.getRequiredComponents().size(); i++) {
            materials.add(Material.UNKNOWN);
        }
        NBTTagCompound baseTag = event.tool.buildTag(materials);

        // set free modifiers
        NBTTagCompound toolTag = TagUtil.getToolTag(event.tag);
        //modifiers = toolTag.getInteger(Tags.FREE_MODIFIERS);
        int modifiers = Math.max(0, Config.getStartingModifiers());
        toolTag.setInteger(Tags.FREE_MODIFIERS, modifiers);
        TagUtil.setToolTag(event.tag, toolTag);

        if (TinkerUtil.getModifierTag(event.tag, KeletuTinkerTweaks.modToolLeveling.getModifierIdentifier()).isEmpty()) {
            KeletuTinkerTweaks.modToolLeveling.apply(event.tag);
        }

        if (!TinkerUtil.hasModifier(event.tag, KeletuTinkerTweaks.modToolLeveling.getModifierIdentifier())) {
            KeletuTinkerTweaks.modToolLeveling.apply(event.tag);
        }

        if(event.tool instanceof Pickaxe && toolTag.getInteger(Tags.HARVESTLEVEL) > 1) {
            if (TinkerUtil.getModifierTag(event.tag, KeletuTinkerTweaks.modHarvestLeveling.getModifierIdentifier()).isEmpty()) {
                KeletuTinkerTweaks.modHarvestLeveling.apply(event.tag);
            }

            if (!TinkerUtil.hasModifier(event.tag, KeletuTinkerTweaks.modHarvestLeveling.getModifierIdentifier())) {
                KeletuTinkerTweaks.modHarvestLeveling.apply(event.tag);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if(event.getItemStack().getItem() == KeletuTinkerTweaks.rubberChicken && event.getItemStack().hasTagCompound() && event.getItemStack().getTagCompound().hasKey("Original")) {
            event.getToolTip().add(1, TextFormatting.DARK_RED.toString() + TextFormatting.ITALIC + I18n.translateToLocal("tooltip.chicken1"));
            event.getToolTip().add(2, TextFormatting.DARK_RED + I18n.translateToLocal("tooltip.chicken2"));
            return;
        }
        Tooltips.addTooltips(event.getItemStack(), event.getToolTip());
    }

    private EventHandler() {
    }
}
