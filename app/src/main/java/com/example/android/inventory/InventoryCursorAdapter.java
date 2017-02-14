package com.example.android.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by Will on 2/11/2017.
 */

public class InventoryCursorAdapter extends CursorAdapter {
    private Cursor mCursor;
    private Context mContext;

    public InventoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;

        ViewHolder holder = new ViewHolder();
        holder.name = (TextView) view.findViewById(R.id.item_name);
        holder.price = (TextView) view.findViewById(R.id.item_price);
        holder.quantity = (TextView) view.findViewById(R.id.item_quantity);
        holder.sold = (TextView) view.findViewById(R.id.item_sold);
        holder.sell = (Button) view.findViewById(R.id.sell_button);

        holder.name.setText(cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME)));
        holder.price.setText(context.getString(R.string.price) + "  $" + String.format("%.2f", ((double)cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE))) / 100));
        holder.quantity.setText(context.getString(R.string.quantity) + "  " + cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY)));
        holder.sold.setText(context.getString(R.string.sold) + "  " + cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SOLD)));
        holder.sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View parentRow = (View) view.getParent();
                ListView listView = (ListView) parentRow.getParent();
                final int position = listView.getPositionForView(parentRow);
                mCursor.moveToPosition(position);

                if (mCursor.getInt(mCursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY)) > 0) {
                    ContentValues values = new ContentValues();
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, mCursor.getInt(mCursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY)) - 1);
                    values.put(InventoryEntry.COLUMN_PRODUCT_SOLD, mCursor.getInt(mCursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SOLD)) + 1);
                    String selection = InventoryEntry._ID + "=?";
                    String[] selectionArgs = new String[]{String.valueOf(mCursor.getInt(mCursor.getColumnIndex(InventoryEntry._ID)))};
                    mContext.getContentResolver().update(InventoryEntry.CONTENT_URI, values, selection, selectionArgs);
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.zero_quantity), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    static class ViewHolder {
        TextView name;
        TextView price;
        TextView quantity;
        TextView sold;
        Button sell;
    }
}
