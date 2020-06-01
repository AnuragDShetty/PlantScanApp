package com.example.plantscanapp.model;

import java.io.Serializable;

public class HistoryModel implements Serializable {
    String id,name,date;
    public HistoryModel(){}
    public HistoryModel(String id,String name,String date){
        this.id=id;
        this.name=name;
        this.date=date;

    }

    public String getId(){return id;}
    public String getName(){return name;}
    public String getDate(){return date;}

    public void setId(String id){this.id=id;}
    public void setName(String name) {
        this.name = name;
    }
    public void setDate(String date){this.date=date;}
}
