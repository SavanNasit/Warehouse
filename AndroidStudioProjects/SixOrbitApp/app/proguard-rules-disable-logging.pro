##
## Disable logging
##

# Disable Android logging
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# This gets rid of System.out.println() and System.out.print()
# WARNING: if you're using this functions for other PrintStreams in your app, this can break things!
-assumenosideeffects class java.io.PrintStream {
    public void println(...);
    public void print(...);
}

