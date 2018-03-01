package sonar.calculator.mod.common.tileentity.machines;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.api.items.IStability;
import sonar.calculator.mod.client.gui.machines.GuiStorageChamber;
import sonar.calculator.mod.common.containers.ContainerStorageChamber;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.common.tileentity.TileEntityLargeInventory;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.inventory.ILargeInventory;
import sonar.core.inventory.SonarLargeInventory;
import sonar.core.utils.IGuiTile;
import sonar.core.utils.SonarCompat;

public class TileEntityStorageChamber extends TileEntityLargeInventory implements IGuiTile, ILargeInventory {

	public CircuitType circuitType = CircuitType.None;

	@Override
	public void update() {
		super.update();
		this.resetCircuitType();
	}

	public TileEntityStorageChamber() {
		super.inv = new SonarLargeInventory(14, 1024) {
			// needs fixing I think
			@Override
			public boolean isItemValidForSlot(int slot, ItemStack item) {
				if (!SonarCompat.isEmpty(item) && item.getMetadata() == slot) {
					CircuitType stackType = getCircuitType(item);
					if (stackType == null) {
						return false;
					}
					if (((TileEntityStorageChamber) listener).circuitType != CircuitType.None) {
						if (((TileEntityStorageChamber) listener).circuitType != stackType) {
							return false;
						}
					}

					return super.isItemValidForSlot(slot, item);
				}
				return false;
			}
		};
		syncList.addParts(inv);
	}

	@Override
	public SonarLargeInventory getTileInv() {
		return inv;
	}

	public void resetCircuitType() {
		if (isServer()) {
			StoredItemStack[] slots = inv.slots;
			for (StoredItemStack stack : slots) {
				if (stack != null && stack.getStackSize() != 0) {
					CircuitType type = getCircuitType(stack.item);
					if (type != null && type != CircuitType.None) {
						circuitType = type;
						return;
					}
				}
			}
			circuitType = CircuitType.None;
		}
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type.isType(SyncType.SAVE, SyncType.DEFAULT_SYNC)) {
			circuitType = CircuitType.values()[nbt.getInteger("type")];
			if (circuitType == null) {
				circuitType = CircuitType.None;
			}
		}
		if (type.isType(SyncType.DROP)) {
			inv.readData(nbt, SyncType.SAVE);
			circuitType = CircuitType.values()[nbt.getInteger("type")];
			if (circuitType == null) {
				circuitType = CircuitType.None;
			}
		}
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type.isType(SyncType.SAVE, SyncType.DEFAULT_SYNC)) {
			nbt.setInteger("type", circuitType.ordinal());
		}
		if (type.isType(SyncType.DROP)) {
			inv.writeData(nbt, SyncType.SAVE);
			nbt.setInteger("type", circuitType.ordinal());
		}
		return nbt;
	}

	public static CircuitType getCircuitType(ItemStack stack) {
		if (stack.getItem() == Calculator.circuitBoard && stack.getItem() instanceof IStability) {
			IStability stability = (IStability) stack.getItem();
			if (stability.getStability(stack) && stack.hasTagCompound()) {
				if (stack.getTagCompound().getBoolean("Analysed")) {
					return CircuitType.Stable;
				}
			} else if (!stack.hasTagCompound()) {
				return CircuitType.Analysed;
			} else if (stack.getTagCompound().getBoolean("Analysed")) {
				return CircuitType.Analysed;
			}
		} else if (stack.getItem() == Calculator.circuitDamaged) {
			return CircuitType.Damaged;
		} else if (stack.getItem() == Calculator.circuitDirty) {
			return CircuitType.Dirty;
		}
		return null;
	}

	public enum CircuitType {
		Analysed, Stable, Damaged, Dirty, None;

		public boolean isProcessed() {
			return this == Analysed || this == Stable;
		}

		public boolean isStable() {
			return this == Stable;
		}
	}

	@Override
	public Object getGuiContainer(EntityPlayer player) {
		return new ContainerStorageChamber(player, this);
	}

	@Override
	public Object getGuiScreen(EntityPlayer player) {
		return new GuiStorageChamber(player, this);
	}
}
