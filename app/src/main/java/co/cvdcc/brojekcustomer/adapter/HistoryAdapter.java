package co.cvdcc.brojekcustomer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.home.submenu.history.HistoryDetailActivity;
import co.cvdcc.brojekcustomer.mMassage.InProgressFinishedMassageActivity;
import co.cvdcc.brojekcustomer.model.Driver;
import co.cvdcc.brojekcustomer.model.ItemHistory;
import co.cvdcc.brojekcustomer.model.json.fcm.DriverRequest;
import co.cvdcc.brojekcustomer.utils.Log;

import java.util.ArrayList;

/**
 * Created by GagahIB on 23/11/2016.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<ItemHistory> mDataSet;
    Context context;
    private boolean isCompleted = false;



    public HistoryAdapter(Context context, ArrayList<ItemHistory> dataSet, boolean isCompleted) {
        this.context = context;
        mDataSet = dataSet;
        this.isCompleted = isCompleted;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;

        v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_history, viewGroup, false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        ItemHistory history = (ItemHistory) mDataSet.get(position);
        holder.itemHistory = history;
        holder.isCompleted = isCompleted;
        holder.context = context;
        holder.status.setText(history.status);
        holder.date.setText(history.waktu_order);
        holder.address.setText(history.alamat_asal);
        holder.image.setImageDrawable(context.getResources().getDrawable(history.image_id));

    }


    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView status, date, address;
        ImageView image;
        Context context;
        ItemHistory itemHistory;
        boolean isCompleted;

        public ViewHolder(View view) {
            super(view);
            this.status = (TextView) view.findViewById(R.id.status);
            this.date = (TextView) view.findViewById(R.id.date);
            this.address = (TextView) view.findViewById(R.id.address);
            this.image = (ImageView) view.findViewById(R.id.image);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(context, HistoryDetailActivity.class);
//                    intent.putExtra("driver", new Driver());
//                    intent.putExtra("request", new DriverRequest());
//                    intent.putExtra("transaction", itemHistory);
//                    intent.putExtra("isCompleted", isCompleted);
////                    intent.putExtra("reg_id", itemHistory.reg_id);
//                    intent.putExtra("time_distance", 10);
//                    context.startActivity(intent);

                    Log.e("LOAD_HISTORY", itemHistory.order_fitur);
                    if(itemHistory.order_fitur.equalsIgnoreCase("m-massage")) {
                        Log.e("LOAD_HISTORY", "m-massage");
                        Intent intentMassage = new Intent(context, InProgressFinishedMassageActivity.class);
                        intentMassage.putExtra(InProgressFinishedMassageActivity.IS_COMPLETED_ID, isCompleted);
                        intentMassage.putExtra(InProgressFinishedMassageActivity.ITEM_HISTORY_ID, itemHistory);
                        context.startActivity(intentMassage);
                    } else {
                        Log.e("LOAD_HISTORY", "others");
                        Intent intent = new Intent(context, HistoryDetailActivity.class);
                        intent.putExtra("driver", new Driver());
                        intent.putExtra("request", new DriverRequest());
                        intent.putExtra("transaction", itemHistory);
                        intent.putExtra("isCompleted", isCompleted);
                        //                    intent.putExtra("reg_id", itemHistory.reg_id);
                        intent.putExtra("time_distance", 10);
                        context.startActivity(intent);
                    }
                }
            });

        }
    }

}