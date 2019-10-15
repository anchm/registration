package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_NAME = "name";
    public static final String APP_PREFERENCES_SECONDNAME = "secondName";
    public static final String APP_PREFERENCES_PASSWORD = "password ";
    public static final String APP_PREFERENCES_DATA = "data";

    private SharedPreferences mSettings;
    enum field { FIRST_NAME, SECOND_NAME, DATA, PASSWORD, RETRY_PASSWORD };
    // Объявляем об использовании следующих объектов:
    private EditText firstName;
    private EditText secondName;
    private EditText password;
    private EditText retryPassword;
    private EditText data;
    private TextView textForError;
    private Button registration;

    private static String patternPassword="^(?=.*[0-9].*)(?=.*[a-z].*)(?=.*[A-Z].*)[0-9a-zA-Z]{8,}$";//(Строчные и прописные латинские буквы, цифры)
    private static String patternName="^[a-zA-Z][a-zA-Z]{1,20}$";
    private static String  patternSecondName="[a-zA-Z][a-zA-Z]{1,20}$";
    private static String patternData="\\d{1,2}/\\d{1,2}/\\d{4}";

    SparseArray<String> arrayWithError = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (mSettings.contains(APP_PREFERENCES_NAME) && mSettings.contains(APP_PREFERENCES_PASSWORD)) {//проверка на наличие записи
            // Получаем число из настроек
            Intent intent = new Intent(MainActivity.this,Second.class);
            intent.putExtra("name",mSettings.getString(APP_PREFERENCES_NAME,""));
            startActivity(intent);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Связываемся с элементами нашего интерфейса:
        findViews();

        fillMapWithError();

        createCalendar();

        registration.setEnabled(false);

        setActivityButtonRegistrarion();
    }

    // Обрабатываем нажатие кнопки "Войти":
    public void Login(View view) {
        boolean[] res = new boolean[5];
        getData(res);

        if(isDataCorrect(res)){
            Toast.makeText(getApplicationContext(), "Logged in!",Toast.LENGTH_SHORT).show();

            saveSettings();

            // Выполняем переход на другой экран:
            Intent intent = new Intent(MainActivity.this,Second.class);
            intent.putExtra("name",firstName.getText().toString());
            startActivity(intent);
        }
        else{
            String str = "";
            for(int i=0; i<res.length; i++){
                if(!res[i]) str += arrayWithError.get(i) + ", ";
            }
            textForError.setText("Error with " + str);
        }
    }

    private void findViews(){
        firstName = (EditText) findViewById(R.id.Name);
        secondName = (EditText) findViewById(R.id.SecondName);
        retryPassword = (EditText)findViewById(R.id.RetryPassword);
        password = (EditText) findViewById(R.id.Password);
        data = (EditText) findViewById(R.id.BirthDate);
        textForError = (TextView)findViewById(R.id.Errors);
        registration = (Button)findViewById(R.id.Registration);
    }

    private boolean isEmpty(EditText text){
        return text.length() == 0 ? true : false;
    }

    private boolean isDataCorrect(boolean[] arr){
        for (boolean b: arr){
            if (!b) return false;
        }
        return true;
    }

    private void fillMapWithError(){
        arrayWithError.put(field.FIRST_NAME.ordinal(),"first name");
        arrayWithError.put(field.SECOND_NAME.ordinal(),"second name");
        arrayWithError.put(field.DATA.ordinal(),"data");
        arrayWithError.put(field.PASSWORD.ordinal(),"password (must contain large and small letters and numbers)");
        arrayWithError.put(field.RETRY_PASSWORD.ordinal(),"retry password");
    }

    @SuppressLint("NewApi")
    private void createCalendar(){
        final DatePickerDialog datePickerDialog;
        datePickerDialog = new DatePickerDialog(this);

        data.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    datePickerDialog.show();
                    datePickerDialog.getDatePicker().setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            data.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
                            datePickerDialog.hide();
                        }
                    });
                }
            }
        });
    }

    private void getData(boolean[] res){
        res[field.FIRST_NAME.ordinal()]=Pattern.matches(patternName,firstName.getText().toString());
        res[field.SECOND_NAME.ordinal()]=Pattern.matches(patternSecondName,secondName.getText().toString());
        res[field.DATA.ordinal()]=Pattern.matches(patternData,data.getText().toString());
        res[field.PASSWORD.ordinal()]=Pattern.matches(patternPassword,password.getText().toString());
        res[field.RETRY_PASSWORD.ordinal()]=password.getText().toString().equals(retryPassword.getText().toString());
    }

    private void setActivityButtonRegistrarion(){

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("aaaaaaaaaaaa\n");
                if(!isEmpty(firstName) && !isEmpty(secondName) && !isEmpty(data) && !isEmpty(password) && !isEmpty(retryPassword)){
                    registration.setEnabled(true);
                }
            }
        };

        firstName.addTextChangedListener(textWatcher);
        secondName.addTextChangedListener(textWatcher);
        data.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        retryPassword.addTextChangedListener(textWatcher);

    }

    private void saveSettings(){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_NAME, firstName.getText().toString());//Сохраняем данные о регистрации
        editor.putString(APP_PREFERENCES_PASSWORD, password.getText().toString());
        editor.putString(APP_PREFERENCES_SECONDNAME, secondName.getText().toString());
        editor.putString(APP_PREFERENCES_DATA, data.getText().toString());
        editor.apply();
    }
}
