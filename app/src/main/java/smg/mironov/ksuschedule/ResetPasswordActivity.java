package smg.mironov.ksuschedule;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smg.mironov.ksuschedule.API.ApiClient;
import smg.mironov.ksuschedule.API.ApiService;
import smg.mironov.ksuschedule.Utils.PasswordResetRequest;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;

    private TextView resetButton;

    private LinearLayout resetLayout;

    private LinearLayout successReset;

    private TextView toLoginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_screen);

        emailEditText = findViewById(R.id.emailEditText);
        resetButton = findViewById(R.id.reset_button_text);
        resetLayout = findViewById(R.id.resetLayout);
        successReset = findViewById(R.id.resetIsSuccessful);
        toLoginButton = findViewById(R.id.toLogin_ButtonText);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        toLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void reset() {
        String email = emailEditText.getText().toString();


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Некорректный email", Toast.LENGTH_SHORT).show();
            return;
        }

        PasswordResetRequest resetRequest = new PasswordResetRequest(email);
        sendResetResponse(resetRequest);
    }

    private void sendResetResponse(PasswordResetRequest resetRequest) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.requestPasswordReset(resetRequest);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    resetLayout.setVisibility(View.GONE);
                    successReset.setVisibility(View.VISIBLE);
                } else {
                    Log.e("API_ERROR", "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "Request failed: " + t.getMessage(), t);
            }
        });

    }
}
