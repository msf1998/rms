package com.mfs.resourcesmanagement;

import com.mfs.resourcesmanagement.po.SysFile;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class ResourcesManagementApplicationTests {

    @Test
    void contextLoads() {
        System.out.print(Arrays.toString("/".split("/")));
    }

}
