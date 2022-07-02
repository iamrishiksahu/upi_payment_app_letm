package com.abcd.letm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;

import dev.shreyaspatil.easyupipayment.EasyUpiPayment;
import dev.shreyaspatil.easyupipayment.exception.AppNotFoundException;
import dev.shreyaspatil.easyupipayment.listener.PaymentStatusListener;
import dev.shreyaspatil.easyupipayment.model.TransactionDetails;
import dev.shreyaspatil.easyupipayment.model.TransactionStatus;

public class MainActivity extends AppCompatActivity implements PaymentStatusListener {

    /*
    * Implementing UPI Payment process through
    * Easy UPI payment library @version: 3.0.3
    * To make a UPI payment and receive response.
    */

    //Declaring essential variables for transaction.

    private EditText nameEt, upiIdEt, amountEt, descEt;
    private Button payBtn;

    //Note: Change this merchant code with a valid merchant code otherwise
    //this application will not work
    public static String MERCHANT_CODE = "enter_a_valid_merchant_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing variables for transaction.

        nameEt = findViewById(R.id.editTextTextPersonName);
        upiIdEt = findViewById(R.id.editTextTextPersonName2);
        amountEt = findViewById(R.id.editTextNumberDecimal);
        payBtn = findViewById(R.id.button);
        descEt = findViewById(R.id.descriptionEtId);


        //Making the pay button functional

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isImportantFieldEmpty()){
                    launchUPIPayment();
                }
            }

        });
    }

    @Override
    public void onTransactionCancelled() {
        //Transaction is cancelled.
        toast("Transaction is aborted!");
    }

    @Override
    public void onTransactionCompleted(TransactionDetails transactionDetails) {

        //Transaction is completed (Either success or failure)

        /*
        * On Transaction Completion, we will handle
        * three states, @FAILURE, @SUCCESS & @SUBMITTED
        * separately
        *
        * Here, F -> Failure | S -> Success | P -> Pending
        *
        * */

        TransactionStatus status = transactionDetails.getTransactionStatus();

        if(status.equals(TransactionStatus.FAILURE)){
            //Transaction is failed.
            toast("Transaction has failed!");

        }else if(status.equals(TransactionStatus.SUCCESS)){
            //Transaction is successful.
            toast("Transaction was successful!");


        }else if(status.equals(TransactionStatus.SUBMITTED)){
            //Transaction is submitted (pending).
            toast("Transaction is pending :) Please wait, till 2 Hours.");


        }

    }


    /*
    *Below is a function to display toast message.
    * */
    public void toast (String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    /*
    * Generating the transaction id
    * */
    public String generateTransactionID(){
        return UUID.randomUUID().toString().substring(0,8);
    }

    /*
    * Checking for input texts areas are not null
    * */

    public boolean isImportantFieldEmpty () {

        if (upiIdEt.getText().toString() != "") {
            if (amountEt.getText().toString() != "") {
                if (nameEt.getText().toString() != "") {

                    //All good! ready to go for transaction.
                    return false;

                } else {
                    toast("Please enter payee name!");
                    return true;
                }
            } else {
                toast("Please enter a valid amount!");
                return true;

            }
        } else {
            toast("Please enter a UPI ID to send money!");
            return true;

        }
    }

    /*
    * After checking for faults, the below function will
    * be used to launch the upi payment process
    * */
    private void launchUPIPayment() {


        //EasyUPIPayment implementation

        String upiID = upiIdEt.getText().toString();
        String name = nameEt.getText().toString();
        String amount = amountEt.getText().toString();
        String description = descEt.getText().toString();
        String transactionID = generateTransactionID();
        String transactionRef = "ref" + transactionID ;

        /*
        * Amount is accepted in positive decimal only
        * So converting the amount into decimal number is required.
        */

        if(!amount.contains(".")){
            amount = amount + ".0";
        }

        try {
            EasyUpiPayment upiPayment = new EasyUpiPayment.Builder(MainActivity.this)
                    .setPayeeVpa(upiID)
                    .setPayeeName(name)
                    .setAmount(amount)
                    .setDescription(description)
                    .setPayeeMerchantCode(MERCHANT_CODE)
                    .setTransactionId(transactionID)
                    .setTransactionRefId(transactionRef)
                    .build();

            upiPayment.startPayment();
            upiPayment.setPaymentStatusListener(MainActivity.this);


        } catch (Exception e) {
            /*
            * Any error in the above statements would be printed to StackTrace
            * and user will be asked to enter valid details
            */
            toast("Please enter valid details!");
            e.printStackTrace();
        }

    }

}