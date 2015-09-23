package relish.permoveo.com.relish.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.util.TypefaceSpan;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * Created by byfieldj on 9/23/15.
 */
public class TipCalculatorActivity extends RelishActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tvTotalAmount)
    TextView totalAmount;

    @Bind(R.id.etTotal)
    EditText etTotalAmount;

    @Bind(R.id.tvTipPercentage)
    TextView tipPercentage;

    @Bind(R.id.tvNumPeople)
    TextView numPeople;

    @Bind(R.id.etNumPeople)
    EditText etNumPeople;

    @Bind(R.id.tvDisplayTipAmount)
    TextView displayTipAmount;

    @Bind(R.id.tvDisplayTotalPerPerson)
    TextView displayTotalPerPerson;

    @Bind(R.id.tvTotalPerPerson)
    TextView totalPerPerson;

    @Bind(R.id.tvDisplayTotalToPay)
    TextView displayTotalToPay;

    @Bind(R.id.tvTotalToPay)
    TextView totalToPay;

    @Bind(R.id.tvTipAmount)
    TextView tipAmount;

    @Bind(R.id.bSendMoney)
    Button sendMoney;

    @Bind(R.id.etTipPercentage)
    EditText etTipPercentage;

    private double amountTotal;
    private double amountTipPercentage;
    private int amountNumPeople;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_bill);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0.0f);

        SpannableString s = new SpannableString("Split & Send");
        s.setSpan(new TypefaceSpan(this, "ProximaNovaBold.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        toolbar.setBackgroundColor(getResources().getColor(R.color.main_color));
        updateToolbar(toolbar);

        initViews();
    }

    private void initViews(){

        totalAmount.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        etTotalAmount.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        tipPercentage.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        numPeople.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        displayTipAmount.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        displayTotalPerPerson.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        totalPerPerson.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        displayTotalToPay.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        totalToPay.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        sendMoney.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        tipAmount.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        etTipPercentage.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        etNumPeople.setTypeface(TypefaceUtil.PROXIMA_NOVA);

        etTotalAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    DecimalFormat f = new DecimalFormat("##.00");
                    Double total = Double.parseDouble(s.toString());
                    String formattedTotal = f.format(total);

                    Double finalTotal = Double.parseDouble(formattedTotal);


                    setTotalAmount(finalTotal);
                }catch(NumberFormatException e){
                    e.printStackTrace();
                }
                performCalculation(getAmountTotal(), getTipPercentage(), getNumberOfPeople());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etTipPercentage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    DecimalFormat f = new DecimalFormat("##.00");
                    Double tip = Double.parseDouble(s.toString());

                    String formattedTip = f.format(tip);
                    Double finalTip = Double.parseDouble(formattedTip);
                    setTipPercentage(finalTip);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                performCalculation(getAmountTotal(), getTipPercentage(), getNumberOfPeople());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etNumPeople.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    Integer numPeople = Integer.parseInt(s.toString());
                    setNumberOfPeople(numPeople);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                performCalculation(getAmountTotal(), getTipPercentage(), getNumberOfPeople());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }

    private void performCalculation(final double total, final double tipPercentage, final int numberPeople){

        //First, let's calculate the tip %

        double tipTotal = (total * tipPercentage) / 100;
        double tipRounded = (double) Math.round(tipTotal * 100) / 100;
        Log.d("TipCalc", "Rounded Tip -> " + tipRounded);



        // Add that to the total to get the new total + tip
        double newTotal = total + tipRounded;

        //Round to 2 decimal places
        double roundedTotal = (double) Math.round(newTotal * 100) / 100;
        Log.d("TipCalc", "Rounded Total -> " + roundedTotal);

        //Next, divide by the number of people
        double dividedTotal = roundedTotal / numberPeople;
        double roundedDividedTotal = (double) Math.round(dividedTotal * 100) / 100;
        Log.d("TipCalc", "Rounded Divided Total -> " + roundedDividedTotal);



        DecimalFormat f = new DecimalFormat("##.00");
        String finalTip = f.format(tipRounded);
        Double finalTipDouble = Double.parseDouble(finalTip);
        tipAmount.setText(""+finalTipDouble);


        totalToPay.setText(""+roundedTotal);
        if(getNumberOfPeople() == 0){
            totalPerPerson.setText("0.00");

        }else {
            totalPerPerson.setText("" + roundedDividedTotal);
        }



    }

    private void displayResults(final double tip, final double each, final double total){
        //Display the tip amount
        tipAmount.setText(""+tip);
        totalPerPerson.setText(""+each);
        totalToPay.setText(""+total);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setTotalAmount(final double total){
        this.amountTotal = total;
    }

    private double getAmountTotal(){
        return this.amountTotal;
    }

    private void setTipPercentage(final double tip){
        this.amountTipPercentage = tip;
    }

    private double getTipPercentage(){
        return this.amountTipPercentage;
    }

    private void setNumberOfPeople(final int num){
        this.amountNumPeople = num;
    }


    private int getNumberOfPeople(){
        return this.amountNumPeople;
    }
}
