package com.zslin.code.tools;

import com.zslin.code.dto.FieldDto;
import com.zslin.core.common.NormalTools;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 代码生成变量配置
 */
@Slf4j
public class CodeGenerateCommon {

    private static final String TAB = "\t";
    private static final String LINE = "\n";
    private static final String BLANK = " ";
    private static final String FILE_SEP = File.separator;

    public static String getBlank() {
        return getBlank(1);
    }
    public static String getBlank(Integer size) {
        return getCountStr(BLANK, size);
    }
    public static String getLine() {
        return getLine(1);
    }
    public static  String getLine(Integer size) {
        return getCountStr(LINE, size);
    }

    private static String getCountStr(String str, Integer size) {
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<size;i++) {sb.append(str);}
        return sb.toString();
    }
    public static String getTab() {
        return getTab(1);
    }

    public static String getTab(Integer size) {
        return getCountStr(TAB, size);
    }

    public static void checkFile(String path) {
        File f = new File(path);
        if(!f.exists()) {f.mkdirs();}
    }

    private static String buildSingleLineNodes() {
        return getLine()+"*"+getLine();
    }

    public static String buildFields(List<FieldDto> fieldDtoList) {
        StringBuffer sb = new StringBuffer();
        for(FieldDto fd : fieldDtoList) {sb.append(buildSingleField(fd));}
        return sb.toString();
    }

    private static String buildSingleField(FieldDto fd) {
        StringBuffer sb = new StringBuffer();
        String type = fd.getType();
        sb.append(getLine())
                .append(getTab()).append("/**").append(getLine())
                .append(getTab()).append("* ").append(fd.getDesc()).append(getLine());
        if(fd.getRemark()!=null && !"".equals(fd.getRemark())) {
            sb.append(getTab()).append("* @remark ").append(fd.getRemark()).append(getLine());
        }
        sb.append(getTab()).append("*/").append(getLine());
        if("longString".equalsIgnoreCase(type)) {
            sb.append(getTab()).append("@Lob").append(getLine());
            type = "String";
        }
        String validation = buildValidate(fd.getValidations());
        if(!NormalTools.isNullOr(validation)) {
            sb.append(getTab()).append(buildValidate(fd.getValidations())).append(getLine()); //添加验证信息
        }
        sb.append(getTab()).append("private").append(getBlank()).append(type)
                .append(getBlank()).append(fd.getName()).append(";").append(getLine());
        return sb.toString();
    }

    /**
     * 生成数据表名
     * @param pck 包名
     * @param cls 类名
     * @return
     */
    public static String buildTableName(String pck, String cls) {
        return getPrefix(pck)+"_"+camel2Underline(cls).toLowerCase();
    }

    /**
     * 获取Velocity模板路径
     * @return
     */
    public static String getVelocityPath() {
        String outPath = System.getProperty("user.dir")+FILE_SEP+"src"+FILE_SEP+"main"+FILE_SEP+"resources"+FILE_SEP+"templates"+FILE_SEP+"velocity"+FILE_SEP;;
        return outPath;
    }

    private static String getPrefix(String pck) {
        return pck.substring(pck.lastIndexOf(".")+1);
    }

    /**
     * 驼峰法转下划线
     *
     * @param line
     *            源字符串
     * @return 转换后的字符串
     */
    public static String camel2Underline(String line) {
        if (line == null || "".equals(line)) {
            return "";
        }
        line = String.valueOf(line.charAt(0)).toUpperCase()
                .concat(line.substring(1));
        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile("[A-Z]([a-z\\d]+)?");
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(word.toUpperCase());
            sb.append(matcher.end() == line.length() ? "" : "_");
        }
        return sb.toString();
    }

    private static String rebuildValidateStr(String validation) {
        validation = validation.replaceAll("；", ";")
                .replaceAll("_", "-")
                .replaceAll("“", "\"");
        return validation;
    }

    /**
     * 构建表单验证
     * @param validation 格式如：@NotBlank-街道不能为空；@Length-min=5-街道至少5个字
     * @return
     */
    public static String buildValidate(String validation) {
        if(NormalTools.isNullOr(validation)) {return null;}
        validation = rebuildValidateStr(validation);
        String [] array = validation.split(";");
        StringBuffer sb = new StringBuffer();
        boolean isFirst = true;
        boolean isFirstRule = true;
        for(String valid: array) {
            String [] singleArray = valid.split("-");
            for(String sa : singleArray) {
                if(sa.startsWith("@")) {
                    if(isFirst) {
                        sb.append(sa).append("(");
                        isFirst = false;
                    } else {
                        sb.append(")\n").append(sa).append("(");
                    }
                    isFirstRule = true;
                } else {
                    if(!isFirstRule) {sb.append(", ");}
                    isFirstRule = false;
                    String[]rule = sa.split("=");
                    if(rule.length==1) {
                        //@NotEmpty(message = "msgUrl不能为空")
                        sb.append("message=").append(buildValidateValue(rule[0]));
                    } else if(rule.length==2) {
                        sb.append(rule[0]).append("=").append(buildValidateValue(rule[1]));
                    }
                }
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * 生成表单验证的包信息
     * @param fieldDtoList
     * @return
     */
    public static String buildValidatePck(List<FieldDto> fieldDtoList) {
        StringBuffer sb = new StringBuffer();
        List<String> tmpList = new ArrayList<>();
        for(FieldDto dto : fieldDtoList) {
            //@NotBlank-街道不能为空；@Length-min=5-街道至少5个字
            String validation = rebuildValidateStr(dto.getValidations());
            String [] array = validation.split(";");
            for(String single:array) {
                String [] temp = single.split("-");
                for(String tmp : temp) {
                    if(tmp.startsWith("@") && !tmpList.contains(tmp.substring(1))) {tmpList.add(tmp.substring(1));}
                }
            }
        }
        for(String t : tmpList) {
            if("Range".equalsIgnoreCase(t) || "Length".equalsIgnoreCase(t)) {
                sb.append("import org.hibernate.validator.constraints.").append(t).append(";\n");
            } else {
                sb.append("import javax.validation.constraints.").append(t).append(";\n");
            }
        }
        log.info("--->"+sb.toString());
        return sb.toString();
    }

    private static String buildValidateValue(String rule) {
        boolean isNumber = NormalTools.isNumeric(rule);
        return isNumber?rule:("\""+rule+"\"");
    }
}
