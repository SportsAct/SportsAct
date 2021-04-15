package com.codepath.jorge.mainactivity.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.jorge.mainactivity.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button bntCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        bntCreateAccount = findViewById(R.id.btnCreateAccount);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                UserLogin(username, password);
            }
        });

        bntCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = bntCreateAccount.getText().toString();
                String password = bntCreateAccount.getText().toString();
                // Create the ParseUser
                ParseUser user1 = new ParseUser();
                // Set core propertiesg
                user1.setUsername("KenSmith");
                user1.setPassword("secret123");
                user1.setEmail("email@example.com");
                // Set custom properties
                //user1.put("phone", "650-253-0000");
                user1.getParseUser(user1.getUsername());


               // String username = bntCreateAccount.getText().toString();
                //String password = bntCreateAccount.getText().toString();

                UserLogin(username, password);

                // Invoke signUpInBackground
                user1.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // Hooray! Let them use the app now.
                            goMainActivity();
                            user1.logInInBackground("KenSmith", "secret123", new LogInCallback() {
                                public void done(ParseUser user, ParseException e) {
                                    if (user != null) {
                                        // Hooray! The user is logged in
                                        goMainActivity();
                                    } else {
                                        // Signup failed. Look at the ParseException to see what happened.
                                    }
                                }

                            });
                        } else {
                            // Sign up didn't succeed. Look at the ParseException
                            // to figure out what went wrong

                        }
                    }
                });
            }
        });
    }

    private void UserLogin(String username, String password) {
        Log.i(TAG, "Attempting to login user" + username);

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with login", e);
                    return;
                }

                //TODO: navigate to main activity if the user has signed in properly

                goMainActivity();
                Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
