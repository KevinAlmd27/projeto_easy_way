package view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import controller.Controller;
import devandroid.kevin.calculadoraco2.R;

public class MainActivity extends AppCompatActivity {

    List<String> combustivel;
    Controller controller;

    TextView txtResultado;

    Spinner spinner;

    EditText editDistancia;
    EditText editLitrosAbastecidos;
    EditText editConsumoPorLitro;

    Button btnCalcular;
    Button btnSalvar;
    Button btnLimpar;
    Button btnFinalizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtResultado = findViewById(R.id.txtResultado);
        spinner = findViewById(R.id.spinner);
        editDistancia = findViewById(R.id.editDistancia);
        editLitrosAbastecidos = findViewById(R.id.editLitrosAbastecidos);
        editConsumoPorLitro = findViewById(R.id.editConsumoPorLitro);
        btnCalcular = findViewById(R.id.btnCalcular);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnLimpar = findViewById(R.id.btnLimpar);
        btnFinalizar = findViewById(R.id.btnFinalizar);

        controller = new Controller();
        combustivel = controller.getListaCombustivel();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                controller.dadosParaSpinner());
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinner.setAdapter(adapter);

        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isDadosOk = true;

                if (TextUtils.isEmpty(editLitrosAbastecidos.getText())) {
                    editLitrosAbastecidos.setError(" Obrigatório");
                    editLitrosAbastecidos.requestFocus();
                    isDadosOk = false;
                }
                if (TextUtils.isEmpty(editDistancia.getText())) {
                    editDistancia.setError(" Obrigatório");
                    editDistancia.requestFocus();
                    isDadosOk = false;
                }
                if (TextUtils.isEmpty(editConsumoPorLitro.getText())) {
                    editConsumoPorLitro.setError(" Obrigatório");
                    editConsumoPorLitro.requestFocus();
                    isDadosOk = false;
                }

                if (isDadosOk) {
                    double litrosAbastecidos = Double.parseDouble(editLitrosAbastecidos.getText().toString());
                    double distancia = Double.parseDouble(editDistancia.getText().toString());
                    double consumoPorLitro = Double.parseDouble(editConsumoPorLitro.getText().toString());

                    double litrosNecessarios = distancia / consumoPorLitro;

                    String combustivelSelecionado = spinner.getSelectedItem().toString();

                    double fatorEmissao;
                    switch (combustivelSelecionado) {
                        case "Gasolina Comum":
                        case "Gasolina Aditivada":
                        case "Gasolina Reformulada":
                            fatorEmissao = 1.45;
                            break;
                        case "Gasolina Premium/alta Octanagem":
                            fatorEmissao = 1.55;
                            break;
                        case "Diesel":
                            fatorEmissao = 2.65;
                            break;
                        case "Etanol":
                            fatorEmissao = 1.23;
                            break;
                        default:
                            fatorEmissao = 1.18;
                            break;
                    }

                    double emissaoCO2 = litrosAbastecidos * fatorEmissao;
                    txtResultado.setText(String.format("Emissão de CO₂: %.2f kg", emissaoCO2));
                }
            }
        }); // <-- Aqui estava o erro: a chave de fechamento da onClickListener estava fora de lugar.
    }
}
