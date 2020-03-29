package com.example.plantscanapp.model;

import java.io.Serializable;

public class HistoryModel implements Serializable {
    String name,date;
    public HistoryModel(){}
    public HistoryModel(String name,String date){
        this.name=name;
        this.date=date;
    }
    public String getName(){return name;}
    public String getDate(){return date;}

    public void setDate(String date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }
}
