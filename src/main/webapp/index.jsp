<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8"  %>
<html>
<body>
<h2>输入支付金额</h2>
<table>
    <tr>
        <form action="/test/pay.do" method="get">
            <tr><td><input type="text" value="" name="payNum"><span style="color: red;">交易金额</span></td></tr>
            <tr><td><input type="submit" name="test" value="test"></td></tr>
        </form>
    </tr>
</table>
</body>
</html>
