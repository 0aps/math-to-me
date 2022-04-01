package com.mis.mathtome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import tk.pratanumandal.expr4j.ExpressionEvaluator;

public class HomeActivity extends AppCompatActivity {
    final Context self = this;
    final int MAX_COUNTER = 30;
    final private ArrayList<String> history = new ArrayList<>();
    private TextView textViewGenerated;
    private TextView textViewInput;
    private TextView textViewCounter;
    private String expression = "";
    private int tries = 0;
    private int score = 0;
    private double generatedNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        textViewGenerated = findViewById(R.id.textViewGenerated);
        textViewInput = findViewById(R.id.textViewInput);
        textViewCounter = findViewById(R.id.textViewCounter);

        textViewInput.setText(expression);
        countDownTimer.start();
        this.setRandomNumber();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            this.logout();
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
    }

    public void onNumberSelected(View view) {
        Button buttonNumber = (Button) view;
        expression += buttonNumber.getText().toString();
        textViewInput.setText(expression);
    }

    public void onRemoveNumber(View view) {
        expression = !expression.isEmpty() ? expression.substring(0, expression.length() - 1) : "";
        textViewInput.setText(expression);
    }

    public void onClearExpression(View view) {
        expression = "";
        textViewInput.setText(expression);
    }

    public void onTotalSelected(View view) {
        final String generatedExpression = textViewGenerated.getText().toString();
        if (expression.isEmpty() || expression.equals(generatedExpression)) return;

        textViewGenerated = findViewById(R.id.textViewGenerated);
        ExpressionEvaluator exprEval = new ExpressionEvaluator();
        double result;

        try {
            result = exprEval.evaluate(this.getExpression());
            if (result == generatedNumber) {
                score += 1;
            }
            history.add(String.format(Locale.getDefault(), "%s\t\t\t%s", generatedExpression, expression));
        } catch (Exception e) {
            showMessage("Esta operación es inválida, por favor revisa!");
        }

        this.setRandomNumber();
        expression = "";
        textViewInput.setText(expression);
        tries++;
    }

    private int getRandomNumber(int until) {
        return new Random().nextInt(until);
    }

    private String getRandomOperation() {
        String[] operations = {"+", "-", "*"};
        return operations[this.getRandomNumber(operations.length)];
    }

    private String getExpression() {
        return expression.replaceAll("×", "*")
                .replaceAll("÷", "/");
    }

    private void setRandomNumber() {
        ExpressionEvaluator exprEval = new ExpressionEvaluator();
        final int maxNumber = 30;
        int firstNumber = this.getRandomNumber(maxNumber);
        int secondNumber = this.getRandomNumber(maxNumber);
        int thirdNumber = this.getRandomNumber(maxNumber);
        String firstOperation = this.getRandomOperation();
        String secondOperation = this.getRandomOperation();
        String operation = String.format(Locale.getDefault(), "%d%s%d%s%d",
                firstNumber, firstOperation, secondNumber, secondOperation, thirdNumber);
        generatedNumber =  exprEval.evaluate(operation);
        textViewGenerated.setText(operation.replaceAll("\\*", "×"));
    }

    private void reset() {
        expression = "";
        textViewInput.setText(expression);
        tries = 0;
        score = 0;
        this.setRandomNumber();
        countDownTimer.start();
    }

    private void showMessage(String message) {
        new AlertDialog.Builder(self)
                .setTitle("¡Aviso!")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    reset();
                })
                .show();
    }

    CountDownTimer countDownTimer = new CountDownTimer(MAX_COUNTER * 1000, 1000) {
        @Override
        public void onTick(long milliseconds) {
            textViewCounter.setText(String.format(Locale.getDefault(), "%d", milliseconds / 1000));
        }

        @Override
        public void onFinish() {
            final String historyMessage =  String.join("\n", history);
            new AlertDialog.Builder(self)
                    .setTitle("Puntuación")
                    .setMessage(String.format("Has conseguido %s / %s. \n\n%s\n\n Intenta otra vez!", score, tries, historyMessage))
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                        reset();
                    })
                    .show();
        }
    };
}