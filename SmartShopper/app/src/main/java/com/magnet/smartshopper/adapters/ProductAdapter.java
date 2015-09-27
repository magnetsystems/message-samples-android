package com.magnet.smartshopper.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.magnet.smartshopper.R;
import com.magnet.smartshopper.walmart.model.Product;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {
    // View lookup cache
    private static class ViewHolder {
        public ImageView ivItemImage;
        public TextView tvItemTitle;
        public TextView tvItemPrice;
    }

    public ProductAdapter(Context context, ArrayList<Product> aBooks) {
        super(context, 0, aBooks);
    }

    // Translates a particular `Product` given a position
    // into a relevant row within an AdapterView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Product product = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_product, parent, false);
            viewHolder.ivItemImage = (ImageView)convertView.findViewById(R.id.ivItemImage);
            viewHolder.tvItemTitle = (TextView)convertView.findViewById(R.id.tvItemName);
            viewHolder.tvItemPrice = (TextView)convertView.findViewById(R.id.tvItemPrice);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.tvItemTitle.setText(product.getName());
        viewHolder.tvItemPrice.setText(product.getSalePrice());
        Picasso.with(getContext()).load(Uri.parse(product.getThumbnailImage())).error(R.drawable.notification_template_icon_bg).into(viewHolder.ivItemImage);
        // Return the completed view to render on screen
        return convertView;
    }
}
