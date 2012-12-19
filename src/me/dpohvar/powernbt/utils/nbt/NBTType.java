package me.dpohvar.powernbt.utils.nbt;

import me.dpohvar.powernbt.utils.versionfix.VersionFix;
import me.dpohvar.powernbt.utils.versionfix.XNBTBase;
import org.bukkit.ChatColor;
import static org.bukkit.ChatColor.*;

public enum NBTType {
    END((byte)0,"end",void.class,WHITE),
    BYTE((byte)1,"byte",byte.class,RED),//b
    SHORT((byte)2,"short",short.class,YELLOW),//s
    INT((byte)3,"int",int.class,BLUE),//i
    LONG((byte)4,"long",long.class,AQUA),//l
    FLOAT((byte)5,"float",float.class,DARK_PURPLE),//f
    DOUBLE((byte)6,"double",double.class,LIGHT_PURPLE),//d
    BYTEARRAY((byte)7,"byte[]",byte[].class,DARK_RED),//b
    STRING((byte)8,"string",String.class,GREEN),
    LIST((byte)9,"list", VersionFix.fixClass("net.minecraft.server.NBTTagList"),DARK_GRAY),
    COMPOUND((byte)10,"compound",VersionFix.fixClass("net.minecraft.server.NBTTagCompound"),GRAY),
    INTARRAY((byte)11,"int[]",int[].class,DARK_BLUE),//i
    ;
    public final String name;
    public final byte type;
    public final Class clazz;
    public final ChatColor color;

    NBTType(byte type, String name, Class clazz,ChatColor color){
        this.name = name;
        this.type = type;
        this.clazz = clazz;
        this.color = color;
    }

    public static NBTType fromByte(byte b){
        for (NBTType t: values())if(t.type==b) return t;
        return END;
    }
    public static NBTType fromBase(XNBTBase base){
        return fromByte(base.getTypeId());
    }
    public static NBTType fromClass(Class c){
        for (NBTType t: values())if(t.clazz.isAssignableFrom(c)) return t;
        return END;
    }
    public static NBTType fromString(String name){
        String s = name.toLowerCase();
        for (NBTType t: values())if(s.equalsIgnoreCase(t.name)) return t;
        for (NBTType t: values())if(t.name.toLowerCase().startsWith(s)) return t;
        return END;
    }
}
