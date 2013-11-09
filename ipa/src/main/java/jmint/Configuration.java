package jmint;

/**
 * Created with IntelliJ IDEA.
 * User: gdevanla
 * Date: 10/4/13
 * Time: 2:10 PM
 * To change this template use File | Settings | File Templates.
 */
public final class Configuration {

    static String sourceFolder = "";
    static String[] packageUnderTest = {
            "MutantInjectionArtifacts",
            "org.apache.tools.ant",
            //"org.apache.bcel",
            //"org.jgrapht",
            //"org.jfree",
            //"com.google.test",
            "TestArtifacts"};

    static String resultRootFolder = "/tmp/jMint/testartifacts";

}
