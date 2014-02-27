package com.cloupix.eve.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cloupix.eve.R;
import com.cloupix.eve.business.UsuarioCompleto;
import com.cloupix.eve.business.exceptions.EveHttpException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by AlonsoApp on 16/11/13.
 */
public class GestorComunicacionesREST
{
    public static String STATUS_CODE = "status_code";
    public static String ERROR_MESSAGE = "error_message";
    public static String JSON_STRING = "json_string";

    public static String SERVER_IP = "82.223.247.186";
    public static String SERVER_PORT_SSL = "443";
    public static String SERVER_PORT = "80";

    private static String HTTP = "http://";
    private static String HTTPS = "https://";


    private String url;
    private Context context;

    public GestorComunicacionesREST(String serverIP, String serverPort, Context context)
    {
        this.url = serverIP + ":" + serverPort + "/Service1";
        this.context = context;
    }

    public UsuarioCompleto userLogin(String userName, String userPass, String authTokenType)throws EveHttpException, Exception
    {
        String str_url_query = url + "/getUserToken/"+sanearString(userName)+"/"+sanearString(userPass)+"/"+sanearString(authTokenType);

        // Mago de Oz
        return new UsuarioCompleto(
                0,
                "Iñigo Alonso",
                "AlonsoApp@gmail.com",
                0L,
                "AC057B",
                ""
        );

        /* TODO: Descomentar esto cuando no hagamos Mago de Oz
        String jsonString = getJSON(str_url_query);

        JSONObject jsonObject = new JSONObject(jsonString);
        String token = jsonObject.getString("Token");

        return token;*/
    }

    public UsuarioCompleto userSignInUp(String userName, String userPass, String userEmail)throws EveHttpException, Exception
    {
        String str_url_query = url + "/userSigninUp/"+sanearString(userName)+"/"+sanearString(userEmail);

        // Mago de Oz
        return new UsuarioCompleto(
                0,
                "Iñigo Alonso",
                "AlonsoApp@gmail.com",
                0L,
                "AC057B",
                ""
        );


        /* TODO: Descomentar esto cuando no hagamos Mago de Oz
        String jsonString = getJSON(str_url_query);

        JSONObject jsonObject = new JSONObject(jsonString);
        String token = jsonObject.getString("Token");

        return token;*/
    }

    public Bitmap getImage(String type, String quality, String id) throws Exception
    {

        return BitmapFactory.decodeResource(context.getResources(), R.drawable.deleteme_profile);
        //TODO: Descomentar esto cuando no hagamos Mago de Oz
    /*
        this.url = url + "/getImage/"+type+"/"+quality+"/"+id;
        Bitmap bitmap = null;

        //--------- HTTPS ----------- CREO QUE ESTA MAL
        //URL url = new URL(HTTPS + this.url);
        //--------- HTTP ------------
        URL url = new URL(HTTP + this.url);

        URLConnection connection = url.openConnection();
        connection.setUseCaches(true);
        Object response = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        if (response instanceof Bitmap) {
            bitmap = (Bitmap)response;
        }
        return bitmap;*/
    }

    private void enableHttpResponseCache() {
        try {
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            File httpCacheDir = new File(context.getCacheDir(), "http");
            Class.forName("android.net.http.HttpResponseCache")
                    .getMethod("install", File.class, long.class)
                    .invoke(null, httpCacheDir, httpCacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {
            //Log.d(TAG, "HTTP response cache is unavailable.");
        }
    }

    private String sanearString(String strSucio){
        //TODO: Sanear Bien!!
        String strLimpio = strSucio.replace("@", "%40");
        strLimpio = strLimpio.replace(' ', '+');
        return strLimpio;
    }

    private String getJSON(String str_url_query) throws EveHttpException, Exception
    {
        StringBuilder builder = new StringBuilder();
        DefaultHttpClient client;
        //--------- HTTPS ----------- //TODO: Descoemntar esto cuando este listo el certificado y lo almacenemos en al keystore
        //client = new HttpsClient(this.context);
        //HttpGet httpGet = new HttpGet(str_url_query);
        //--------- HTTP -----------
        client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(HTTP + str_url_query);


        HttpResponse response = client.execute(httpGet);
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if (statusCode == 200)
        {
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null)
            {
                builder.append(line);
            }
        }else{
            throw new EveHttpException(statusCode);
        }

        return builder.toString();
    }
}

