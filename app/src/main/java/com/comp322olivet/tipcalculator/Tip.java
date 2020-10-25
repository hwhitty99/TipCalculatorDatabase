package com.comp322olivet.tipcalculator;

import android.annotation.SuppressLint;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tip {

    private long id;
    private long dateMillis;
    private float billAmount;
    private float tipPercent;
    
    public Tip() {
        setId(0);
        setDateMillis(System.currentTimeMillis());
        setBillAmount(0);
        setTipPercent(.15f);
    }

    public Tip(long id, long dateMillis, float billAmount, float tipPercent) {
        this.setId(id);
        this.setDateMillis(dateMillis);
        this.setBillAmount(billAmount);
        this.setTipPercent(tipPercent);
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDateMillis() {
		return dateMillis;
	}

	@SuppressLint("SimpleDateFormat")
	public String getDateStringFormatted() {
    	// set the date with formatting
    	Date date = new Date(dateMillis);
    	SimpleDateFormat sdf = new SimpleDateFormat("MMM d yyyy HH:mm:ss");
    	return sdf.format(date);
	}

	public void setDateMillis(long dateMillis) {
		this.dateMillis = dateMillis;
	}

	public float getBillAmount() {
		return billAmount;
	}

	public String getBillAmountFormatted() {
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        return currency.format(billAmount);
	}

	public void setBillAmount(float billAmount) {
		this.billAmount = billAmount;
	}

	public float getTipPercent() {
		return tipPercent;
	}
	
	public String getTipPercentFormatted() {
        NumberFormat percent = NumberFormat.getPercentInstance();
        return percent.format(tipPercent);    			
	}

	public void setTipPercent(float tipPercent) {
		this.tipPercent = tipPercent;
	}    
}