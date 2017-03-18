package com.qa.framework.library.sms;

import com.qa.framework.bean.Param;
import com.qa.framework.config.PropConfig;
import com.qa.framework.library.httpclient.HttpMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Send message.
 */
public class sendMessage {
    /**
     * Send msg string.
     *
     * @param mobile  the mobile
     * @param message the message
     * @return the string
     * @throws IOException the io exception
     */
    public static String sendMsg(String mobile, String message) throws IOException {
        String url = "http://sdk2.entinfo.cn/webservice.asmx/SendSMS";

        List<Param> params = addParams(mobile, message);
        String result = HttpMethod.usePostMethod(url, null, params, false, false);
        if (result.contains("成功")) {
            return mobile + "发送短信成功";
        } else {
            return mobile + "发送短信失败，错误代码为:" + result;
        }
    }

    private static List<Param> addParams(String mobile, String message) {
        List<Param> params = new ArrayList<Param>();
        Param param1 = new Param();
        param1.setName("sn");
        param1.setValue(PropConfig.getSN());
        Param param2 = new Param();
        param2.setName("pwd");
        param2.setValue(PropConfig.getSNPWD());
        Param param3 = new Param();
        param3.setName("mobile");
        param3.setValue(mobile);
        Param param4 = new Param();
        param4.setName("content");
        param4.setValue(message);
        params.add(param1);
        params.add(param2);
        params.add(param3);
        params.add(param4);
        return params;
    }

    /**
     * Send msg string.
     *
     * @param mobiles the mobiles
     * @param message the message
     * @return the string
     * @throws IOException the io exception
     */
    public static String sendMsg(List<String> mobiles, String message) throws IOException {
        String afterSend = "短信结果：\n";
        for (String mobile : mobiles) {
            String reslut = sendMsg(mobile, message);
            if (reslut.contains("失败")) {
                afterSend = afterSend + reslut + "\n";
            }
        }
        return afterSend;
    }
}
