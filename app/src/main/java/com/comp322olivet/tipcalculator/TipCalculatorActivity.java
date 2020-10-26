package com.comp322olivet.tipcalculator;

import java.text.NumberFormat;
import java.util.ArrayList;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import static android.content.ContentValues.TAG;

public class TipCalculatorActivity extends Activity 
implements OnEditorActionListener, OnClickListener {

    // define variables for the widgets
    private EditText billAmountEditText;
    private TextView percentTextView;   
    private Button   percentUpButton;
    private Button   percentDownButton;
    private TextView tipTextView;
    private TextView totalTextView;
    
    // define instance variables that should be saved
    private String billAmountString = "";
    private float tipPercent = .15f;
    private long nSaves = 0;

    // set up preferences
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator);

        // get references to the widgets
        billAmountEditText = (EditText) findViewById(R.id.billAmountEditText);
        percentTextView = (TextView) findViewById(R.id.percentTextView);
        percentUpButton = (Button) findViewById(R.id.percentUpButton);
        percentDownButton = (Button) findViewById(R.id.percentDownButton);
        tipTextView = (TextView) findViewById(R.id.tipTextView);
        totalTextView = (TextView) findViewById(R.id.totalTextView);

        // set the listeners
        billAmountEditText.setOnEditorActionListener(this);
        percentUpButton.setOnClickListener(this);
        percentDownButton.setOnClickListener(this);
        
        // get default SharedPreferences object
        prefs = PreferenceManager.getDefaultSharedPreferences(this);        
    }
    
    @Override
    public void onPause() {
        // save the instance variables       
        Editor editor = prefs.edit();
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.putLong("nSaves", nSaves);
        editor.commit();        

        super.onPause();      
    }
    
    @Override
    public void onResume() {
        super.onResume();

        // get the instance variables
        billAmountString = prefs.getString("billAmountString", "");
        tipPercent = prefs.getFloat("tipPercent", 0.15f);
        nSaves = prefs.getLong("nSaves", 0);

        // set the bill amount on its widget
        billAmountEditText.setText(billAmountString);
        
        // calculate and display
        calculateAndDisplay();

        //string builder used for displaying tips in Logcat
        StringBuilder sb = new StringBuilder();

        //database object for array list and logcat
        TipDB db = new TipDB(this);

        //used to create String that goes to logcat
        ArrayList<Tip> tips = db.getTips();

        //appends all tips to string builder
        for (Tip t : tips) {
            sb.append(t.getId() + "|" + t.getDateMillis() + "|" + t.getBillAmount()
                    + "|" + t.getTipPercent() + "\n");
        }

        //displays all tips
        Log.d(TAG, sb.toString());
        //displays formatted date of most recent tip
        Log.d(TAG, db.getRecentTip().getDateStringFormatted() + "\n");
        //displays average tip percentage
        Log.d(TAG, Float.toString(db.getAverage()));
    }
    
    public void calculateAndDisplay() {        

        // get the bill amount
        billAmountString = billAmountEditText.getText().toString();
        float billAmount; 
        if (billAmountString.equals("")) {
            billAmount = 0;
        }
        else {
            billAmount = Float.parseFloat(billAmountString);
        }
        
        // calculate tip and total 
        float tipAmount = billAmount * tipPercent;
        float totalAmount = billAmount + tipAmount;
        
        // display the other results with formatting
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipTextView.setText(currency.format(tipAmount));
        totalTextView.setText(currency.format(totalAmount));
        
        NumberFormat percent = NumberFormat.getPercentInstance();
        percentTextView.setText(percent.format(tipPercent));
    }
    
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
    		actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            calculateAndDisplay();
        }        
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.percentDownButton:
            tipPercent = tipPercent - .01f;
            calculateAndDisplay();
            break;
        case R.id.percentUpButton:
            tipPercent = tipPercent + .01f;
            calculateAndDisplay();
            break;
        }
    }

    //called when saveButton gets clicked
    public void saveTip(View view) {

        //in case user clicks save before confirming the amount entered
        calculateAndDisplay();
        TipDB db = new TipDB(this);

        //create new tip object with the current amounts on screen
        Tip tip = new Tip(nSaves, System.currentTimeMillis(), Float.parseFloat(billAmountString), tipPercent);

        //increments number of saved tips
        nSaves++;

        //saves tip to database
        db.saveTip(tip);

        //resets the bill amount
        billAmountEditText.setText("");

        //sets new tip percentage to the current average
        tipPercent = db.getAverage();
        calculateAndDisplay();
    }
}