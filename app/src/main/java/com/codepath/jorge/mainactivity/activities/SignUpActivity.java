package com.codepath.jorge.mainactivity.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.codepath.jorge.mainactivity.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

//todo open the name in 2, so they enter name last name
//todo do some validation checks
//todo ask for email
public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = "SignUpActivity";
    private EditText etUsername;
    private EditText etPassword;
    private EditText etRealName;
    private Button btnCreateAccount;
    private ImageView imageBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etRealName = findViewById(R.id.etRealName);
        imageBackground = findViewById(R.id.imageBackgroundSignUp);
        btnCreateAccount = findViewById(R.id.btnSignUp);

        Glide.with(this).load(R.drawable.giphy).into(imageBackground);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String name = etRealName.getText().toString();

                UserSignUp(username, password, name);
            }
        });
    }

    private void UserSignUp(String username, String password, String name) {

        ParseUser user = new ParseUser();

        user.setUsername(username);
        user.setPassword(password);
        user.put("name", name);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG,"There was a problem creating the account", e);
                    Toast.makeText(SignUpActivity.this, "There was a problem creating the account", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(SignUpActivity.this, "Successful Sign Up! Welcome to Sport Connect " + username + "!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();


            }
        });

    }
}
