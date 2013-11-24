package com.cloupix.eve;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.cloupix.eve.authentication.Authenticator;
import com.cloupix.eve.business.AuthResponse;
import com.cloupix.eve.business.CropOption;
import com.cloupix.eve.business.adapters.CropOptionAdapter;
import com.cloupix.eve.business.exceptions.EVEHttpException;
import com.cloupix.eve.logic.AuthenticatorLogic;
import com.cloupix.eve.logic.ImageLogic;
import com.cloupix.eve.logic.SharedPreferencesManager;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlonsoApp on 16/11/13.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity implements LoginFragment.LoginFragmentCallbacks, PortadaFragment.PortadaFragmentCallbacks, SigninupOneFragment.SigninupOneCallbacks, SigninupTwoFragment.SigninupTwoCallbacks
{
    // Estos atributos valen para especificar que fragment se tiene que abrir en el metodo openFragment
    public static final int PORTADA = 0;
    public static final int LOGIN = 1;
    public static final int SIGNINUP_ONE = 2;
    public static final int SIGNINUP_TWO = 3;

    private static final int SUBMIT_LOGIN = 0;
    private static final int SUBMIT_SIGNINUP = 1;

    //--------- ImageSelectionWithCrop -------

    private Uri mImageCaptureUri;
    private ImageView mImageView;
    private AlertDialog dialogImagePicker;
    private Bitmap profileBitmap;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private static final int PICK_FROM_FILE_KITKAT = 4;

    //--------- ImageSelectionWithCrop -------

    public static String ARG_USER_PASS = "arg_user_pass";
    public static String ARG_IS_ADDING_NEW_ACCOUNT = "arg_is_adding_new_account";
    public static String COMES_FROM_MAIN_ACTIVITY = "comes_from_main_activity";
    public static String ARG_ACCOUNT_TYPE = "account_type";
    public static String ARG_AUTH_TYPE = "auth_type";
    private static String STATE_FRAGMENT_ATACHED = "fragment_atached";
    private static String STATE_PROFILE_BITMAP = "profile_bitmap";

    private String signinupUserName;
    private int fragmentAttached = PORTADA;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autheticator);
        // Recuperamos el estado anterior.
        /* Lo hago con un metodo propio y no con el onRestoreInstanceState porque (no se porque)
         * no se llama al crearse la Activity
         */
        restoreInstanceState(savedInstanceState);
    }

    @Override
    public void onLoginFragmentSubmit(String userEmail, String userPass) {
        submit(userEmail, userPass, "", SUBMIT_LOGIN);
    }

    @Override
    public void onPortadaFragmentLoginClicked() {
        openFragment(LOGIN, false);
    }

    @Override
    public void onPortadaFragmentSigninupClicked() {
        openFragment(SIGNINUP_ONE, false);
    }

    @Override
    public void onImageButtonClicked(ImageView imageView) {
        mImageView = imageView;
        captureImageInitialization();
        dialogImagePicker.show();
    }

    @Override
    public void onNextButtonClicked(String userName) {
        signinupUserName = userName;
        openFragment(SIGNINUP_TWO, false);
    }

    @Override
    public void onDoneButtonClicked(String email, String password) {
        submit(email, password, signinupUserName, SUBMIT_SIGNINUP);
    }

    private void openFragment(int fragmentId, boolean isFirstFragment){
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment;
        FragmentTransaction fragmentTransaction;
        switch (fragmentId){
            case PORTADA:
                PortadaFragment portadaFragment;
                fragment = fragmentManager.findFragmentByTag(getResources().getString(R.string.tag_fragment_portada));
                // Comprobamos que la clase que hemos sacado es de tipo LoginFragment, si no lo es
                // es que se trata de otro fragmento que se encontraba en el authenticatorContainer
                if (fragment instanceof PortadaFragment){
                    portadaFragment = (PortadaFragment) fragment;
                }else{
                    portadaFragment = new PortadaFragment();
                }

                // Creamos la transaccion que sustituira el actual fragmento por el nueo y enviara el viejo a la BackStack
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.autheticatorContainer, portadaFragment, getString(R.string.tag_fragment_portada));
                // Cuando es el primer fragment que se abre, si mandas el estado anterio al backStrack queda un estado extra en blanco
                // que aparece al pulsar el boton back al final de la Stack
                if (!isFirstFragment)
                    fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case LOGIN:
                LoginFragment loginFragment;
                fragment = fragmentManager.findFragmentByTag(getResources().getString(R.string.tag_fragment_login));
                // Comprobamos que la clase que hemos sacado es de tipo LoginFragment, si no lo es
                // es que se trata de otro fragmento que se encontraba en el authenticatorContainer
                if (fragment instanceof LoginFragment){
                    loginFragment = (LoginFragment) fragment;
                }else{
                    loginFragment = new LoginFragment();
                }

                // Creamos la transaccion que sustituira el actual fragmento por el nueo y enviara el viejo a la BackStack
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.autheticatorContainer, loginFragment, getString(R.string.tag_fragment_login));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case SIGNINUP_ONE:
                SigninupOneFragment signinupOneFragment;
                fragment = fragmentManager.findFragmentByTag(getResources().getString(R.string.tag_fragment_signinup_one));
                // Comprobamos que la clase que hemos sacado es de tipo LoginFragment, si no lo es
                // es que se trata de otro fragmento que se encontraba en el authenticatorContainer
                if (fragment instanceof SigninupOneFragment){
                    signinupOneFragment = (SigninupOneFragment) fragment;
                }else{
                    signinupOneFragment = new SigninupOneFragment();
                }

                // Creamos la transaccion que sustituira el actual fragmento por el nueo y enviara el viejo a la BackStack
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.autheticatorContainer, signinupOneFragment, getString(R.string.tag_fragment_signinup_one));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case SIGNINUP_TWO:
                SigninupTwoFragment signinupTwoFragment;
                fragment = fragmentManager.findFragmentByTag(getResources().getString(R.string.tag_fragment_signinup_two));
                // Comprobamos que la clase que hemos sacado es de tipo LoginFragment, si no lo es
                // es que se trata de otro fragmento que se encontraba en el authenticatorContainer
                if (fragment instanceof SigninupTwoFragment){
                    signinupTwoFragment = (SigninupTwoFragment) fragment;
                }else{
                    signinupTwoFragment = new SigninupTwoFragment();
                }

                // Creamos la transaccion que sustituira el actual fragmento por el nueo y enviara el viejo a la BackStack
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.autheticatorContainer, signinupTwoFragment, getString(R.string.tag_fragment_signinup_two));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
        }
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        //Si teniamos un fragmento abierto se habrira de nuevo el mismo
        if(savedInstanceState!=null){
            openFragment(savedInstanceState.getInt(STATE_FRAGMENT_ATACHED, PORTADA), true);
        }else{
            openFragment(PORTADA, true);
        }
        // Si teniamos un profileBitmap lo recuperamos
        if(savedInstanceState!=null){
            profileBitmap = savedInstanceState.getParcelable(STATE_PROFILE_BITMAP);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Guardamos el idFragment que estaba abierto por ultima vez al llamarse a onSaveInstanceState()
        outState.putInt(STATE_FRAGMENT_ATACHED, fragmentAttached);
        if(profileBitmap!=null)
            outState.putParcelable(STATE_PROFILE_BITMAP, profileBitmap);
    }

    // Metodo para que los fragments en onResume espeficiquen en que fragment se encuentra la activity
    public void onFragmentAttached(int fragmentId){
        this.fragmentAttached = fragmentId;
    }


    // Este metodo vale tanto para login como para Signinup, solamente hay que especificar el tipo de submit que s equiere hacer
    public void submit(final String userEmail, final String userPass, final String userFullName, final int submitType) {

        new AsyncTask<Void, Void, Intent>() {

            private final ProgressDialog dialog = new ProgressDialog(AuthenticatorActivity.this);
            private int statusCode = 0;
            private boolean errorOcurred = false;
            private AuthResponse authResponse;

            @Override
            protected void onPreExecute() {
                this.dialog.setMessage(getApplicationContext().getString(R.string.msg_login_en_curso));
                this.dialog.setIndeterminate(true);
                this.dialog.show();
                super.onPreExecute();
            }

            @Override
            protected Intent doInBackground(Void... params) {

                AuthenticatorLogic authLogic = new AuthenticatorLogic(getApplicationContext());
                try {
                    // Dependiendo del submitType hacemos una cosa u otra
                    if(submitType == SUBMIT_LOGIN){
                        authResponse = authLogic.userLogin(userEmail, userPass, Authenticator.AUTH_TOKEN_TYPE);
                    }else{
                        authResponse = authLogic.userSignInUp(userEmail, userPass, userFullName, Authenticator.AUTH_TOKEN_TYPE);

                        // Enviamos la imagen al servidor y la guardamos en la cache y sd
                        if(profileBitmap!=null && authResponse!=null){
                            ImageLogic imageLogic = new ImageLogic(getApplicationContext());
                            imageLogic.saveAndUploadProfileImage(
                                    profileBitmap,
                                    ImageLogic.TYPE_PROFILE,
                                    ImageLogic.QUALITY_HD,
                                    authResponse.getUserProfileImageId(),
                                    authResponse.getUserEmail(),
                                    authResponse.getUserToken());
                        }
                    }
                    // Si por algún casual el logic nos devuelve un authResponse nulo salimos de inmediato porque algo ha salido mal
                    if(authResponse==null){
                        errorOcurred=true;
                        return null;
                    }
                    if(!TextUtils.isEmpty(authResponse.getUserToken()))
                    {
                        final Intent res = new Intent();
                        res.putExtra(AccountManager.KEY_ACCOUNT_NAME, userEmail);
                        res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Authenticator.ACCOUNT_TYPE);
                        res.putExtra(AccountManager.KEY_AUTHTOKEN, authResponse.getUserToken());
                        res.putExtra(ARG_USER_PASS, userPass);
                        return res;
                    }
                } catch (EVEHttpException ex) {
                    statusCode = ex.getStatusCode();
                    errorOcurred=true;
                    ex.printStackTrace();
                } catch (Exception ex) {
                    statusCode = 600;
                    errorOcurred=true;
                    ex.printStackTrace();
                }
                // Si el token esta vacio ha tenido que haber un error
                errorOcurred=true;
                return null;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                this.dialog.cancel();
                if(!errorOcurred)
                {
                    // Guarda en la tabla ususario de la BD toda la información
                    authResponse.save();
                    finishLogin(intent);
                }else if(statusCode==401){
                    Toast.makeText(getApplicationContext(), getString(R.string.error_401), Toast.LENGTH_SHORT).show();
                }else if(statusCode==412){
                    Toast.makeText(getApplicationContext(), getString(R.string.error_412), Toast.LENGTH_LONG).show();
                }else{
                    //TODO: swith de los errores
                }
            }

        }.execute();
    }

    private void finishLogin(Intent intent) {
        AccountManager mAccountManager = AccountManager.get(this);
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(ARG_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false))
        {
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = Authenticator.AUTH_TOKEN_TYPE;
            // Creamos una cuenta en el dispositivo y le asignamos el token que hemos recibido del servidor
            // (Si no especificamos ningún token será necesaria otra llamada al servidor para volver a solicitarlo)
            try{

                mAccountManager.addAccountExplicitly(account, accountPassword, null);
                mAccountManager.setAuthToken(account, authtokenType, authtoken);

            }catch(Exception e)
            {
                e.printStackTrace();
            }
        } else {
            mAccountManager.setPassword(account, accountPassword);
        }
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        if(getIntent().getBooleanExtra(COMES_FROM_MAIN_ACTIVITY, false)){
            Intent intentMainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intentMainActivity);
        }
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
        sharedPreferencesManager.setAccountUserName(accountName);
        finish();
    }

    //----- ImageSelectionWithCrop ----------

    private void captureImageInitialization() {
        /**
         * a selector dialogImagePicker to display two image source options, from camera
         * ‘Take from camera’ and from existing files ‘Select from gallery’
         */
        final String[] items = getResources().getStringArray(R.array.array_input_imagen);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.msg_elige_imagen));
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) { // pick from
                // camera
                if (item == 0) {
                    /**
                     * To take a photo from camera, pass intent action
                     * ‘MediaStore.ACTION_IMAGE_CAPTURE‘ to open the camera app.
                     */
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    /**
                     * Also specify the Uri to save the image on specified path
                     * and file name. Note that this Uri variable also used by
                     * gallery app to hold the selected image path.
                     */
                    /*mImageCaptureUri = Uri.fromFile(new File(Environment
                            .getExternalStorageDirectory(), "tmp_avatar_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg"));*/
                    mImageCaptureUri = Uri.fromFile(new File(Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "tmp_avatar_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg"));

                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                    try {
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    // pick from file
                    /**
                     * To select an image from existing files, use
                     * Intent.createChooser to open image chooser. Android will
                     * automatically display a list of supported applications,
                     * such as image gallery or file manager.
                     */
                    Intent intent = new Intent();

                    intent.setType("image/*");

                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
                    {
                        intent.setAction(Intent. ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent, PICK_FROM_FILE_KITKAT);
                    }else{
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, getString(R.string.msg_completar_accion)), PICK_FROM_FILE);
                    }
                }
            }
        });

        dialogImagePicker = builder.create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case PICK_FROM_CAMERA:
                /**
                 * After taking a picture, do the crop
                 */
                doCrop();

                break;

            case PICK_FROM_FILE:
                /**
                 * After selecting image from files, save the selected path
                 */
                mImageCaptureUri = data.getData();

                doCrop();

                break;

            case PICK_FROM_FILE_KITKAT:
                /**
                 * ¿Porque KitKat es diferente?
                 * KitKat ha diseñado los providers de cara a que s epuedan usar con tdo tipo de documentos
                 * Por eso, cuando seleccionas una imagen del contentProvider te da una uri diferente a la
                 * que te suele dar el provider tradicional
                 * URI Tradicional: content://media/external/images/media/62
                 * URI KitKat: content://com.android.providers.media.documents/document/image:62
                 * Cuando pasas esta uri al crop y este intenta acceder, parece que no tiene suficientes
                 * permisos para leer y escribir esa uri
                 */

                pickFromFileKitkat(requestCode, resultCode, data);


                break;
            case CROP_FROM_CAMERA:
                Bundle extras = data.getExtras();
                /**
                 * After cropping the image, get the bitmap of the cropped image and
                 * display it on imageview.
                 */
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    // Guardamos el bitmap no recortado para cuando se llame al onSaveInstanceState y al DONE
                    profileBitmap = photo;
                    // Finalemente redondeamos el bitmap
                    ImageLogic imageLogic = new ImageLogic(getApplicationContext());
                    photo = imageLogic.getRoundedCornerBitmap(photo);

                    mImageView.setImageBitmap(photo);
                }

                File f = new File(mImageCaptureUri.getPath());
                /**
                 * Delete the temporary image
                 */
                if (f.exists())
                    f.delete();

                break;

        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void pickFromFileKitkat(int requestCode, int resultCode, Intent data){
        mImageCaptureUri = data.getData();
        final int takeFlags = data.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // Check for the freshest data.
        getContentResolver().takePersistableUriPermission(mImageCaptureUri, takeFlags);

        // TODO: Averiguar una forma de acceder a la nueva uri desde el crop
        // Comento el crop porque no vale una puta mierda con la nueva uri de KitKat
        //doCrop();

        // Asi que de momento si tienes KitKat te jodes y no tienes crop
        // Sacamos el bitmap de esa uri
        ParcelFileDescriptor parcelFileDescriptor = null;
        Bitmap bitmap = null;
        try {
            parcelFileDescriptor = getContentResolver().openFileDescriptor(mImageCaptureUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getString(R.string.error_image_null), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getString(R.string.error_image_null), Toast.LENGTH_SHORT).show();
        }

        // Hacemos nosotros el crop que nos apetezca
        if(bitmap!=null){
            // Calculamos el lado del cuadrado que vamos a recortar
            // Será el valor más pequeño entre anchura y altura
            int lado;
            if(bitmap.getHeight()>bitmap.getWidth()){
                lado = bitmap.getWidth();
                bitmap = Bitmap.createBitmap(bitmap, 0, ((bitmap.getHeight()/2)-(lado/2)), lado, lado);
            }else{
                lado = bitmap.getHeight();
                bitmap = Bitmap.createBitmap(bitmap, ((bitmap.getWidth()/2)-(lado/2)), 0, lado, lado);
            }
            // Guardamos el bitmap no recortado para cuando se llame al onSaveInstanceState y al DONE
            profileBitmap = bitmap;
            // Finalemente redondeamos el bitmap
            ImageLogic imageLogic = new ImageLogic(getApplicationContext());
            bitmap = imageLogic.getRoundedCornerBitmap(bitmap);
            mImageView.setImageBitmap(bitmap);
        }

    }

    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
        /**
         * Open image crop app by starting an intent
         * ‘com.android.camera.action.CROP‘.
         */
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        /**
         * Check if there is image cropper app installed.
         */
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);

        int size = list.size();

        /**
         * If there is no image cropper app, display warning message
         */
        if (size == 0) {

            Toast.makeText(this, getString(R.string.error_crop_app_not_found), Toast.LENGTH_SHORT).show();

            return;
        } else {
            /**
             * Specify the image path, crop dimension and scale
             */
            intent.setData(mImageCaptureUri);

            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            /**
             * There is posibility when more than one image cropper app exist,
             * so we have to check for it first. If there is only one app, open
             * then app.
             */

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                /**
                 * If there are several app exist, create a custom chooser to
                 * let user selects the app.
                 */
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);

                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);

                    co.appIntent = new Intent(intent);

                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.msg_elige_crop_app));
                builder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {

                                startActivityForResult(cropOptions.get(item).appIntent, CROP_FROM_CAMERA);

                            }
                        });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            getContentResolver().delete(mImageCaptureUri, null, null);
                            mImageCaptureUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();

                alert.show();
            }

        }

    }

    public Bitmap getProfileBitmap(){
        return this.profileBitmap;
    }
}

