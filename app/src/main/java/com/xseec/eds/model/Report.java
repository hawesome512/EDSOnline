package com.xseec.eds.model;

import com.xseec.eds.util.Generator;

import java.util.List;

//nj--存储报表信息 2018/11/16

public class Report<T> {

    private String title;
    private String leftTitle;
    private String leftValue;
    private String centerTitle;
    private String centerValue;
    private String rightTitle;
    private String rightValue;

    //NJ--定义报表数据类型 0：操作、工单、异常 1：能耗 2：温度、湿度
    private int reportType;
    //nj--报表数据列表
    private List<String> environmentList;
    private List<T> leftList;
    private List<T> centerList;
    private List<T> rightList;

    public Report(String[] reports,int reportType){
        if (reports.length==4){
            this.title=reports[0];
            this.leftTitle=reports[1];
            this.centerTitle=reports[2];
            this.rightTitle=reports[3];
            this.reportType=reportType;
        }
    }

    public String getLeftValue() {
        switch (reportType){
            case 0:
                leftValue=Generator.countList( leftList );
                break;
            case 1:
                leftValue=Generator.getReportSum( (List<String>) leftList );
                break;
            case 2:
                leftValue=Generator.getReportMax( environmentList );
                break;
        }
        return leftValue;
    }

    public String getCenterValue() {
        switch (reportType){
            case 0:
                centerValue=Generator.countList( centerList );
                break;
            case 1:
                centerValue=Generator.getReportSum( (List<String>) centerList );
                break;
            case 2:
                centerValue=Generator.getReportMin( environmentList );
                break;
        }
        return centerValue;
    }


    public String getRightValue() {
        switch (reportType){
            case 0:
                rightValue=Generator.countList( rightList );
                break;
            case 1:
                float realValue=Float.valueOf( getLeftValue() );
                float forecastValue=Float.valueOf( getCenterValue() );
                rightValue= String.valueOf( Math.round( (realValue-forecastValue)/forecastValue*10000 )
                        /100f )+"%" ;
                break;
            case 2:
                rightValue=Generator.getReportAve( environmentList );
                break;
        }
        return rightValue;
    }

    public int getReportType() { return reportType; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getLeftTitle() { return leftTitle; }

    public String getCenterTitle() { return centerTitle; }

    public String getRightTitle() { return rightTitle; }

    public void setEnvironmentList(List<String> totalList) {
        this.environmentList = totalList;
    }

    public List<T> getLeftList() { return leftList; }

    public void setLeftList(List<T> leftList) { this.leftList = leftList; }

    public List<T> getCenterList() { return centerList; }

    public void setCenterList(List<T> centerList) { this.centerList = centerList; }

    public List<T> getRightList() { return rightList; }

    public void setRightList(List<T> rightList) { this.rightList = rightList; }
}
