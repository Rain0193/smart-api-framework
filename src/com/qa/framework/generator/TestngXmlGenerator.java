package com.qa.framework.generator;


import com.library.common.IOHelper;
import com.library.common.XmlHelper;
import org.dom4j.Element;

import java.io.File;

/**
 * The type Testng xml generator.
 */
public class TestngXmlGenerator {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        for (String arg : args) {
            System.out.println("------->" + arg);
        }
        autoGenerateFactory(args[0], args[1]);
    }

    /**
     * Auto generate factory.
     *
     * @param outputPath the output path
     * @param threadCnt  the thread cnt
     */
    public static void autoGenerateFactory(String outputPath, String threadCnt) {
        XmlHelper xml = new XmlHelper();
        xml.createDocument();
        Element root = xml.createDocumentRoot("suite");
        xml.addAttribute(root, "name", "FactoryRun");
        xml.addAttribute(root, "thread-count", threadCnt);
        xml.addAttribute(root, "parallel", "methods");
        xml.addAttribute(root, "verbose", "1");
        Element listeners = xml.addChildElement(root, "listeners");
        Element listener = xml.addChildElement(listeners, "listener");
        xml.addAttribute(listener, "class-name", "com.qa.framework.testnglistener.RetryListener");
        Element test = xml.addChildElement(root, "test");
        xml.addAttribute(test, "name", "FactoryExecutor_execute");
        xml.addAttribute(test, "timeout", "600000");
        Element classes = xml.addChildElement(test, "classes");
        Element cls = xml.addChildElement(classes, "class");
        xml.addAttribute(cls, "name", "com.qa.framework.factory.ExecutorFactory");
        Element methods = xml.addChildElement(cls, "methods");
        Element include = xml.addChildElement(methods, "include");
        xml.addAttribute(include, "name", "execute");
        IOHelper.deleteDirectory(outputPath);
        IOHelper.createNestDirectory(outputPath);
        if (outputPath.endsWith("/")) {
            xml.saveTo(outputPath + "FactoryRun.xml");
        } else {
            xml.saveTo(outputPath + File.separator + "FactoryRun.xml");
        }
    }
}
