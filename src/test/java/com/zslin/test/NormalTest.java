package com.zslin.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.app.tools.CouponTools;
import com.zslin.business.dao.ICouponDao;
import com.zslin.business.dao.IProductDao;
import com.zslin.business.dao.IProductFavoriteRecordDao;
import com.zslin.business.dao.IProductSpecsDao;
import com.zslin.business.mini.dto.NewCustomDto;
import com.zslin.business.mini.tools.AccessTokenTools;
import com.zslin.business.mini.tools.MiniCommonTools;
import com.zslin.business.mini.tools.MiniUtils;
import com.zslin.business.model.Coupon;
import com.zslin.business.model.Product;
import com.zslin.business.model.ProductFavoriteRecord;
import com.zslin.business.model.ProductSpecs;
import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.service.TestService;
import com.zslin.core.tasker.BeanCheckTools;
import com.zslin.core.tools.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.util.ClassUtils;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(value = "zsl")
public class NormalTest implements ApplicationContextAware {

    private RestTemplate template = new RestTemplate();

    @Autowired
    private AccessTokenTools accessTokenTools;

    @Autowired
    private MiniCommonTools miniCommonTools;

    @Autowired
    private BeanCheckTools beanCheckTools;

    @Autowired
    private IProductDao productDao;

    @Autowired
    private BuildAdminMenuTools buildAdminMenuTools;

    @Autowired
    private SortTools sortTools;

    @Autowired
    private TestService testService;

    @Autowired
    private BeanFactory factory;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Autowired
    private IProductFavoriteRecordDao productFavoriteRecordDao;

    @Autowired
    private IProductSpecsDao productSpecsDao;

    @Autowired
    private ICouponDao couponDao;

    @Test
    public void test21() {
        String str = NormalTools.curDatetime();
//        String str2 = CouponTools.buildEndTime(86400);
        System.out.println("str1: "+str);
        System.out.println("str2: "+str);
    }

    @Test
    public void test20() {
        Coupon c = couponDao.findByRuleSn("TEST");
        Coupon c1 = couponDao.findByRuleSn("BUY_PRODUCT");
        System.out.println("test:::"+c);
        System.out.println("product:::"+c1);
        System.out.println("----------->");
    }

    @Test
    public void test19() {
        List<Product> list = productDao.searchByTitle("苹果");
        System.out.println("---------->size::"+list.size());
        for(Product p : list) {
            System.out.println(p);
        }
    }

    @Test
    public void test18() {
        Product p = productDao.findOne(1);
        for(int i=0;i<22;i++) {
            Product np = new Product();
            MyBeanUtils.copyProperties(p, np, "id");
            np.setTitle(p.getTitle()+"_"+(i+1));
            np.setReadCount(0);
            np.setFavoriteCount(0);
            productDao.save(np);
            for(int j=1;j<=3;j++) {
                ProductSpecs ps = new ProductSpecs();
                ps.setCateId(np.getCateId());
                ps.setCateName(np.getCateName());
                ps.setName("果号—"+i+"-"+j);
                ps.setOrderNo(j);
                ps.setOriPrice((i+1)*j*1.5f);
                ps.setPrice((i+1)*j*1.0f);
                ps.setProId(np.getId());
                ps.setProTitle(np.getTitle());
                ps.setRemark("果号—"+i+"-"+j+" 这里是描述");
                productSpecsDao.save(ps);
            }
        }
    }

    @Test
    public void test17() {
        ProductFavoriteRecord pfr = productFavoriteRecordDao.findOne(9);
        System.out.println(pfr);
        for(int i=0;i<33;i++) {
            ProductFavoriteRecord p = new ProductFavoriteRecord();
            MyBeanUtils.copyProperties(pfr, p, "id");
            p.setProTitle(pfr.getProTitle()+"_"+i);
            productFavoriteRecordDao.save(p);
        }
    }

