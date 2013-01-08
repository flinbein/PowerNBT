package me.dpohvar.powernbt.utils.nbt;

import me.dpohvar.powernbt.utils.versionfix.VersionFix;
import me.dpohvar.powernbt.utils.versionfix.XNBTBase;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

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
    public List<String> getTypes() {
        int id = item.getTypeId();
        List<String> s = new ArrayList<String>();
        s.add("item");
        if (id == 387 || id == 386) s.add("item_book");
        else if (id >= 298 && id <= 301) s.add("item_leather");
        else if (id == 397) s.add("item_skull");
        else if (id == 403) s.add("item_enchbook");
        else if (id == 373) s.add("item_potion");
        else if (id == 401) s.add("item_rocket");
        else if (id == 402) s.add("item_firework");
        if ((id >= 267 && id <= 279)
                || (id >= 283 && id <= 286)
                || (id >= 290 && id <= 294)
                || (id >= 298 && id <= 317)
                || id == 261
                || id == 346
                || id == 359
                || id == 256
                || id == 257
                ) s.add("item_repair");
        return s;
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
