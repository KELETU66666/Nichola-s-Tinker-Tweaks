package liketechnik.tinkertweaks.mobhead.blocks;

import liketechnik.tinkertweaks.mobhead.IguanaMobHeads;
import liketechnik.tinkertweaks.mobhead.tilenetities.IguanaSkullTileEntity;
import liketechnik.tinkertweaks.reference.Reference;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import java.util.Random;

@Optional.Interface(modid = "Thaumcraft", iface = "thaumcraft.api.crafting.IInfusionStabiliser")
public class IguanaSkullBlock extends BlockSkull /*implements IInfusionStabiliser */{

	public IguanaSkullBlock() {
		super();

        this.setHardness(1.0F);
        this.setSoundType(SoundType.STONE);
        this.setTranslationKey(Reference.prefix("skull"));
        this.setRegistryName("skull");
	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new IguanaSkullTileEntity();
	}

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(IguanaMobHeads.skullItem);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return IguanaMobHeads.skullItem;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        if(world.getTileEntity(pos) == null && !(world.getTileEntity(pos) instanceof IguanaSkullTileEntity))
            return;

        drops.add(new ItemStack(IguanaMobHeads.skullItem, 1, ((IguanaSkullTileEntity)world.getTileEntity(pos)).getSkullType()));
    }

    //@Optional.Method(modid = "Thaumcraft")
    //@Override
    //public boolean canStabaliseInfusion(World world, int x, int y, int z) {
    //    return true;
    //}
}