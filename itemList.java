package com.company;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class itemList {
    public boolean success;
    @SerializedName("productIds")
    public List<String> items;

}
