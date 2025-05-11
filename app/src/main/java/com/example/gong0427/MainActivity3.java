package com.example.gong0427;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

//import android.os.Bundle;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class MainActivity3 extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main3);
//
//        int score = getIntent().getIntExtra("SCORE", 0);
//        String resultDetails = getIntent().getStringExtra("RESULT_DETAILS");
//
//        TextView tvScore = findViewById(R.id.tv_score);
//        TextView tvDetails = findViewById(R.id.tv_details);
//
//        tvScore.setText("最终得分：" + score + "/100");
//        tvDetails.setText("考试结果已保存到本地数据库\n\n" + resultDetails);
//    }
//}
public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        double score = getIntent().getDoubleExtra("SCORE", 0.0); // 接收 double
        String resultDetails = getIntent().getStringExtra("RESULT_DETAILS");

        TextView tvScore = findViewById(R.id.tv_score);
        TextView tvDetails = findViewById(R.id.tv_details);

        // 格式化输出，保留两位小数
        tvScore.setText(String.format("最终得分：%.2f/100", score));
        tvDetails.setText("考试结果已保存到本地数据库\n\n" + resultDetails);
    }
}