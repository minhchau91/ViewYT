package common.helpers;

import org.testng.xml.XmlTest;

public final class XMLHelper {

    private XMLHelper() {
    }

    public static String getPropertyValue(final String propertyName, final XmlTest testSuite) {
        String value = "";
        value = System.getProperty(propertyName);

        if (value == null || value.isEmpty()) {
            value = testSuite.getParameter(propertyName);
        }

        return value;
    }

    public static boolean getPropertyValueAsBoolean(final String propertyName, final XmlTest testSuite) {
        String strValue = getPropertyValue(propertyName, testSuite);
        return Boolean.parseBoolean(strValue);
    }

    public static int getPropertyValueAsNumber(final String propertyName, final XmlTest testSuite) {
        String strValue = getPropertyValue(propertyName, testSuite);
        return strValue == null ? 0 : Integer.parseInt(strValue);
    }
}