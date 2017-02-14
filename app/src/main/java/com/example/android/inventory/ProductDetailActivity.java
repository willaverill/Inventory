package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.DbBitmapUtility;
import com.example.android.inventory.data.InventoryContract;
import com.example.android.inventory.data.InventoryContract.InventoryEntry;

import java.io.IOException;

public class ProductDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_PRODUCT_LOADER = 0;
    private Uri mCurrentProductUri;

    private ImageView mProductImage;
    private TextView mProductName;
    private TextView mProductPrice;
    private TextView mProductQuantity;
    private TextView mProductSold;

    private Button decreaseQuantity;
    private Button increaseQuantity;
    private Button sell;
    private Button order;
    private Button delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        mCurrentProductUri = getIntent().getData();

        mProductImage = (ImageView) findViewById(R.id.product_image);
        mProductName = (TextView) findViewById(R.id.product_name);
        mProductPrice = (TextView) findViewById(R.id.product_price);
        mProductQuantity = (TextView) findViewById(R.id.product_quantity);
        mProductSold = (TextView) findViewById(R.id.product_sold);

        decreaseQuantity = (Button) findViewById(R.id.decrease_quantity);
        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(mProductQuantity.getText().toString().trim()) > 0) {
                    ContentValues values = new ContentValues();
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(mProductQuantity.getText().toString().trim()) - 1);
                    getContentResolver().update(mCurrentProductUri, values, null, null);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.zero_quantity), Toast.LENGTH_SHORT).show();
                }
            }
        });

        increaseQuantity = (Button) findViewById(R.id.increase_quantity);
        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(mProductQuantity.getText().toString().trim()) + 1);
                getContentResolver().update(mCurrentProductUri, values, null, null);
            }
        });

        sell = (Button) findViewById(R.id.sell);
        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(mProductQuantity.getText().toString().trim()) > 0) {
                    ContentValues values = new ContentValues();
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(mProductQuantity.getText().toString().trim()) - 1);
                    values.put(InventoryEntry.COLUMN_PRODUCT_SOLD, Integer.parseInt(mProductSold.getText().toString().trim()) + 1);
                    getContentResolver().update(mCurrentProductUri, values, null, null);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.zero_quantity), Toast.LENGTH_SHORT).show();
                }
            }
        });

        order = (Button) findViewById(R.id.order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String body = getString(R.string.name) + "  " + mProductName.getText().toString().trim() + "\n"
                        + getString(R.string.price) + "  " + mProductPrice.getText().toString().trim() + "\n"
                        + getString(R.string.quantity) + "  " + mProductQuantity.getText().toString().trim() + "\n"
                        + getString (R.string.sold) + "  " + mProductSold.getText().toString().trim();

                final Intent result = new Intent(android.content.Intent.ACTION_SEND);
                result.setType("plain/text");
                result.putExtra(android.content.Intent.EXTRA_TEXT, body);
                startActivity(result);
            }
        });

        delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog();
            }
        });

        getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_delete);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getContentResolver().delete(mCurrentProductUri, null, null);
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_SOLD,
                InventoryEntry.COLUMN_PRODUCT_IMAGE};

        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            mProductName.setText(cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME)));
            mProductPrice.setText("  $" + String.format("%.2f", ((double)cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE))) / 100));
            mProductQuantity.setText("  " + cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY)));
            mProductSold.setText("  " + cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SOLD)));
            mProductImage.setImageDrawable(new BitmapDrawable(getResources(), DbBitmapUtility.getImage(cursor.getBlob(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_IMAGE)))));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductName.setText("");
        mProductPrice.setText("");
        mProductQuantity.setText("");
        mProductSold.setText("");
        mProductImage.setImageDrawable(null);
    }
}
