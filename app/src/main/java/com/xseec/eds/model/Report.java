package com.xseec.eds.model;

import java.util.List;

//nj--存储报表信息 2018/11/16

public class Report<T> {

    private String title;
    private String left;
    private String center;
    private String right;


    public void setEnvironmentList(List<String> totalList) {
        this.environmentList = totalList;
    }

    //nj--报表数据列表
    private List<String> environmentList;
    private List<T> leftList;
    private List<T> centerList;
    private List<T> rightList;

    public Report(String[] reports){
        this.title=reports[0];
        this.left=reports[1];
        this.center=reports[2];
        this.right=reports[3];
    }

    private String countList(List list){
        if (list!=null){
            return String.valueOf( list.size() );
        }else {
            return "0";
        }
    }

    public String getValueLeft(){
        return countList( leftList );
    }

    public String getValueCenter(){
        return countList( centerList );
    }

    public String getValueRight(){
        return countList( rightList );
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getLeft() { return left; }

    public String getCenter() { return center; }

    public String getRight() { return right; }

    public List<String> getEnvironmentList() { return environmentList; }

    public List<T> getLeftList() { return leftList; }

    public void setLeftList(List<T> leftList) { this.leftList = leftList; }

    public List<T> getCenterList() { return centerList; }

    public void setCenterList(List<T> centerList) { this.centerList = centerList; }

    public List<T> getRightList() { return rightList; }

    public void setRightList(List<T> rightList) { this.rightList = rightList; }
}
