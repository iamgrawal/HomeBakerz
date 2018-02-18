package com.android.aman.homebakerz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.CardRequirements;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static com.google.android.gms.wallet.AutoResolveHelper.getStatusFromIntent;

public class ItemDetailedDescriptionActivity extends AppCompatActivity {

    ImageView imageView;
    TextView name, price, description;
    private PaymentsClient mPaymentsClient;
    private Button itemPayButton;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 1;
    private String url = "https://pal-test.adyen.com/pal/servlet/Payment/v30/authorise";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detailed_description);

        Intent intentThatStartedThisActivity = getIntent();
        ItemInfoClass itemDetails = intentThatStartedThisActivity.getParcelableExtra("KEY");

        imageView = (ImageView) findViewById(R.id.itemDetailedDescriptionImgView);
        name = (TextView) findViewById(R.id.itemDetailedDescriptionItemNameTxtView);
        price = (TextView) findViewById(R.id.itemDetailedDescriptionItemPriceTxtView);
        description = (TextView) findViewById(R.id.itemDetailedDescriptionItemDescriptionTxtView);
        itemPayButton = findViewById(R.id.itemDetailedDescriptionButton);

        Glide.with(getApplicationContext())
                .load(itemDetails.getUrl())
                .into(imageView);
        name.setText(itemDetails.getName());
        price.setText(itemDetails.getPrice().toString());

        itemPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePayment();
            }
        });
    }

    private void makePayment() {
        mPaymentsClient = Wallet.getPaymentsClient(this, new Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build());
        isReadyToPay();
    }

    private void isReadyToPay() {
        IsReadyToPayRequest request =
                IsReadyToPayRequest.newBuilder().addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
                        .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD).build();
        Task<Boolean> task = mPaymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(
                new OnCompleteListener<Boolean>() {
                    public void onComplete(Task<Boolean> task) {
                        try {
                            boolean result = task.getResult(ApiException.class);
                            if (result == true) {
                                // Show Google as payment option.
                                PaymentDataRequest request = createPaymentDataRequest();
                                if (request != null) {
                                    AutoResolveHelper.resolveTask(
                                            mPaymentsClient.loadPaymentData(request),
                                            ItemDetailedDescriptionActivity.this,LOAD_PAYMENT_DATA_REQUEST_CODE);
                                }
                            } else {
                                // Hide Google as payment option.
                                Toast.makeText(ItemDetailedDescriptionActivity.this, "Configure Android Pay", Toast.LENGTH_SHORT).show();
                            }
                        } catch (ApiException exception) {
                        }
                    }
                });
    }

    private PaymentDataRequest createPaymentDataRequest() {
        PaymentDataRequest.Builder request =
                PaymentDataRequest.newBuilder()
                        .setTransactionInfo(
                                TransactionInfo.newBuilder()
                                        .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                                        .setTotalPrice("0")      // TODO: 2/14/2018 setTotalPrice
                                        .setCurrencyCode("INR")
                                        .build())
                        .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
                        .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
                        .setCardRequirements(
                                CardRequirements.newBuilder()
                                        .addAllowedCardNetworks(
                                                Arrays.asList(
                                                        WalletConstants.CARD_NETWORK_AMEX,
                                                        WalletConstants.CARD_NETWORK_DISCOVER,
                                                        WalletConstants.CARD_NETWORK_VISA,
                                                        WalletConstants.CARD_NETWORK_MASTERCARD))
                                        .build());

        PaymentMethodTokenizationParameters params =
                PaymentMethodTokenizationParameters.newBuilder()
                        .setPaymentMethodTokenizationType(
                                WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY)
                        .addParameter("gateway", "adyen")         //gateway identifier of a Google-supported gateway
                        .addParameter("gatewayMerchantId", "exampleGatewayMerchantId")  //unique merchant identifier provided by your gateway.
                        .build();

        request.setPaymentMethodTokenizationParameters(params);
        return request.build();
    }

    private JSONObject makeJson(String token) throws JSONException{
        // Create new payment request

        JSONObject paymentRequest = new JSONObject();

        paymentRequest.put("reference", "Home Bakerz - " + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()));
        paymentRequest.put("merchantAccount", "Merchant account");       //todo set account

        // Set amount

        JSONObject amount = new JSONObject();

        amount.put("currency", "INR");

        amount.put("value", price.getText().toString().isEmpty()? 0: Double.parseDouble(price.getText().toString()));

        paymentRequest.put("amount", amount);

        // Billing Address
        JSONObject billingAddress = new JSONObject();
        billingAddress.put("street", "Simon Carmiggeltstraat");
        billingAddress.put("houseNumberOrName", "6-50");
        billingAddress.put("postalCode", "1011 DJ");
        billingAddress.put("city", "Amsterdam");
        billingAddress.put("stateOrProvince", "");
        billingAddress.put("country", "NL");
        paymentRequest.put("billingAddress", billingAddress);

        JSONObject additionalData = new JSONObject();
        additionalData.put("androidpay.token", token);
        paymentRequest.put("additionalData", additionalData);
        return paymentRequest;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        String token = paymentData.getPaymentMethodToken().getToken();
                        Log.d("dexter", "onActivityResult: "+token);
                        try {
                            JSONObject json = makeJson(token);
                            Log.d("dexter", "onActivityResult: "+json);
                            sendData(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = getStatusFromIntent(data);
                        // Log the status for debugging.
                        // Generally, there is no need to show an error to
                        // the user as the Google Pay API will do that.
                        break;
                    default:
                        // Do nothing.
                }
                break;
            default:
                // Do nothing.
        }
    }

    private void sendData(JSONObject json) {

        OutputStream out = null;
        try {

            URL url = new URL(this.url);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            out = new BufferedOutputStream(urlConnection.getOutputStream());

            BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));

            writer.write(json.toString());

            writer.flush();

            writer.close();

            out.close();

            urlConnection.connect();


        } catch (Exception e) {

            System.out.println(e.getMessage());



        }

        /*HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        HttpResponse response = httpclient.execute(httppost);*/

    }
}
