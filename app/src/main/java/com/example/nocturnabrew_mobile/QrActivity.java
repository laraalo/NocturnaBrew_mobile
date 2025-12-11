package com.example.nocturnabrew_mobile;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.example.nocturnabrew_mobile.adapters.CartManager;
import com.example.nocturnabrew_mobile.api.ApiService;
import com.example.nocturnabrew_mobile.network.RetrofitInstance;
import com.example.nocturnabrew_mobile.models.CartItem;
import com.example.nocturnabrew_mobile.models.OrderRequest;
import com.example.nocturnabrew_mobile.models.OrderResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class QrActivity extends AppCompatActivity {

    private static final int REQ_PERMISSION = 300;

    private TextView ticketTextView;
    private Button btnGeneratePDFandSendOrder, btnCancelOrder;

    private List<CartItem> cartItems;
    private String userEmail;
    private String userToken;

    private String lastOrderId = "";
    private boolean orderCompleted = false;   // <<<< NUEVO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        ticketTextView = findViewById(R.id.ticket_text);
        btnGeneratePDFandSendOrder = findViewById(R.id.btnConfirm);
        btnCancelOrder = findViewById(R.id.btnCancel);

        SharedPreferences authPrefs = getSharedPreferences("auth", MODE_PRIVATE);
        userEmail = authPrefs.getString("USER_EMAIL", "");
        userToken = authPrefs.getString("USER_TOKEN", "");

        // Cargar carrito
        CartManager.getInstance().loadCart(this, userEmail);
        cartItems = CartManager.getInstance().getItems();

        ticketTextView.setText(buildTicketText());

        btnGeneratePDFandSendOrder.setOnClickListener(v -> {
            if (!orderCompleted)
                sendOrderToBackend();
            else {
                startActivity(new Intent(QrActivity.this, MyOrdersActivity.class));
                finish();
            }
        });

        btnCancelOrder.setOnClickListener(v -> {
            startActivity(new Intent(QrActivity.this, MenuActivity.class));
            finish();
        });
    }

    // ------------------------------------------------------------
    // TICKET EN PANTALLA
    private String buildTicketText() {
        StringBuilder sb = new StringBuilder();
        sb.append("     NOCTURNA BREW\n");
        sb.append("     TICKET DE COMPRA\n");
        sb.append("-----------------------------\n");

        double total = 0;

        for (CartItem item : cartItems) {
            sb.append(item.getProduct().getName()).append("\n");
            sb.append("Cant: ").append(item.getQuantity()).append("  ");
            sb.append("Precio: ").append(item.getProduct().getPrice()).append("\n");
            sb.append("-----------------------------\n");

            total += item.getProduct().getPrice() * item.getQuantity();
        }

        sb.append("TOTAL: $").append(total).append("\n");

        return sb.toString();
    }

    // ------------------------------------------------------------
    // GENERAR QR
    private Bitmap generateQR(String text) {
        try {
            BitMatrix matrix = new MultiFormatWriter()
                    .encode(text, BarcodeFormat.QR_CODE, 400, 400);

            Bitmap bmp = Bitmap.createBitmap(400, 400, Bitmap.Config.RGB_565);

            for (int x = 0; x < 400; x++) {
                for (int y = 0; y < 400; y++) {
                    bmp.setPixel(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            return bmp;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String buildQRText(String orderId) {

        StringBuilder sb = new StringBuilder();
        sb.append("NOCTURNA BREW\n");
        sb.append("Order #").append(orderId).append("\n\n");

        double total = 0;

        sb.append("Products:\n");

        for (CartItem item : cartItems) {

            double subtotal = item.getProduct().getPrice() * item.getQuantity();
            sb.append("- ")
                    .append(item.getQuantity()).append("x ")
                    .append(item.getProduct().getName())
                    .append(" = $").append(subtotal)
                    .append("\n");

            total += subtotal;
        }

        sb.append("\nTotal: $").append(total).append("\n");
        sb.append("Thank you! ❤️");

        return sb.toString();
    }

    // ------------------------------------------------------------
    // PDF
    private void generatePDF() {

        PdfDocument pdf = new PdfDocument();
        PdfDocument.PageInfo info =
                new PdfDocument.PageInfo.Builder(600, 1000, 1).create();

        PdfDocument.Page page = pdf.startPage(info);
        Canvas canvas = page.getCanvas();

        Paint title = new Paint();
        title.setTextAlign(Paint.Align.CENTER);
        title.setTextSize(28);

        Paint normal = new Paint();
        normal.setTextSize(20);

        Paint bold = new Paint();
        bold.setTextSize(22);
        bold.setFakeBoldText(true);

        int y = 80;

        canvas.drawText("NOCTURNA BREW", 300, y, title);
        y += 40;

        canvas.drawText(buildDate(), 300, y, normal);
        y += 40;

        canvas.drawLine(40, y, 560, y, normal);
        y += 40;

        canvas.drawText("Productos", 300, y, bold);
        y += 40;

        double total = 0;
        for (CartItem item : cartItems) {

            double sub = item.getQuantity() * item.getProduct().getPrice();

            canvas.drawText(item.getQuantity() + "× " + item.getProduct().getName(),
                    80, y, normal);

            canvas.drawText("$" + sub, 480, y, normal);

            y += 35;
            total += sub;
        }

        canvas.drawLine(40, y, 560, y, normal);
        y += 40;

        canvas.drawText("Total:", 80, y, bold);
        canvas.drawText("$" + total, 480, y, bold);
        y += 40;

        canvas.drawLine(40, y, 560, y, normal);
        y += 30;

        Bitmap qr = generateQR(buildQRText(lastOrderId));
        if (qr != null)
            canvas.drawBitmap(qr, 100, y, null);

        y += 420;

        canvas.drawText("ORDEN #" + lastOrderId, 300, y, bold);
        y += 60;

        canvas.drawText("Gracias por tu pedido ❤️", 300, y, normal);

        pdf.finishPage(page);

        File file = null;

        try {
            File dir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), "NocturnaBrew");

            if (!dir.exists()) dir.mkdirs();

            String filename = "TICKET_" + lastOrderId + ".pdf";

            file = new File(dir, filename);

            FileOutputStream out = new FileOutputStream(file);
            pdf.writeTo(out);
            out.close();

            Toast.makeText(this,
                    "PDF guardado: " + filename,
                    Toast.LENGTH_LONG).show();

            openPdfFile(file);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error guardando PDF", Toast.LENGTH_LONG).show();
        }

        pdf.close();
    }

    // ------------------------------------------------------------
    private void openPdfFile(File file) {
        Uri uri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                file
        );

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this,
                    "No tienes una app para abrir PDF",
                    Toast.LENGTH_LONG).show();
        }
    }

    private String buildDate() {
        SimpleDateFormat sdf =
                new SimpleDateFormat("M/d/yyyy, hh:mm:ss a");

        return sdf.format(new Date());
    }

    // ------------------------------------------------------------
    private void sendOrderToBackend() {

        List<OrderRequest.Item> backendItems = new ArrayList<>();
        double total = 0;

        for (CartItem item : cartItems) {
            backendItems.add(
                    new OrderRequest.Item(
                            item.getProduct().getProductId(),
                            item.getQuantity()
                    ));
            total += item.getProduct().getPrice() * item.getQuantity();
        }

        OrderRequest request = new OrderRequest(backendItems, total, "");

        ApiService api = RetrofitInstance.getApiService();
        String tokenToSend = "UserToken " + userToken;

        Call<OrderResponse> call = api.createOrder(tokenToSend, request);

        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(QrActivity.this,
                            "Error al crear la orden", Toast.LENGTH_LONG).show();
                    return;
                }

                OrderResponse orderResponse = response.body();
                lastOrderId = orderResponse.getOrder().getOrderId();

                saveOrderToLocal(orderResponse, userEmail);

                clearCart(userEmail);

                orderCompleted = true;

                btnGeneratePDFandSendOrder.setText("Back to My Orders");
                btnCancelOrder.setText("Back to Home");

                generatePDF();

                Toast.makeText(QrActivity.this,
                        "Orden creada correctamente", Toast.LENGTH_LONG).show();
                clearCart(userEmail);
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Toast.makeText(QrActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // ------------------------------------------------------------
    private void saveOrderToLocal(OrderResponse orderResponse, String email) {
        SharedPreferences prefs = getSharedPreferences("ORDERS_DB", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String key = "orders_" + email;

        String json = prefs.getString(key, "[]");

        Type type = new TypeToken<List<OrderResponse.Order>>(){}.getType();
        List<OrderResponse.Order> orders = gson.fromJson(json, type);

        orders.add(orderResponse.getOrder());

        editor.putString(key, gson.toJson(orders));
        editor.apply();
    }



    private void clearCart(String email) {
        // ELIMINA todo el código manual de SharedPreferences que tenías aquí.
        // Solo llama al manager:
        CartManager.getInstance().clearAndSaveCart(this, email);


    }
}





