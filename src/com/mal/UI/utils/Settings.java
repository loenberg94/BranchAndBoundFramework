package com.mal.UI.utils;

import bb_framework.enums.NodeStrategy;

public class Settings {

    String dataset;
    String constraintString;
    ProblemInstance[] instances;

    public String toXML(){
        return XmlParser.parseToString(this);
    }
}
