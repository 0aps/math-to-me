package com.mis.mathtome;

import androidx.appcompat.app.AppCompatActivity;

import tk.pratanumandal.expr4j.ExpressionEvaluator;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    final Context self = this;
    final int MAX_COUNTER = 30;
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
        setContentView(R.layout.activity_main);

        textViewGenerated = findViewById(R.id.textViewGenerated);
        textViewInput = findViewById(R.id.textViewInput);
        textViewCounter = findViewById(R.id.textViewCounter);

        textViewInput.setText(expression);
        countDownTimer.start();
        this.setRandomNumber();
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
        if (expression.isEmpty() || expression.equals(Double.toString(generatedNumber))) return;

        textViewGenerated = findViewById(R.id.textViewGenerated);
        ExpressionEvaluator exprEval = new ExpressionEvaluator();
        double result;

        try {
            result = exprEval.evaluate(this.getExpression());
            if (result == generatedNumber) {
                score += 1;
            }
        } catch (Exception e) {
            showMessage("Esta operación es inválida, por favor revisa!");
        }

        this.setRandomNumber();
        expression = "";
        textViewInput.setText(expression);
        tries++;
    }

    private double getRandomNumber() {
        return Math.ceil(Math.random() * 100 + 1);
    }

    private String getExpression() {
        return expression.replaceAll("×", "*")
                .replaceAll("÷", "/");
    }

    private void setRandomNumber() {
        generatedNumber = this.getRandomNumber();
        textViewGenerated.setText(String.format(Locale.getDefault(), "%.0f", generatedNumber));
    }

    private void reset() {
        expression = "";
        textViewInput.setText(expression);
        tries = 0;
        score = 0;
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
            new AlertDialog.Builder(self)
                    .setTitle("Puntuación")
                    .setMessage(String.format("Has conseguido %s / %s. Intenta otra vez!", score, tries))
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                        reset();
                    })
                    .show();
        }
    };
}