package sonar.calculator.mod.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import sonar.core.utils.SonarCompat;

public class ContainerInfoCalculator extends Container {
	public EntityPlayer player;

	public ContainerInfoCalculator(EntityPlayer player) {
		this.player = player;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

    @Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
		return SonarCompat.getEmpty();
	}
}