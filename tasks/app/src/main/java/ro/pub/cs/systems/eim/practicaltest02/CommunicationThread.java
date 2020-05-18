package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client!");
            String informationType = bufferedReader.readLine();
            if (informationType == null || informationType.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client: information type!");
                return;
            }

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
            HttpClient httpClient = new DefaultHttpClient();
            String pageSourceCode = "";
            HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + "?q=" + "Bucharest" + "&APPID=" + Constants.WEB_SERVICE_API_KEY + "&units=" + Constants.UNITS);
            HttpResponse httpGetResponse = httpClient.execute(httpGet);
            HttpEntity httpGetEntity = httpGetResponse.getEntity();
            if (httpGetEntity != null) {
                pageSourceCode = EntityUtils.toString(httpGetEntity);

            }
            if (pageSourceCode == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                return;
            } else {
                Log.i(Constants.TAG, pageSourceCode);
            }
            pageSourceCode = "{\"time\":{\"updated\":\"May 18, 2020 06:21:00 UTC\",\"updatedISO\":\"2020-05-18T06:21:00+00:00\",\"updateduk\":\"May 18, 2020 at 07:21 BST\"},\"disclaimer\":\"This data was produced from the CoinDesk Bitcoin Price Index (USD). Non-USD currency data converted using hourly conversion rate from openexchangerates.org\",\"bpi\":{\"USD\":{\"code\":\"USD\",\"rate\":\"9,788.4483\",\"description\":\"United States Dollar\",\"rate_float\":9788.4483},\"EUR\":{\"code\":\"EUR\",\"rate\":\"9,047.8641\",\"description\":\"Euro\",\"rate_float\":9047.8641}}}";
            JSONObject content = new JSONObject(pageSourceCode);

            JSONObject bpi = content.getJSONObject("bpi");
            JSONObject usd_object = bpi.getJSONObject("USD");
            JSONObject eur_object = bpi.getJSONObject("EUR");
            String USD_rate = usd_object.getString("rate");
            String EUR_rate = eur_object.getString("rate");
            JSONObject time = content.getJSONObject("time");
            String lastUpdated = time.getString("updated");
            Log.i(Constants.TAG, USD_rate);
            Log.i(Constants.TAG, EUR_rate);

            String result = null;
            switch(informationType) {
                case "USD":
                    result = "Current price for USD: " +  USD_rate + '\'' + "last updated: " + lastUpdated;
                    break;
                case "EUR":
                    result = "Current price for EUR: " + EUR_rate + '\'' + "last updated: " + lastUpdated;
                    break;
                default:
                    result = "[COMMUNICATION THREAD] Wrong information type (USD/ EUR)!";
            }
            printWriter.println(result);
            printWriter.flush();

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } catch(JSONException jsonException) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + jsonException.getMessage());
            if (Constants.DEBUG) {
                jsonException.printStackTrace();
            }
        }
    }
}
