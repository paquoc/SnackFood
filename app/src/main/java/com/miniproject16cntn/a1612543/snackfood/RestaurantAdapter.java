package com.miniproject16cntn.a1612543.snackfood;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RestaurantAdapter extends ArrayAdapter<Restaurant> {

    private Context context;
    private List<Restaurant> arrRestaurant;

    public RestaurantAdapter(@NonNull Context context, List<Restaurant> arrRestaurant) {
        super(context, 0, arrRestaurant);
        this.context = context;
        this.arrRestaurant = arrRestaurant;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        Restaurant restaurant = getItem(position);
        if (convertView == null)
        {
            convertView = createItemViewAt(position);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name_restaurant);
            viewHolder.list = (TextView) convertView.findViewById(R.id.list_eating);
            viewHolder.price = (TextView) convertView.findViewById(R.id.price);
            viewHolder.status = (TextView) convertView.findViewById(R.id.status);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    private View createItemViewAt(int position) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.layout_restaurant, null);
        Restaurant restaurantData = getItem(position);
        bindingRestaurantDataToView(restaurantData, view);
        return view;
    }

    private void bindingRestaurantDataToView(Restaurant restaurantData, View view) {
        ((TextView) view.findViewById(R.id.name_restaurant)).setText(restaurantData.getName());
        ((TextView) view.findViewById(R.id.list_eating)).setText(restaurantData.getEating());
        ((TextView) view.findViewById(R.id.price)).setText(restaurantData.getIntervalPrice() + "đ");
        if (restaurantData.isActive())
            ((TextView) view.findViewById(R.id.status)).setText("Đang mở cửa");
        else ((TextView) view.findViewById(R.id.status)).setText("Đang đóng cửa");

        ((ImageView) view.findViewById(R.id.imageView)).setImageBitmap(restaurantData.getBitmap());
    }

    static class ViewHolder {
        ImageView imageView;
        TextView name;
        TextView list;
        TextView price;
        TextView status;
    }
}
