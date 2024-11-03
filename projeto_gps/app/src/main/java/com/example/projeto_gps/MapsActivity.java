package com.example.projeto_gps;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.projeto_gps.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;



import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String origem = "-23.5325,-46.4540";
        String destino = "-23.5404, -46.5399";
        String destino2 = "-23.5371, -46.5012";
        String apiKey = "AIzaSyBBrje-m4EVpbf3ju-NHrgL96hlmAjPT80";

        // Exemplo: adicionar um marcador na origem e calcular rota
        LatLng origemMarcador = new LatLng(-23.5325, -46.4540); // Origem: Itaquera
        LatLng destinoMarcador = new LatLng(-23.5404, -46.5399); // Destino: Guilhermina
        LatLng destino2Marcador = new LatLng(-23.5371, -46.5012); // Parada: Carrão

        googleMap.addMarker(new MarkerOptions().position(origemMarcador).title("Origem: Itaquera"));
        googleMap.addMarker(new MarkerOptions().position(destinoMarcador).title("Destino: Guilhermina"));
        googleMap.addMarker(new MarkerOptions().position(destino2Marcador).title("Destino: Carrão"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origemMarcador, 10f));

        // Inicia a tarefa de buscar rota entre origem e destino
        String urlStr = "https://maps.googleapis.com/maps/api/directions/json?origin="
                +                   origem
                + "&destination=" + destino
                + "&waypoints=" +   destino2
                + "&key=" +         apiKey;
        new DirectionsTask().execute(urlStr);
    }

    private class DirectionsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String response = "";
            try {
                String urlStr = params[0];
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                response = sb.toString();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            parseAndDrawRoute(result); // Desenha a rota no mapa após a resposta
        }

        private void parseAndDrawRoute(String json) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray routes = jsonObject.getJSONArray("routes");
                JSONObject route = routes.getJSONObject(0);
                JSONArray legs = route.getJSONArray("legs");

                PolylineOptions polylineOptions = new PolylineOptions();

                // Loop para percorrer cada trecho da rota
                for (int j = 0; j < legs.length(); j++) {
                    JSONArray steps = legs.getJSONObject(j).getJSONArray("steps");

                    for (int i = 0; i < steps.length(); i++) {
                        String polyline = steps.getJSONObject(i).getJSONObject("polyline").getString("points");
                        List<LatLng> points = decodePolyline(polyline);
                        polylineOptions.addAll(points);
                    }
                }

                mMap.addPolyline(polylineOptions);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        private List<LatLng> decodePolyline(String encoded) {
            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((lat / 1E5), (lng / 1E5));
                poly.add(p);
            }

            return poly;
        }
    }
}

