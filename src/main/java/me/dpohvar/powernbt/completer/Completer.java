package me.dpohvar.powernbt.completer;

import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.StringParser;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public abstract class Completer implements TabCompleter {
    public class TabFormer {
        private final String query;
        private final LinkedHashSet<String> tabs = new LinkedHashSet<String>();
        LinkedList<String> words = new LinkedList<String>();

        public boolean isQueryEmpty() {
            return query == null || query.isEmpty();
        }

        public String getQuery() {
            return query;
        }

        public String pollAllAsString() {
            String s = StringUtils.join(words, ' ');
            words.clear();
            return s;
        }

        public boolean isLastQuery() {
            return words.isEmpty() && (query == null || query.isEmpty());
        }

        public TabFormer(List<String> val) {
            words = new LinkedList<String>(val);
            if (words.size() == 0) query = "";
            else query = words.pollLast();
        }

        public String poll() {
            String s = words.poll();
            return s == null ? "" : s;
        }

        public int size() {
            return words.size();
        }

        public void add(String... strings) {
            for (String s : strings) if (s != null) tabs.add(s);
        }

        public void addIfStarts(String... strings) {
            for (String s : strings) if (s.toLowerCase().startsWith(query.toLowerCase())) tabs.add(s);
        }

        public void addIfStartsCase(String... strings) {
            for (String s : strings) if (s.startsWith(query)) tabs.add(s);
        }

        public void addIfHas(String... strings) {
            for (String s : strings) if (s.toLowerCase().contains(query.toLowerCase())) tabs.add(s);
        }

        public void addIfHasCase(String... strings) {
            for (String s : strings) if (s.contains(query)) tabs.add(s);
        }

        public List<String> getResult() {
            return new ArrayList<String>(tabs);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        Caller caller = plugin.getCaller(sender);
        TabFormer former = null;
        String line = StringUtils.join(args, ' ');
        try {
            LinkedList<String> words = new LinkedList<String>();
            for (String s : plugin.getTokenizer().tokenize(line).values()) {
                if (s.startsWith("\"") && s.endsWith("\"")) {
                    s = StringParser.parse(s.substring(1, s.length() - 1));
                }
                words.add(s);
            }
            if (line.endsWith(" ")) words.add("");
            former = new TabFormer(words);
            fillTabs(caller, former);
            return former.getResult();
        } catch (Throwable t) {
            if (former == null) return new ArrayList<String>();
            return former.getResult();
        }
    }

    protected abstract void fillTabs(Caller caller, TabFormer former) throws Exception;

    protected static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    protected static boolean matches(String command, String... t) {
        if (command == null) return false;
        for (String s : t) if (command.equals(s)) return true;
        return false;
    }

}
