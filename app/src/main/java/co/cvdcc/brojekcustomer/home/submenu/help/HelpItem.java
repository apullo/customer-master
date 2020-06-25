package co.cvdcc.brojekcustomer.home.submenu.help;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import co.cvdcc.brojekcustomer.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bradhawk on 10/30/2016.
 */

public class HelpItem extends AbstractItem<HelpItem, HelpItem.ViewHolder> {

    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    @DrawableRes
    private int iconRes;

    @StringRes
    private int nameRes;


    public HelpItem(int iconRes, int nameRes) {
        this.iconRes = iconRes;
        this.nameRes = nameRes;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public int getNameRes() {
        return nameRes;
    }

    public void setNameRes(int nameRes) {
        this.nameRes = nameRes;
    }

    @Override
    public int getType() {
        return R.id.itemHelp_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_help;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.iconView.setImageResource(iconRes);
        holder.titleView.setText(nameRes);
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemHelp_icon)
        ImageView iconView;

        @BindView(R.id.itemHelp_title)
        TextView titleView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(ViewHolder.this, itemView);

        }
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }


}
