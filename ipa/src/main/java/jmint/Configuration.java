package jmint;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public final class Configuration {

    public final static String propertiesFileName = "jmint.properties";
    protected PropertiesConfiguration config;
    public static String[] packageUnderTest;
    static String resultRootFolder = "/tmp/jMint/testartifacts";

   static {
        try {
            PropertiesConfiguration config = new PropertiesConfiguration(propertiesFileName);
            packageUnderTest = config.getStringArray("jmint.package_names");
            resultRootFolder = config.getString("jmint.results_location");

        } catch (ConfigurationException e) {
            System.out.println("Unable to load config file jmint.properties. Please make sure it is in the classpath.");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

        }
    }

   /* static String[] packageUnderTest = {
            "MutantInjectionArtifacts",
            //"org.apache.tools.ant",
            //"org.apache.bcel",
            //"org.jgrapht",
            //"org.jfree",
            //"com.google.test",
            //"com.mongodb",
            "org.scribe",
            //"com.twilio",
            //"org.annolab.",
            //com.github",
            //"com.sampullara",
            //"com.twitter",
            //"mustachejava",
           //"org.yaml",
            //"org.jconsole",
            //"jonelo.jacksum",
            "TestArtifacts"};*/



    public static String[] getPackageUnderTest() {
        return packageUnderTest;
    }
}
