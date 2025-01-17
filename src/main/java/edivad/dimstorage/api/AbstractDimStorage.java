package edivad.dimstorage.api;

import edivad.dimstorage.manager.DimStorageManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public abstract class AbstractDimStorage {

  public final DimStorageManager manager;
  public final Frequency freq;
  private boolean dirty;
  private int changeCount;

  public AbstractDimStorage(DimStorageManager manager, Frequency freq) {
    this.manager = manager;
    this.freq = freq;

    this.dirty = false;
    this.changeCount = 0;
  }

  public void setDirty() {
    if (manager.isServer()) {
      if (!dirty) {
        dirty = true;
        manager.requestSave(this);
      }

      changeCount++;
    }
  }

  public void setClean() {
    dirty = false;
  }

  public int getChangeCount() {
    return changeCount;
  }

  public abstract void clearStorage();

  public abstract String type();

  public abstract CompoundTag saveToTag(HolderLookup.Provider registries);

  public abstract void loadFromTag(HolderLookup.Provider registries, CompoundTag tag);

  @Override
  public String toString() {
    return freq + ",type=" + type();
  }
}
