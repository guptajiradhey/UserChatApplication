package com.example.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    public String mVerificationId;
    MaterialButton btnverify;
    EditText mobileno, etUsername;
    EditText otp;
    MaterialButton sendotp;
    boolean isotpsend = false;
    ProgressDialog pd;
    FirebaseUser user;
    String mno;
    TextView tvtimer;
    private FirebaseAuth mAuth;
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                pd.dismiss();
                otp.setText(code);
                verifyCode(code);

            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            pd.dismiss();
            Toast.makeText(LoginActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            sendotp.setEnabled(false);
            startTimer();
            mVerificationId = s;
            isotpsend = true;
            pd.dismiss();
            Toast.makeText(LoginActivity.this, "Verification Code Sent", Toast.LENGTH_SHORT).show();

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnverify = findViewById(R.id.btnVerify);
        mobileno = findViewById(R.id.etmobileno);
        otp = findViewById(R.id.etotp);
        sendotp = findViewById(R.id.tvsendotp);
        etUsername = findViewById(R.id.etname);
        tvtimer = findViewById(R.id.tvtimer);
        tvtimer.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendotptophone();
            }
        });
        btnverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isotpsend) {
                    Toast.makeText(LoginActivity.this, "First Request OTP", Toast.LENGTH_SHORT).show();
                    return;
                }
                String code = otp.getText().toString();
                if (code.isEmpty() || code.length() < 6) {
                    Toast.makeText(LoginActivity.this, "Please Enter OTP", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etUsername.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please Enter UserName", Toast.LENGTH_SHORT).show();
                    return;
                }

                verifyCode(code);
            }
        });
    }

    private void sendotptophone() {

        mno = mobileno.getText().toString();

        if (mno.isEmpty()) {
            Toast.makeText(this, "Mobile Number cannot be Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mno.length() != 10) {
            Toast.makeText(this, "Mobile Number should be 10 Digits", Toast.LENGTH_SHORT).show();
            return;
        }

        String phn_no = "+" + 91 + mno;
        SendVerificationCode(phn_no);
    }

    private void SendVerificationCode(String number) {
        pd = new ProgressDialog(this);
        pd.setMessage("Sending OTP Please Wait...");
        pd.setCancelable(false);
        pd.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                this,
                mCallBack);
    }

    private void startTimer() {

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {

                tvtimer.setVisibility(View.VISIBLE);

                tvtimer.setText("You can resend OTP in " + (millisUntilFinished / 1000) + " seconds.");

            }

            public void onFinish() {

                tvtimer.setVisibility(View.INVISIBLE);

                sendotp.setEnabled(true);

            }

        }.start();
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        signUpUserByCredential(credential);
    }

    private void signUpUserByCredential(PhoneAuthCredential credential) {
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait...");
        pd.show();

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    String Uid = mAuth.getCurrentUser().getUid();
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                    ProgressDialog pg = new ProgressDialog(LoginActivity.this);
                    pg.setMessage("Please Wait...");
                    pg.show();
                    myRef.child("Customer").child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            pg.dismiss();
                            if (snapshot.hasChild(Uid)) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {

                                creteUser();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                } else {
                    showErrorDialog(Objects.requireNonNull(task.getException()).getMessage());

                }
            }
        });
    }

    private void creteUser() {
        String Uid = mAuth.getCurrentUser().getUid();
        HashMap<String, Object> map = new HashMap<>();
        map.put("UserName", etUsername.getText().toString());
        map.put("PhoneNo", mno);
        map.put("UserId", Uid);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("Customer").child("Users").child(Uid).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Error " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showErrorDialog(String message) {
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle("")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.ic_baseline_phone_android_24)
                .show();
        pd.dismiss();
    }
}