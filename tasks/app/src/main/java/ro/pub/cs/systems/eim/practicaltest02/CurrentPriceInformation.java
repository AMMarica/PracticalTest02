package ro.pub.cs.systems.eim.practicaltest02;

import android.widget.EditText;

public class CurrentPriceInformation {
    private String lastUpdated;
    private String USD;
    private String EUR;

    public CurrentPriceInformation() {
        this.lastUpdated = null;
        this.USD = null;
        this.EUR = null;
    }

    public CurrentPriceInformation(String lastUpdated, String USD, String EUR) {
        this.lastUpdated = lastUpdated;
        this.USD = USD;
        this.EUR = EUR;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getUSD() {
        return USD;
    }

    public void setUSD(String USD) {
        this.USD = USD;
    }

    public String getEUR() {
        return EUR;
    }

    public void setEUR(String EUR) {
        this.EUR = EUR;
    }

    @Override
    public String toString() {
        return "CurrentPriceInformation{" +
                "lastUpdated='" + lastUpdated + '\'' +
                ", USD rate='" + USD + '\'' +
                ", EUR rate='" + EUR + '\'' +
                '}';
    }
}
