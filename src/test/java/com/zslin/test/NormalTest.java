package com.zslin.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Profile("zsl")
public class NormalTest {

    @Test
    public void test01() {
        System.out.println("-------------->");
    }
}
