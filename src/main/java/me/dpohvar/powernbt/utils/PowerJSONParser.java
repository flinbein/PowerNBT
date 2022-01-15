package me.dpohvar.powernbt.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PowerJSONParser {

    private static final Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

    public static Object parse(String value){
        return gson.fromJson(value, Object.class);
    }

    public static String stringify(Object value){
        return gson.toJson(value);
    }

    public static void write(Object value, Writer writer) throws IOException{
        gson.toJson(value, writer);
    }

    public static Object read(Reader reader) throws IOException{
        return gson.fromJson(reader, Object.class);
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