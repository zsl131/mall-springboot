package com.zslin.test;

import com.zslin.business.mini.tools.AccessTokenTools;
import com.zslin.business.mini.tools.MiniCommonTools;
import com.zslin.core.common.NormalTools;
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
