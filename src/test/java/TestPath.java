import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2018/2/25.
 */
public class TestPath {

    public static void main (String args[]){
        TestPath testPath = new TestPath();
        testPath.path();
    }

    public void path(){
        String a = "E:\\当面付JavaDemo\\JFBDemo\\target\\JFBDemo\\src\\main\\webapp\\upload\\qr-tradeprecreate15195397247888579887.png";

        String b = "upload" +  a.split("upload")[1];
        System.out.println(b);

    }
}
