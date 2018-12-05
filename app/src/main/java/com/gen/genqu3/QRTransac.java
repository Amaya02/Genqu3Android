package com.gen.genqu3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QRTransac extends AppCompatActivity {

    public final static int QRcodeWidth = 500 ;
    Bitmap bitmap ;
    private ImageView iv;
    TextView tran_name, com_name, tran_date, tran_stat, tran_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrtransac);

        Intent intent = getIntent();

        String transacname = intent.getStringExtra("TRANSACNAME");
        String companyname = intent.getStringExtra("COMPANYNAME");
        String date = intent.getStringExtra("DATE");
        String date2 = intent.getStringExtra("DATE2");
        String status = intent.getStringExtra("STATUS");
        String id = intent.getStringExtra("ID");
        String time = intent.getStringExtra("TIME");

        iv = (ImageView) findViewById(R.id.imgqr);
        tran_name = (TextView) findViewById(R.id.tran_name);
        com_name = (TextView) findViewById(R.id.com_name);
        tran_date = (TextView) findViewById(R.id.tran_date);
        tran_stat = (TextView) findViewById(R.id.tran_stat);
        tran_start = (TextView) findViewById(R.id.tran_start);

        tran_name.setText(transacname);
        com_name.setText(companyname);
        tran_date.setText(date);
        tran_stat.setText(status);
        tran_start.setText(date2+" - "+time);

        try {
            bitmap = TextToImageEncode(id);
            iv.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.main_menu2, menu);
        return true;
    }

}
