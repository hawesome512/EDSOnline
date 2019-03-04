package com.xseec.eds.model.tags;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class EnergyTag extends StoredTag{
    private static final String NULL_TAG="*";
    private static final String SPLIT="/";
    public static final int VALID_INFOS_LENGTH=3;

    private String alias;
    private String level;

    public EnergyTag(String tagName) {
        super(tagName,DataType.MAX);
    }

    public EnergyTag(String[] infos){
        super(infos[1],DataType.MAX);
        level=infos[0];
        alias=infos[2];
    }

    public boolean isNull(){
        return getTagName().equals(NULL_TAG);
    }

    public String getAlias() {
        return alias;
    }

    public String getLevel() {
        return level;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public static String[] getInfos(String information){
        return information.split(SPLIT);
    }

    public List<EnergyTag> getParent(List<EnergyTag> energyTags){
        List<EnergyTag> parent=new ArrayList<>();
        for(int i=1;i<=level.length()-1;i++){
            String parentLevel=level.substring(0,i);
            for(EnergyTag energyTag:energyTags){
                if (energyTag.getLevel().equals(parentLevel)){
                    parent.add(energyTag);
                    break;
                }
            }
        }
        //自身也包含进父级中
        parent.add(this);
        return parent;
    }

    public List<EnergyTag> getChildren(List<EnergyTag> energyTags){
        List<EnergyTag> children=new ArrayList<>();
        String regex=getLevel()+"\\d";
        Pattern pattern=Pattern.compile(regex);
        for(EnergyTag energyTag:energyTags){
            if(pattern.matcher(energyTag.getLevel()).find()){
                children.add(energyTag);
            }
        }
        return children;
    }
}
