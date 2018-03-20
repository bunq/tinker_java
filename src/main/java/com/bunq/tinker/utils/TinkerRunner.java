package com.bunq.tinker.utils;

public class TinkerRunner {

    /**
     * Package constants.
     */
    public static final String PACKAGE_TINKER_PREFIX = "com.bunq.tinker.";

    /**
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if (args.length <= 0) return;

        String className = args[0];
        Class classObject = Class.forName(PACKAGE_TINKER_PREFIX + className);
        ITinker tinker = (ITinker) classObject.newInstance();

        tinker.run(args);
    }
}
