package me.dpohvar.powernbt.utils.query;

public record FreeIndexSelector() implements IntegerSelector {

    public int indexToGet(int size){
        return size - 1;
    }

    public int indexToDelete(int size){
        return size - 1;
    }

    public int indexToSet(int size){
        return size;
    }

    @Override
    public String toString() {
        return "[]";
    }
}
