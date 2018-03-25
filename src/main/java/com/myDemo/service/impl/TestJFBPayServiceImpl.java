package com.myDemo.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeQueryRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.Utils;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Maps;
import com.myDemo.common.Const;
import com.myDemo.service.TestJFBPayService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * Created by JC on 2018/2/25.
 */
@Service("testJFBPayServiceImpl")
public class TestJFBPayServiceImpl implements TestJFBPayService {

    private static Log log = LogFactory.getLog(TestJFBPayServiceImpl.class);

    // 支付宝当面付2.0服务
    private static AlipayTradeService tradeService;

    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }


    @Override
    public Map<String,Object> payService(double payNum, HttpServletRequest request){

        Map<String,Object> returnMap = new HashMap<String,Object>();

        //二维码生成的路径
        String qrPath = null;

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = "tradeprecreate" + System.currentTimeMillis()
                + (long) (Math.random() * 10000000L);

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = Const.SUBJECT;

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = String.valueOf(payNum);

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = Const.UNDISCOUNTABLEAMOUNT;

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = Const.BODY;

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = Const.OPERATOR_ID;

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = Const.STORE_ID;

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx小面包", 1000, 1);
        // 创建好一个商品后添加至商品明细列表
        goodsDetailList.add(goods1);

        // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
        goodsDetailList.add(goods2);

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl("http://ryangogo.s1.natapp.cc/test/callback.do")
                //支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                //沙箱里也需要配置
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                // 需要修改为运行机器上的路径



                String path = "E:\\当面付JavaDemo\\JFBDemo\\src\\main\\webapp\\upload";
                //请将path配置为虚拟图片目录(敲黑板)，否则在jsp中你将看不到二维码，具体方法参照
                //http://blog.csdn.net/h3243212/article/details/50819218#%E5%B7%A5%E7%A8%8B%E9%85%8D%E7%BD%AE%E5%92%8C%E7%8E%AF%E5%A2%83

                File f = new File(TestJFBPayServiceImpl.class.getResource("/").getPath());
                String haha = f.getParentFile().getParent();
                System.out.println(haha);

                //判断有没有文件夹，没有就生成一个
                File floder = new File(path);
                if(!floder.exists()){
                    floder.setWritable(true);
                    floder.mkdirs();
                }

                qrPath = String.format(path  + "\\qr-%s.png",response.getOutTradeNo());

                //生成二维码到指定路径
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                log.info(qrPath);
                break;

            case FAILED:
                log.error("支付宝预下单失败!!!");
                returnMap.put("status",Const.ReturnStatus.FAIL.getStatus());
                return returnMap;
            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                returnMap.put("status",Const.ReturnStatus.FAIL.getStatus());
                return returnMap;
            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                returnMap.put("status",Const.ReturnStatus.FAIL.getStatus());
                return returnMap;
        }
        returnMap.put("status",Const.ReturnStatus.SUCCESS.getStatus());
        returnMap.put("outTradeNo",outTradeNo);
        returnMap.put("qrPath",qrPath);
        return returnMap;
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }


    @Override
    public Map<String,Object> tradeQueryService(String outTradeNo){

        Map<String,Object> returnMap = new HashMap<String,Object>();

        // 创建查询请求builder，设置请求参数
        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder()
                .setOutTradeNo(outTradeNo);

        AlipayF2FQueryResult result = tradeService.queryTradeResult(builder);
        AlipayTradeQueryResponse response = result.getResponse();

        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("查询返回该订单支付成功: )");

                dumpResponse(response);

                log.info(response.getTradeStatus());
                if (Utils.isListNotEmpty(response.getFundBillList())) {
                    for (TradeFundBill bill : response.getFundBillList()) {
                        log.info(bill.getFundChannel() + ":" + bill.getAmount());
                    }
                }
                //如果交易成功，成功信息会在“msg”当中
                returnMap.put("msg",response.getMsg());
                return returnMap;

            case FAILED:
                //log.error("查询返回该订单支付失败或被关闭!!!");
                log.error(response.getSubMsg());
                //如果交易失败，失败信息会在“subMsg”当中
                returnMap.put("msg",response.getSubMsg());
                return returnMap;

            case UNKNOWN:
                //log.error("系统异常，订单支付状态未知!!!");
                log.error(response.getSubMsg());
                returnMap.put("msg",response.getSubMsg());
                return returnMap;

            default:
                //log.error("不支持的交易状态，交易返回异常!!!");
                log.error(response.getSubMsg());
                returnMap.put("msg",response.getSubMsg());
                return returnMap;
        }
    }

    @Override
    public String callBackService(HttpServletRequest request,Map<String,String> params){
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if(!alipayRSACheckedV2){
                return "没有通过验证";
            }
        } catch (AlipayApiException e) {
            return "支付宝验证回调异常";
        }
        return "通过验证";
    }

}



/*
view-source:http://localhost:8080/test/target/JFBDemo/upload/qr-tradeprecreate15195370638694083052.png

        file//E:/JavaDemo/JFBDemo/target/JFBDemo/upload/qr-tradeprecreate15195370638694083052.png*/
