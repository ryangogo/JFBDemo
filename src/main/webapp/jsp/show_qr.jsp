<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018/2/25
  Time: 11:36
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String base = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    String basePath = base;
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<base href="<%=basePath%>">
<html>
<head>
    <title>支付宝沙箱支付Demo</title>
    <img src="${qrPath}" alt="用沙箱客户端扫描二维码完成支付"/></head></br>
    <span>订单号为："${outTradeNo}"</span>
    <h4>用支付宝沙箱扫描上图二维码完成测试</h4></br>
    <a href="jsp/trade_query.jsp">支付结果查询（需扫码后方可查询）</a>

</head>
<body>
</body>
</html>
