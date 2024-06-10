package liketechnik.tinkertweaks.level.modifier;

import liketechnik.tinkertweaks.LiketechniksTinkerTweaks;
import liketechnik.tinkertweaks.level.ToolHarvestLevelNBT;
import liketechnik.tinkertweaks.config.Config;
import static liketechnik.tinkertweaks.level.ToolHarvestLevelNBT.TAG_IS_BOOSTED;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import static slimeknights.tconstruct.library.utils.Tags.HARVESTLEVEL;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.tools.modifiers.ToolModifier;

public class ModMiningLevelBoost extends ToolModifier {
    // the maximum mining level obtainable with this head
    private int maxLvl = 0;

    public ModMiningLevelBoost(String name, ItemStack recipe, int maxLvl) {
        super(name, 0x8cf4e2);

        addAspects(new ModifierAspect.SingleAspect(this), new ModifierAspect.DataAspect(this), ModifierAspect.harvestOnly);
        this.addItem(recipe, 1, 1);
        this.maxLvl = maxLvl;
    }

    @Override
    public boolean canApplyCustom(ItemStack input) {
        NBTTagCompound tags = input.getTagCompound().getCompoundTag("Stats");
        NBTTagCompound tags1 = TinkerUtil.getModifierTag(input, LiketechniksTinkerTweaks.modHarvestLeveling.getModifierIdentifier());
        ToolHarvestLevelNBT data = new ToolHarvestLevelNBT(tags1);

        // Modifier available?
        if(Config.mobHeadRequiresModifier() && TinkerUtil.hasModifier(tags1, this.getIdentifier()))
            return false;

        // already applied?
        if(TinkerUtil.hasModifier(tags1, this.getIdentifier()))
            return false;

        // got required harvest level?
        int hlvl = tags.getInteger(HARVESTLEVEL);
        return hlvl < maxLvl && !data.isBoosted;
    }

    @Override
    public void apply(ItemStack tool) {
        super.apply(tool);
        NBTTagCompound tag1 = TinkerUtil.getModifierTag(tool, LiketechniksTinkerTweaks.modHarvestLeveling.getModifierIdentifier());
        if (!tag1.isEmpty()) {
            tag1.setBoolean(TAG_IS_BOOSTED, true);
        }
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        ToolHarvestLevelNBT.levelUpMiningLevel(rootCompound, null);

        // add a modifier if it doesn't require one, because ModBoolean will substract one on modify
        if(!Config.mobHeadRequiresModifier()) {
            ToolNBT tags = TagUtil.getToolStats(rootCompound);
            tags.harvestLevel += 1;
        }
    }
}