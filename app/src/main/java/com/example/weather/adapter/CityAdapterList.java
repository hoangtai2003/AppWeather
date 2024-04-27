package com.example.weather.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.model.City;
import com.example.weather.roomdatabase.AppDatabase;

import java.util.List;

public class CityAdapterList extends RecyclerView.Adapter<CityAdapterList.CityViewHoder>{
    private List<City> mListCities;
    private Context context;
    private ClickListeners clickListeners;

    public CityAdapterList(Context context, List<City> mListCities, ClickListeners clickListeners) {
        this.context = context;
        this.mListCities = mListCities;
        this.clickListeners = clickListeners;
    }

    @NonNull
    @Override
    public CityAdapterList.CityViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city_list, parent, false);
        return new CityAdapterList.CityViewHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHoder holder, int position) {
        City city = mListCities.get(position);
        if (city == null) {
            return;
        }

        holder.tvCityName.setText(city.getCityName()+ " - " + city.getCountryName());
    }


    @Override
    public int getItemCount() {
        if (mListCities != null) {
            return mListCities.size();
        }
        return 0;
    }

    public class CityViewHoder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private TextView tvCityName;

        public CityViewHoder(@NonNull View itemView) {
            super(itemView);
            tvCityName = itemView.findViewById(R.id.tvCityName);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListeners.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListeners.onItemLongClick(getAdapterPosition(), v);
            return true;
        }
    }

    public interface ClickListeners{
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }
}
