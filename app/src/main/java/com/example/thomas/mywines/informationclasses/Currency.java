package com.example.thomas.mywines.informationclasses;

import com.example.thomas.mywines.activities.MainActivity;

public class Currency {

    public static final String USD = "USD";
    public static final String EUR = "EUR";
    public static final String GBP = "GBP";

    public static String getCurrentCurrency(){
        String currency = MainActivity.preferences.getString("currency", EUR);
        if(currency.equals(EUR))
            return "euros";
        if(currency.equals(USD))
            return "dollars";
        else
            return "pounds";
    }
}
