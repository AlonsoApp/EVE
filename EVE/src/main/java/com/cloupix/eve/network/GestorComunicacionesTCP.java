package com.cloupix.eve.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cloupix.eve.business.Lista;
import com.cloupix.eve.business.Usuario;
import com.cloupix.eve.business.UsuarioCompleto;
import com.cloupix.eve.business.exceptions.EveHttpException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by AlonsoApp on 17/01/14.
 */
public class GestorComunicacionesTCP {

    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private BufferedReader br;
    private String CLRF = "\r\n";

    public static String SERVER_IP = "192.168.43.220";
    public static int SERVER_PORT = 1170;

    public GestorComunicacionesTCP(String serverTCPIP, int serverTCPPort) throws IOException
    {
        socket = new Socket(serverTCPIP, serverTCPPort);

        dos = new DataOutputStream(socket.getOutputStream());
        dis = new DataInputStream(socket.getInputStream());

        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());

        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public UsuarioCompleto userLogin(String userName, String userPass, String authTokenType)throws EveHttpException, Exception
    {
        UsuarioCompleto usuarioCompleto = new UsuarioCompleto();

        dos.writeBytes("LOGIN" + CLRF);
        String lineaUser = limpiarStr(userName)+";"+limpiarStr(userPass)+";"+limpiarStr(authTokenType) + CLRF;
        dos.writeBytes(lineaUser);

        /**Iniciamos espera respuesta LOGIN*/
        String linea = br.readLine();
        if(!linea.equals("LOGIN.RESULT"))
            throw new EveHttpException(501);
        linea = br.readLine();
        String codigo = linea.substring(0, 3);

        /**Comprobamos respuesta a comando user*/
        if(Integer.parseInt(codigo)!=200)
        {
            // Si el statusCode no es 200 lanzamos excepción
            throw new EveHttpException(Integer.parseInt(codigo));
        }else{
            linea = br.readLine();
            while(!linea.equals("END.LOGIN.RESULT")){
                StringTokenizer st = new StringTokenizer(linea, ";");
                usuarioCompleto.setUserId(Long.parseLong(desLimpiarStr(st.nextToken().trim())));
                usuarioCompleto.setUserFullName(desLimpiarStr(st.nextToken().trim()));
                usuarioCompleto.setUserProfileImageId(Long.parseLong(desLimpiarStr(st.nextToken().trim())));
                usuarioCompleto.setUserToken(desLimpiarStr(st.nextToken().trim()));
                linea = br.readLine();
            }
        }

        return usuarioCompleto;
    }

    public UsuarioCompleto userSignInUp(String userFullName, String userPass, String userEmail)throws EveHttpException, Exception
    {
        UsuarioCompleto usuarioCompleto = new UsuarioCompleto();

        dos.writeBytes("SIGNINUP" + CLRF);
        String lineaUser = limpiarStr(userFullName)+";"+limpiarStr(userPass)+";"+limpiarStr(userEmail) + CLRF;
        dos.writeBytes(lineaUser);

        /**Iniciamos espera respuesta LOGIN*/
        String linea = br.readLine();
        if(!linea.equals("SIGNINUP.RESULT"))
            throw new EveHttpException(501);
        linea = br.readLine();
        String codigo = linea.substring(0, 3);

        /**Comprobamos respuesta a comando user*/
        if(Integer.parseInt(codigo)!=200)
        {
            // Si el statusCode no es 200 lanzamos excepción
            throw new EveHttpException(Integer.parseInt(codigo));
        }else{
            linea = br.readLine();
            while(!linea.equals("END.SIGNINUP.RESULT")){
                StringTokenizer st = new StringTokenizer(linea, ";");
                usuarioCompleto.setUserId(Long.parseLong(desLimpiarStr(st.nextToken().trim())));
                usuarioCompleto.setUserFullName(desLimpiarStr(st.nextToken().trim()));
                usuarioCompleto.setUserProfileImageId(Long.parseLong(desLimpiarStr(st.nextToken().trim())));
                usuarioCompleto.setUserToken(desLimpiarStr(st.nextToken().trim()));
                linea = br.readLine();
            }
        }

        return usuarioCompleto;
    }

