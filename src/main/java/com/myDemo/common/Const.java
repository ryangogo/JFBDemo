package com.myDemo.common;

/**
 * Created by JC on 2018/2/25.
 */
public class Const {

    public final static String SUBJECT = "xxx品牌xxx门店当面付扫码消费";

    public final static String UNDISCOUNTABLEAMOUNT = "0";

    public final static String BODY = "购买商品3件共20.00元";

    public final static String OPERATOR_ID = "test_operator_id";

    public final static String STORE_ID = "test_store_id";

    //map中返回值的两种状态，预下单成功返回success，失败则返回false
    public enum ReturnStatus{

        SUCCESS(true),
        FAIL(false);

        ReturnStatus(boolean status){
            this.status = status;
        }

        private boolean status;

        public boolean getStatus() {
            return status;
        }


    }

}
