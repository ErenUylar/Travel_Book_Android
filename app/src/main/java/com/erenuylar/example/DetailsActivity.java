package com.erenuylar.example;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.erenuylar.example.databinding.ActivityDetailsBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

public class DetailsActivity extends AppCompatActivity {

    private ActivityDetailsBinding binding;
    private SQLiteDatabase database;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectBitmap;
    Intent intentimage;
    int id;
    String travelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        database = this.openOrCreateDatabase("Travel", MODE_PRIVATE, null);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if (info.equals("new")) {
            binding.imageSelect.setImageResource(R.drawable.select);
            binding.editTextTravel.setText("");
            binding.editTextTextCity.setText("");
            binding.editTextTextDate.setText("");
            binding.button2.setVisibility(View.INVISIBLE);
        } else {
            id = intent.getIntExtra("id", 0);
            binding.button2.setVisibility(View.VISIBLE);
            binding.buttonSave.setText("Edit Travel");
            binding.buttonSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.buttonSave.setText("Save Edit");
                    binding.button2.setVisibility(View.INVISIBLE);

                    binding.buttonSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (selectBitmap != null) {
                                String travelName = binding.editTextTravel.getText().toString();
                                String cityName = binding.editTextTextCity.getText().toString();
                                String date = binding.editTextTextDate.getText().toString();

                                Bitmap smallImage = smallImage(selectBitmap, 300);

                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                smallImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
                                byte[] bytes = outputStream.toByteArray();

                                if (travelName.equals("") || cityName.equals("") || date.equals("")) {
                                    Snackbar.make(view, "Fill in the Fields", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    try {

                                        database.execSQL("CREATE TABLE IF NOT EXISTS travel (id INTEGER PRIMARY KEY, travelName VARCHAR, cityName VARCHAR, date VARCHAR, image BLOB)");
                                        String sqlString = "UPDATE travel SET travelName=?, cityName=?, date=?, image=? WHERE id=?";

                                        SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
                                        sqLiteStatement.bindString(1, travelName);
                                        sqLiteStatement.bindString(2, cityName);
                                        sqLiteStatement.bindString(3, date);
                                        sqLiteStatement.bindBlob(4, bytes);
                                        sqLiteStatement.bindString(5, String.valueOf(id));
                                        sqLiteStatement.execute();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    Toast.makeText(DetailsActivity.this, "Successful!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(DetailsActivity.this, ListActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.sag, R.anim.sol);
                                }
                            } else {
                                Snackbar.make(view, "Select Image", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                    try {
                        Cursor cursor = database.rawQuery("SELECT * FROM travel WHERE id=?", new String[]{String.valueOf(id)});
                        int travelIx = cursor.getColumnIndex("travelName");
                        int cityIx = cursor.getColumnIndex("cityName");
                        int dateIx = cursor.getColumnIndex("date");
                        int imageIx = cursor.getColumnIndex("image");

                        while (cursor.moveToNext()) {
                            travelName = cursor.getString(travelIx);
                            binding.editTextTravel.setText(cursor.getString(travelIx));
                            binding.editTextTextCity.setText(cursor.getString(cityIx));
                            binding.editTextTextDate.setText(cursor.getString(dateIx));

                            byte[] byteArray = cursor.getBlob(imageIx);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                            binding.imageSelect.setImageBitmap(bitmap);
                            selectBitmap = bitmap;
                        }
                        cursor.close();
                        binding.imageSelect.setEnabled(true);
                        binding.editTextTravel.setEnabled(true);
                        binding.editTextTextCity.setEnabled(true);
                        binding.editTextTextDate.setEnabled(true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            try {
                Cursor cursor = database.rawQuery("SELECT * FROM travel WHERE id=?", new String[]{String.valueOf(id)});
                int travelIx = cursor.getColumnIndex("travelName");
                int cityIx = cursor.getColumnIndex("cityName");
                int dateIx = cursor.getColumnIndex("date");
                int imageIx = cursor.getColumnIndex("image");

                while (cursor.moveToNext()) {
                    travelName = cursor.getString(travelIx);
                    binding.editTextTravel.setText(cursor.getString(travelIx));
                    binding.editTextTextCity.setText(cursor.getString(cityIx));
                    binding.editTextTextDate.setText(cursor.getString(dateIx));

                    byte[] byteArray = cursor.getBlob(imageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    binding.imageSelect.setImageBitmap(bitmap);
                }
                cursor.close();
                binding.imageSelect.setEnabled(false);
                binding.editTextTravel.setEnabled(false);
                binding.editTextTextCity.setEnabled(false);
                binding.editTextTextDate.setEnabled(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        registerLauncher();
    }

    public void back(View view) {
        Intent intents = new Intent(DetailsActivity.this, ListActivity.class);
        intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intents);
        overridePendingTransition(R.anim.sag, R.anim.sol);
    }

    public void deleted(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Travel");
        builder.setMessage(travelName + " will be deleted \nAre you sure?");
        builder.setIcon(R.drawable.delete);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    database.execSQL("DELETE FROM travel WHERE id=?", new Object[]{id});
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(DetailsActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(DetailsActivity.this, ListActivity.class);
                intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent3);
                overridePendingTransition(R.anim.sag, R.anim.sol);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Toast.makeText(DetailsActivity.this, "Not deleted", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getWindow().setBackgroundDrawableResource(R.color.menuTop);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(android.R.color.holo_red_light));
            }
        });
        dialog.show();
    }

    public void save(View view) {
        if (intentimage != null) {
            String travelName = binding.editTextTravel.getText().toString();
            String cityName = binding.editTextTextCity.getText().toString();
            String date = binding.editTextTextDate.getText().toString();

            Bitmap smallImage = smallImage(selectBitmap, 300);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            smallImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
            byte[] bytes = outputStream.toByteArray();

            if (travelName.equals("") || cityName.equals("") || date.equals("")) {
                Snackbar.make(view, "Fill in the Fields", Snackbar.LENGTH_SHORT).show();
            } else {
                try {

                    database.execSQL("CREATE TABLE IF NOT EXISTS travel (id INTEGER PRIMARY KEY, travelName VARCHAR, cityName VARCHAR, date VARCHAR, image BLOB)");
                    String sqlString = "INSERT INTO travel (travelName, cityName, date, image) VALUES (?, ?, ?, ?)";

                    SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
                    sqLiteStatement.bindString(1, travelName);
                    sqLiteStatement.bindString(2, cityName);
                    sqLiteStatement.bindString(3, date);
                    sqLiteStatement.bindBlob(4, bytes);
                    sqLiteStatement.execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(this, "Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DetailsActivity.this, ListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.sag, R.anim.sol);
            }
        } else {
            Snackbar.make(view, "Select Image", Snackbar.LENGTH_SHORT).show();
        }
    }

    public Bitmap smallImage(Bitmap image, int maxSize) {
        int witdh = image.getWidth();
        int heigth = image.getHeight();

        float Ratio = (float) witdh / (float) heigth;

        if (Ratio > 1) {
            witdh = maxSize;
            heigth = (int) (witdh / Ratio);
        } else {
            heigth = maxSize;
            witdh = (int) (heigth * Ratio);
        }

        return image.createScaledBitmap(image, witdh, heigth, false);
    }

    public void selectimage(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Permission Needed!", Snackbar.LENGTH_INDEFINITE).setAction("Allow", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentGallery);
        }
    }

    private void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    intentimage = result.getData();
                    if (intentimage != null) {
                        Uri imageUri = intentimage.getData();
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);
                                selectBitmap = ImageDecoder.decodeBitmap(source);
                                binding.imageSelect.setImageBitmap(selectBitmap);
                            } else {
                                selectBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                                binding.imageSelect.setImageBitmap(selectBitmap);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentGallery);
                } else {
                    Toast.makeText(DetailsActivity.this, "Permission Needed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}