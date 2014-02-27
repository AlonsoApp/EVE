package com.cloupix.eve.logic;

import com.cloupix.eve.R;
import com.cloupix.eve.business.exceptions.EveHttpException;
import com.cloupix.eve.network.GestorComunicacionesTCP;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by AlonsoApp on 16/11/13.
 */
public class ImageLogic
{


    public static int TYPE_SQUARE = 0;
    public static int TYPE_ROUND = 1;


	private Context context;
    private LruCache<String, Bitmap> mMemoryCache;
	
	public ImageLogic(Context context){
		this.context = context;
	}


    /**
     * Metodo que asigna devuelve un bitmap de nombre imageType_imageQuality_imageId
     * Lo busca en la memoryCache,  en la diskCache (esto aún no) y finalmente hace una consulta al servidor
     *
     * @param type Tipo de la imagen: Profile, List, List Background...
     * @param quality Calidad de la imagen que queremos
     * @param id El id de imagen
     */
    private Bitmap getBitmap(String type, String quality, String id) throws Exception {
        final String imageKey = type+"_"+quality+"_"+String.valueOf(id);

        Bitmap bitmap = null;
        if(mMemoryCache!=null)
            bitmap = getBitmapFromMemCache(imageKey);

        if (bitmap != null)
        {
            return bitmap;
        } else {
            // TODO: Comprobar si existe en la cache persistente
            boolean existeCacheDisco = false;

            if(!existeCacheDisco){
                /*// Sustituido por GestorComunicacionesTCP
                GestorComunicacionesREST gcREST = new GestorComunicacionesREST(GestorComunicacionesREST.SERVER_IP, GestorComunicacionesREST.SERVER_PORT, context);
                bitmap = gcREST.getImage(type, quality, String.valueOf(id));
                */
                //GestorComunicacionesTCP gcTCP = new GestorComunicacionesTCP(GestorComunicacionesTCP.SERVER_IP, GestorComunicacionesTCP.SERVER_PORT, context);
                //bitmap = gcTCP.getImage(type, quality, String.valueOf(id));
                //addBitmapToMemoryCache(imageKey, bitmap);
            }
        }
        return bitmap;
    }


    // Recibe por parametro y devuelve otro redondeado
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        // Si queremos que sea complemamente redondo tenemos que darle un radio de la mitad
        // de su altura (en mi caso las imgs son completamente redondas)
        final float roundPx = bitmap.getHeight()/2;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && mMemoryCache!=null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        try{
            if(mMemoryCache!=null)
                return mMemoryCache.get(key);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    /**
     * Metodo que envia al servidor una imagen y la guarda en la cache y sd
     *
     * @param bitmap El Bitmap de la imagen que queremos enviar y guardar
     * @param type Tipo de la imagen: Profile, List, List Background...
     * @param quality Calidad de la imagen que queremos
     * @param id El id de imagen
     * @param userEmail Email del usuario que el que se hará la autenticacion frente al servidor para subir la imagen
     * @param userToken Token del usuario que el que se hará la autenticacion frente al servidor para subir la imagen
     */
    public void saveAndUploadProfileImage(Bitmap bitmap, String type, String quality, long id, String userEmail, String userToken){
        uploadBitmap(bitmap, type, quality, id, userEmail, userToken);
        save(bitmap, type, quality, id);
    }

    private void uploadBitmap(Bitmap bitmap, String type, String quality, long id, String userEmail, String userToken){

    }

    public void uploadBitmapById(Bitmap bitmap, long idImagen,  String userEmail, String userToken) throws EveHttpException, Exception{
        GestorComunicacionesTCP gcTCP = new GestorComunicacionesTCP(GestorComunicacionesTCP.SERVER_IP, GestorComunicacionesTCP.SERVER_PORT);
        try{
            gcTCP.uploadBitmapById(bitmap, idImagen, userEmail, userToken, context);
        }catch (EveHttpException e){
            throw e;
        }catch (Exception e){
            throw e;
        }finally {
            gcTCP.desconectar();
        }
    }

    private void save(Bitmap bitmap, String type, String quality, long id){

    }

}
