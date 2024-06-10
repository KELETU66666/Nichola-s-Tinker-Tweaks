package liketechnik.tinkertweaks.claybuckets;

import liketechnik.tinkertweaks.config.Config;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

import javax.annotation.Nullable;

public class FluidClayBucketWrapper extends FluidBucketWrapper {

	public FluidClayBucketWrapper(ItemStack container) {
		super(container);
	}

	@Override
	@Nullable
	public FluidStack getFluid() {
		return IguanaItems.clayBucketFired.getFluid(container);
	}

	@Override
	protected void setFluid(FluidStack stack) {
		if(stack == null) {
			// if the current fluid breaks, return empty
			if (IguanaItems.clayBucketFired.doesBreak(container)) {
				container = ItemStack.EMPTY;
			} else {
				container = new ItemStack(IguanaItems.clayBucketFired);
			}
		} else {
			container = IguanaItems.clayBucketFired.withFluid(stack.getFluid());
		}
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if (container.getCount() != 1 || resource == null || resource.amount < Fluid.BUCKET_VOLUME || IguanaItems.clayBucketFired.hasFluid(container) || !canFillFluidType(resource)) {
			return 0;
		}

		if (doFill) {
			setFluid(resource);
		}

		return Fluid.BUCKET_VOLUME;
	}

	@Override
	public boolean canFillFluidType(FluidStack fluid) {
		if(!Config.bucketHotFluids() && IguanaItems.clayBucketFired.doesBreak(fluid)) {
			return false;
		}
		return super.canFillFluidType(fluid);
	}
}