package me.dpohvar.powernbt.utils.nbt;

import me.dpohvar.powernbt.utils.versionfix.VersionFix;
import me.dpohvar.powernbt.utils.versionfix.XNBTBase;
import me.dpohvar.powernbt.utils.versionfix.XNBTTagCompound;
import me.dpohvar.powernbt.utils.versionfix.XNBTTagList;

import java.util.List;
import java.util.Queue;

import static me.dpohvar.powernbt.utils.versionfix.StaticValues.*;
import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTEdit {
    private NBTTagable tagable;

    /*
     Object:
        NBTBase
        NBTVariable
        LivingEntity
        Entity

      */
    public NBTEdit(NBTTagable tagable) {
        this.tagable = tagable;
    }

    public XNBTBase getBase(NBTQuery query) {
        Queue<Object> queue = query.getQueue();
        XNBTBase current = tagable.getRootBase();
        while(true){
            Object t = queue.poll();
            if(t==null||current==null) return current;
            if(current.getTypeId()==typeCompound && t instanceof String){
                XNBTTagCompound compound = VersionFix.getShell(XNBTTagCompound.class,current.getProxyObject());
                String key = (String) t;
                if (!compound.hasKey(key)) return null;
                current = VersionFix.getShell(XNBTBase.class, compound.get(key));
            } else if(current.getTypeId()==typeList && t instanceof Integer) {
                XNBTTagList list = VersionFix.getShell(XNBTTagList.class,current.getProxyObject());
                int index = (Integer) t;
                if (list.size()==0) return null;
                else if (index==-1) current = VersionFix.getShell(XNBTBase.class,list.get(list.size()-1));
                else if (list.size()<index) current = VersionFix.getShell(XNBTBase.class,list.get(index));
                else return null;
            } else throw new RuntimeException(plugin.translate("error_nochildren",current.getName()));
        }
    }

    public boolean setBase(NBTQuery query, Object base) {
        // create path if not exists and set copy of base
        return false;
    }

    public boolean removeBase(NBTQuery query) {
        Queue<Object> queue = query.getQueue();
        if(queue.isEmpty()) {
            tagable.removeRootBase();
            return true;
        }
        XNBTBase current = tagable.getRootBase();
        while(true){
            if(queue.size()==1){
                Object t = queue.poll();
                if(current.getTypeId()==typeCompound && t instanceof String){
                    XNBTTagCompound compound = VersionFix.getShell(XNBTTagCompound.class,current.getProxyObject());
                    String key = (String) t;
                    if (!compound.hasKey(key)) return false;
                    compound.remove(key);
                } else if(current.getTypeId()==typeList && t instanceof Integer) {
                    XNBTTagList list = VersionFix.getShell(XNBTTagList.class,current.getProxyObject());
                    int index = (Integer) t;
                    List<Object> bases = (List<Object>) list.getProxyField("list");
                    if (bases.size()==0) return false;
                    else if (bases.size()>=index) return false;
                    else if (index==-1) bases.remove(bases.size()-1);
                    else bases.remove(index);
                } else throw new RuntimeException(plugin.translate("error_nochildren",current.getName()));
                return true;
            }
            Object t = queue.poll();
            if(current==null) return false;
            if(current.getTypeId()==typeCompound && t instanceof String){
                XNBTTagCompound compound = VersionFix.getShell(XNBTTagCompound.class,current.getProxyObject());
                String key = (String) t;
                if (!compound.hasKey(key)) return false;
                current = VersionFix.getShell(XNBTBase.class, compound.get(key));
            } else if(current.getTypeId()==typeList && t instanceof Integer) {
                XNBTTagList list = VersionFix.getShell(XNBTTagList.class,current.getProxyObject());
                int index = (Integer) t;
                if (list.size()==0) return false;
                else if (index==-1) current = VersionFix.getShell(XNBTBase.class,list.get(list.size()-1));
                else if (list.size()<index) current = VersionFix.getShell(XNBTBase.class,list.get(index));
                else return false;
            } else throw new RuntimeException(plugin.translate("error_nochildren",current.getName()));
        }
    }
}
