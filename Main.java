package com.company;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    public static double WalletMoney;

    public static String apikey = "";

    public static void main(String[] args) {

        System.out.println("Insert your API Key... (You can find your api key by typing in '/api' in game!)");
        Scanner sc = new Scanner(System.in);

        itemList i = new itemList();
        Gson gson = new Gson();
        String charset = "UTF-8";

        String holdInput = sc.nextLine();
        if(!holdInput.equals("default"))
        {
            apikey = holdInput;
        }

        String fullurl = "https://api.hypixel.net/skyblock/bazaar/products?key=" + apikey;

        System.out.println("Insert Investment Amount");
        WalletMoney = sc.nextDouble();

        URLConnection connection = null;

        try {
            connection = new URL(fullurl).openConnection();
            connection.setRequestProperty("Accept-Charset", charset);
            InputStream response = connection.getInputStream();
            try{
                Scanner scanner = new Scanner(response);
                String responseJson = scanner.useDelimiter("//A").next();
                i = gson.fromJson(responseJson, itemList.class);
                System.out.println("Connection to hypixel API successful!");
                System.out.println("Successfully retrieved item list!");
            } catch (Exception e)
            {
                System.out.println("Scanner Issue");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        GetPrices(i);

    }





    public static void GetPrices(itemList allItems)
    {
        int size = allItems.items.size();
        ArrayList<ItemData> itemsData = new ArrayList<ItemData>();
        ArrayList<product> RawData = new ArrayList<product>();
        System.out.println("Getting Item Summaries...");
        System.out.println("This takes around 3 minutes...");
        Gson gson = new Gson();
        for (int i = 0; i < size; i++) {
            String s = allItems.items.get(i);
            String fullurl = "https://api.hypixel.net/skyblock/bazaar/product?key=" + apikey + "&productId=" + s;
            String charset = "UTF-8";

            URLConnection connection = null;

            try {
                connection = new URL(fullurl).openConnection();
                connection.setRequestProperty("Accept-Charset", charset);
                InputStream response = connection.getInputStream();
                try{
                    Scanner scanner = new Scanner(response);
                    String responseJson = scanner.useDelimiter("//A").next();
                    product p = gson.fromJson(responseJson, product.class);
                    RawData.add(p);
                    ItemData item = parseInfo(p);
                    if (item != null)
                        itemsData.add(item);
                    System.out.print("\rItems Processed: " + i + "/" + size);

                } catch (Exception e)
                {
                    System.out.print(s + " doesn't have any buy and/or sell orders");
                }
            } catch (MalformedURLException e) {
                System.out.println("Error: Product " + s + "failed at Connection with the following Stack Trace." );
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Error: Product " + s + "failed at Connection with the following Stack Trace." );
                e.printStackTrace();
            }

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("FATAL Error: Product " + s + "failed at sleep with the following Stack Trace." );
                e.printStackTrace();
            }
        }

        Collections.sort(itemsData, new Comparator<ItemData>() {
            @Override
            public int compare(ItemData c1, ItemData c2) {
                return Double.compare(c1.profits, c2.profits);
            }
        });

        Collections.reverse(itemsData);
        System.out.println("-------------------------------------");
        System.out.println("-------highest 50 profit items-------");
        System.out.println("-------------------------------------");
        for(int i = 0; i < 25; i++)
        {
            System.out.println("------" + itemsData.get(i).productId + "------");
            System.out.println("Amount to buy: " + itemsData.get(i).amountToBuy);
            System.out.println("Estimated Buy Price: " + itemsData.get(i).BuyPrice);
            System.out.println("Estimated Sell Price: " + itemsData.get(i).SellPrice);
            System.out.println("Buy Volume: " + itemsData.get(i).buyVolume);
            System.out.println("Sell Volume: " + itemsData.get(i).sellVolume);
            System.out.println("Estimated Profit Per Unit: " + itemsData.get(i).profitPerUnit);
            System.out.println("Estimated Profits: " + itemsData.get(i).profits);
        }

        System.out.println("-------------------------------------");
        System.out.println("-----------Print finished------------");
        System.out.println("--Restart the program to scan again--");
        System.out.println("-------------------------------------");

    }

    public static ItemData parseInfo(product p)
    {
        double buyPrice = p.product_info.buySummary.get(0).pricePerUnit;
        double sellPrice = p.product_info.sellSummary.get(0).pricePerUnit;
        double leftOverMoney = WalletMoney % buyPrice;
        double buyCost = WalletMoney - leftOverMoney;
        int amountToBuy = (int)(buyCost / buyPrice);
        double sellAmount = amountToBuy * sellPrice;
        double profit = sellAmount - buyCost;
        if(profit < 0 || amountToBuy <= 0)
            return null;
        ItemData newItem = new ItemData();
        newItem.productId = p.product_info.product_id;
        newItem.amountToBuy = amountToBuy;
        newItem.BuyPrice = buyPrice;
        newItem.SellPrice = sellPrice;
        newItem.buyVolume = Integer.parseInt(p.product_info.quickStatus.get("buyMovingWeek"));
        newItem.sellVolume = Integer.parseInt(p.product_info.quickStatus.get("sellMovingWeek"));
        newItem.profits = profit * 0.99;
        newItem.profitPerUnit = profit / amountToBuy;
        if (newItem.amountToBuy > 10000)
            return null;

        if(newItem.buyVolume < 500000 || newItem.sellVolume < 500000)
            return null;

        return newItem;
    }

}