    public Usuario getUsuarioById(long idUser) throws EveHttpException, Exception{
        Usuario usuario = new Usuario();

        dos.writeBytes("GET_USUARIO_BY_ID" + CLRF);
        dos.writeBytes(idUser + CLRF);

        /**Iniciamos espera respuesta GET_USUARIO_BY_EMAIL*/
        String linea = br.readLine();
        if(!linea.equals("GET_USUARIO_BY_EMAIL.RESULT"))
            throw new EveHttpException(501);
        linea = br.readLine();
        String codigo = linea.substring(0, 3);

        /**Comprobamos respuesta a comando user*/
        if(Integer.parseInt(codigo)!=200)
        {
            // Si el statusCode no es 200 lanzamos excepción
            throw new EveHttpException(Integer.parseInt(codigo));
        }else{
            linea = br.readLine();
            while(!linea.equals("END.LOGIN.RESULT")){
                StringTokenizer st = new StringTokenizer(linea, ";");
                usuario.setUserFullName(desLimpiarStr(st.nextToken().trim()));
                usuario.setUserEmail(desLimpiarStr(st.nextToken().trim()));
                usuario.setUserProfileImageId(Long.parseLong(desLimpiarStr(st.nextToken().trim())));
                linea = br.readLine();
            }
        }


        return usuario;
    }

    public void updateUsuarioInfoById(UsuarioCompleto usuario) throws EveHttpException, Exception {
        dos.writeBytes("UPDATE_USUARIO_INFO_BY_ID" + CLRF);
        String lineaAuth = usuario.getUserId()+";"+ limpiarStr(usuario.getUserFullName())+";"+limpiarStr(usuario.getUserEmail())+";"+limpiarStr(usuario.getUserToken())+";"+limpiarStr(usuario.getUserPassword()) + CLRF;
        dos.writeBytes(lineaAuth);

        /**Iniciamos espera respuesta*/
        String linea = br.readLine();
        if(!linea.equals("UPDATE_USUARIO_INFO_BY_ID.RESULT"))
            throw new EveHttpException(501);
        linea = br.readLine();
        String statusCode = linea.substring(0, 3);

        /**Comprobamos respuesta a comando user*/
        if(Integer.parseInt(statusCode)!=200)
            throw new EveHttpException(Integer.parseInt(statusCode));
    }

    public void crearLista(String userEmail, String authToken, Lista nuevaLista) throws EveHttpException, Exception  {
        dos.writeBytes("CREAR_LISTA" + CLRF);
        String lineaAuth = limpiarStr(userEmail)+";"+ limpiarStr(authToken) + CLRF;
        dos.writeBytes(lineaAuth);

        /**Iniciamos espera respuesta del auth*/
        String linea = br.readLine();
        String statusCode = linea.substring(0, 3);

        /**Comprobamos respuesta a comando auth*/
        if(Integer.parseInt(statusCode)!=201)
            throw new EveHttpException(Integer.parseInt(statusCode));


        oos.writeObject(nuevaLista);

        /**Iniciamos espera respuesta de la creacion de la lista*/
        linea = br.readLine();
        statusCode = linea.substring(0, 3);

        /**Comprobamos respuesta al comando*/
        if(Integer.parseInt(statusCode)!=200)
            throw new EveHttpException(Integer.parseInt(statusCode));

    }

    public ArrayList<Lista> getListasByUserEmailToken(String userEmail, String authToken) throws EveHttpException, Exception {
        dos.writeBytes("GET_LISTAS_BY_USER_EMAIL_TOKEN" + CLRF);
        String lineaAuth = limpiarStr(userEmail)+";"+ limpiarStr(authToken) + CLRF;
        dos.writeBytes(lineaAuth);

        /**Iniciamos espera respuesta del auth*/
        String linea = br.readLine();
        String statusCode = linea.substring(0, 3);

        /**Comprobamos respuesta a comando auth*/
        if(Integer.parseInt(statusCode)!=200)
            throw new EveHttpException(Integer.parseInt(statusCode));

        Object objectListaListas = ois.readObject();
        ArrayList<Lista> listaListas = null;
        if(objectListaListas instanceof ArrayList)
            listaListas = (ArrayList<Lista>) objectListaListas;

        if(listaListas==null)
            throw new EveHttpException(600);

        return listaListas;
    }

