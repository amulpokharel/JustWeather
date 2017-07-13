package com.amulp.justweather.data;

/**
 * Created by Amul on 6/8/17.
 */

public class Temperature {
    private double tempInC = 0;

    public Temperature(int temperature){
        tempInC = (double)temperature;
    }

    public Temperature(double temperature){
        tempInC = temperature;
    }

    public Temperature(String temperature){
        tempInC = Double.parseDouble(temperature);
    }

    public Temperature(){
        tempInC = 0;
    }

    public void setTemp(int temperature){
        tempInC = (double)temperature;
    }

    public void setTemp(double temperature){
        tempInC = temperature;
    }

    public void setTemp(String temperature){
        tempInC = Double.parseDouble(temperature);
    }

    //fahrenheit
    public int inFahrenheit(){
        return (int)((tempInC * (9.0/5.0))+32.0);
    }

    //kelvin
    public int inKelvin(){
        return (int)(tempInC + 273.15);
    }

    public int inCelsius(){
        return (int)tempInC;
    }


}
