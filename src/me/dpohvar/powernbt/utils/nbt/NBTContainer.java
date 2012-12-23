package me.dpohvar.powernbt.utils.nbt;

import me.dpohvar.powernbt.utils.versionfix.VersionFix;
import me.dpohvar.powernbt.utils.versionfix.XNBTBase;
import me.dpohvar.powernbt.utils.versionfix.XNBTTagCompound;
import me.dpohvar.powernbt.utils.versionfix.XNBTTagList;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import static me.dpohvar.powernbt.PowerNBT.plugin;
import static me.dpohvar.powernbt.utils.nbt.NBTType.*;
import static me.dpohvar.powernbt.utils.versionfix.StaticValues.*;
import static me.dpohvar.powernbt.utils.versionfix.VersionFix.getNew;
import static me.dpohvar.powernbt.utils.versionfix.VersionFix.getShell;

public abstract class NBTContainer {

    abstract public XNBTBase getRootBase();

    abstract public void setRootBase(XNBTBase base);

    abstract public String getName();

    public void removeRootBase() {
        setRootBase(getShell(XNBTBase.class, getNew(classNBTTagCompound, noInput)));
    }

    public abstract Object getObject();


    public XNBTBase getBase(NBTQuery query) {
        if (query == null) return getRootBase();
        Queue<Object> queue = query.getQueue();
        XNBTBase current = getRootBase();
        while (true) {
            Object t = queue.poll();
            if (t == null || current == null) return current;
            if (current.getTypeId() == typeCompound && t instanceof String) {
                XNBTTagCompound compound = VersionFix.getShell(XNBTTagCompound.class, current.getProxyObject());
                String key = (String) t;
                if (!compound.hasKey(key)) return null;
                current = VersionFix.getShell(XNBTBase.class, compound.get(key));
            } else if (current.getTypeId() == typeList && t instanceof Integer) {
                XNBTTagList list = VersionFix.getShell(XNBTTagList.class, current.getProxyObject());
                int index = (Integer) t;
                if (list.size() == 0) return null;
                else if (index == -1) current = VersionFix.getShell(XNBTBase.class, list.get(list.size() - 1));
                else if (list.size() > index) current = VersionFix.getShell(XNBTBase.class, list.get(index));
                else return null;
            } else throw new RuntimeException(plugin.translate("error_nochildren", current.getName()));
        }
    }

    public boolean setBase(NBTQuery query, XNBTBase base) {
        if (query == null || query.isEmpty()) {
            setRootBase(base);
            return true;
        }
        Queue<Object> queue = query.getQueue();
        XNBTBase root = getRootBase();
        if (root == null) {
            Object z = queue.peek();
            if (z instanceof String) root = COMPOUND.getDefault();
            else if (z instanceof Integer) root = LIST.getDefault();
        } else {
            root = getShell(XNBTBase.class, root.clone());
        }
        XNBTBase current = root;
        while (true) {
            if (queue.size() == 1) {
                Object t = queue.poll();
                if (current.getTypeId() == typeCompound && t instanceof String) {
                    XNBTTagCompound compound = VersionFix.getShell(XNBTTagCompound.class, current.getProxyObject());
                    String key = (String) t;
                    compound.set(key, base.clone());
                    setRootBase(root);
                    return true;
                } else if (current.getTypeId() == typeList && t instanceof Integer) {
                    XNBTTagList xList = VersionFix.getShell(XNBTTagList.class, current.getProxyObject());
                    List<Object> list = (List<Object>) xList.getProxyField("list");
                    int index = (Integer) t;
                    if (index == -1) index = list.size();
                    NBTType type = NBTType.fromBase(base);
                    NBTType subType = NBTType.fromByte((Byte) xList.getProxyField("type"));
                    if (xList.size() != 0 && !type.equals(subType)) {
                        //todo: error: uncomp types
                        throw new RuntimeException(plugin.translate("error_nochildren", current.getName()));
                    }
                    while (index >= list.size()) {
                        xList.add(type.getDefault().clone());
                    }
                    list.set(index, base.clone());
                    setRootBase(root);
                    return true;
                } else {
                    throw new RuntimeException(plugin.translate("error_nochildren", current.getName()));
                }
            }
            Object t = queue.poll();
            if (current.getTypeId() == typeCompound && t instanceof String) {
                XNBTTagCompound compound = VersionFix.getShell(XNBTTagCompound.class, current.getProxyObject());
                String key = (String) t;
                if (!compound.hasKey(key)) {
                    Object z = queue.peek();
                    XNBTBase b = null;
                    if (z instanceof String) b = COMPOUND.getDefault();
                    else if (z instanceof Integer) b = LIST.getDefault();
                    compound.set(key, b);
                    current = b;
                } else {
                    current = VersionFix.getShell(XNBTBase.class, compound.get(key));
                }
            } else if (current.getTypeId() == typeList && t instanceof Integer) {
                XNBTTagList xList = VersionFix.getShell(XNBTTagList.class, current.getProxyObject());
                NBTType subType = NBTType.fromByte((Byte) xList.getProxyField("type"));
                List<Object> list = (List<Object>) xList.getProxyField("list");
                int index = (Integer) t;
                if (index == -1) index = list.size();
                XNBTBase b = null;
                if (subType != END) {
                    b = getShell(XNBTBase.class, subType.getDefault().clone());
                } else {
                    Object z = queue.peek();
                    if (z instanceof String) b = COMPOUND.getDefault();
                    else if (z instanceof Integer) b = LIST.getDefault();
                }
                while (index >= list.size()) {
                    xList.add(b.clone());
                }
                current = getShell(XNBTBase.class, xList.get(index));
            } else throw new RuntimeException(plugin.translate("error_nochildren", current.getName()));
        }
    }

