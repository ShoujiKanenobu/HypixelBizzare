package com.company;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

public class productSummary {
    public String product_id;
    @SerializedName("buy_summary")
    public List<summaries> buySummary;
    @SerializedName("sell_summary")
    public List<summaries> sellSummary;
    @SerializedName("quick_status")
    public HashMap<String, String> quickStatus = new HashMap<String, String>();
    @SerializedName("week_historic")
    public List<historic> weekHistoric;
}
