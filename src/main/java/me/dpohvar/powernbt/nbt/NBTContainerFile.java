package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.utils.PowerJSONParser;
import me.dpohvar.powernbt.utils.StringParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NBTContainerFile extends NBTContainer<File> {

    private final File file;

    public NBTContainerFile(File file) {
        this(file, null);
    }

    public NBTContainerFile(File file, String selector) {
        super(selector == null ? "file:"+ StringParser.wrapToQuotesIfNeeded(file.toString()) : selector);
        this.file = file;
    }

    public File getObject() {
        return file;
    }

    @Override
    public Object readTag() {

        boolean isNBT;
        if (!file.exists() || file.isDirectory()) return null;
        try (var input = getDataInputStream()) {
            byte b = input.readByte();
            NBTType nbtType = NBTType.fromByte(b);
            isNBT = (nbtType != null);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("no file",e);
        } catch (IOException e) {
            throw new RuntimeException("can't read file",e);
        }

        try {
            if (isNBT) return NBTManager.getInstance().read(file);
            else return PowerJSONParser.read(file);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException("can't read file",e);
        } catch (Exception e) {
            throw new RuntimeException("wrong format",e);
        }
    }

    protected DataInputStream getDataInputStream() throws FileNotFoundException {
        return new DataInputStream(new FileInputStream(file));
    }

    protected final boolean isNBT(Object base){
        return NBTType.fromValueOrNull(base) != null;
    }

    @Override
    public void writeTag(Object base) {
        try {
            if (isNBT(base)) {
                writeTagNBT(base);
            } else {
                writeTagJSON(base);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("file "+file+" not found", e);
        } catch (Exception e) {
            throw new RuntimeException("can't write to file", e);
        }
    }

    public void writeTagNBT(Object base) throws Exception{
        NBTManager.getInstance().write(file, base);
    }

    public void writeTagJSON(Object base) throws IOException{
        PowerJSONParser.write(base, file);
    }

    @Override
    public List<String> getTypes() {
        return new ArrayList<>();
    }

    @Override
    public void eraseTag() {
        file.delete();
    }

    @Override
    protected Class<File> getContainerClass() {
        return File.class;
    }

    @Override
    public String toString(){
        return file.getName();
    }
}
