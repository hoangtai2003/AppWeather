package com.example.weather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.model.City;
import com.example.weather.R;

import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHoder> {

    private List<City> mListCities;
//    private List<City> mListCitiesOld;
    private Context context;
    private ClickListeners clickListeners;

    public CityAdapter(Context context, List<City> mListCities, ClickListeners clickListeners) {
        this.context = context;
        this.mListCities = mListCities;
//        this.mListCitiesOld = mListCities;
        this.clickListeners = clickListeners;
    }

    @NonNull
    @Override
    public CityViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city, parent, false);
        return new CityViewHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHoder holder, int position) {
        City city = mListCities.get(position);
        if (city == null) {
            return;
        }

        holder.tvCityName.setText(city.getCityName()+ " - " + city.getCountryName());
        holder.btnAddCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListCities != null) {
            return mListCities.size();
        }
        return 0;
    }

    public class CityViewHoder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvCityName;
        private ImageButton btnAddCity;

        public CityViewHoder(@NonNull View itemView) {
            super(itemView);
            tvCityName = itemView.findViewById(R.id.tvCityName);
            btnAddCity = itemView.findViewById(R.id.btnAddCity);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListeners.onItemClick(getAdapterPosition(), v);
        }
    }

//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                String strSearch = constraint.toString();
//                if (strSearch.isEmpty()) {
//                    mListCities = mListCitiesOld;
//                } else {
//                    List<City> list = new ArrayList<>();
//                    for (City city: mListCitiesOld) {
//                        if (city.getCityName().toLowerCase().contains(strSearch.toLowerCase())) {
//                            list.add(city);
//                        }
//                    }
//
//                    mListCities = list;
//                }
//
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = mListCities;
//
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                mListCities = (List<City>) results.values;
//                notifyDataSetChanged();
//            }
//        };
//    }
    public interface ClickListeners{
        void onItemClick(int position, View v);
    }
}
