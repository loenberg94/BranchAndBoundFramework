package com.mal.UI.utils;

import bb_framework.enums.ProblemType;

public class Settings {
    String dataset;
    String constraintString;
    ProblemInstance[] instances;
    ProblemType problemType;
    String[] filesUsedIn;

    public Settings(String set, String cs, ProblemType pt, ProblemInstance[] pi){
        dataset = set;
        constraintString = cs;
        instances = pi;
        problemType = pt;
        filesUsedIn = new String[pi.length];
        for(int i = 0; i < pi.length; i++) filesUsedIn[i] = pi[i].getFileName();
    }

    public String toXML(){
        return XmlParser.parseToString(this);
    }
}
