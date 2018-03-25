<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018/3/1
  Time: 15:02
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String base = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    String basePath = base;
%>
<html>
<head>
    <title>查询订单信息</title>
</head>
<body>
<table>
    <tr>
        <form action="/test/tradeQuery.do" method="get">
            <tr><td><input type="text" value="" name="outTradeNo"><span style="color: red;">数字</span></td></tr>
            <tr><td><input type="submit" name="test" value="test"></td></tr>
        </form>
    </tr>
</table>


</body>
</html>
