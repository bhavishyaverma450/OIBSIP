package com.example.unitconverterapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private Spinner categorySpinner, fromSpinner, toSpinner;
    private EditText valueInput;
    private TextView resultText;
    private Button convertButton;

    private final String[] categories = {
            "Length", "Mass", "Temperature"
    };

    private final String[] lengthUnits = {
            "Meters", "Kilometers", "Centimeters",
            "Millimeters", "Miles", "Feet", "Inches"
    };

    private final String[] weightUnits = {
            "Kilograms", "Grams", "Milligrams",
            "Pounds", "Ounces"
    };

    private final String[] temperatureUnits = {
            "Celsius", "Fahrenheit", "Kelvin"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categorySpinner = findViewById(R.id.categorySpinner);
        fromSpinner = findViewById(R.id.fromSpinner);
        toSpinner = findViewById(R.id.toSpinner);
        valueInput = findViewById(R.id.valueInput);
        resultText = findViewById(R.id.resultText);
        convertButton = findViewById(R.id.convertButton);

        setSpinnerAdapter(categorySpinner, categories);

        categorySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {
                        updateUnits(position);
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                });

        convertButton.setOnClickListener(v -> convertValue());
    }

    private void setSpinnerAdapter(
            Spinner spinner, String[] units) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                units
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
    }

    private void updateUnits(int categoryPosition) {
        String[] units;

        if (categoryPosition == 0) {
            units = lengthUnits;
        } else if (categoryPosition == 1) {
            units = weightUnits;
        } else {
            units = temperatureUnits;
        }

        setSpinnerAdapter(fromSpinner, units);
        setSpinnerAdapter(toSpinner, units);

        fromSpinner.setSelection(0);
        toSpinner.setSelection(1);
        resultText.setText("Your result will appear here");
    }

    private void convertValue() {
        String input = valueInput.getText().toString().trim();

        if (input.isEmpty()) {
            valueInput.setError("Please enter a value");
            Toast.makeText(this, "Please enter a number",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        double value;

        try {
            value = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            valueInput.setError("Invalid input");
            Toast.makeText(this, "Invalid input",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (Double.isNaN(value) || Double.isInfinite(value)) {
            valueInput.setError("Enter a finite number");
            return;
        }

        int category = categorySpinner.getSelectedItemPosition();
        String from = fromSpinner.getSelectedItem().toString();
        String to = toSpinner.getSelectedItem().toString();

        double result;

        if (category == 0) {
            result = convertLength(value, from, to);
        } else if (category == 1) {
            result = convertWeight(value, from, to);
        } else {
            result = convertTemperature(value, from, to);
        }

        if (Double.isNaN(result) || Double.isInfinite(result)) {
            return;
        }

        DecimalFormat formatter = new DecimalFormat("0.######");
        resultText.setText(formatter.format(result) + " " + to);
        valueInput.setError(null);
    }

    private double convertLength(
            double value, String from, String to) {
        double meters = value * lengthFactor(from);
        return meters / lengthFactor(to);
    }

    private double lengthFactor(String unit) {
        switch (unit) {
            case "Kilometers": return 1000.0;
            case "Centimeters": return 0.01;
            case "Millimeters": return 0.001;
            case "Miles": return 1609.344;
            case "Feet": return 0.3048;
            case "Inches": return 0.0254;
            default: return 1.0;
        }
    }

    private double convertWeight(
            double value, String from, String to) {
        double kilograms = value * weightFactor(from);
        return kilograms / weightFactor(to);
    }

    private double weightFactor(String unit) {
        switch (unit) {
            case "Grams": return 0.001;
            case "Milligrams": return 0.000001;
            case "Pounds": return 0.45359237;
            case "Ounces": return 0.028349523125;
            default: return 1.0;
        }
    }

    private double convertTemperature(
            double value, String from, String to) {
        double celsius;

        switch (from) {
            case "Fahrenheit":
                celsius = (value - 32.0) * 5.0 / 9.0;
                break;
            case "Kelvin":
                celsius = value - 273.15;
                break;
            default:
                celsius = value;
        }

        if (celsius < -273.15) {
            Toast.makeText(this,
                    "Temperature cannot be below absolute zero",
                    Toast.LENGTH_LONG).show();
            return Double.NaN;
        }

        switch (to) {
            case "Fahrenheit":
                return celsius * 9.0 / 5.0 + 32.0;
            case "Kelvin":
                return celsius + 273.15;
            default:
                return celsius;
        }
    }
}
