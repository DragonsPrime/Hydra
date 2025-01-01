package com.pinecone.framework.util;


import com.pinecone.framework.system.Nullable;

public abstract class PatternMatchUtils {
    public PatternMatchUtils() {

    }

    public static boolean simpleMatch(@Nullable String pattern, @Nullable String str) {
        if (pattern != null && str != null) {
            int firstIndex = pattern.indexOf(42);
            if (firstIndex == -1) {
                return pattern.equals(str);
            }
            else if (firstIndex == 0) {
                if (pattern.length() == 1) {
                    return true;
                }
                else {
                    int nextIndex = pattern.indexOf(42, 1);
                    if (nextIndex == -1) {
                        return str.endsWith(pattern.substring(1));
                    }
                    else {
                        String part = pattern.substring(1, nextIndex);
                        if (part.isEmpty()) {
                            return simpleMatch(pattern.substring(nextIndex), str);
                        }
                        else {
                            for( int partIndex = str.indexOf(part); partIndex != -1; partIndex = str.indexOf(part, partIndex + 1) ) {
                                if (simpleMatch(pattern.substring(nextIndex), str.substring(partIndex + part.length()))) {
                                    return true;
                                }
                            }

                            return false;
                        }
                    }
                }
            }
            else {
                return str.length() >= firstIndex && pattern.substring(0, firstIndex).equals(str.substring(0, firstIndex)) && simpleMatch(pattern.substring(firstIndex), str.substring(firstIndex));
            }
        }
        else {
            return false;
        }
    }

    public static boolean simpleMatch(@Nullable String[] patterns, String str) {
        if ( patterns != null ) {
            for( int i = 0; i < patterns.length; ++i ) {
                String pattern = patterns[i];
                if ( simpleMatch(pattern, str) ) {
                    return true;
                }
            }
        }

        return false;
    }
}

