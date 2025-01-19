package io.github.willena.maven.plugins.githooks;

public class DemoMain {

    public static String[] receivedArgs;

    public static void main(String[] args) {
        System.out.println("Hello args = " + args);
        DemoMain.receivedArgs = args;
    }
}