    public Bitmap getImagenById(long idImagen) throws EveHttpException, Exception{
        dos.writeBytes("GET_IMAGE_BY_ID_IMAGEN" + CLRF);
        dos.writeBytes(idImagen + CLRF);
        String linea = br.readLine();
        String statusCode = linea.substring(0, 3);

        /**Comprobamos respuesta que nos indica si la imagen existe y va a proceder a enviarla*/
        if(Integer.parseInt(statusCode)!=200)
            throw new EveHttpException(Integer.parseInt(statusCode));


        linea = br.readLine();
        int length = Integer.parseInt(linea);
        byte[] buffer = new byte[length];
        byte b;
        dos.writeBytes("dalee"+ CLRF);
        for(int i=0; i<length; i++)
        {
            b = dis.readByte();
            buffer[i] = b;
        }

        return BitmapFactory.decodeByteArray(buffer , 0, buffer.length);
    }

    public void uploadBitmapById(Bitmap bitmap, long idImage, String userEmail, String userToken, Context context)throws Exception{

        dos.writeBytes("UPLOAD_IMAGE_BY_ID_IMAGEN" + CLRF);
        String lineaAuth = limpiarStr(userEmail)+";"+limpiarStr(userToken)+";"+idImage + CLRF;
        dos.writeBytes(lineaAuth);

        /**Iniciamos espera respuesta LOGIN*/
        String linea = br.readLine();
        if(!linea.equals("UPLOAD_IMAGE_BY_ID_IMAGEN.AUTH.RESULT"))
            throw new EveHttpException(501);
        linea = br.readLine();
        String statusCode = linea.substring(0, 3);

        /**Comprobamos respuesta a comando user*/
        if(Integer.parseInt(statusCode)!=200)
            throw new EveHttpException(Integer.parseInt(statusCode));


        File file = new File(context.getCacheDir(), Long.toString(idImage)+".jpg");
        file.createNewFile();

        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
        byte[] bitmapData = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitmapData);
        fos.flush();
        fos.close();



        //File dir = Environment.getExternalStorageDirectory();
        File yourFile = new File(context.getCacheDir(), Long.toString(idImage)+".jpg");



        //File file = null;
        FileInputStream fis = null;
        byte[] buffer = new byte[(int)yourFile.length()];
        fis = new FileInputStream(yourFile);
        //Enviamos el tamano del archivo
        dos.writeBytes(yourFile.length() + CLRF);

        br.readLine();
        fis.read(buffer);
        dos.write(buffer, 0, (int) file.length());
        dos.flush();
        if(fis!=null)
            fis.close();
        //Borramos el archivo cacheado
        yourFile.delete();

        linea = br.readLine();
        if(!linea.equals("UPLOAD_IMAGE_BY_ID_IMAGEN.RESULT"))
            throw new EveHttpException(501);
        linea = br.readLine();
        statusCode = linea.substring(0, 3);

        /**Comprobamos respuesta a comando user*/
        if(Integer.parseInt(statusCode)!=200)
            throw new EveHttpException(Integer.parseInt(statusCode));
    }

    private String desLimpiarStr(String str){
        String strLimpio = str.replace("%01", ";");
        strLimpio = strLimpio.replaceAll("%02", CLRF);
        if(strLimpio.equals("%03"))
            strLimpio = "";
        return strLimpio;
    }

    private String limpiarStr(String str){
        String strLimpio = str.replace(";", "%01");
        strLimpio = strLimpio.replaceAll(CLRF, "%02");
        if(strLimpio.equals(""))
            strLimpio = "%03";
        return strLimpio;
    }

    public void desconectar() throws IOException{
        dos.writeBytes("DICONECT" + CLRF);
        dos.close();
        oos.close();
        ois.close();
        br.close();
        socket.close();
    }
}
