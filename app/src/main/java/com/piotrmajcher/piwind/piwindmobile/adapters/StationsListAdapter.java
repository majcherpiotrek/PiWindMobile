package com.piotrmajcher.piwind.piwindmobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.piotrmajcher.piwind.piwindmobile.R;
import com.piotrmajcher.piwind.piwindmobile.dto.MeteoStationTO;

import java.util.List;

public class StationsListAdapter extends BaseAdapter {

    static class ViewHolder {
        private ImageView stationThumbnail;
        private TextView stationName;
        private TextView stationDescription;
    }

    private Context context;
    private List<MeteoStationTO> stationsList;

    public StationsListAdapter(@NonNull Context context, @NonNull List<MeteoStationTO> stationsList) {
        this.context = context;
        this.stationsList = stationsList;
    }

    @Override
    public int getCount() {
        return this.stationsList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.stationsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.station_list_item, parent, false);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.stationName = (TextView) convertView.findViewById(R.id.station_name);
        viewHolder.stationDescription = (TextView) convertView.findViewById(R.id.station_description);
        viewHolder.stationThumbnail = (ImageView) convertView.findViewById(R.id.station_thumbnail);

        viewHolder.stationName.setText(stationsList.get(position).getName());
        viewHolder.stationDescription.setText(stationsList.get(position).getStationBaseURL());
        viewHolder.stationThumbnail.setImageResource(R.drawable.ic_bell_disabled);

        convertView.setTag(viewHolder);

        return convertView;
    }

    public void updateStationsList(List<MeteoStationTO> stationsList) {
        this.stationsList = stationsList;
        notifyDataSetChanged();
    }
}
