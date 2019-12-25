package com.zslin.test;

import com.zslin.business.dao.IProductDao;
import com.zslin.business.mini.dto.NewCustomDto;
import com.zslin.business.mini.tools.AccessTokenTools;
import com.zslin.business.mini.tools.MiniCommonTools;
import com.zslin.business.mini.tools.MiniUtils;
import com.zslin.core.common.NormalTools;
import com.zslin.core.tasker.BeanCheckTools;
import com.zslin.core.tools.Base64Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLDecoder;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(value = "zsl")
public class NormalTest {

    private RestTemplate template = new RestTemplate();

    @Autowired
    private AccessTokenTools accessTokenTools;

    @Autowired
    private MiniCommonTools miniCommonTools;

    @Autowired
    private BeanCheckTools beanCheckTools;

    @Autowired
    private IProductDao productDao;

    @Test
    public void test10() {
        productDao.updateSpecsCount(5, 1);
    }

    @Test
    public void test09() {
        beanCheckTools.checkMethod("testService", "handler", "zslzsl");
        System.out.println("++++++++++++++++++++++++++++++");
        beanCheckTools.checkMethod("testService", "handler", "sdfsf", 5);
        System.out.println("=========================================");
        beanCheckTools.checkMethod("testService", "handler");
        System.out.println("1----------------------------------------------1");
        beanCheckTools.checkMethod("testService", "handler", null);
        System.out.println("2----------------------------------------------2");
        beanCheckTools.checkMethod("testService", "test", null);
        System.out.println("3----------------------------------------------3");
        beanCheckTools.checkMethod("zslzsService", "handler", null);
    }

    @Test
    public void test08() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        System.out.println(NormalTools.getDate("2019-12-18 18:18:18", pattern));
    }

    @Test
    public void test07() {
        String enc = "HSu0ylkXSAPqgi8eZxSFdjlNUTvmU5d3zzj7Egd fDc2wty0vtiE2s3qyB/jN34GdOFgI3n1d7OLCYo1t/TGoM 7wWXGM1S qVfI9KYjqDuXhhTMBeJ2Ffl1Ahhw8 bfCnG9O IbutFXXYvceWnfDZH/RFIKHxfA katYBNH7oxLJx5/Z4E7nA3OuEpXVbLjJvGlSsuOPX16sT5q/73xmoR 53/tkhK06VWBlh9PyxOPTtYxw1aBWbU6qBPqWkNVQr1GVTa/yPTq8wkJW4eju2iRX4Iq9ZZauFzkebeElLWlo5dU J0r5aQpLBbBIT3MfttpGW iFQUW KCbE1Z3O2Vkm1QMjn1qAJPaH QUCXbH4NaFecMpW0veCTM4U25H1DlpnQcDG5/1qiUhk3dGEpJdJ2tH63xFkHze9 Hm0DwivYys3L41S/msndCTToxDkFQh03AQN5nO qABGLEm9ucr/q/2dxgMda6vq6r6th M7l2oFgk05GOhzjFeDxKKw2lCIUbFePAX C9XA26dmLW n8sGCzRbZDmPRSKBd1M=";
        enc = enc.replaceAll(" ", "+");
//               enc = "HSu0ylkXSAPqgi8eZxSFdjlNUTvmU5d3zzj7Egd+fDc2wty0vtiE2s3qyB/jN34GdOFgI3n1d7OLCYo1t/TGoM+7wWXGM1S+qVfI9KYjqDuXhhTMBeJ2Ffl1Ahhw8+bfCnG9O+IbutFXXYvceWnfDZH/RFIKHxfA+katYBNH7oxLJx5/Z4E7nA3OuEpXVbLjJvGlSsuOPX16sT5q/73xmoR+53/tkhK06VWBlh9PyxOPTtYxw1aBWbU6qBPqWkNVQr1GVTa/yPTq8wkJW4eju2iRX4Iq9ZZauFzkebeElLWlo5dU+J0r5aQpLBbBIT3MfttpGW+iFQUW+KCbE1Z3O2Vkm1QMjn1qAJPaH+QUCXbH4NaFecMpW0veCTM4U25H1DlpnQcDG5/1qiUhk3dGEpJdJ2tH63xFkHze9+Hm0DwivYys3L41S/msndCTToxDkFQh03AQN5nO+qABGLEm9ucr/q/2dxgMda6vq6r6th+M7l2oFgk05GOhzjFeDxKKw2lCIUbFePAX+C9XA26dmLW+n8sGCzRbZDmPRSKBd1M=";
        String iv = "R5W2FVYMEDD29BHk0aDFlg==";
        String sessionKey = "QbnmzydXklCXXNcIrhqw6A==";
        NewCustomDto res = MiniUtils.decryptionUserInfo(enc, sessionKey, iv);
        System.out.println("=========>"+res);
    }

    @Test
    public void test06() throws Exception {
        BufferedInputStream bis = miniCommonTools.getUnlimited("id=123&p=aaa", true, false);
        OutputStream os = new FileOutputStream(new File("D:/temp/1.png"));
        int len;
        byte[] arr = new byte[1024];
        while ((len = bis.read(arr)) != -1)
        {
            os.write(arr, 0, len);
            os.flush();
        }
        os.close();
    }

    @Test
    public void test05() {
        String token = accessTokenTools.getAccessToken();
        System.out.println("------>"+token);
    }

    @Test
    public void test04() {
        System.out.println(NormalTools.curDate());
        System.out.println(NormalTools.curDatetime());
        System.out.println(NormalTools.getNow("yyyy-MM-dd HH:mm"));
        System.out.println(NormalTools.getNow("yyyy-MM-dd HH:mm:ss.d"));
    }

    @Test
    public void test03() {
        String code = "asdfsdf";
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=wxdc7b00047690374f&secret=bb3303d93f2837d553da56836cf24c68&js_code="+code+"&grant_type=authorization_code";
        String res = template.getForObject(url, String.class);
        System.out.println(res);
    }

    @Test
    public void test01() {
        System.out.println("-------------->");
    }

    @Test
    public void test02() throws Exception {
        String str = "JUU2JTgzJUIzJUU2JTk0JTgwJUU3JTk5JUJCJUU3JTlBJTg0JUU4JTgzJTk2JUU1JUFEJTkw";

        String real = Base64Utils.getFromBase64(str);
        System.out.println(real);

        real = URLDecoder.decode(real, "utf-8");

        System.out.println(real);
    }
}
