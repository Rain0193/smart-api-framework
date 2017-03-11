package com.qa.framework.factory;

import com.qa.framework.bean.TestData;
import com.qa.framework.core.TestXmlData;
import com.qa.framework.mock.IMockServer;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Factory;

import static com.qa.framework.classfinder.ClassHelper.findImplementClass;

public class ExecutorFactory {
    private IMockServer mockServer = null;

    @BeforeSuite(alwaysRun = true)
    public void beforeClass() throws IllegalAccessException, InstantiationException {
        Class<?> clazz = findImplementClass(IMockServer.class);
        if (clazz != null) {
            mockServer = (IMockServer) clazz.newInstance();
            mockServer.startServer();
            mockServer.settingRules();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterClass() {
        if (mockServer != null) {
            mockServer.stopServer();
        }
    }

    @BeforeMethod
    public void beforeMethod(ITestContext context) {
        System.out.println("beforeMethod");
    }


    /**
     * Execute object [ ].
     *
     * @param testData   the test data
     * @param url        the url
     * @param httpMethod the http method
     * @return the object [ ]
     */
    @Factory(dataProviderClass = TestXmlData.class, dataProvider = "xmlFactoryData")
    public Object[] execute(TestData testData, String url, String httpMethod) {
        Object[] tests = new Object[1];
        tests[0] = new Executor(testData, url, httpMethod);
        return tests;
    }

}
