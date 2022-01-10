package me.dpohvar.powernbt.utils;

import com.google.gson.*;
import me.dpohvar.powernbt.api.NBTManager;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.util.stream.Collectors.joining;

public class PowerJSONParser {

    private static final JsonParser parser = new JsonParser();
    private static final Gson gson = new Gson();

    public static Object parse(String value){
        JsonElement parsed = parser.parse(value);
        return convertToJavaValue(parsed);
    }

    private static Object convertToJavaValue(JsonElement json){
        if (json.isJsonArray()) {
            JsonArray asJsonArray = json.getAsJsonArray();
            List<Object> list = new ArrayList<>();
            for (JsonElement jsonElement : asJsonArray) {
                Object powerValue = convertToJavaValue(jsonElement);
                list.add(powerValue);
            }
            return list;
        }
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            Map<String,Object> cmp = new HashMap<>();
            for (Map.Entry<String, JsonElement> e : jsonObject.entrySet()) {
                Object powerValue = convertToJavaValue(e.getValue());
                if (powerValue != null) cmp.put(e.getKey(), powerValue);
            }
            return cmp;
        }
        if (json.isJsonNull()) return null;
        if (json.isJsonPrimitive()) {
            JsonPrimitive primitive = json.getAsJsonPrimitive();
            if (primitive.isBoolean()) return primitive.getAsBoolean();
            if (primitive.isNumber()) {
                Number number = primitive.getAsNumber();
                if (number.toString().contains(".")) return number.doubleValue();
                return number.longValue();
            }
            if (primitive.isString()) return primitive.getAsString();
        }
        return null;
    }

    public static String stringify(Object value){
        return gson.toJson(value);
    }

    public static void write(Object value, Writer writer) throws IOException{
        gson.toJson(value, writer);
    }

    public static Object read(Reader reader) throws IOException{
        JsonElement parse = parser.parse(reader);
        return convertToJavaValue(parse);
    }

    public static void write(Object value, File file) throws IOException {
        try (var writer = new FileWriter(file)) {
            write(value, writer);
        }
    }

    public static Object read(File file) throws IOException {
        try (var reader = new FileReader(file)) {
            return read(reader);
        }
    }
    public static void writeCompressed(Object value, File file) throws IOException {
        try (var writer = new OutputStreamWriter(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(file))))) {
            write(value, writer);
        }
    }

    public static Object readCompressed(File file) throws IOException {
        try (var reader = new InputStreamReader(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))))) {
            return read(reader);
        }
    }


}