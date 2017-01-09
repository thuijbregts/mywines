package com.example.thomas.mywines.activities.winesActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.MainActivity;
import com.example.thomas.mywines.informationclasses.Wine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class PictureActivity extends Activity{

    public static final int RESULT_LOAD_IMAGE = 1;
    private static final int CAMERA_REQUEST = 1888;

    private long wineId;
    private int type;
    private String picturePath;
    private String thumbnailPath;

    private Bitmap imageToSave;
    private Bitmap thumbnailToSave;

    private ImageCanvas image;

    private RelativeLayout edit;
    private RelativeLayout save;

    private Space space;

    private boolean imageChanged = false;
    private boolean thumbnailChanged = false;

    private Intent resultIntent;
    private File dir;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wines_picture_dialog);

        dir = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM), "images");
        dir.mkdirs();
        final File output = new File(dir, "tmp.jpg");
        RelativeLayout change = (RelativeLayout) findViewById(R.id.dialog_picture_change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(PictureActivity.this, R.style.Dialog);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.wines_picture_dialog_choice);

                RelativeLayout galleryButton = (RelativeLayout) dialog.findViewById(R.id.dialog_picture_choice_gallery);
                galleryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, RESULT_LOAD_IMAGE);
                        dialog.dismiss();
                    }
                });

                RelativeLayout cameraButton = (RelativeLayout) dialog.findViewById(R.id.dialog_picture_choice_camera);
                cameraButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri  = Uri.fromFile(output);
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        resultIntent = getIntent();
        Bundle extras = getIntent().getExtras();
        wineId = extras.getLong("id");
        type = extras.getInt("type");
        picturePath = extras.getString("image");
        thumbnailPath = extras.getString("thumbnail");

        image = new ImageCanvas(this);

        final RelativeLayout save = (RelativeLayout) findViewById(R.id.dialog_picture_save_thumbnail);
        this.save = save;
        final RelativeLayout edit = (RelativeLayout) findViewById(R.id.dialog_picture_edit_thumbnail);
        this.edit = edit;

        RelativeLayout cancel = (RelativeLayout) findViewById(R.id.dialog_picture_cancel);
        RelativeLayout ok = (RelativeLayout) findViewById(R.id.dialog_picture_ok);

        space = (Space) findViewById(R.id.dialog_picture_space);

        if(picturePath.equals(Wine.NO_IMAGE)) {
            image.setBackgroundResource(R.drawable.no_picture_big);
            edit.setVisibility(View.GONE);
        }
        else {
            imageToSave = loadImageFromStorage(picturePath);
            image.setImageBitmap(imageToSave);
            thumbnailToSave = loadImageFromStorage(thumbnailPath);
            space.setVisibility(View.GONE);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLayout();
                thumbnailToSave = image.getThumbnail();
                thumbnailChanged = true;
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLayout();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED, resultIntent);
                finish();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageChanged || thumbnailChanged){
                    final ProgressDialog progress =  new ProgressDialog(PictureActivity.this);
                    progress.setMessage(getResources().getString(R.string.loading));
                    progress.show();
                    Thread mThread = new Thread() {
                        @Override
                        public void run() {
                            String thumbPath;
                            boolean ok = false;
                            if(thumbnailChanged){
                                String thumbName = "thumb_" + wineId;
                                thumbPath = saveToInternalStorage(thumbnailToSave, thumbName);
                                ok = true;
                            }
                            else
                                thumbPath = thumbnailPath;

                            String imagePath;
                            if(imageChanged){
                                String imageName = "pic_" + wineId;
                                imagePath = saveToInternalStorage(imageToSave, imageName);
                                ok = true;
                            }
                            else
                                imagePath = picturePath;

                            if(ok)
                                setResult(RESULT_OK, resultIntent);
                            Bundle bundle = new Bundle();
                            bundle.putInt("type", type);
                            bundle.putLong("id", wineId);
                            bundle.putString("image", imagePath);
                            bundle.putString("thumbnail", thumbPath);
                            resultIntent.putExtras(bundle);
                            progress.dismiss();
                            finish();
                        }
                    };
                    mThread.start();

                }
                else
                    finish();
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setMessage(getResources().getString(R.string.loading));
            progress.show();
            final Thread mThread = new Thread() {
                @Override
                public void run() {
                    String picturePath;
                    if (requestCode == RESULT_LOAD_IMAGE) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picturePath = cursor.getString(columnIndex);
                        cursor.close();
                    } else {
                        picturePath = dir.getAbsolutePath() + "/tmp.jpg";
                    }
                    final File file = new File(picturePath);
                    picturePath = file.getAbsolutePath();

                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                    Bitmap bitmapRotated = rotateBitmap(bitmap, picturePath);
                    if (bitmapRotated != null)
                        bitmap = bitmapRotated;
                    imageToSave = Bitmap.createScaledBitmap(bitmap, image.getWidth(), image.getHeight(), false);
                    thumbnailToSave = image.getThumbnail();
                    imageChanged = true;
                    thumbnailChanged = true;
                    PictureActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            image.setEditing(false);
                            space.setVisibility(View.GONE);
                            edit.setVisibility(View.VISIBLE);
                            save.setVisibility(View.GONE);
                            image.setImageBitmap(imageToSave);
                            image.invalidate();
                        }
                    });

                    progress.dismiss();
                }
            };
            mThread.start();
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, String path) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    private String saveToInternalStorage(Bitmap bitmap, String fileName){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File directory = cw.getDir("images", Context.MODE_PRIVATE);

        File file = new File(directory, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public static Bitmap loadImageFromStorage(String path)
    {
        try {
            File f = new File(path);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private void changeLayout(){
        if(image.isEditing()){
            image.setEditing(false);
            edit.setVisibility(View.VISIBLE);
            save.setVisibility(View.GONE);
        }
        else{
            image.setEditing(true);
            edit.setVisibility(View.GONE);
            save.setVisibility(View.VISIBLE);
        }
    }

    public Bitmap getImageToSave() {
        return imageToSave;
    }

    public Bitmap getThumbnailToSave() {
        return thumbnailToSave;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED, resultIntent);
    }
}