    public boolean removeBase(NBTQuery query) {
        if (query == null || query.isEmpty()) {
            removeRootBase();
            return true;
        }
        XNBTBase root = getRootBase();
        if (root == null) {
            return false;
        } else {
            root = getShell(XNBTBase.class, root.clone());
        }
        XNBTBase current = root;
        Queue<Object> queue = query.getQueue();
        while (true) {
            if (queue.size() == 1) {
                Object t = queue.poll();
                if (current.getTypeId() == typeCompound && t instanceof String) {
                    XNBTTagCompound compound = VersionFix.getShell(XNBTTagCompound.class, current.getProxyObject());
                    String key = (String) t;
                    if (!compound.hasKey(key)) return false;
                    Map<String, Object> map = (Map<String, Object>) compound.getProxyField("map");
                    map.remove(key);
                    setRootBase(root);
                } else if (current.getTypeId() == typeList && t instanceof Integer) {
                    XNBTTagList list = VersionFix.getShell(XNBTTagList.class, current.getProxyObject());
                    int index = (Integer) t;
                    List<Object> bases = (List<Object>) list.getProxyField("list");
                    if (bases.size() == 0) return false;
                    else if (bases.size() >= index) return false;
                    else if (index == -1) bases.remove(bases.size() - 1);
                    else bases.remove(index);
                    setRootBase(root);
                } else throw new RuntimeException(plugin.translate("error_nochildren", current.getName()));
                return true;
            }
            Object t = queue.poll();
            if (current == null) return false;
            if (current.getTypeId() == typeCompound && t instanceof String) {
                XNBTTagCompound compound = VersionFix.getShell(XNBTTagCompound.class, current.getProxyObject());
                String key = (String) t;
                if (!compound.hasKey(key)) return false;
                current = VersionFix.getShell(XNBTBase.class, compound.get(key));
            } else if (current.getTypeId() == typeList && t instanceof Integer) {
                XNBTTagList list = VersionFix.getShell(XNBTTagList.class, current.getProxyObject());
                int index = (Integer) t;
                if (list.size() == 0) return false;
                else if (index == -1) current = VersionFix.getShell(XNBTBase.class, list.get(list.size() - 1));
                else if (list.size() < index) current = VersionFix.getShell(XNBTBase.class, list.get(index));
                else return false;
            } else throw new RuntimeException(plugin.translate("error_nochildren", current.getName()));
        }
    }

}
