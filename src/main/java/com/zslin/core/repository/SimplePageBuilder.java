package com.zslin.core.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Created by 钟述林 393156105@qq.com on 2017/1/8 15:19.
 */
public class SimplePageBuilder {

    public static final int SIZE_DEFAULT = 15;

    public static Pageable generate(Integer page, Integer size, Sort sort) {
        page = page == null? 0:page;
        size = size == null? SIZE_DEFAULT: size;
        if(sort==null) return PageRequest.of(page, size);
        return PageRequest.of(page, size, sort);
    }

    public static Pageable generate(Integer page) {
        return generate(page, SIZE_DEFAULT,null);
    }

    public static Pageable generate(Integer page, Sort sort) {
        return generate(page, SIZE_DEFAULT,sort);
    }
}
