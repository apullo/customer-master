package co.cvdcc.brojekcustomer.mFood;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.items.AbstractItem;

import co.cvdcc.brojekcustomer.MangJekApplication;
import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.model.PesananFood;
import co.cvdcc.brojekcustomer.utils.Log;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by Afif on 1/3/2017.
 */

public class MakananItem extends AbstractItem<MakananItem, MakananItem.ViewHolder> {

    Context context;
    OnCalculatePrice calculatePrice;
    public int id;
    public String namaMenu;
    public String deskripsiMenu;
    public long harga;
    public long cost;
    public int quantity;

    public String catatan;

    private Realm realm;

    private TextWatcher catatanUpdater;

    public MakananItem(Context context, OnCalculatePrice calculatePrice) {
        this.context = context;
        this.calculatePrice = calculatePrice;

        catatanUpdater = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                catatan = s.toString();
                if(quantity > 0) UpdatePesanan(id, cost, quantity, catatan);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    @Override
    public int getType() {
        return R.id.makanan_item;
    }

    @Override
    public void bindView(final MakananItem.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        realm = MangJekApplication.getInstance(context).getRealmInstance();

        holder.makananText.setText(namaMenu);
        holder.deskripsiText.setText(deskripsiMenu);
        holder.hargaText.setText(getFormattedPrice(harga));
        holder.quantityText.setText(String.valueOf(quantity));
        holder.notesText.setEnabled(quantity > 0);
        holder.notesText.setText(catatan);

        holder.notesText.addTextChangedListener(catatanUpdater);

        holder.addQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                holder.quantityText.setText("" + quantity);
                holder.notesText.setEnabled(true);
                CalculateCost();
                if (quantity == 1) {
                    AddPesanan(id, cost, quantity, catatan);
                } else if (quantity > 1) {
                    UpdatePesanan(id, cost, quantity, catatan);
                }

                if(calculatePrice != null) calculatePrice.calculatePrice();
            }
        });

        holder.removeQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity - 1 >= 0) {
                    quantity--;
                    holder.quantityText.setText(String.valueOf(quantity));
                    CalculateCost();
                    UpdatePesanan(id, cost, quantity, catatan);

                    if (quantity == 0) {
                        DeletePesanan(id);
                        holder.notesText.setText("");
                        holder.notesText.setEnabled(false);
                    }
                }

                if(calculatePrice != null) calculatePrice.calculatePrice();
            }
        });
    }

    private void CalculateCost() {
        cost = quantity * harga;
        //Log.e("Cost", cost+"");
    }

    private void AddPesanan(int idMakanan, long totalHarga, int qty, String notes) {
        PesananFood pesananfood = new PesananFood();
        pesananfood.setIdMakanan(idMakanan);
        pesananfood.setTotalHarga(totalHarga);
        pesananfood.setQty(qty);
        pesananfood.setCatatan(notes);
        realm.beginTransaction();
        realm.copyToRealm(pesananfood);
        realm.commitTransaction();

        Log.e("Added", idMakanan + "");
        Log.e("Added", qty + "");
    }

    private void UpdatePesanan(int idMakanan, long totalHarga, int qty, String notes) {
        realm.beginTransaction();
        PesananFood updateFood = realm.where(PesananFood.class).equalTo("idMakanan", idMakanan).findFirst();
        updateFood.setTotalHarga(totalHarga);
        updateFood.setQty(qty);
        updateFood.setCatatan(notes);
        realm.copyToRealm(updateFood);
        realm.commitTransaction();

        Log.e("Updated", qty + "");
    }

    private void DeletePesanan(int idMakanan) {
        realm.beginTransaction();
        PesananFood deleteFood = realm.where(PesananFood.class).equalTo("idMakanan", idMakanan).findFirst();
        deleteFood.deleteFromRealm();
        realm.commitTransaction();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_makanan;
    }

    private String getFormattedPrice(long price) {
        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(price);
        return String.format(Locale.US, "Rp. %s ,-", formattedTotal);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.makanan_text)
        TextView makananText;

        @BindView(R.id.deskripsi_text)
        TextView deskripsiText;

        @BindView(R.id.harga_text)
        TextView hargaText;

        @BindView(R.id.notes_text)
        EditText notesText;

        @BindView(R.id.add_quantity)
        TextView addQuantity;

        @BindView(R.id.quantity_text)
        TextView quantityText;

        @BindView(R.id.remove_quantity)
        TextView removeQuantity;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnCalculatePrice {
        void calculatePrice();
    }

}
