package me.dpohvar.powernbt.utils;

import static me.dpohvar.powernbt.utils.ReflectionUtils.*;

public class MojangsonUtils {

    public static MojangsonUtils mojangsonUtils = new MojangsonUtils();

    RefClass cMojangsonTypeParser = getRefClass("{nms}.MojangsonTypeParser, {MojangsonTypeParser}");
    RefClass cMojangsonParser = getRefClass("{nms}.MojangsonParser, {MojangsonParser}");
    RefMethod mGetParser = cMojangsonParser.findMethodByParams(String.class, String.class);
    RefMethod mParse = cMojangsonTypeParser.findMethodByReturnType("{nms}.NBTBase, {nm}.nbt.NBTBase, {NBTBase}");

    private MojangsonUtils(){}

    public Object parseString(String tagName, String value){
        Object parser = mGetParser.call(tagName, value);
        return mParse.of(parser).call();
    }

}
