package fpt.edu.vn.stickershop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

import fpt.edu.vn.stickershop.Api.CreateOrder;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class OrderPayment extends AppCompatActivity {

    private TextView tvQty, tvTotal;
    private Button   btnPay;

    private int    qty;
    private double total;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_order_payment);

        /* Khởi tạo SDK một lần duy nhất (SANDBOX) */
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        /* Nhận dữ liệu từ Intent */
        qty   = getIntent().getIntExtra("soLuong", 1);
        total = getIntent().getDoubleExtra("total", 1_000_000);

        /* Ánh xạ view */
//        tvQty  = findViewById(R.id.textViewSoluong);
//        tvTotal = findViewById(R.id.textViewTongTien);
//        btnPay  = findViewById(R.id.buttonThanhToan);

        NumberFormat vnd = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvQty.setText(String.valueOf(qty));
        tvTotal.setText(vnd.format(total));

        /* Khi bấm “Thanh toán bằng ZaloPay”  */
        btnPay.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    /* 1. Tạo đơn hàng trực tiếp (không cần backend) */
                    CreateOrder orderApi = new CreateOrder();
                    JSONObject data = orderApi.createOrder(String.valueOf((long) total));

                    if (!"1".equals(data.getString("return_code"))) {
                        Toast.makeText(this, "Tạo đơn hàng thất bại", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String zpTransToken = data.getString("zp_trans_token");

                    /* 2. Gọi SDK thanh toán */
                    ZaloPaySDK.getInstance().payOrder(
                            this,
                            zpTransToken,
                            "zalopay2553://app",
                            new PayOrderListener() {
                                @Override
                                public void onPaymentSucceeded(String transactionId, String transToken, String appTransID) {

                                }

                                @Override
                                public void onPaymentCanceled(String zpTransToken, String appTransID) {

                                }

                                @Override
                                public void onPaymentError(ZaloPayError error, String zpTransToken, String appTransID) {
                        }
                            });

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Lỗi tạo đơn hàng", Toast.LENGTH_SHORT).show();
                }

            }).start();
        });
    }

    /* Bắt deeplink từ ZaloPay */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}
