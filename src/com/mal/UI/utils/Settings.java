package com.mal.UI.utils;

import bb_framework.enums.NodeStrategy;

public class Settings {
    String dataset;
    String constraintString;
    ProblemInstance[] instances;

    public Settings(String set, String cs, ProblemInstance[] pi){
        dataset = set;
        constraintString = cs;
        instances = pi;
    }

    public String toXML(){
        return XmlParser.parseToString(this);
    }
}
