package com.rmsi.android.mast.util;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.adapter.SpinnerAdapter;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Option;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Contains various GUI functions
 */
public class GuiUtility {

    /**
     * Appends LinearLayout with provided attributes
     * @param layout Layout to append
     * @param attributes List of attributes
     */
    public static void appendLayoutWithAttributes(LinearLayout layout, List<Attribute> attributes){
        if(attributes != null && attributes.size() > 0) {
            LayoutInflater inflater = (LayoutInflater) layout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (Attribute attr : attributes) {
                layout.addView(createViewFromAttribute(attr, inflater));
            }
        }
    }

    /**
     * Create View item based on provided attribute
     *
     * @param attribute Attribute to be used for creating View item
     * @param inflater Layout inflater to be used for getting appropriate layout
     */
    public static View createViewFromAttribute(Attribute attribute, LayoutInflater inflater) {
        View container = null;

        if (attribute.getControlType() == 1) //Edittext(string)
        {
            container = inflater.inflate(R.layout.item_edit_text, null, false);
            attribute.setView(createInputRow(container, attribute));
        } else if (attribute.getControlType() == 2)  //Date
        {
            container = inflater.inflate(R.layout.item_date, null, false);
            attribute.setView(createTimePickerRow(container, attribute));
        } else if (attribute.getControlType() == 3)  //Boolean
        {
            container = inflater.inflate(R.layout.item_spinner, null, false);
            attribute.setView(createSpinnerViewForBoolean(container, attribute));
        } else if (attribute.getControlType() == 4)  //Edittext(Numeric)
        {
            container = inflater.inflate(R.layout.item_edittext_numeric, null, false);
            attribute.setView(createInputRow(container, attribute));
        } else if (attribute.getControlType() == 5)  //spinner
        {
            container = inflater.inflate(R.layout.item_spinner, null, false);
            attribute.setView(createSpinnerViewFromArray(container, attribute));
        }
        return container;
    }

