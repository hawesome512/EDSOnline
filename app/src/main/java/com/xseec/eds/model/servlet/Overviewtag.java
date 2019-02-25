package com.xseec.eds.model.servlet;

import com.xseec.eds.R;
import com.xseec.eds.model.tags.OverviewTag;
import com.xseec.eds.util.EDSApplication;
import com.xseec.eds.util.Generator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/2/16.
 */

public class Overviewtag extends BaseModel {

    private String item1;
    private String item2;
    private String item3;
    private String item4;
    private String item5;
    private String item6;

    private static final String SPLIT="/";
    private static final String NULL="*";

    public String getItem1() {
        return item1;
    }

    public void setItem1(String item1) {
        this.item1 = item1;
    }

    public String getItem2() {
        return item2;
    }

    public void setItem2(String item2) {
        this.item2 = item2;
    }

    public String getItem3() {
        return item3;
    }

    public void setItem3(String item3) {
        this.item3 = item3;
    }

    public String getItem4() {
        return item4;
    }

    public void setItem4(String item4) {
        this.item4 = item4;
    }

    public String getItem5() {
        return item5;
    }

    public void setItem5(String item5) {
        this.item5 = item5;
    }

    public String getItem6() {
        return item6;
    }

    public void setItem6(String item6) {
        this.item6 = item6;
    }

    public List<OverviewTag> getOverviewTagList(){
        List<OverviewTag> tagList=new ArrayList<>();
        tagList.add(convertOverviewTag(item1));
        tagList.add(convertOverviewTag(item2));
        tagList.add(convertOverviewTag(item3));
        tagList.add(convertOverviewTag(item4));
        tagList.add(convertOverviewTag(item5));
        tagList.add(convertOverviewTag(item6));
        return tagList;
    }

    private OverviewTag convertOverviewTag(String item){
        String[] items=item.split(SPLIT);
        OverviewTag tag=null;
        if(items.length==4){
            int tagResId= Generator.getImageRes("overviewtag_"+items[0]);
            String unit=items[3].equals(NULL)?null:items[3];
            tag=new OverviewTag(tagResId,items[1],null,items[2],unit);
        }
        return tag;
    }

    @Override
    public String toJson() {
        return EDSApplication.getContext().getString(R.string.svl_overviewtag_request,id,item1,item2,item3,item4,item5,item6);
    }
}
