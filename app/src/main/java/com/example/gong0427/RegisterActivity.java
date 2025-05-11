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
//public class RegisterActivity extends AppCompatActivity {
//
//    private EditText etUsername, etPassword;
//    private Button btnRegister;
//    private RadioGroup rgRegisterType;
//    private RadioButton rbRegisterUser, rbRegisterAdmin;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
//
//        etUsername = findViewById(R.id.et_username);
//        etPassword = findViewById(R.id.et_password);
//        btnRegister = findViewById(R.id.btn_register);
//        rgRegisterType = findViewById(R.id.rg_register_type);
//        rbRegisterUser = findViewById(R.id.rb_register_user);
//        rbRegisterAdmin = findViewById(R.id.rb_register_admin);
//
//        btnRegister.setOnClickListener(v -> {
//            String username = etUsername.getText().toString();
//            String password = etPassword.getText().toString();
//
//            if (username.isEmpty() || password.isEmpty()) {
//                Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
//
//            // 检查是否已存在相同身份的账号
//            String existingUser = sharedPreferences.getString("username", "");
//            String existingAdmin = sharedPreferences.getString("admin_username", "");
//
//            if (rbRegisterUser.isChecked() && !existingUser.isEmpty()) {
//                Toast.makeText(this, "用户账户已存在，请勿重复注册", Toast.LENGTH_SHORT).show();
//                return; // 停止执行后续代码
//            }
//
//            if (rbRegisterAdmin.isChecked() && !existingAdmin.isEmpty()) {
//                Toast.makeText(this, "管理员账户已存在，请勿重复注册", Toast.LENGTH_SHORT).show();
//                return; // 停止执行后续代码
//            }
//
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//
//            if (rbRegisterUser.isChecked()) {
//                // 注册为普通用户
//                editor.putString("username", username);
//                editor.putString("password", password);
//            } else {
//                // 注册为管理员
//                editor.putString("admin_username", username);
//                editor.putString("admin_password", password);
//            }
//
//            editor.apply();
//            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
//
//            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
//            finish();
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

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnRegister;
    private RadioGroup rgRegisterType;
    private RadioButton rbRegisterUser, rbRegisterAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 初始化视图组件
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);
        rgRegisterType = findViewById(R.id.rg_register_type);
        rbRegisterUser = findViewById(R.id.rb_register_user);
        rbRegisterAdmin = findViewById(R.id.rb_register_admin);

        // 注册按钮点击事件处理
        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            boolean isUser = rbRegisterUser.isChecked();
            String userKeyPrefix = isUser ? "user_" : "admin_";
            String existingUserKey = userKeyPrefix + username;

            // 检查是否已经存在相同身份的账号
            if (sharedPreferences.contains(existingUserKey)) {
                Toast.makeText(this, (isUser ? "用户" : "管理员") + "账户已存在，请勿重复注册", Toast.LENGTH_SHORT).show();
                return; // 停止执行后续代码
            }

            // 注册为普通用户或管理员
            editor.putString(existingUserKey, password); // 使用用户名作为部分键值

            editor.apply(); // 应用更改
            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();

            // 跳转到登录页面
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }
}