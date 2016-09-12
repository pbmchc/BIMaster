package com.example.piotrek.bimaster;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.piotrek.bimaster.R;
import com.example.piotrek.bimaster.services.ConnectionTask;
import com.example.piotrek.bimaster.utils.Utils;

public class CredentialsActivity extends AppCompatActivity {

    Button btnSave;
    EditText edtUser;
    EditText edtPass;
    EditText edtServer;
    EditText edtInstance;
    String username;
    String password;
    String server;
    String instance;
    int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);

        btnSave = (Button) findViewById(R.id.btnSave);
        edtUser = (EditText) findViewById(R.id.edtName);
        edtPass = (EditText) findViewById(R.id.edtPass);
        edtServer = (EditText) findViewById(R.id.edtServer);
        edtInstance = (EditText) findViewById(R.id.edtInstance);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = edtUser.getText().toString().trim();
                password = edtPass.getText().toString().trim();
                server = edtServer.getText().toString().trim();
                instance = edtInstance.getText().toString().trim();

                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e)
                {

                }
                new ConnectionTask(CredentialsActivity.this, username, password).execute("http://" + server + "/" + instance + "/api/v1.0/CatalogItems");

            }
        });
    }

    public void getCode(int code)
    {
        this.code = code;

        if (code == 200)
        {
            Intent intent = new Intent(CredentialsActivity.this, MainActivity.class);
            Utils.saveSP(CredentialsActivity.this, "username", username);
            Utils.saveSP(CredentialsActivity.this, "password", password);
            Utils.saveSP(CredentialsActivity.this, "server", server);
            Utils.saveSP(CredentialsActivity.this, "instance", instance);
            Toast.makeText(CredentialsActivity.this, "Zalogowano", Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
        else
        {
            if (String.valueOf(code).contains("40"))
                Toast.makeText(CredentialsActivity.this, code + "Niepoprawne dane", Toast.LENGTH_LONG).show();
            else if (String.valueOf(code).contains("50"))
                Toast.makeText(CredentialsActivity.this, code + "Błąd serwera", Toast.LENGTH_LONG).show();
            else if (!isOnline())
                Toast.makeText(CredentialsActivity.this, code + "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(CredentialsActivity.this, code + "Nieznany błąd", Toast.LENGTH_LONG).show();

        }
    }

    public boolean isOnline()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }


}
