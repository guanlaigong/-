//package com.example.gong0427;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class LoginActivity extends AppCompatActivity {
//
//    private EditText etLoginUsername, etLoginPassword;
//    private Button btnLogin, btnGotoRegister;
//
//    private RadioGroup rgLoginType;
//    private RadioButton rbUser, rbAdmin;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        etLoginUsername = findViewById(R.id.et_login_username);
//        etLoginPassword = findViewById(R.id.et_login_password);
//        btnLogin = findViewById(R.id.btn_login);
//        btnGotoRegister = findViewById(R.id.btn_goto_register);
//        rgLoginType = findViewById(R.id.rg_login_type);
//        rbUser = findViewById(R.id.rb_user);
//        rbAdmin = findViewById(R.id.rb_admin);
//
//        // 登录按钮逻辑
//
//        btnLogin.setOnClickListener(v -> {
//            String username = etLoginUsername.getText().toString();
//            String password = etLoginPassword.getText().toString();
//
//            if (username.isEmpty() || password.isEmpty()) {
//                Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
//
//            String savedUsername, savedPassword;
//
//            if (rbUser.isChecked()) {
//                // 普通用户登录
//                savedUsername = sharedPreferences.getString("username", "");
//                savedPassword = sharedPreferences.getString("password", "");
//            } else {
//                // 管理员登录
//                savedUsername = sharedPreferences.getString("admin_username", "");
//                savedPassword = sharedPreferences.getString("admin_password", "");
//            }
//
//            if (username.equals(savedUsername) && password.equals(savedPassword)) {
//                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(LoginActivity.this, MainActivity.class));
////                if (rbAdmin.isChecked()) {
////                    startActivity(new Intent(LoginActivity.this, UserManagementActivity.class));
////                } else {
////                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
////                }
//                finish();
//            } else {
//                Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        // 跳转到注册页面的逻辑
//        btnGotoRegister.setOnClickListener(v -> {
//            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
//        });
//    }
//}

package com.example.gong0427;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnGotoRegister;
    private RadioGroup rgLoginType;
    private RadioButton rbUser, rbAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化视图组件
        etUsername = findViewById(R.id.et_login_username);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);
        btnGotoRegister = findViewById(R.id.btn_goto_register);
        rgLoginType = findViewById(R.id.rg_login_type);
        rbUser = findViewById(R.id.rb_user);
        rbAdmin = findViewById(R.id.rb_admin);

        // 登录按钮点击事件处理
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);

            boolean isUser = rbUser.isChecked();
            String userKeyPrefix = isUser ? "user_" : "admin_";
            String userKey = userKeyPrefix + username;

            // 获取存储的密码
            String storedPassword = sharedPreferences.getString(userKey, null);

            if (storedPassword == null) {
                Toast.makeText(this, (isUser ? "用户" : "管理员") + "账户不存在", Toast.LENGTH_SHORT).show();
                return;
            }

            if (storedPassword.equals(password)) {
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();

                // 在登录按钮点击事件处理中替换原有的跳转逻辑
                if (rbAdmin.isChecked()) {
                    // 跳转到题目管理页面
                    startActivity(new Intent(LoginActivity.this, QuestionManagementActivity.class));
                } else {
                    // 跳转到主页面
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                finish();
            } else {
                Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            }
        });

        // 注册跳转按钮点击事件处理
        btnGotoRegister.setOnClickListener(v -> {
            // 跳转到注册页面
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
}