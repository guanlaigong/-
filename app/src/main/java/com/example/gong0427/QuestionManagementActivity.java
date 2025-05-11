package com.example.gong0427;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class QuestionManagementActivity extends AppCompatActivity {

    private QuestionDbHelper dbHelper;
    private ArrayAdapter<String> adapter;
    public ArrayList<String> questionList;
    private ArrayList<Long> idList; // 用于存储每个问题对应的ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_management);

        dbHelper = new QuestionDbHelper(this);
        questionList = new ArrayList<>();
        idList = new ArrayList<>(); // 初始化ID列表
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, questionList);

        ListView lvQuestions = findViewById(R.id.lv_questions);
        lvQuestions.setAdapter(adapter);
        lvQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEditDialog(position);
            }
        });

        Button btnAddQuestion = findViewById(R.id.btn_add_question);
        btnAddQuestion.setOnClickListener(v -> addQuestion());

        Button btnDeleteAllQuestions = findViewById(R.id.btn_delete_all_questions);
        btnDeleteAllQuestions.setOnClickListener(v -> deleteAllQuestions());

        loadQuestions();

        // 如果数据库为空，则插入默认题目
        if (questionList.isEmpty()) {
            insertDefaultQuestions();
        }
    }

    public void insertDefaultQuestions() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 题目1
        long q1Id = insertQuestion(db, "计算机中负责执行算术运算和逻辑运算的核心部件是？");
        insertOption(db, q1Id, "硬盘", false);
        insertOption(db, q1Id, "控制器", false);
        insertOption(db, q1Id, "运算器", true);   // 正确答案
        insertOption(db, q1Id, "显卡", false);

        // 题目2
        long q2Id = insertQuestion(db, "二进制数 1111 对应的十进制数是？");
        insertOption(db, q2Id, "10", false);
        insertOption(db, q2Id, "15", true);      // 正确答案
        insertOption(db, q2Id, "31", false);
        insertOption(db, q2Id, "8", false);

        // 题目3
        long q3Id = insertQuestion(db, "以下哪项属于操作系统的主要功能？");
        insertOption(db, q3Id, "文字处理", false);
        insertOption(db, q3Id, "进程调度", true);  // 正确答案
        insertOption(db, q3Id, "图像编辑", false);
        insertOption(db, q3Id, "病毒查杀", false);

        // 题目4
        long q4Id = insertQuestion(db, "HTTP协议默认使用的端口号是？");
        insertOption(db, q4Id, "21", false);
        insertOption(db, q4Id, "80", true);       // 正确答案
        insertOption(db, q4Id, "443", false);
        insertOption(db, q4Id, "3306", false);

        // 题目5
        long q5Id = insertQuestion(db, "以下数据结构中属于线性结构的是？");
        insertOption(db, q5Id, "树", false);
        insertOption(db, q5Id, "图", false);
        insertOption(db, q5Id, "链表", true);     // 正确答案
        insertOption(db, q5Id, "二叉树", false);

        Toast.makeText(this, "默认题目已加载", Toast.LENGTH_SHORT).show();
        loadQuestions();
    }

    private long insertQuestion(SQLiteDatabase db, String questionText) {
        ContentValues values = new ContentValues();
        values.put("question", questionText);
        return db.insert("questions", null, values);
    }

    private void insertOption(SQLiteDatabase db, long questionId, String optionText, boolean isCorrect) {
        ContentValues values = new ContentValues();
        values.put("question_id", questionId);
        values.put("option_text", optionText);
        values.put("is_correct", isCorrect ? 1 : 0);
        db.insert("options", null, values);
    }

    private void loadQuestions() {
        questionList.clear();
        idList.clear(); // 清空ID列表
        Cursor cursor = dbHelper.getReadableDatabase().query(
                "questions",
                new String[]{"_id", "question"},
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            idList.add(cursor.getLong(0)); // 添加题目ID到ID列表
            questionList.add(cursor.getString(1));
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void addQuestion() {
        EditText etNewQuestion = findViewById(R.id.et_new_question);
        EditText etOptionA = findViewById(R.id.et_option_a);
        EditText etOptionB = findViewById(R.id.et_option_b);
        EditText etOptionC = findViewById(R.id.et_option_c);
        EditText etOptionD = findViewById(R.id.et_option_d);


        String questionText = etNewQuestion.getText().toString();
        if (!questionText.isEmpty()) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues questionValues = new ContentValues();
            questionValues.put("question", questionText);
            long questionId = db.insert("questions", null, questionValues);

            saveOption(db, questionId, etOptionA.getText().toString(), ((RadioButton)findViewById(R.id.rb_option_a)).isChecked());
            saveOption(db, questionId, etOptionB.getText().toString(), ((RadioButton)findViewById(R.id.rb_option_b)).isChecked());
            saveOption(db, questionId, etOptionC.getText().toString(), ((RadioButton)findViewById(R.id.rb_option_c)).isChecked());
            saveOption(db, questionId, etOptionD.getText().toString(), ((RadioButton)findViewById(R.id.rb_option_d)).isChecked());

            etNewQuestion.setText("");
            etOptionA.setText("");
            etOptionB.setText("");
            etOptionC.setText("");
            etOptionD.setText("");

            loadQuestions();
            Toast.makeText(this, "题目已添加", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "请输入题目内容", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveOption(SQLiteDatabase db, long questionId, String optionText, boolean isCorrect) {
        if (!optionText.isEmpty()) {
            ContentValues optionValues = new ContentValues();
            optionValues.put("question_id", questionId);
            optionValues.put("option_text", optionText);
            optionValues.put("is_correct", isCorrect ? 1 : 0);
            db.insert("options", null, optionValues);
        }
    }

    private void deleteAllQuestions() {
        dbHelper.getWritableDatabase().delete("questions", null, null);
        loadQuestions();
    }

    private void showEditDialog(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(QuestionManagementActivity.this);
        builder.setTitle("编辑题目");

        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_question, null);
        final EditText inputQuestion = dialogView.findViewById(R.id.et_edit_question);
        final EditText inputOptionA = dialogView.findViewById(R.id.et_edit_option_a);
        final EditText inputOptionB = dialogView.findViewById(R.id.et_edit_option_b);
        final EditText inputOptionC = dialogView.findViewById(R.id.et_edit_option_c);
        final EditText inputOptionD = dialogView.findViewById(R.id.et_edit_option_d);
        final RadioButton rbOptionA = dialogView.findViewById(R.id.rb_edit_option_a);
        final RadioButton rbOptionB = dialogView.findViewById(R.id.rb_edit_option_b);
        final RadioButton rbOptionC = dialogView.findViewById(R.id.rb_edit_option_c);
        final RadioButton rbOptionD = dialogView.findViewById(R.id.rb_edit_option_d);

        // 加载当前题目的信息
        Long currentQuestionId = idList.get(position);
        Cursor cursor = dbHelper.getReadableDatabase().query(
                "questions",
                new String[]{"question"},
                "_id = ?",
                new String[]{String.valueOf(currentQuestionId)},
                null, null, null
        );
        if (cursor.moveToFirst()) {
            inputQuestion.setText(cursor.getString(0));
        }
        cursor.close();

        cursor = dbHelper.getReadableDatabase().query(
                "options",
                new String[]{"option_text", "is_correct"},
                "question_id = ?",
                new String[]{String.valueOf(currentQuestionId)},
                null, null, null
        );
        int index = 0;
        while (cursor.moveToNext()) {
            switch (index) {
                case 0:
                    inputOptionA.setText(cursor.getString(0));
                    rbOptionA.setChecked(cursor.getInt(1) == 1);
                    break;
                case 1:
                    inputOptionB.setText(cursor.getString(0));
                    rbOptionB.setChecked(cursor.getInt(1) == 1);
                    break;
                case 2:
                    inputOptionC.setText(cursor.getString(0));
                    rbOptionC.setChecked(cursor.getInt(1) == 1);
                    break;
                case 3:
                    inputOptionD.setText(cursor.getString(0));
                    rbOptionD.setChecked(cursor.getInt(1) == 1);
                    break;
            }
            index++;
        }
        cursor.close();

        builder.setView(dialogView);
        builder.setPositiveButton("保存", (dialog, which) -> {
            updateQuestion(position, inputQuestion.getText().toString(),
                    inputOptionA.getText().toString(), rbOptionA.isChecked(),
                    inputOptionB.getText().toString(), rbOptionB.isChecked(),
                    inputOptionC.getText().toString(), rbOptionC.isChecked(),
                    inputOptionD.getText().toString(), rbOptionD.isChecked());
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateQuestion(int position, String updatedQuestion,
                                String optionAText, boolean isOptionACorrect,
                                String optionBText, boolean isOptionBCorrect,
                                String optionCText, boolean isOptionCCorrect,
                                String optionDText, boolean isOptionDCorrect) {
        ContentValues values = new ContentValues();
        values.put("question", updatedQuestion);

        dbHelper.getWritableDatabase().update(
                "questions",
                values,
                "_id = ?",
                new String[]{String.valueOf(idList.get(position))}
        );

        dbHelper.getWritableDatabase().delete("options", "question_id = ?", new String[]{String.valueOf(idList.get(position))});
        saveOption(dbHelper.getWritableDatabase(), idList.get(position), optionAText, isOptionACorrect);
        saveOption(dbHelper.getWritableDatabase(), idList.get(position), optionBText, isOptionBCorrect);
        saveOption(dbHelper.getWritableDatabase(), idList.get(position), optionCText, isOptionCCorrect);
        saveOption(dbHelper.getWritableDatabase(), idList.get(position), optionDText, isOptionDCorrect);

        loadQuestions(); // 刷新列表
    }
}