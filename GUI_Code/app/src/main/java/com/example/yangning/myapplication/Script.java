package com.example.yangning.myapplication;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

import java.io.Serializable;

/**
 * Created by yangning on 17/5/2.
 */
@DynamoDBTable(tableName = "Script")
public class Script implements Serializable{
    private String name;
    private String data;

    @DynamoDBHashKey(attributeName = "Script_Name")
    public String getName() {
        return name;
    }

    public void setName(String Name) {
        this.name = Name;
    }

    @DynamoDBAttribute(attributeName = "Data")
    public String getData() {
        return data;
    }

    public void setData(String Data) {
        this.data = Data;
    }
}