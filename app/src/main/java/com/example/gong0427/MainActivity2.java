package com.example.gong0427;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {

    private QuestionDbHelper dbHelper;
    private LinearLayout questionContainer;
    private Button btnSubmit;
    private TextView tvCountdown;

    // 存储每个题目的正确答案和用户的回答
    private Map<Integer, String> correctAnswers = new HashMap<>();
    private Map<Integer, Integer> userSelections = new HashMap<>();

    private CountDownTimer countDownTimer;
    private static final long TOTAL_TIME = 600000; // 10分钟倒计时（单位毫秒）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        dbHelper = new QuestionDbHelper(this);
        questionContainer = findViewById(R.id.questionContainer); // 注意修改XML中的LinearLayout id
        btnSubmit = findViewById(R.id.btn_submit);
        tvCountdown = findViewById(R.id.tv_countdown);

        loadQuestionsFromDatabase();

        startCountDown();

        btnSubmit.setOnClickListener(v -> checkAnswers());

    }



    private void startCountDown() {
        countDownTimer = new CountDownTimer(TOTAL_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                tvCountdown.setText(String.format("剩余时间: %d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvCountdown.setText("时间到！");
                checkAnswers();
            }
        }.start();
    }

    private void loadQuestionsFromDatabase() {
        Cursor questionCursor = dbHelper.getReadableDatabase().query("questions", null, null, null, null, null, null);

        int questionIndex = 1;

        while (questionCursor.moveToNext()) {
            int questionId = questionCursor.getInt(questionCursor.getColumnIndexOrThrow("_id"));
            String questionText = questionCursor.getString(questionCursor.getColumnIndexOrThrow("question"));

            // 添加题目TextView
            TextView tvQuestion = new TextView(this);
            tvQuestion.setText(questionIndex + ". " + questionText);
            tvQuestion.setTextSize(18);
            tvQuestion.setPadding(0, 0, 0, 8);
            questionContainer.addView(tvQuestion);

            // 创建RadioGroup
            RadioGroup radioGroup = new RadioGroup(this);
            radioGroup.setId(View.generateViewId());

            // 查询该题目的所有选项
            Cursor optionCursor = dbHelper.getReadableDatabase().query(
                    "options",
                    null,
                    "question_id=?",
                    new String[]{String.valueOf(questionId)},
                    null, null, null);

            List<String> options = new ArrayList<>();
            String correctOption = "";

            while (optionCursor.moveToNext()) {
                String optionText = optionCursor.getString(optionCursor.getColumnIndexOrThrow("option_text"));
                boolean isCorrect = optionCursor.getInt(optionCursor.getColumnIndexOrThrow("is_correct")) == 1;
                if (isCorrect) correctOption = optionText;

                RadioButton rb = new RadioButton(this);
                rb.setText(optionText);
                radioGroup.addView(rb);
            }
            optionCursor.close();

            // 记录这道题的正确答案
            correctAnswers.put(questionIndex, correctOption);

            // 保存RadioGroup以便后续获取用户选择
            radioGroup.setTag("question_" + questionIndex);
            questionContainer.addView(radioGroup);

            questionIndex++;
        }
        questionCursor.close();
    }

    private void checkAnswers() {
        double score = 0.0;
        StringBuilder resultDetails = new StringBuilder();

        for (int i = 1; i <= correctAnswers.size(); i++) {
            View view = questionContainer.findViewWithTag("question_" + i);
            if (view instanceof RadioGroup) {
                RadioGroup rg = (RadioGroup) view;
                int selectedId = rg.getCheckedRadioButtonId();
                String userAnswer = "未作答";
                String correct = correctAnswers.get(i);

                if (selectedId != -1) {
                    RadioButton selected = rg.findViewById(selectedId);
                    userAnswer = selected.getText().toString();
                    if (userAnswer.equals(correct)) {
                        score += 100.0/correctAnswers.size();
                    }
                }

                // 添加题目详情到StringBuilder
                resultDetails.append(i).append(". 正确答案: ").append(correct)
                        .append(", 你的答案: ").append(userAnswer).append("\n");
            }
        }

        // 创建Intent并传递数据到MainActivity3
        Intent intent = new Intent(this, MainActivity3.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("RESULT_DETAILS", resultDetails.toString());
        startActivity(intent);
    }
}