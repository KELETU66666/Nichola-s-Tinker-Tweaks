package keletu.tinkertweaks.mobhead.items;

import keletu.tinkertweaks.mobhead.IguanaMobHeads;
import keletu.tinkertweaks.reference.Reference;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class IguanaSkull extends ItemSkull {
    public static final int META_ENDERMAN  = 0;
    public static final int META_PIGZOMBIE = 1;
    public static final int META_BLAZE     = 2;
    public static final int META_BLIZZ     = 3;

    private static final ModelBiped EMPTY_MODEL = new ModelBiped();

    // an entry for a head
    public static class HeadEntry {
        public String name;
        public String iconString;

        public HeadEntry(String name) {
            this.name = name;
        }
    }

    private static final Map<Integer, HeadEntry> headEntries = new HashMap<Integer, HeadEntry>();
    // add vanilla heads
    static {
        addHead(0, "enderman", "skull_enderman");
        addHead(1, "pigman", "skull_pigman");
        addHead(2, "blaze", "skull_blaze");
        EMPTY_MODEL.setVisible(false);
    }

    public static void addHead(int meta, String name, String icon) {
        headEntries.put(meta, new HeadEntry(name));
    }

    public static boolean isHeadRegistered(int meta) { return headEntries.containsKey(meta); }

    @SideOnly(Side.CLIENT)
    @Nonnull
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        return (ModelBiped) EMPTY_MODEL;
    }

	public IguanaSkull() {
		super();
        this.setTranslationKey(Reference.prefix("skull"));
        this.setRegistryName("skull");
        this.setContainerItem(Items.SKULL);
	}

	/**
	 * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
	 * different names based on their damage or NBT.
	 */
	@Override
	public String getTranslationKey(ItemStack par1ItemStack)
	{
		Integer i = par1ItemStack.getItemDamage();
        if(!headEntries.containsKey(i))
            i = 0;

    	return getTranslationKey() + "." + headEntries.get(i).name;
	}

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
        if(this.isInCreativeTab(tab)) {
            for (Integer meta : headEntries.keySet())
                items.add(new ItemStack(this, 1, meta));
        }
	}

    @Override
    public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
        return armorType == EntityEquipmentSlot.HEAD; // 0 = helmet
    }


    // copy'n'paste of the ItemSkull method, but places our own skull instead of the vanilla one
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (facing == EnumFacing.DOWN)
        {
            return EnumActionResult.FAIL;
        }
        else
        {
            if (worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos))
            {
                facing = EnumFacing.UP;
                pos = pos.down();
            }
            IBlockState iblockstate = worldIn.getBlockState(pos);
            Block block = iblockstate.getBlock();
            boolean flag = block.isReplaceable(worldIn, pos);

            if (!flag)
            {
                if (!worldIn.getBlockState(pos).getMaterial().isSolid() && !worldIn.isSideSolid(pos, facing, true))
                {
                    return EnumActionResult.FAIL;
                }

                pos = pos.offset(facing);
            }

            ItemStack itemstack = player.getHeldItem(hand);

            if (player.canPlayerEdit(pos, facing, itemstack) && IguanaMobHeads.skullBlock.canPlaceBlockAt(worldIn, pos))
            {
                if (worldIn.isRemote)
                {
                    return EnumActionResult.SUCCESS;
                }
                else
                {
                    worldIn.setBlockState(pos, IguanaMobHeads.skullBlock.getDefaultState().withProperty(BlockSkull.FACING, facing), 11);
                    int i = 0;

                    if (facing == EnumFacing.UP)
                    {
                        i = MathHelper.floor((double)(player.rotationYaw * 16.0F / 360.0F) + 0.5D) & 15;
                    }

                    TileEntity tileentity = worldIn.getTileEntity(pos);

                    if (tileentity instanceof TileEntitySkull)
                    {
                        TileEntitySkull tileentityskull = (TileEntitySkull)tileentity;

                        tileentityskull.setType(itemstack.getMetadata());

                        tileentityskull.setSkullRotation(i);
                    }

                    if (player instanceof EntityPlayerMP)
                    {
                        CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, itemstack);
                    }

                    itemstack.shrink(1);
                    return EnumActionResult.SUCCESS;
                }
            }
            else
            {
                return EnumActionResult.FAIL;
            }
        }
    }
}