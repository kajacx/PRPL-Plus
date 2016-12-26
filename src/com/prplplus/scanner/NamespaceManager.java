package com.prplplus.scanner;

import java.util.HashSet;

public class NamespaceManager {

    //without underscore
    private HashSet<String> usedPrefixes = new HashSet<>();

    public static final String PRPL_PREFIX = "prpl_plus__";

    {
        usedPrefixes.add(PRPL_PREFIX.substring(0, PRPL_PREFIX.length() - 2));
    }

    //returns unique prefix with underscore
    public String getPrefixFor(String name) {
        String prefix = name; //TODO: better prefix extraction

        if (!usedPrefixes.contains(prefix)) {
            usedPrefixes.add(prefix);
            return prefix + "__";
        }

        int i = 0;
        do {
            i++;
        } while (usedPrefixes.contains(prefix + i));

        usedPrefixes.add(prefix + i);

        return prefix + i + "__";
    }
}
