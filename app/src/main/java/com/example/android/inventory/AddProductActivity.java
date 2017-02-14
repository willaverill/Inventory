package com.example.android.inventory;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventory.data.DbBitmapUtility;
import com.example.android.inventory.data.InventoryContract.InventoryEntry;

import java.io.IOException;

public class AddProductActivity extends AppCompatActivity {
    private ImageView mImageField;
    private Button mImageButton;

    private EditText mNameField;
    private EditText mPriceField;
    private EditText mQuantityField;
    private EditText mSoldField;

    private Button mSaveButton;

    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        mImageField = (ImageView) findViewById(R.id.image_field);
        mImageButton = (Button) findViewById(R.id.image_button);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        mNameField = (EditText) findViewById(R.id.name_field);
        mPriceField = (EditText) findViewById(R.id.price_field);
        mQuantityField = (EditText) findViewById(R.id.quantity_field);
        mSoldField = (EditText) findViewById(R.id.sold_field);

        mSaveButton = (Button) findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mImageField.getDrawable() == null || TextUtils.isEmpty(mNameField.getText()) || TextUtils.isEmpty(mPriceField.getText()) || TextUtils.isEmpty(mQuantityField.getText()) || TextUtils.isEmpty(mSoldField.getText())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.empty_product_information), Toast.LENGTH_SHORT).show();
                } else {
                    insertProduct();
                    finish();
                }
            }
        });
    }

    private void insertProduct() {
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, mNameField.getText().toString());
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, (int) Math.floor(Double.parseDouble(mPriceField.getText().toString()) * 100));
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(mQuantityField.getText().toString()));
        values.put(InventoryEntry.COLUMN_PRODUCT_SOLD, Integer.parseInt(mSoldField.getText().toString()));
        values.put(InventoryEntry.COLUMN_PRODUCT_IMAGE, DbBitmapUtility.getBytes(((BitmapDrawable)mImageField.getDrawable()).getBitmap()));

        Uri uri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                mImageField.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
