package keletu.tinkertweaks.mobhead.items;

import keletu.tinkertweaks.mobhead.IguanaMobHeads;
import keletu.tinkertweaks.reference.Reference;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class Wearable extends Item {
    private static final String[] textureTypes = new String[] {"bucket_holey", "clay_bucket_cracked", "enderman_jaw", "bathat"};

    public Wearable() {
        this.setContainerItem(IguanaMobHeads.wearables);
        this.setTranslationKey(Reference.prefix("wearable"));
        this.setRegistryName("wearable");

        this.setMaxStackSize(1);
    }

    @Override
    public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
        return armorType == EntityEquipmentSlot.HEAD; // 0 = helmet
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addInformation(ItemStack item, World player, List<String> tooltips, ITooltipFlag advanced) {
        // specul tooltips
        tooltips.add(TextFormatting.DARK_GRAY +  I18n.format("tooltip." + textureTypes[item.getItemDamage()]));
    }

    @Override
    public String getTranslationKey(ItemStack par1ItemStack)
    {
        int i = par1ItemStack.getItemDamage();

        if (i < 0 || i >= textureTypes.length)
            i = 0;

        return getTranslationKey() + "." + textureTypes[i];
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getSubItems(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List)
    {
        if(this.isInCreativeTab(par2CreativeTabs)) {
            for (int j = 0; j < textureTypes.length; ++j)
                par3List.add(new ItemStack(this, 1, j));
        }
    }
}