package com.myDemo.controller;

import com.google.common.collect.Maps;
import com.myDemo.service.TestJFBPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by JC on 2018/2/24.
 */
@Controller
@RequestMapping("/test/")
public class TestJFBPayController {

    private static  final Logger logger = LoggerFactory.getLogger(TestJFBPayController.class);

    @Autowired
    public TestJFBPayService testJFBPayServiceImpl;

    /**
     * 测试当面付2.0生成支付二维码
     * @param payNum
     */
    @RequestMapping("pay.do")
    public String pay(double payNum, HttpServletRequest request,Model model) {
        Map returnMap = testJFBPayServiceImpl.payService(payNum,request);
        if((boolean)returnMap.get("status")){
            String qrPath = (String)returnMap.get("qrPath");

            String realPath = qrPath.split("upload")[1];//虚拟图片目录
            String outTradeNo = (String)returnMap.get("outTradeNo");//商户订单号，需要保证不重复

            model.addAttribute("qrPath","pic"  + realPath);
            model.addAttribute("outTradeNo",outTradeNo);
            return "show_qr.jsp";
        }else{
            model.addAttribute("outTradeNo","预失败");
            return "show_qr.jsp";
        }
    }

    /**
     * 支付宝支付后的回调接口在开发调试阶段需要用natapp将接口进行穿透使支付宝能访问这个接口
     * 该接口用于验签
     * @param request
     * @return
     */
    @RequestMapping("callback.do")
    @ResponseBody
    public void callback(HttpServletRequest request){
        Map<String,String> params = Maps.newHashMap();
        Map requestParams = request.getParameterMap();
        for(Iterator iter = requestParams.keySet().iterator(); iter.hasNext();){
            String name = (String)iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for(int i = 0 ; i <values.length;i++){
                valueStr = (i == values.length -1)?valueStr + values[i]:valueStr + values[i]+",";
            }
            params.put(name,valueStr);
        }

        //非常重要,验证回调的正确性,是不是支付宝发的.并且呢还要避免重复通知.
        String callBackInfo = testJFBPayServiceImpl.callBackService(request,params);
        logger.info(callBackInfo);
        // TODO: 2018/3/24  业务代码

    }

    /**
     * 根据订单号（out_trade_no）查询订单信息
     *
     * @param outTradeNo
     * @return
     */
    @RequestMapping("tradeQuery.do")
    public String tradeQuery(String outTradeNo,Model model){
        Map returnMap = testJFBPayServiceImpl.tradeQueryService(outTradeNo);
        model.addAttribute("fs",returnMap.get("msg"));
        return "result.jsp";
    }

}
