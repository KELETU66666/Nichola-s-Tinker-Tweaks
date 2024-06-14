package keletu.tinkertweaks.level;

import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;

public class ToolHarvestLevelNBT extends ModifierNBT {

    public static final String TAG_BOOST_EXP = "head_exp";
    public static final String TAG_IS_BOOSTED = "harvest_level_modified";

    public int bxp;
    public boolean isBoosted;

    public ToolHarvestLevelNBT(NBTTagCompound tag) {
        super(tag);
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);
        bxp = tag.getInteger(TAG_BOOST_EXP);
        isBoosted = tag.getBoolean(TAG_IS_BOOSTED);
    }

    @Override
    public void write(NBTTagCompound tag) {
        super.write(tag);
       //int hlvl = tag.getInteger(HARVESTLEVEL);
       ////if(!Config.pickaxeBoostRequired)
       ////    return;
       //if (hlvl == 0/* || !(tool instanceof Pickaxe || tool instanceof Hammer)*/)
       //    return;

        tag.setLong(TAG_BOOST_EXP, bxp);
        tag.setBoolean(TAG_IS_BOOSTED, isBoosted);
    }
}
