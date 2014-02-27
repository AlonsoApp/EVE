package com.cloupix.eve;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cloupix.eve.authentication.Authenticator;
import com.cloupix.eve.business.CropOption;
import com.cloupix.eve.business.adapters.CropOptionAdapter;
import com.cloupix.eve.logic.ImageLogic;
import com.cloupix.eve.logic.SharedPreferencesManager;
import com.cloupix.eve.logic.UsuarioLogic;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlonsoApp on 19/01/14.
 */
public class ProfileActivity extends Activity implements View.OnClickListener{

    private ImageButton imgBtnProfileImage;
    private EditText editTextUserFullName, editTextUserEmail, editTextPassword;

    private SharedPreferencesManager spm;

    //--------- ImageSelectionWithCrop -------

    private Uri mImageCaptureUri;
    private AlertDialog dialogImagePicker;
    private Bitmap profileBitmap;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private static final int PICK_FROM_FILE_KITKAT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        cargarActionBar();

        cargarPreferencias();
        cargarElementos();

        cargarUsuario();
    }

    private void cargarActionBar(){
        final LayoutInflater inflater = (LayoutInflater) getActionBar().getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        final View customActionBarView = inflater.inflate(R.layout.actionbar_custom_view_done, null);



        // Show the custom action bar view and hide the normal Home icon and title.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView);
    }

    private void cargarPreferencias(){
        spm = new SharedPreferencesManager(getApplicationContext());
    }

    private void cargarElementos(){
        imgBtnProfileImage = (ImageButton) findViewById(R.id.imgBtnProfileImage);
        editTextUserFullName = (EditText) findViewById(R.id.editTextProfileUserFullName);
        editTextUserEmail = (EditText) findViewById(R.id.editTextProfileUserEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextProfilePassword);

        editTextUserFullName.setText(spm.getUserFullName());
        editTextUserEmail.setText(spm.getAccountUserName());
    }

    private void cargarUsuario(){
        UsuarioLogic usuarioLogic = new UsuarioLogic();
        usuarioLogic.fillNavigationHeaderInfo(editTextUserFullName, editTextUserEmail, imgBtnProfileImage, getApplicationContext());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgBtnProfileImage:
                captureImageInitialization();
                dialogImagePicker.show();
                break;
            case R.id.actionbar_done:

                String userFullName = editTextUserFullName.getText().toString();
                String userEmail = editTextUserEmail.getText().toString();
                String userPassword = editTextPassword.getText().toString();
                // Comprbamos los string
                if(TextUtils.isEmpty(userFullName)){
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_fullname_vacio), Toast.LENGTH_SHORT).show();
                    break;
                }
                if(TextUtils.isEmpty(userEmail)){
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_email_vacio), Toast.LENGTH_SHORT).show();
                    break;
                }
                if(!userEmail.contains("@") || !userEmail.contains(".")){
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_email_valido), Toast.LENGTH_SHORT).show();
                    break;
                }
                UsuarioLogic usuarioLogic = new UsuarioLogic();
                usuarioLogic.updateUsuarioInfo(userFullName, userEmail, userPassword, profileBitmap, ProfileActivity.this);
                finish();

                break;
            case R.id.btnLogout:

                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle(R.string.logout)
                        .setMessage(R.string.msg_seguro_salir)
                        .setCancelable(false)
                        .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                logout();
                            }
                        })
                        .show();
                break;
            default:
                break;
        }
    }


    private void logout(){
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
        sharedPreferencesManager.setAccountUserName("");
        sharedPreferencesManager.setAccountUserFullName("");
        sharedPreferencesManager.setAccountUserId(-1L);
        sharedPreferencesManager.setAccountUserProfileImageId(-1L);


        AccountManager am = AccountManager.get(ProfileActivity.this);

        Account[] arrayCuentas = am.getAccountsByType(Authenticator.ACCOUNT_TYPE);
        if(arrayCuentas.length<1)
            return;
        am.removeAccount(arrayCuentas[0], null, null);

        Intent intent = new Intent(getApplicationContext(), AuthenticatorActivity.class);
        intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AuthenticatorActivity.COMES_FROM_MAIN_ACTIVITY, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    // Image View
    private void captureImageInitialization() {
        /**
         * a selector dialogImagePicker to display two image source options, from camera
         * ‘Take from camera’ and from existing files ‘Select from gallery’
         */
        final String[] items;
        if(profileBitmap!=null){
            items = getResources().getStringArray(R.array.array_input_imagen_borrar);
        }else{
            items = getResources().getStringArray(R.array.array_input_imagen);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.msg_elige_imagen));
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) { // pick from
                // camera
                switch (item){
                    case 0:
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
                        break;
                    case 1:
                        // pick from file
                        /**
                         * To select an image from existing files, use
                         * Intent.createChooser to open image chooser. Android will
                         * automatically display a list of supported applications,
                         * such as image gallery or file manager.
                         */
                        Intent intentFile = new Intent();

                        intentFile.setType("image/*");

                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
                        {
                            intentFile.setAction(Intent. ACTION_OPEN_DOCUMENT);
                            intentFile.addCategory(Intent.CATEGORY_OPENABLE);
                            startActivityForResult(intentFile, PICK_FROM_FILE_KITKAT);
                        }else{
                            intentFile.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intentFile, getString(R.string.msg_completar_accion)), PICK_FROM_FILE);
                        }
                        break;
                    case 2:
                        // Borrar imagen seleccionada
                        profileBitmap=null;

                        imgBtnProfileImage.setImageResource(R.drawable.profile_image_holder);

                        break;
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

                    imgBtnProfileImage.setImageBitmap(photo);
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
            imgBtnProfileImage.setImageBitmap(bitmap);
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

}
