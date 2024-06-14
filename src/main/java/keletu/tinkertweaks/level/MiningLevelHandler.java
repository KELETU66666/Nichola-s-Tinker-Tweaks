package keletu.tinkertweaks.level;

import keletu.tinkertweaks.KeletuTinkerTweaks;
import keletu.tinkertweaks.util.ModSupportHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import slimeknights.tconstruct.library.client.model.ModifierModelLoader;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = KeletuTinkerTweaks.MODID)
public final class MiningLevelHandler {
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent evt) {
        Field obj = ReflectionHelper.findField(slimeknights.tconstruct.common.ClientProxy.class, "modifierLoader");
        obj.setAccessible(true);

        try {
            ((ModifierModelLoader) obj.get(null)).registerModifierFile("zombiehead", new ResourceLocation(KeletuTinkerTweaks.MODID, "models/item/modifiers/zombiehead"));
            ((ModifierModelLoader) obj.get(null)).registerModifierFile("skeletonhead", new ResourceLocation(KeletuTinkerTweaks.MODID, "models/item/modifiers/skeletonhead"));
            ((ModifierModelLoader) obj.get(null)).registerModifierFile("creeperhead", new ResourceLocation(KeletuTinkerTweaks.MODID, "models/item/modifiers/creeperhead"));
            ((ModifierModelLoader) obj.get(null)).registerModifierFile("witherskeletonhead", new ResourceLocation(KeletuTinkerTweaks.MODID, "models/item/modifiers/witherskeletonhead"));
            ((ModifierModelLoader) obj.get(null)).registerModifierFile("dragonhead", new ResourceLocation(KeletuTinkerTweaks.MODID, "models/item/modifiers/dragonhead"));
            ((ModifierModelLoader) obj.get(null)).registerModifierFile("endermanhead", new ResourceLocation(KeletuTinkerTweaks.MODID, "models/item/modifiers/endermanhead"));
            ((ModifierModelLoader) obj.get(null)).registerModifierFile("blazehead", new ResourceLocation(KeletuTinkerTweaks.MODID, "models/item/modifiers/blazehead"));
            if (ModSupportHelper.ThermalFoundation)
                ((ModifierModelLoader) obj.get(null)).registerModifierFile("blizzhead", new ResourceLocation(KeletuTinkerTweaks.MODID, "models/item/modifiers/blizzhead"));
            ((ModifierModelLoader) obj.get(null)).registerModifierFile("zombiepigmanhead", new ResourceLocation(KeletuTinkerTweaks.MODID, "models/item/modifiers/zombiepigmanhead"));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}