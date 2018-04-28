package com.mal.UI.utils;

public class listview_item {
    private String name;

    private boolean strat_bst = true;
    private boolean strat_dpth = false;
    private boolean strat_brth = false;

    private boolean lp_relaxation = false;
    private String branch_val = "";

    private String bound_file = "";

    public listview_item(String name){ this.name = name; }

    public void setBound_file(String bound_file) {
        this.bound_file = bound_file;
    }

    public void setLp_relaxation(boolean lp_relaxation) {
        this.lp_relaxation = lp_relaxation;
    }

    public void setBranch_val(String branch_val) {
        this.branch_val = branch_val;
    }

    public boolean isLp_relaxation() {
        return lp_relaxation;
    }

    public boolean isStrat_brth() {
        return strat_brth;
    }

    public boolean isStrat_bst() {
        return strat_bst;
    }

    public boolean isStrat_dpth() {
        return strat_dpth;
    }

    public void setStrat_brth(boolean strat_brth) {
        this.strat_brth = strat_brth;
    }

    public void setStrat_bst(boolean strat_bst) {
        this.strat_bst = strat_bst;
    }

    public void setStrat_dpth(boolean strat_dpth) {
        this.strat_dpth = strat_dpth;
    }

    public String getName() {
        return name;
    }

    public String getBound_file() {
        return bound_file;
    }

    public String getBranch_val() {
        return branch_val;
    }
}
