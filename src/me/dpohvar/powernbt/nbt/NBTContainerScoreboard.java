package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.StaticValues;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class NBTContainerScoreboard extends NBTContainer {

    private static final Class class_NBTTagCompound = StaticValues.getClass("NBTTagCompound");
    private static final Class class_CraftScoreboard = StaticValues.getClass("CraftScoreboard");
    private static final Class class_ServerScoreboard = StaticValues.getClass("ServerScoreboard");
    private static final Class class_Scoreboard = StaticValues.getClass("Scoreboard");
    private static final Class class_ScoreboardSaveData = StaticValues.getClass("ScoreboardSaveData");
    private static Field field_Scoreboard_Craft;
    private static Field field_ScoreboardSaveData;
    private static Method method_Read;
    private static Method method_Write;
    private static Constructor construct_CraftScoreboard;

    static{
        try{
            field_Scoreboard_Craft = StaticValues.getFieldByType(class_CraftScoreboard,class_Scoreboard);
            field_ScoreboardSaveData = StaticValues.getFieldByType(class_ServerScoreboard,class_ScoreboardSaveData);
            construct_CraftScoreboard = class_CraftScoreboard.getDeclaredConstructor(class_Scoreboard);
            construct_CraftScoreboard.setAccessible(true);
            if (field_Scoreboard_Craft !=null) field_Scoreboard_Craft.setAccessible(true);
            if (field_ScoreboardSaveData!=null) field_ScoreboardSaveData.setAccessible(true);
            for(Method m:class_ScoreboardSaveData.getDeclaredMethods()){
                if (m.getParameterTypes().length!=1) continue;
                if (!m.getParameterTypes()[0].equals(class_NBTTagCompound)) continue;
                if(m.getName().endsWith("a")) method_Write = m;
                if(m.getName().endsWith("b")) method_Read = m;
                m.setAccessible(true);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    Scoreboard scoreboard;

    public NBTContainerScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public Scoreboard getObject() {
        return scoreboard;
    }

    @Override
    public List<String> getTypes() {
        return Arrays.asList("scoreboard");
    }

    @Override
    public NBTTagCompound getCustomTag() {
        return getTag();
    }

    public NBTTagCompound getTag() {
        NBTTagCompound base;
        base = new NBTTagCompound();
        try {
            Object board = field_Scoreboard_Craft.get(scoreboard);
            Object data = field_ScoreboardSaveData.get(board);
            method_Read.invoke(data, base.getHandle());
        } catch (Exception e){
            e.printStackTrace();
        }
        return base;
    }

    @Override
    public void setTag(NBTBase base) {
        try{
            Object board = field_Scoreboard_Craft.get(scoreboard);
            Object data = field_ScoreboardSaveData.get(board);
            Scoreboard temp = (Scoreboard) construct_CraftScoreboard.newInstance(board);
            for(Objective o:temp.getObjectives()) o.unregister();
            for(Team t:temp.getTeams()) t.unregister();
            method_Write.invoke(data,base.getHandle());
            field_ScoreboardSaveData.set(board,data);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setCustomTag(NBTBase base) {
        if (!(base instanceof NBTTagCompound)) return;
        setTag(base);
    }

    @Override
    public String getName() {
        return "Scoreboard";
    }
}
