package com.example.gong0427;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import java.util.Map;

public class UserManagementActivity extends AppCompatActivity {
    private LinearLayout userListContainer;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        userListContainer = findViewById(R.id.userListContainer);
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);  // 修改这里，使用相同的SharedPreferences名称
        
        Button btnAddUser = findViewById(R.id.btnAddUser);
        btnAddUser.setOnClickListener(v -> showAddUserDialog());

        loadUsers();
    }

    private void loadUsers() {
        userListContainer.removeAllViews();
        Map<String, ?> allUsers = sharedPreferences.getAll();
        
        for (Map.Entry<String, ?> entry : allUsers.entrySet()) {
            String username = entry.getKey();
            String password = entry.getValue().toString();
            
            View userView = LayoutInflater.from(this).inflate(R.layout.item_user, null);
            TextView tvUsername = userView.findViewById(R.id.tvUsername);
            Button btnEdit = userView.findViewById(R.id.btnEdit);
            Button btnDelete = userView.findViewById(R.id.btnDelete);
            
            tvUsername.setText(username);
            
            btnEdit.setOnClickListener(v -> showEditUserDialog(username, password));
            btnDelete.setOnClickListener(v -> deleteUser(username));
            
            userListContainer.addView(userView);
        }
    }

    private void showAddUserDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null);
        EditText etUsername = dialogView.findViewById(R.id.etUsername);
        EditText etPassword = dialogView.findViewById(R.id.etPassword);
        RadioGroup rgUserType = dialogView.findViewById(R.id.rgUserType);

        new AlertDialog.Builder(this)
            .setTitle("添加用户")
            .setView(dialogView)
            .setPositiveButton("确定", (dialog, which) -> {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                
                // 根据选择的用户类型添加前缀
                boolean isAdmin = rgUserType.getCheckedRadioButtonId() == R.id.rbAdmin;
                String prefixedUsername = isAdmin ? "admin_" + username : "user_" + username;
                
                if (!username.isEmpty() && !password.isEmpty()) {
                    addUser(prefixedUsername, password);
                } else {
                    Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private void showEditUserDialog(String username, String password) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null);
        EditText etUsername = dialogView.findViewById(R.id.etUsername);
        EditText etPassword = dialogView.findViewById(R.id.etPassword);
        
        etUsername.setText(username);
        etPassword.setText(password);

        new AlertDialog.Builder(this)
            .setTitle("编辑用户")
            .setView(dialogView)
            .setPositiveButton("确定", (dialog, which) -> {
                String newPassword = etPassword.getText().toString();
                if (!newPassword.isEmpty()) {
                    updateUser(username, newPassword);
                }
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private void addUser(String username, String password) {
        if (sharedPreferences.contains(username)) {
            Toast.makeText(this, "用户已存在", Toast.LENGTH_SHORT).show();
            return;
        }
        
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(username, password);
        editor.apply();
        
        loadUsers();
        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
    }

    private void updateUser(String username, String newPassword) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(username, newPassword);
        editor.apply();
        
        loadUsers();
        Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
    }

    private void deleteUser(String username) {
        new AlertDialog.Builder(this)
            .setTitle("删除用户")
            .setMessage("确定要删除用户 " + username + " 吗？")
            .setPositiveButton("确定", (dialog, which) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(username);
                editor.apply();
                
                loadUsers();
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("取消", null)
            .show();
    }
}