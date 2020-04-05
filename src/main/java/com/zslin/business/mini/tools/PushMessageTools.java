package com.zslin.business.mini.tools;

import com.zslin.business.mini.dto.MsgDto;
import com.zslin.business.mini.dto.PushMsgDto;
import com.zslin.business.mini.dto.SingleDataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class PushMessageTools {

    @Autowired
    private AccessTokenTools  accessTokenTools;

    public void push(String toUser, String tempId, String page, MsgDto... content) {
        String accessToken = accessTokenTools.getAccessToken();
        //System.out.println("------"+accessToken);
        RestTemplate template = new RestTemplate();
        String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token="+accessToken;
//        String str = template.getForObject(url, String.class);
        PushMsgDto con = buildCon(toUser, tempId, page, content);
        /*String str = template.postForObject(url, buildCon(toUser, tempId, page, content), String.class);
        System.out.println(str);*/
        ResponseEntity<String> entity = template.postForEntity(url, con, String.class);
       // System.out.println(entity.getBody());
    }

    private PushMsgDto buildCon(String toUser, String tempId, String page, MsgDto... content) {
        PushMsgDto dto = new PushMsgDto();
        Map<String, SingleDataDto> cons = new HashMap<>();
        for(MsgDto d : content) {
            cons.put(d.getKey(), new SingleDataDto(d.getValue()));
        }
        dto.setData(cons);
        dto.setPage(page);
        dto.setTemplate_id(tempId);
        dto.setTouser(toUser);
        return dto;
    }
}
