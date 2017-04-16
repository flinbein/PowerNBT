package me.dpohvar.powernbt.utils;

import static me.dpohvar.powernbt.utils.ReflectionUtils.*;

/**
 * @deprecated use {@link me.dpohvar.powernbt.utils.NBTParser}
 */
@Deprecated
public class MojangsonUtils {

    public static MojangsonUtils mojangsonUtils = new MojangsonUtils();

    private RefClass cMojangsonTypeParser = getRefClass("{nms}.MojangsonTypeParser, {nm}.nbt.JsonToNBT$Any, {MojangsonTypeParser}");
    private RefClass cMojangsonParser = getRefClass("{nms}.MojangsonParser, {nm}.nbt.JsonToNBT, {MojangsonParser}");
    private RefMethod mGetParser = cMojangsonParser.findMethodByParams(String.class, String.class);
    private RefMethod mParse = cMojangsonTypeParser.findMethodByReturnType("{nms}.NBTBase, {nm}.nbt.NBTBase, {NBTBase}");

    private MojangsonUtils(){}

    public Object parseString(String tagName, String value){
        Object parser = mGetParser.call(tagName, value);
        return mParse.of(parser).call();
    }

}
