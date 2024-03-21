package com.example.btvn_05;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    ProgressBar myBarHorizontal;
    EditText txtDataBox;
    Button btnDoItAgain;
    int globalVar = 0, accum = 0, progressStep = 1; // Chỉnh sửa progressStep thành 5
    final int MAX_PROGRESS = 100;
    Handler myHandler = new Handler();
    TextView process;
    int speed=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các thành phần giao diện
        myBarHorizontal = findViewById(R.id.myBarHor);
        txtDataBox = findViewById(R.id.txtBox1);
        btnDoItAgain = findViewById(R.id.btnDoItAgain);
        process = findViewById(R.id.textView);
        process.setText("0%");

        // Thiết lập sự kiện click cho nút "Do it Again"
        btnDoItAgain.setOnClickListener(v -> {
            startProcess(); // Gọi phương thức startProcess() khi nút được click
        });
    }

    // Phương thức bắt đầu quá trình tính toán
    private void startProcess() {
        try {
            // Lấy giá trị nhập vào từ EditText và chuyển đổi thành số nguyên
            speed = Integer.parseInt(txtDataBox.getText().toString()) /10;

            // Kiểm tra nếu giá trị nhập vào là âm hoặc bằng 0, hiển thị thông báo lỗi
            if (speed <= 0) {
                Toast.makeText(MainActivity.this, "Please enter a positive integer", Toast.LENGTH_SHORT).show();
                return;
            }


            // Thiết lập lại thanh tiến trình và các biến
            txtDataBox.setText("");
            process.setText("0%");
            btnDoItAgain.setEnabled(false);
            accum = 0;
            myBarHorizontal.setMax(MAX_PROGRESS);
            myBarHorizontal.setProgress(0);
            myBarHorizontal.setVisibility(View.VISIBLE);

            // Tạo và chạy luồng nền để tính toán
            Thread myBackgroundThread = new Thread(backgroundTask);
            myBackgroundThread.start();

        } catch (NumberFormatException e) {
            // Xử lý nếu người dùng nhập không phải là số
            Toast.makeText(MainActivity.this, "Please enter a valid integer", Toast.LENGTH_SHORT).show();
        }
    }

    // Runnable để cập nhật thanh tiến trình trên giao diện
    private Runnable foregroundRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                myBarHorizontal.incrementProgressBy(progressStep);
                accum += progressStep;

                // Tính phần trăm và cập nhật TextView
                int percentage = (accum * 100) / myBarHorizontal.getMax();
                process.setText(percentage + "%");

                if (accum >= myBarHorizontal.getMax()) {
                    myBarHorizontal.setVisibility(View.VISIBLE);
                    btnDoItAgain.setEnabled(true);
                }
            } catch (Exception e) {
                Log.e("<<foregroundTask>>", e.getMessage());
            }
        }
    };

    // Runnable để thực hiện công việc tính toán ở nền
    // Runnable để thực hiện công việc tính toán ở nền
    private Runnable backgroundTask = new Runnable() {
        @Override
        public void run() {
            try {
                // Lặp qua các bước tính toán
                for (int n = 0; n < MAX_PROGRESS; n++) {
                    Thread.sleep(1*speed); // Dừng một khoảng thời gian để mô phỏng công việc
                    globalVar++;
                    myHandler.post(foregroundRunnable); // Gửi Runnable để cập nhật giao diện
                }
            } catch (InterruptedException e) {
                Log.e("<<foregroundTask>>", e.getMessage());
            }
        }
    };

}
