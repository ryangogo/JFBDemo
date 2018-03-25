package com.myDemo.service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by JC on 2018/2/25.
 */
public interface TestJFBPayService {

     Map<String,Object> payService(double payNum, HttpServletRequest request);

     Map<String,Object> tradeQueryService(String outTradeNo);

     String callBackService(HttpServletRequest request,Map<String,String> params);
}
