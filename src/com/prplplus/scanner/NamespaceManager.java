package com.prplplus.scanner;

import java.util.HashSet;

import com.prplplus.Settings;

public class NamespaceManager {

    //without underscore
    private HashSet<String> usedPrefixes = new HashSet<>();

    public static final String PRPL_PREFIX = "prpl_plus" + Settings.PREFIX_DELIM;

    {
        usedPrefixes.add(PRPL_PREFIX.substring(0, PRPL_PREFIX.length() - Settings.PREFIX_DELIM.length()));
    }

    //returns unique prefix with underscore
    public String getPrefixForScript(String name) {
        return getPrefix("s");
    }

    public String getPrefixForMain(String name) {
        return getPrefix("m");
    }

    public String getPrefixForFunc(String name) {
        return getPrefix("f");
    }

    private String getPrefix(String prefix) {
        if (!usedPrefixes.contains(prefix)) {
            usedPrefixes.add(prefix);
            return prefix + Settings.PREFIX_DELIM;
        }

        int i = 0;
        do {
            i++;
        } while (usedPrefixes.contains(prefix + i));

        usedPrefixes.add(prefix + i);

        return prefix + i + Settings.PREFIX_DELIM;
    }
}
