package com.zslin.core.service;

import com.zslin.core.dto.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestService {

    public JsonResult handler(String params) {
      log.info("testService.handler params: {}", params);
      return JsonResult.success("调用成功");
    }

    public JsonResult handler(String params, Integer b) {
        return JsonResult.success();
    }
}
