package com.zslin.code.tools;

import com.zslin.code.dto.EntityDto;
import com.zslin.core.common.NormalTools;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

/**
 * 生成Service
 */
public class CodeGenerateServiceTools {

    /**
     * 生成Service文件
     * @param path Java文件生成路径
     * @param pck package包名
     * @param ed EntityDto对象
     * @throws Exception
     */
    public static void generate(String path, String pck, EntityDto ed) throws Exception {
//        String sep = File.separator;
        String outPath = CodeGenerateCommon.getVelocityPath();
        Properties pro = new Properties();
        pro.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        pro.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        pro.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, outPath);
        VelocityEngine ve = new VelocityEngine(pro);

        String clsName = ed.getCls();

        VelocityContext context = new VelocityContext();
        context.put("pck",pck+".service");
        context.put("author", ed.getAuthor());

        context.put("clsName", clsName);
        context.put("date", NormalTools.curDate());
        context.put("serviceClsName", buildClsName(clsName));
        context.put("entityPck", pck+".model");
        context.put("clsDesc", ed.getDesc());
        context.put("pModuleName", ed.getPModuleName());
        context.put("url", ed.getUrl());
        context.put("daoClsName", "I"+clsName+"Dao");
        context.put("daoClsPck", pck+".dao");
        context.put("daoName", buildDaoName(clsName));

        Template t = ve.getTemplate("serviceTemplate.vm", "UTF-8");

        File file = new File(path+buildClsName(ed.getCls())+".java");

        FileOutputStream outStream = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(outStream,
                "UTF-8");
        BufferedWriter sw = new BufferedWriter(writer);
        t.merge(context, sw);
        sw.flush();
        sw.close();
        outStream.close();
    }

    /**
     * 生成类名
     * @param clsName 实体类类名
     * @return
     */
    private static String buildClsName(String clsName) {
        StringBuffer sb = new StringBuffer();
        sb.append(clsName).append("Service");
        return sb.toString();
    }

    private static String buildDaoName(String clsName) {
        String res = clsName.substring(0,1).toLowerCase()+clsName.substring(1) + "Dao";
        return res;
    }
}
