package me.dpohvar.powernbt.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.*;

public class VersionFix {
    static String version = "";
    static boolean fuck = false;

    public static interface FixInterface {
        Object getProxyObject();

        Object getProxyField(String name);

        void setProxyField(String name, Object value);
    }

    private static class FixHandler implements InvocationHandler {
        private Object o;

        public FixHandler(Object o) {
            this.o = o;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("getProxyObject")) {
                return o;
            } else if (method.getName().equals("getProxyField")) {
                Field f = o.getClass().getDeclaredField((String) args[0]);
                f.setAccessible(true);
                return f.get(o);
            } else if (method.getName().equals("setProxyField")) {
                Field f = o.getClass().getDeclaredField((String) args[0]);
                f.setAccessible(true);
                f.set(o, args[1]);
                return null;
            }
            if (args != null) for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof FixInterface) {
                    args[i] = ((FixInterface) args[i]).getProxyObject();
                }
            }
            Method m = null;
            try {
                m = o.getClass().getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                for (Method t : o.getClass().getMethods()) {
                    if (
                            t.getName().equals(method.getName()) &&
                                    t.getParameterTypes().length == method.getParameterTypes().length
                            ) {
                        m = t;
                        break;
                    }
                }
            }
            if (m == null) throw new NoSuchMethodException(method.toString());
            m.setAccessible(true);
            return m.invoke(o, args);
        }
    }

    static {
        try {
            Object s = Bukkit.getServer();
            Method m = s.getClass().getMethod("getHandle");
            Object cs = m.invoke(s);
            String className = cs.getClass().getName();
            String[] vers = className.split("\\.");
            if (vers.length == 5) {
                fuck = true;
                version = vers[3] + ".";
            }
        } catch (Throwable ignored) {
        }
    }

    public static boolean needFix() {
        return fuck;
    }

    public static Object getNew(Class clazz, Class[] classes, Object... params) {
        try {
            Constructor constructor = clazz.getDeclaredConstructor(classes);
            return constructor.newInstance(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object callStaticMethod(Class clazz, String methodName, Class[] classes, Object... params) {
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof FixInterface) {
                params[i] = ((FixInterface) params[i]).getProxyObject();
            }
        }
        try {
            Method method = clazz.getMethod(methodName, classes);
            method.setAccessible(true);
            return method.invoke(null, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object callMethod(Object o, String methodName, Class[] classes, Object... params) {
        try {
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof FixInterface) {
                    params[i] = ((FixInterface) params[i]).getProxyObject();
                }
            }
            if (o instanceof FixInterface) {
                o = ((FixInterface) o).getProxyObject();
            }
            Method method = o.getClass().getMethod(methodName, classes);
            method.setAccessible(true);
            return method.invoke(o, params);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object callMethodByName(Object o, String methodName, Object... params) {
        try {
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof FixInterface) {
                    params[i] = ((FixInterface) params[i]).getProxyObject();
                }
            }
            Method method = null;
            for (Method m : o.getClass().getMethods()) {
                if (m.getName().equals(methodName)) method = m;
            }
            if (method != null) {
                method.setAccessible(true);
                return method.invoke(o, params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getStaticField(Class clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getField(Object o, Class clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void setStaticField(String className, String fieldName, Object value) {
        try {
            Class clazz = fixClass(className);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T getShell(Class<T> proxy, final Object object) {
        if (object != null) try {
            return (T) Proxy.newProxyInstance(proxy.getClassLoader(), new Class[]{proxy}, new FixHandler(object));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class fixClass(String className) {
        className = className.replace("org.bukkit.craftbukkit.", "org.bukkit.craftbukkit." + version);
        className = className.replace("net.minecraft.server.", "net.minecraft.server." + version);
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Class[] fixClass(String[] classNames) {
        Class[] classes = new Class[classNames.length];
        for (int i = 0; i < classes.length; i++) classes[i] = fixClass(classNames[i]);
        return classes;
    }
}