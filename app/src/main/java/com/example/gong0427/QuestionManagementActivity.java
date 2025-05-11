package com.example.gong0427;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
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
        
        // 添加长按删除功能
        lvQuestions.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteConfirmDialog(position);
                return true;
            }
        });

        Button btnAddQuestion = findViewById(R.id.btn_add_question);
        btnAddQuestion.setOnClickListener(v -> addQuestion());

        Button btnDeleteAllQuestions = findViewById(R.id.btn_delete_all_questions);
        btnDeleteAllQuestions.setOnClickListener(v -> showDeleteAllConfirmDialog());

        Button btnUserManagement = findViewById(R.id.btnUserManagement);
        btnUserManagement.setOnClickListener(v -> {
            Intent intent = new Intent(QuestionManagementActivity.this, UserManagementActivity.class);
            startActivity(intent);
        });

        // 设置CheckBox点击事件
        setupCheckBoxes();
        
        // 加载题目
        loadQuestions();
        
        // 如果数据库为空，则插入默认题目
        if (questionList.isEmpty()) {
            insertDefaultQuestions();
        }
    }
    
    private void setupCheckBoxes() {
        CheckBox rbOptionA = findViewById(R.id.rb_option_a);
        CheckBox rbOptionB = findViewById(R.id.rb_option_b);
        CheckBox rbOptionC = findViewById(R.id.rb_option_c);
        CheckBox rbOptionD = findViewById(R.id.rb_option_d);
        
        // 设置点击事件，不需要特殊处理，CheckBox本身就支持点击切换
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

            saveOption(db, questionId, etOptionA.getText().toString(), ((CheckBox)findViewById(R.id.rb_option_a)).isChecked());
            saveOption(db, questionId, etOptionB.getText().toString(), ((CheckBox)findViewById(R.id.rb_option_b)).isChecked());
            saveOption(db, questionId, etOptionC.getText().toString(), ((CheckBox)findViewById(R.id.rb_option_c)).isChecked());
            saveOption(db, questionId, etOptionD.getText().toString(), ((CheckBox)findViewById(R.id.rb_option_d)).isChecked());

            etNewQuestion.setText("");
            etOptionA.setText("");
            etOptionB.setText("");
            etOptionC.setText("");
            etOptionD.setText("");
            
            // 重置CheckBox状态
            ((CheckBox)findViewById(R.id.rb_option_a)).setChecked(false);
            ((CheckBox)findViewById(R.id.rb_option_b)).setChecked(false);
            ((CheckBox)findViewById(R.id.rb_option_c)).setChecked(false);
            ((CheckBox)findViewById(R.id.rb_option_d)).setChecked(false);

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
    
    // 显示删除单个题目的确认对话框
    private void showDeleteConfirmDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除题目");
        builder.setMessage("确定要删除这个题目吗？");
        builder.setPositiveButton("确定", (dialog, which) -> {
            deleteQuestion(position);
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    
    // 删除单个题目
    private void deleteQuestion(int position) {
        long questionId = idList.get(position);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // 删除题目及其选项
        db.delete("options", "question_id = ?", new String[]{String.valueOf(questionId)});
        db.delete("questions", "_id = ?", new String[]{String.valueOf(questionId)});
        
        loadQuestions();
        Toast.makeText(this, "题目已删除", Toast.LENGTH_SHORT).show();
    }
    
    // 显示删除所有题目的确认对话框
    private void showDeleteAllConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除所有题目");
        builder.setMessage("确定要删除所有题目吗？");
        builder.setPositiveButton("确定", (dialog, which) -> {
            deleteAllQuestions();
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void deleteAllQuestions() {
        dbHelper.getWritableDatabase().delete("options", null, null);
        dbHelper.getWritableDatabase().delete("questions", null, null);
        loadQuestions();
        Toast.makeText(this, "所有题目已删除", Toast.LENGTH_SHORT).show();
    }

    private void showEditDialog(final int position) {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(QuestionManagementActivity.this);
            builder.setTitle("编辑题目");

            // 检查布局文件是否存在
            final View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_question, null);
            if (dialogView == null) {
                Toast.makeText(this, "加载编辑界面失败", Toast.LENGTH_SHORT).show();
                return;
            }

            final EditText inputQuestion = dialogView.findViewById(R.id.et_edit_question);
            final EditText inputOptionA = dialogView.findViewById(R.id.et_edit_option_a);
            final EditText inputOptionB = dialogView.findViewById(R.id.et_edit_option_b);
            final EditText inputOptionC = dialogView.findViewById(R.id.et_edit_option_c);
            final EditText inputOptionD = dialogView.findViewById(R.id.et_edit_option_d);
            final CheckBox rbOptionA = dialogView.findViewById(R.id.rb_edit_option_a);
            final CheckBox rbOptionB = dialogView.findViewById(R.id.rb_edit_option_b);
            final CheckBox rbOptionC = dialogView.findViewById(R.id.rb_edit_option_c);
            final CheckBox rbOptionD = dialogView.findViewById(R.id.rb_edit_option_d);

            // 检查position是否有效
            if (position < 0 || position >= idList.size()) {
                Toast.makeText(this, "无效的题目位置", Toast.LENGTH_SHORT).show();
                return;
            }

            // 加载当前题目的信息
            Long currentQuestionId = idList.get(position);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            
            try {
                Cursor cursor = db.query(
                        "questions",
                        new String[]{"question"},
                        "_id = ?",
                        new String[]{String.valueOf(currentQuestionId)},
                        null, null, null
                );

                if (cursor != null && cursor.moveToFirst()) {
                    inputQuestion.setText(cursor.getString(0));
                    cursor.close();
                }

                cursor = db.query(
                        "options",
                        new String[]{"option_text", "is_correct"},
                        "question_id = ?",
                        new String[]{String.valueOf(currentQuestionId)},
                        null, null, null
                );

                if (cursor != null) {
                    int index = 0;
                    while (cursor.moveToNext() && index < 4) {
                        String optionText = cursor.getString(0);
                        boolean isCorrect = cursor.getInt(1) == 1;

                        switch (index) {
                            case 0:
                                inputOptionA.setText(optionText);
                                rbOptionA.setChecked(isCorrect);
                                break;
                            case 1:
                                inputOptionB.setText(optionText);
                                rbOptionB.setChecked(isCorrect);
                                break;
                            case 2:
                                inputOptionC.setText(optionText);
                                rbOptionC.setChecked(isCorrect);
                                break;
                            case 3:
                                inputOptionD.setText(optionText);
                                rbOptionD.setChecked(isCorrect);
                                break;
                        }
                        index++;
                    }
                    cursor.close();
                }
            } catch (Exception e) {
                Toast.makeText(this, "加载题目数据失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            builder.setView(dialogView);
            builder.setPositiveButton("保存", (dialog, which) -> {
                try {
                    updateQuestion(position,
                            inputQuestion.getText().toString(),
                            inputOptionA.getText().toString(), rbOptionA.isChecked(),
                            inputOptionB.getText().toString(), rbOptionB.isChecked(),
                            inputOptionC.getText().toString(), rbOptionC.isChecked(),
                            inputOptionD.getText().toString(), rbOptionD.isChecked());
                } catch (Exception e) {
                    Toast.makeText(this, "保存失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());
            builder.setNeutralButton("删除", (dialog, which) -> {
                showDeleteConfirmDialog(position);
            });

            builder.show();
        } catch (Exception e) {
            Toast.makeText(this, "显示编辑对话框失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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