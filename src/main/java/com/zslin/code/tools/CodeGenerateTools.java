package com.zslin.code.tools;

import com.zslin.code.dto.EntityDto;
import com.zslin.code.dto.FieldDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 代码生成工具类
 */
public class CodeGenerateTools {

    public static void generate(String basePck, InputStream is) {
        generate(basePck, is, 0, 0);
    }

    /**
     * 生成代码
     * @param is Excel文件输入流
     * @param sheetIndex Excel的工作薄序号，从0开始
     * @param startRow 数据开始行，默认从1开始
     */
    public static List<EntityDto> generate(String basePck, InputStream is, Integer sheetIndex, Integer startRow) {
//        String basePackage =

        List<EntityDto> result = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(sheetIndex==null?0:sheetIndex);

            EntityDto eDto=null;
            List<FieldDto> fieldList = null;
            for(int i=startRow;i<=sheet.getLastRowNum();i++) {
                Row row = sheet.getRow(i);
                Cell c1 = row.getCell(0);
                Cell c2 = row.getCell(1);
                Cell c3 = row.getCell(2);
                Cell c4 = row.getCell(3);
                Cell c5 = row.getCell(4);
                boolean isEntity = isTarget(c1, "类");
                if(isEntity) { //如果是对象
                    if(eDto!=null) { //添加
                        eDto.setFields(fieldList);
                        result.add(eDto);
                    }
                    eDto = buildEntity(row);
                    fieldList = new ArrayList<>();
                } else if(isTarget(c1, "字段")) { //如果是字段
                    FieldDto fd = buildField(row);
                    fieldList.add(fd);
                }
            }
            if(eDto!=null) {
                eDto.setFields(fieldList);
                result.add(eDto);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        generateCode(basePck, result);
        return result;
    }

    private static void generateCode(String basePck, List<EntityDto> entList) {
        for(EntityDto ed : entList) {
            String pck = basePck+"."+ed.getPck();
            generateModel(basePck, pck, ed);
            generateDao(basePck, pck, ed);
            generateService(basePck, pck, ed);
        }
    }
    //生成model
    private static void generateModel(String basePck, String pck, EntityDto ed) {
        try {
            String filePath = buildPck(basePck, ed.getPck(), "model");
            CodeGenerateModelTools.generate(filePath, pck, ed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //生成dao
    private static void generateDao(String basePck, String pck, EntityDto ed) {
        String daoClsName = "I"+ed.getCls()+"Dao";
        String filePath = buildPck(basePck, ed.getPck(), "dao");
        if(!existsFile(filePath, daoClsName)) { //如果不存在则生成
            try {
                CodeGenerateDaoTools.generate(filePath, pck, ed);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //生成service
    private static void generateService(String basePck, String pck, EntityDto ed) {
        String daoClsName = ed.getCls()+"Service";
        String filePath = buildPck(basePck, ed.getPck(), "service");
        if(!existsFile(filePath, daoClsName)) { //如果不存在则生成
            try {
                CodeGenerateServiceTools.generate(filePath, pck, ed);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /** 检测是否存在 */
    private static boolean existsFile(String filePath, String javaName) {
        File f = new File(filePath+ File.separator + javaName+".java");
        return f.exists();
    }

    private static EntityDto buildEntity(Row row) {
        EntityDto dto = new EntityDto();
        dto.setPck(getCellStringValue(row, 1));
        dto.setCls(getCellStringValue(row, 2));
        dto.setDesc(getCellStringValue(row, 3));
        dto.setAuthor(getCellStringValue(row, 4));
        dto.setPModuleName(getCellStringValue(row, 5));
        dto.setUrl(getCellStringValue(row, 6));
        dto.setFuns(getCellStringValue(row, 7));
        return dto;
    }

    private static FieldDto buildField(Row row) {
        FieldDto dto = new FieldDto();
        dto.setName(getCellStringValue(row, 1));
        dto.setType(getCellStringValue(row, 2));
        dto.setDesc(getCellStringValue(row, 3));
        dto.setRemark(getCellStringValue(row, 4));
        dto.setValidations(getCellStringValue(row, 6));
        return dto;
    }

    private static String getCellStringValue(Row row, Integer cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if(cell!=null) {return cell.getStringCellValue();}
        else {return "";}
    }

    private static boolean isTarget(Cell cell, String val) {
        if(cell!=null) {
            return val.equals(cell.getStringCellValue());
        }
        return false;
    }

    /**
     * 生成功能包，如：model、controller等
     * @param basePck 绝对路径，定位到具体包的路径下
     * @param pck 如：model
     * @return
     */
    private static String buildFunPck(String basePck, String pck) {
        StringBuffer sb = new StringBuffer(basePck);
        sb.append(pck).append(File.separator);
        String res = sb.toString();
        File file = new File(res);
        if(!file.exists()) {
            file.mkdirs();
        }
        return res;
    }

    /**
     *
     * @param basePck 基础包名，如：com.zslin
     * @param pck 业务包名，如：business.app
     * @param funPck 功能包名，如：model
     * @return
     */
    public static String buildPck(String basePck, String pck, String funPck) {
        return buildFunPck(buildPackage(basePck, pck), funPck);
    }

    public static String buildPackage(String basePck, String pck) {
        String rootPath = getRoot(basePck);
        StringBuffer sb = new StringBuffer(rootPath);
        sb.append(exchangePackage(pck));
        return sb.toString();
    }

    private static String getRoot(String basePackage) {
        //src\main\java
        StringBuffer sb = new StringBuffer(System.getProperty("user.dir"));
//        sb.append(File.separator).append("src").append(File.separator).append("main").
//                append(File.separator).append("java").append(File.separator);

        sb.append(File.separator).append(exchangePackage("src.main.java"));
        sb.append(exchangePackage(basePackage));
        return sb.toString();
    }

    private static String exchangePackage(String pck) {
        String [] array = pck.split("\\.");
        StringBuffer sb = new StringBuffer();
        for(String a : array) {
            sb.append(a).append(File.separator);
        }
        return sb.toString();
    }
}