    @Test
    public void test16() throws Exception {
        String clsName = "webInterceptorService", methodName = "loadWebBase";
//        String clsName = "adminUserService", methodName = "login";
        Object obj = getApplicationContext().getBean(clsName);
        Method method = obj.getClass().getDeclaredMethod(methodName,"params".getClass());

        Class<?> userClass = ClassUtils.getUserClass(obj);
        //method代表接口中的方法，specificMethod代表实现类中的方法
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, userClass);
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        Annotation [] annotations = specificMethod.getAnnotations();
        System.out.println("==========>size:: "+annotations.length);
        for(Annotation an : annotations) {
            System.out.println("---------------------------============="+an.annotationType().getName());
        }
//        TransactionAttribute txAtt = findTransactionAttribute(specificMethod);
    }

    @Test
    public void test15() throws Exception {
//        String clsName = "webInterceptorService", methodName = "loadWebBase";
        String clsName = "adminUserService", methodName = "login";
//        Object obj = factory.getBean("adminUserService");
//        Object obj = getApplicationContext().getBean("adminUserService");
        Object obj = getApplicationContext().getBean(clsName);
//        Class obj = Class.forName("com.zslin.core.service.AdminUserService");
//        System.out.println(obj.getClass().getName()+"========");
//        Method method = obj.getClass().getMethod("login", "params".getClass());
        Method method = obj.getClass().getDeclaredMethod(methodName,"params".getClass());

        Annotation[] annotations = method.getAnnotations();
        System.out.println(method.getName()+"------->annSize: "+ annotations.length);
        for(Annotation an : annotations) {
            System.out.println("---------------------------============="+an.annotationType().getName());
        }
        Annotation[] annos = method.getDeclaredAnnotations();
        System.out.println("------>decSize: "+annos.length);
        for (Annotation an : annos) {
            System.out.println(an.annotationType().getName());
        }

        Annotation ann = method.getAnnotation(NeedAuth.class);
        System.out.println("ann======>"+ann);
    }

    @Test
    public void test14() {
        JsonResult jr = testService.add("");
        System.out.println(jr);
    }

    @Test
    public void test13() {
        String str = "{\"address\":\"嘎斯地方阿斯蒂芬\",\"provinceCode\":\"110000\",\"cityCode\":\"110100\",\"sex\":\"1\",\"papers\":[{\"name\":\"身份证正面\",\"id\":80,\"url\":\"https://zz-specialty.zslin.com/agent_e7b96a3f-fc87-4b30-8b2f-8f09c3e538d1.png\"},\n" +
                "{\"name\":\"身份证背面\",\"id\":81,\"url\":\"https://zz-specialty.zslin.com/agent_6575dc4b-de2b-4d60-a23f-a413144e5a15.png\"}],\"countyCode\":\"110102\",\"headerParams\":{\"unionid\":\"okOD4jgutW_OHQBkIJYD8NL4NhEU\",\"apicode\":\"min\n" +
                "iAgentService.addAgent\",\"openid\":\"oHoS55Tke2HI5m62XKVXRwRm_HAk\",\"nickname\":\"想攀登的胖子\",\"authtoken\":\"test-token\",\"customid\":\"2\"},\"cityName\":\"市辖区\",\"phone\":\"15925061256\",\"identity\":\"532127198803011115\",\"name\n" +
                "\":\"颖三要\",\"hasExperience\":\"1\",\"provinceName\":\"北京市\",\"countyName\":\"西城区\"}";
        JSONArray jsonArray = JsonTools.str2JsonArray(JsonTools.getJsonParam(str, "papers"));
        for(int i=0;i<jsonArray.size();i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            System.out.println("----->" + jsonObj.toJSONString());
        }

/*        ----->{"name":"身份证正面","id":80,"url":"https://zz-specialty.zslin.com/agent_e7b96a3f-fc87-4b30-8b2f-8f09c3e538d1.png"}
        ----->{"name":"身份证背面","id":81,"url":"https://zz-specialty.zslin.com/agent_6575dc4b-de2b-4d60-a23f-a413144e5a15.png"}*/

    }

    @Test
    public void test12() {
        String str = "[{\"orderNo\":4,\"name\":\"移动端管理\",\"id\":158},{\"orderNo\":2,\n" +
                "\"name\":\"七牛管理\",\"id\":271},{\"orderNo\":3,\"name\":\"产品管理\",\"id\":235}]";
        sortTools.handler("AdminMenu", str);
    }

    @Test
    public void test11() {
        buildAdminMenuTools.buildAdminMenusOrderNo();
    }

    @Test
    public void test10() {
        productDao.plusSpecsCount(5, 1);
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
