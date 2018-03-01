package sonar.calculator.mod.common.tileentity.machines;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.api.items.IFlawlessCalculator;
import sonar.calculator.mod.api.modules.IModule;
import sonar.calculator.mod.client.gui.misc.GuiModuleWorkstation;
import sonar.calculator.mod.common.containers.ContainerModuleWorkstation;
import sonar.calculator.mod.common.item.calculators.FlawlessCalculator;
import sonar.calculator.mod.common.item.calculators.modules.EmptyModule;
import sonar.core.common.tileentity.TileEntityInventory;
import sonar.core.inventory.SonarInventory;
import sonar.core.utils.IGuiTile;
import sonar.core.utils.SonarCompat;

public class TileEntityModuleWorkstation extends TileEntityInventory implements IGuiTile {

	public boolean updateCalc;
	public boolean newCalc;

	public TileEntityModuleWorkstation() {
		super.inv = new SonarInventory(this, 1 + FlawlessCalculator.moduleCapacity) {

			@Override
			public void setInventorySlotContents(int i, ItemStack itemstack) {
				if (i != 16) {
					updateCalc = true;
				} else {
					if (SonarCompat.isEmpty(itemstack)) {
						clear();
					} else {
						newCalc = true;
					}
				}
				super.setInventorySlotContents(i, itemstack);
			}

			@Override
			public boolean isItemValidForSlot(int slot, ItemStack stack) {
				if (hasFlawlessCalculator() && !SonarCompat.isEmpty(stack)) {
					IModule module = Calculator.modules.getRegisteredObject(Calculator.moduleItems.getSecondaryObject(stack.getItem()));
					if (module != null) {
						ItemStack calcStack = slots().get(FlawlessCalculator.moduleCapacity);
						IFlawlessCalculator calc = (IFlawlessCalculator) calcStack.getItem();
						if (calc.canAddModule(calcStack, module, slot)) {
							return true;
						}
					}
				}
				return true;
			}

			@Override
			public ItemStack decrStackSize(int slot, int var2) {
				updateCalc = true;
				ItemStack toReturn = super.decrStackSize(slot, var2);
				if (slot == 16) {
					clear();
				}
				return toReturn;
			}

			@Override
			public ItemStack removeStackFromSlot(int i) {
				updateCalc = true;
				ItemStack toReturn = super.removeStackFromSlot(i);
				if (i == 16) {
					clear();
				}
				return toReturn;
			}

			@Override
			public void closeInventory(EntityPlayer player) {
				updateCalc = true;
			}

			@Override
			public int getInventoryStackLimit() {
				return 1;
			}

			@Override
			public void clear() {
				updateCalc = true;
				for (int i = 0; i < getSizeInventory(); i++) {
					if (i != 16) {
						setInventorySlotContents(i, SonarCompat.getEmpty());
					}
				}
			}

			public boolean isUsableByPlayer(EntityPlayer player) {
				// does the player own this FLAWLESS CALCULATOR?
				return true;
			}
		};
		syncList.addPart(inv);
	}

	@Override
	public void update() {
		if (isClient() || !hasFlawlessCalculator()) {
			return;
		}
		super.update();
		ItemStack stack = slots().get(FlawlessCalculator.moduleCapacity);
		IFlawlessCalculator calc = (IFlawlessCalculator) stack.getItem();
		if (newCalc) {
			ArrayList<IModule> modules = calc.getModules(stack);
			int i = 0;
			for (IModule module : modules) {
				Item item = Calculator.moduleItems.getPrimaryObject(module.getName());
				if (item != null) {
					ItemStack moduleStack = new ItemStack(item, 1);
					moduleStack.setTagCompound(calc.getModuleTag(stack, i));
					slots().set(i, moduleStack);
				}
				i++;
			}
			newCalc = false;
			updateCalc = false;
		} else if (updateCalc) {
			ArrayList<IModule> modules = new ArrayList<>();
			for (int i = 0; i < FlawlessCalculator.moduleCapacity; i++) {
				ItemStack target = slots().get(i);
				NBTTagCompound tag = new NBTTagCompound();
				IModule module = !SonarCompat.isEmpty(target) ? Calculator.modules.getRegisteredObject(Calculator.moduleItems.getSecondaryObject(target.getItem())) : EmptyModule.EMPTY;
				if (module == null) {
					module = EmptyModule.EMPTY;
				} else if (!SonarCompat.isEmpty(target)) {
					tag = target.getTagCompound();
				}
				calc.addModule(stack, tag, module, i);
			}
			updateCalc = false;
		}
	}

	public boolean hasFlawlessCalculator() {
		return slots().get(FlawlessCalculator.moduleCapacity).getItem() instanceof IFlawlessCalculator;
	}

	@Override
	public Object getGuiContainer(EntityPlayer player) {
		return new ContainerModuleWorkstation(player.inventory, this);
	}

	@Override
	public Object getGuiScreen(EntityPlayer player) {
		return new GuiModuleWorkstation(player.inventory, this);
	}
}
