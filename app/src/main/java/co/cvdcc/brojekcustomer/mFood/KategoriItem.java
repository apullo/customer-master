package co.cvdcc.brojekcustomer.mFood;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikepenz.fastadapter.items.AbstractItem;

import co.cvdcc.brojekcustomer.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Afif on 12/28/2016.
 */

public class KategoriItem extends AbstractItem<KategoriItem, KategoriItem.ViewHolder> {

    Context context;
    public int idKategori;
    public String kategori;
    public String image;

    public KategoriItem(Context context) {
        this.context = context;
    }

    @Override
    public int getType() {
        return R.id.kategori_item;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.kategoriName.setText(kategori);
        Glide.with(context).load(image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.kategoriIMG);
//        Picasso.with(context).load(image).fit().centerCrop().into(holder.kategoriIMG);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_kategori;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.kategori_img)
        ImageView kategoriIMG;

        @BindView(R.id.kategori_name)
        TextView kategoriName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
