package me.dpohvar.powernbt.utils.query;

public record IndexSelector(int index) implements IntegerSelector {

    public int indexToGet(int size){
        return index < 0 ? size - index : index;
    }

    public int indexToDelete(int size){
        return index < 0 ? size - index : index;
    }

    public int indexToSet(int size){
        return index < 0 ? size - index : index;
    }

    @Override
    public String toString() {
        return "["+index+"]";
    }
}
