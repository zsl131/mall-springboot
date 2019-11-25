package com.zslin.test;

import com.zslin.code.dto.EntityDto;
import com.zslin.code.tools.CodeGenerateCommon;
import com.zslin.code.tools.CodeGenerateTools;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.List;
import java.util.Properties;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(value = "zsl")
public class GenerateTest {

    @Test
    public void test03() throws Exception {
        String outPath = "D:\\temp\\velocity\\";
        String tmpFile = "TemplateEntity.java";
        String targetFile = "ResultEntity.java";

        Properties pro = new Properties();
        pro.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        pro.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        pro.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, outPath);
        VelocityEngine ve = new VelocityEngine(pro);


        VelocityContext context = new VelocityContext();
        context.put("pck","com.zslin.test.export1");
        context.put("name", "产品信息测试测试");
        context.put("author", "钟述林");
        context.put("tableName", "t_test_table");
        context.put("clsName", "TestTableEntity");
        context.put("fields", buildFields());
//        context.put("packageName",packageName);

        Template t = ve.getTemplate(tmpFile, "UTF-8");

        File file = new File(outPath, targetFile);
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if (!file.exists())
            file.createNewFile();

        FileOutputStream outStream = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(outStream,
                "UTF-8");
        BufferedWriter sw = new BufferedWriter(writer);
        t.merge(context, sw);
        sw.flush();
        sw.close();
        outStream.close();
        System.out.println("成功生成Java文件:"
                + (outPath + targetFile).replaceAll("/", "\\\\"));
    }

    private String buildFields() {
        StringBuffer sb = new StringBuffer();
        sb.append(CodeGenerateCommon.getLine())
            .append(CodeGenerateCommon.getTab()).append("/**").append(CodeGenerateCommon.getLine())
            .append(CodeGenerateCommon.getTab()).append("* ").append("菜单名称").append(CodeGenerateCommon.getLine())
            .append(CodeGenerateCommon.getTab()).append("* @remark ").append("中文显示名称").append(CodeGenerateCommon.getLine())
            .append(CodeGenerateCommon.getTab()).append("*/").append(CodeGenerateCommon.getLine());
        sb.append(CodeGenerateCommon.getTab()).append("private String name;");
        return sb.toString();
    }

    @Test
    public void test02() {
        //E:\idea\2020\z_mall\src\main\java\com\zslin\business\model\ProductSpecs.java
        //E:\idea\2020\z_mall\src\main\java\com\zslin\business\
        String pck1 = "business";
        String basePck = "com.zslin";
        System.out.println(CodeGenerateTools.buildPackage(basePck,pck1));
        String pck2 = "app.business";
        System.out.println(CodeGenerateTools.buildPackage(basePck,pck2));
        System.out.println(CodeGenerateTools.buildPck(basePck, pck2, "controller"));
    }

    @Test
    public void test01() throws Exception {
        //E:\idea\2020\z_mall\src\main\java\com\zslin\business\model\ProductSpecs.java
//        String projectPath = System.getProperty("user.dir");
//        System.out.println("projectPath==" + projectPath);
        FileInputStream fis = new FileInputStream(new File("G:\\钟述林\\X项目\\T特产\\model.xlsx"));
        List<EntityDto> res = CodeGenerateTools.generate("com.zslin", fis, 0, 2);
        System.out.println(res);
    }
}
