package com.mal.UI;

import bb_framework.types.Coefficient;
import bb_framework.types.Value;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainControllerTest {

    private String testString = "135 139 149 150 156 163 173 184 192 201 210 214 221 229 240";

    final static Coefficient[] cs = new Coefficient[] {new Value(135.),new Value(139.),new Value(149.),new Value(150.),new Value(156.),
            new Value(163.),new Value(173.),new Value(184.),new Value(192.),new Value(201.),new Value(210.),new Value(214.),new Value(221.),
            new Value(229.), new Value(240.)};

    @Test
    public void getValuesFromString() {
        MainController mc = new MainController();
        Coefficient[] received = mc.getValuesFromString(testString.split(" "),15);
        for(int i = 0; i < cs.length; i++){
            assertEquals(cs[i].getVal(), received[i].getVal());
        }
    }
}