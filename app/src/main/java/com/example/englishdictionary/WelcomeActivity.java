package com.example.englishdictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class WelcomeActivity extends AppCompatActivity {
    private TextView sign_up, sign_in;
    private RelativeLayout sign_up_layout, sign_in_layout;
    private Button confirm_sign_up_btn, confirm_sign_in_btn;
    private EditText sign_in_email_field, sign_in_password_field,
            sign_up_nickname_field, sign_up_email_field, sign_up_password_field, sign_up_confirm_password_field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        WorkWithDB db = new WorkWithDB(this);

        sign_up = findViewById(R.id.sign_up);
        sign_up_layout = findViewById(R.id.sign_up_layout);
        confirm_sign_up_btn = findViewById(R.id.confirm_sign_up);
        sign_up_nickname_field = findViewById(R.id.sign_up_nickname_field);
        sign_up_email_field = findViewById(R.id.sign_up_email_field);
        sign_up_password_field = findViewById(R.id.sign_up_password_field);
        sign_up_confirm_password_field = findViewById(R.id.sign_up_confirm_password_field);


        sign_in = findViewById(R.id.sign_in);
        sign_in_layout = findViewById(R.id.sign_in_layout);
        confirm_sign_in_btn = findViewById(R.id.confirm_sign_in);
        sign_in_email_field = findViewById(R.id.sign_in_email_field);
        sign_in_password_field = findViewById(R.id.sign_in_password_field);

        //TODO swap from getResources().getColor(R.color.white) to another technology
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sign_in_layout.setVisibility(View.INVISIBLE);
                sign_in.setTextColor(getResources().getColor(R.color.white));
                sign_up_layout.setVisibility(View.VISIBLE);
                sign_up.setTextColor(getResources().getColor(R.color.teal_200));
            }
        });

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sign_up_layout.setVisibility(View.INVISIBLE);
                sign_up.setTextColor(getResources().getColor(R.color.white));
                sign_in_layout.setVisibility(View.VISIBLE);
                sign_in.setTextColor(getResources().getColor(R.color.teal_200));
            }
        });

        confirm_sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sign_up_nickname_field.getText().toString().length() < 2) {
                    Toast.makeText(WelcomeActivity.this, "Имя должно содержать больше 1 символа", Toast.LENGTH_SHORT).show();
                } else if (db.getEmails().contains(sign_up_email_field.getText().toString())) {
                    Toast.makeText(WelcomeActivity.this, "Этот email уже зарегестрирован", Toast.LENGTH_SHORT).show();
                } else if (sign_up_password_field.getText().toString().length() < 6) {
                    Toast.makeText(WelcomeActivity.this, "Пароль должен содержать больше 5 символов", Toast.LENGTH_SHORT).show();
                } else if (!sign_up_password_field.getText().toString().equals(sign_up_confirm_password_field.getText().toString())) {
                    Toast.makeText(WelcomeActivity.this, "Поле пароль и поле подтверждения пароля не совпадают", Toast.LENGTH_SHORT).show();
                } else {
                    db.createNewUser(
                            sign_up_nickname_field.getText().toString(),
                            sign_up_email_field.getText().toString(),
                            sign_up_password_field.getText().toString(),
                            1,
                            10,
                            1,
                            1
                    );
                    ArrayList<Object[]> takenUsers = db.getAllUsers();
                    System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE->");
                    for (int i = 0; i < takenUsers.size(); i++) {
                        System.out.println("User number " + i + " have this\n" +
                            "user_id" + takenUsers.get(i)[0] + "\n" +
                            "nickname" + takenUsers.get(i)[1] + "\n" +
                            "email" + takenUsers.get(i)[2] + "\n" +
                            "password" + takenUsers.get(i)[3] + "\n" +
                            "last_learned_word" + takenUsers.get(i)[4] + "\n" +
                            "count_words_per_time" + takenUsers.get(i)[5] + "\n" +
                            "is_the_music_on" + takenUsers.get(i)[6] + "\n" +
                            "is_online_now" + takenUsers.get(i)[7] + "\n");
                    }
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        confirm_sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!db.getEmails().contains(sign_in_email_field.getText().toString())) {
                    Toast.makeText(WelcomeActivity.this, "Такой email не зарегистрирован", Toast.LENGTH_SHORT).show();
                } else if (sign_in_password_field.getText().toString().length() < 6) {
                    Toast.makeText(WelcomeActivity.this, "Заведомо ложный пароль", Toast.LENGTH_SHORT).show();
                } else {
                    int userWithThatEmail = db.getUserIdByEmail(sign_in_email_field.getText().toString());
                    HashMap<String, Object> userInfo = db.getUserInfo(userWithThatEmail);
                    if (!userInfo.get("password").equals(sign_in_password_field.getText().toString())) {
                        Toast.makeText(WelcomeActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                    } else {
                        db.putUserToOnline(userInfo.get("email").toString());
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }
}