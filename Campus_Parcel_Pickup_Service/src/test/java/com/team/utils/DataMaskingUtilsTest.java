package com.team.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DataMaskingUtilsTest {

    @Test
    void maskPhone_shouldKeepPrefixAndSuffix() {
        Assertions.assertEquals("138**1234", DataMaskingUtils.maskPhone("138001234"));
        Assertions.assertEquals("138**3456", DataMaskingUtils.maskPhone("13800123456"));
    }

    @Test
    void maskPhone_shouldHandleShortPhone() {
        Assertions.assertEquals("1**5", DataMaskingUtils.maskPhone("135"));
    }


    @Test
    void maskStudentId_shouldKeepPrefixAndSuffix() {
        Assertions.assertEquals("20**01", DataMaskingUtils.maskStudentId("202301"));
    }

    @Test
    void maskStudentId_shouldHandleShortId() {
        Assertions.assertEquals("1**3", DataMaskingUtils.maskStudentId("123"));
    }
}
