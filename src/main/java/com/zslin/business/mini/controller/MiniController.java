package com.zslin.business.mini.controller;

import com.zslin.business.wx.tools.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by zsl on 2018/7/20.
 */
@Controller
@RequestMapping(value = "mini")
public class MiniController {

    @Autowired
    private SignTools signTools;

    @Autowired
    private EventTools eventTools;

    @Autowired
    private RepeatTools repeatTools;

    @Autowired
    private DatasTools datasTools;

    @Autowired
    private WxMediaTools wxMediaTools;

    /** 获取素材 */
    @GetMapping(value = "media")
    public @ResponseBody
    String media(String type, Integer offset, Integer count) {
        offset = offset==null?0:offset; count = count==null?20:count;
        type = (type==null||"".equals(type))?"news":type;
        String res = wxMediaTools.queryMedias(type, offset, count);
        return res;
    }

    @GetMapping(value = "root")
    public void root(String signature, String timestamp, String nonce, String echostr, HttpServletResponse response) {
        try {

//            System.out.println("signature:"+signature+",timestamp:"+timestamp+",nonce:"+nonce+",echostr:"+echostr);

            PrintWriter out = response.getWriter();
            // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
            if (signTools.checkSignature(signature, timestamp, nonce)) {
                out.print(echostr);
            }
            out.close();

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping(value = "root")
    public @ResponseBody String root(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = null;
        String docSend = "";

        try {
            out = response.getWriter();
            request.setCharacterEncoding("UTF-8");

            Element root = eventTools.getMessageEle(request);

            Node fromUser = root.getElementsByTagName("FromUserName").item(0);
            Node createTime = root.getElementsByTagName("CreateTime").item(0);
            Node msgType = root.getElementsByTagName("MsgType").item(0);
            Node content = root.getElementsByTagName("Content").item(0);
            Node event = root.getElementsByTagName("Event").item(0);
            Node eventKey = root.getElementsByTagName("EventKey").item(0);
            Node msgId = root.getElementsByTagName("MsgId").item(0);
            String builderName = root.getElementsByTagName("ToUserName").item(0).getTextContent(); //开发者微信号

            String msgIdStr = msgId!=null?msgId.getTextContent():null; //消息ID
            String fromOpenid = fromUser.getTextContent(); //用户的openid
            String cTime = createTime.getTextContent(); //创建时间
            String msgTypeStr = msgType.getTextContent(); //事件类型

            if(repeatTools.hasRepeat(msgIdStr, fromOpenid, cTime)) { //如果重复
                out.print(docSend);
                out.flush();
                out.close();
                return "success";
            } else {
                try { System.out.println("content: "+ content.getTextContent()); } catch (Exception e) { e.printStackTrace(); }
                try { System.out.println("event: "+ event.getTextContent()); } catch (Exception e) { e.printStackTrace(); }
                try { System.out.println("eventKey: "+ eventKey.getTextContent()); } catch (Exception e) { e.printStackTrace(); }
                try { System.out.println("msgTypeStr: "+ msgTypeStr); } catch (Exception e) { e.printStackTrace(); }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return "success";
    }
}
