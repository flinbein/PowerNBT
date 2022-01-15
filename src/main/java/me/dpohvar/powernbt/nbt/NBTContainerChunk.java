package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.utils.StringParser;
import org.bukkit.Chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NBTContainerChunk extends NBTContainer<Chunk> {

    Chunk chunk;

    public NBTContainerChunk(Chunk chunk) {
        super("chunk:"+chunk.getX()+":"+chunk.getZ()+":"+ StringParser.wrapToQuotesIfNeeded(chunk.getWorld().getName()));
        this.chunk = chunk;
    }

    public Chunk getObject() {
        return chunk;
    }

    @Override
    public List<String> getTypes() {
        List<String> s = new ArrayList<>();
        s.add("chunk");
        return s;
    }

    @Override
    public NBTCompound readTag() {
        return NBTManager.getInstance().read(chunk);
    }

    @Override
    public void writeTag(Object value) {
        NBTCompound compound = null;
        if (value instanceof NBTCompound c) compound = c;
        else if (value instanceof Map map) compound = new NBTCompound(map);
        if (compound == null) return;
        NBTManager.getInstance().write(chunk, compound);
    }

    @Override
    protected Class<Chunk> getContainerClass() {
        return Chunk.class;
    }

    @Override
    public String toString(){
        return chunk.toString();
    }
}