    private static View createInputRow(View container, final Attribute attribute) {
        TextView field = (TextView) container.findViewById(R.id.field);
        field.setText(attribute.getAttributeName());
        final EditText fieldValue = (EditText) container.findViewById(R.id.fieldValue);
        fieldValue.setTag(attribute.getAttributeid());

        if (CommonFunctions.getRoleID() == 2) {
            fieldValue.setEnabled(false);
        }

        if (attribute.getFieldValue() != null) {
            fieldValue.setText(attribute.getFieldValue(), TextView.BufferType.EDITABLE);
        } else {
            fieldValue.setEnabled(true);
            fieldValue.setText("", TextView.BufferType.EDITABLE);
        }

        fieldValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    try {
                        EditText editTxt = (EditText) v;
                        Integer attribId = (Integer) editTxt.getTag();
                        if (attribute.getAttributeid() == attribId) {
                            attribute.setFieldValue(editTxt.getText().toString());
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });

        fieldValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Integer attribId = (Integer) fieldValue.getTag();
                    if (attribute.getAttributeid() == attribId) {
                        attribute.setFieldValue(s.toString());
                    }
                } catch (Exception e) {
                }
            }
        });
        return fieldValue;
    }

    private static View createTimePickerRow(View container, final Attribute attribute) {
        TextView field = (TextView) container.findViewById(R.id.field);
        field.setText(attribute.getAttributeName());

        final TextView textDatePicker = (TextView) container.findViewById(R.id.textview_datepicker);
        textDatePicker.setTag(attribute.getAttributeid());

        if (CommonFunctions.getRoleID() == 2) {
            textDatePicker.setEnabled(false);
        }

        if (!StringUtility.isEmpty(attribute.getFieldValue())) {
            textDatePicker.setText(DateUtility.formatDateString(attribute.getFieldValue()));
        }

        textDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(textDatePicker, attribute.getFieldValue());
            }
        });

        textDatePicker.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Integer attribId = (Integer) textDatePicker.getTag();
                    if (attribute.getAttributeid() == attribId) {
                        attribute.setFieldValue(s.toString());
                    }
                } catch (Exception e) {
                }
            }
        });

        return textDatePicker;
    }

    /**
     * Shows custom date picker, attached to the TextView control
     * @param textView TextView control to attach date picker to
     * @param dateString Initial date string to display
     */
    public static void showDatePicker(final TextView textView, String dateString){
        final Dialog customTimePicker = new Dialog(textView.getContext(), R.style.DialogTheme);
        customTimePicker.setTitle("Select Date");
        customTimePicker.setContentView(R.layout.dialog_time_picker);
        customTimePicker.getWindow().getAttributes().width = ViewGroup.LayoutParams.MATCH_PARENT;
        final DatePicker datepicker = (DatePicker) customTimePicker.findViewById(R.id.datePicker);

        Button btnSet = (Button) customTimePicker.findViewById(R.id.button_set);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calender = Calendar.getInstance();
            if (!StringUtility.isEmpty(dateString)) {
                calender.setTime(sdf.parse(dateString));
            }

            int day = calender.get(Calendar.DAY_OF_MONTH);
            String strday = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);

            int month = calender.get(Calendar.MONTH) + 1;
            String strmonth = month < 10 ? "0" + String.valueOf(month) : String.valueOf(month);

            int year = calender.get(Calendar.YEAR);
            datepicker.init(year, month-1, day, null);

            if (!StringUtility.isEmpty(dateString))
                textView.setText(new StringBuilder()
                        .append(year).append("-").append(strmonth).append("-").append(strday));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        customTimePicker.show();

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = datepicker.getDayOfMonth();
                String strday = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);
                int month = datepicker.getMonth() + 1;
                String strmonth = month < 10 ? "0" + String.valueOf(month) : String.valueOf(month);
                int year = datepicker.getYear();

                textView.setText(new StringBuilder()
                        .append(year).append("-").append(strmonth).append("-").append(strday));
                customTimePicker.dismiss();
            }
        });
    }

    private static Spinner createSpinnerViewFromArray(View container, final Attribute attribute) {
        TextView fieldAlias = (TextView) container.findViewById(R.id.field);
        final Spinner spinner = (Spinner) container.findViewById(R.id.spinner1);
        fieldAlias.setText(attribute.getAttributeName());
        spinner.setPrompt(attribute.getAttributeName());
        spinner.setTag(attribute.getAttributeid());

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(
                container.getContext(),
                android.R.layout.simple_spinner_item,
                attribute.getOptionsList());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        if (CommonFunctions.getRoleID() == 2)  // Hardcoded Id for Role (1=Trusted Intermediary, 2=Adjudicator)
        {
            spinner.setEnabled(false);
        }

        boolean isPersonExist = CommonFunctions.personExist(attribute.getFEATURE_ID());
        String fieldValue = attribute.getFieldValue();

        if (attribute.getAttributeid() == 31) {
            if (isPersonExist) {
                spinner.setEnabled(false);
            }
            boolean isnonNatural = CommonFunctions.isNonNatural(attribute.getFEATURE_ID());
            if (isnonNatural) {
                fieldValue = "106";
                spinner.setEnabled(false);
            }
        }

        if (attribute.getAttributeid() == 23) {
            String residentValue = CommonFunctions.getResidentValue(attribute.getFEATURE_ID());
            if (residentValue.equalsIgnoreCase("Yes")) {
                fieldValue = "29";
                spinner.setEnabled(false);
            } else if (residentValue.equalsIgnoreCase("No")) {
                fieldValue = "30";
                spinner.setEnabled(false);
            } else {
                fieldValue = "Select an option";
                spinner.setEnabled(false);
            }
        }

        if (fieldValue != null && fieldValue != "" && !fieldValue.equalsIgnoreCase("Select an option")) {
            int currentValue = Integer.parseInt(fieldValue);
            spinner.setSelection(spinnerAdapter.getPosition(currentValue));
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Option selecteditem = (Option) spinner.getSelectedItem();
                attribute.setFieldValue(selecteditem.getOptionId().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        return spinner;
    }

    private static Spinner createSpinnerViewForBoolean(View container, final Attribute attribute) {
        TextView fieldAlias = (TextView) container.findViewById(R.id.field);

        final Spinner spinner = (Spinner) container.findViewById(R.id.spinner1);
        fieldAlias.setText(attribute.getAttributeName());
        spinner.setPrompt(attribute.getAttributeName());

        if (CommonFunctions.getRoleID() == 2) {
            spinner.setEnabled(false);
        }

        String[] list = container.getContext().getResources().getStringArray(R.array.booleanControlValues);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(container.getContext(), android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        if (attribute.getFieldValue() != null && attribute.getFieldValue() != ""
                && (attribute.getFieldValue() != "Select an option"
                || !attribute.getFieldValue().equalsIgnoreCase("Chagua chaguo"))) {
            if (attribute.getFieldValue().equalsIgnoreCase("yes") || attribute.getFieldValue().equalsIgnoreCase("Ndiyo"))
                spinner.setSelection(0);
            if (attribute.getFieldValue().equalsIgnoreCase("no") || attribute.getFieldValue().equalsIgnoreCase("Hapana"))
                spinner.setSelection(1);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String selecteditem = (String) spinner.getSelectedItem();
                attribute.setFieldValue(selecteditem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        return spinner;
    }

    /**
     * Validates provided list of attributes and highlights underlying control in case of missing values in the mandatory fields
     * @param attributeList Attribute to validate;
     */
    public static boolean validateAttributes(List<Attribute> attributeList){
        boolean isValid = true;
        for (Attribute attribute : attributeList){
            if(!validateAttribute(attribute)){
                isValid = false;
            }
        }
        return isValid;
    }

    /**
     * Validates provided attribute and highlights underlying control in case of missing value in the mandatory field
     * @param attribute Attribute to validate;
     */
    public static boolean validateAttribute(Attribute attribute){
        String value = "";
        boolean isValid = true;
        String hasValidation = attribute.getValidation();

        if (attribute.getControlType() == 1) {
            if (attribute.getView() != null) {
                // edit text
                EditText editText = (EditText) attribute.getView();
                value = editText.getText().toString();
                if (hasValidation.equalsIgnoreCase("true") && value.isEmpty()) {
                    isValid = false;
                    attribute.setFieldValue(null);
                } else if (!value.isEmpty()) {
                    attribute.setFieldValue(value);
                } else {
                    attribute.setFieldValue(null);
                }
            } else if (hasValidation.equalsIgnoreCase("true")) {
                isValid = false;
                attribute.setFieldValue(null);
            }
        } else if (attribute.getControlType() == 2) {
            if (attribute.getView() != null) {
                // edit text
                TextView textview = (TextView) attribute.getView();
                value = textview.getText().toString();
                if (hasValidation.equalsIgnoreCase("true") && value.isEmpty()) {
                    isValid = false;
                    attribute.setFieldValue(null);
                } else if (!value.isEmpty()) {
                    attribute.setFieldValue(value);
                } else {
                    attribute.setFieldValue(null);
                }
            } else if (hasValidation.equalsIgnoreCase("true")) {
                isValid = false;
                attribute.setFieldValue(null);
            }
        } else if (attribute.getControlType() == 3) {
            if (attribute.getView() != null) // No Validation as boolean has only Yes OR No
            {
                Spinner spinner = (Spinner) attribute.getView();
                String selecteditem = (String) spinner.getSelectedItem();
                attribute.setFieldValue(selecteditem);
            } else if (hasValidation.equalsIgnoreCase("true")) {
                isValid = false;
                attribute.setFieldValue(null);
            }
        } else if (attribute.getControlType() == 4) {
            if (attribute.getView() != null) {
                // edit text(Numeric)
                EditText editText = (EditText) attribute.getView();
                value = editText.getText().toString();

                if (hasValidation.equalsIgnoreCase("true") && value.isEmpty()) {
                    isValid = false;
                    attribute.setFieldValue(null);
                } else if (!value.isEmpty()) {
                    attribute.setFieldValue(value);
                } else {
                    attribute.setFieldValue(null);
                }
            } else if (hasValidation.equalsIgnoreCase("true")) {
                isValid = false;
                attribute.setFieldValue(null);
            }
        } else if (attribute.getControlType() == 5) {
            if (attribute.getView() != null) {
                // drop down spinner
                Spinner spinner = (Spinner) attribute.getView();
                Option selecteditem = (Option) spinner.getSelectedItem();

                if (hasValidation.equalsIgnoreCase("true") && selecteditem.getOptionId() == 0) {
                    isValid = false;
                    attribute.setFieldValue(null);
                } else if (selecteditem.getOptionId() != 0) {
                    attribute.setFieldValue(selecteditem.getOptionId().toString());
                } else {
                    attribute.setFieldValue(null);
                }
            } else if (hasValidation.equalsIgnoreCase("true")) {
                isValid = false;
                attribute.setFieldValue(null);
            }
        }

        if(!isValid){
            attribute.getView().setBackgroundColor(attribute.getView().getContext().getResources().getColor(R.color.lightred));
        } else {
            if(attribute.getInitialBackground()!=null){
                attribute.getView().setBackground(attribute.getInitialBackground());
            } else {
                attribute.getView().setBackgroundColor(attribute.getView().getContext().getResources().getColor(R.color.white));
            }
        }
        return isValid;
    }
}
