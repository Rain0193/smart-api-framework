package com.qa.framework.generator;


import com.qa.framework.library.base.IOHelper;
import com.qa.framework.library.base.StringHelper;
import com.qa.framework.library.base.XMLHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.dom4j.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestngXmlGenerator {
    /**
     * @param args the input arguments
     */
    public static void main(String[] args) {
        for (String arg : args) {
            System.out.println("------->" + arg);
        }
        autoGenerateFactory(args[0], args[1]);
    }

    public static void autoGenerateFactory(String outputPath, String threadCnt) {
        XMLHelper xml = new XMLHelper();
        xml.createDocument();
        Element root = xml.createDocumentRoot("suite");
        xml.addAttribute(root, "name", "xml_" + threadCnt);
        xml.addAttribute(root, "thread-count", threadCnt);
        xml.addAttribute(root, "parallel", "tests");
        xml.addAttribute(root, "verbose", "1");
        Element listeners = xml.addChildElement(root, "listeners");
        Element listener = xml.addChildElement(listeners, "listener");
        xml.addAttribute(listener, "class-name", "com.qa.framework.testngListener.RetryListener");
        Element test = xml.addChildElement(root, "test");
        xml.addAttribute(test, "name", "FactoryExecutor_execute");
        xml.addAttribute(test, "timeout", "600000");
        Element classes = xml.addChildElement(test, "classes");
        Element cls = xml.addChildElement(classes, "class");
        xml.addAttribute(cls, "name","com.qa.framework.factory.FactoryExecutor");
        Element methods = xml.addChildElement(cls, "methods");
        Element include = xml.addChildElement(methods, "include");
        xml.addAttribute(include, "name", "execute");
        IOHelper.deleteDirectory(outputPath);
        IOHelper.createNestDirectory(outputPath);
        if (outputPath.endsWith("/")) {
            xml.saveTo(outputPath + "xml_" + "Factory.xml");
        } else {
            xml.saveTo(outputPath + File.separator + "xml_" + "Factory.xml");
        }
    }
}