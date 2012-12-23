package me.dpohvar.powernbt.utils.nbt;

import me.dpohvar.powernbt.utils.versionfix.VersionFix;
import me.dpohvar.powernbt.utils.versionfix.XNBTBase;
import org.bukkit.inventory.ItemStack;

import static me.dpohvar.powernbt.utils.versionfix.StaticValues.noInput;
import static me.dpohvar.powernbt.utils.versionfix.StaticValues.oneNBTTagCompound;
import static me.dpohvar.powernbt.utils.versionfix.VersionFix.callMethod;
import static me.dpohvar.powernbt.utils.versionfix.VersionFix.getShell;

public class NBTContainerItem extends NBTContainer {

    ItemStack item;

    public NBTContainerItem(ItemStack item) {
        this.item = item;
    }

    @Override
    public ItemStack getObject() {
        return item;
    }

    @Override
    public XNBTBase getRootBase() {
        Object is = null;
        try {
            is = callMethod(item, "getHandle", noInput);
        } catch (Exception ignored) {
        }
        if (is == null) {
            is = getShell(VersionFix.FixInterface.class, item).getProxyField("handle");
        }
        return getShell(XNBTBase.class, callMethod(is, "getTag", noInput));
    }

    @Override
    public void setRootBase(XNBTBase base) {
        Object is = null;
        try {
            is = callMethod(item, "getHandle", noInput);
        } catch (Exception ignored) {
        }
        if (is == null) {
            is = getShell(VersionFix.FixInterface.class, item).getProxyField("handle");
        }
        callMethod(is, "setTag", oneNBTTagCompound, base);
    }

    @Override
    public String getName() {
        return item.getType().name() + ":" + item.getData().toString();
    }

    @Override
    public void removeRootBase() {
        setRootBase(null);
    }
}
