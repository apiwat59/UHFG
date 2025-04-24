package com.gg.reader.api.utils;

import java.io.FileWriter;

/* loaded from: classes.dex */
public class XhPower {
    public static void setPower(boolean state) {
        try {
            if (state) {
                FileWriter file_cgp_on = new FileWriter("/sys/cgp_ctrl/cgp_on");
                file_cgp_on.write("1");
                file_cgp_on.close();
                FileWriter file_cgp_switch_vbat = new FileWriter("/sys/cgp_ctrl/cgp_switch_vbat");
                file_cgp_switch_vbat.write("1");
                file_cgp_switch_vbat.close();
                FileWriter file_cgp_uart_switch = new FileWriter("/sys/cgp_ctrl/cgp_uart_switch");
                file_cgp_uart_switch.write("0");
                file_cgp_uart_switch.close();
                FileWriter file_cgp_vbus_5v = new FileWriter("/sys/cgp_ctrl/cgp_vbus_5v");
                file_cgp_vbus_5v.write("1");
                file_cgp_vbus_5v.close();
            } else {
                FileWriter file_cgp_on2 = new FileWriter("/sys/cgp_ctrl/cgp_on");
                file_cgp_on2.write("0");
                file_cgp_on2.close();
                FileWriter file_cgp_switch_vbat2 = new FileWriter("/sys/cgp_ctrl/cgp_switch_vbat");
                file_cgp_switch_vbat2.write("0");
                file_cgp_switch_vbat2.close();
                FileWriter file_cgp_vbus_5v2 = new FileWriter("/sys/cgp_ctrl/cgp_vbus_5v");
                file_cgp_vbus_5v2.write("0");
                file_cgp_vbus_5v2.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
