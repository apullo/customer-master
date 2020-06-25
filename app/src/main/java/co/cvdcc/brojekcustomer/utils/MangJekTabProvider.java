package co.cvdcc.brojekcustomer.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.viewpager.widget.PagerAdapter;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import co.cvdcc.brojekcustomer.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bradhawk on 10/10/2016.
 */

public class MangJekTabProvider implements SmartTabLayout.TabProvider, MenuSelector {

    private static int[] iconId = {
            R.drawable.ic_action_home,
            R.drawable.ic_action_history,
            R.drawable.ic_action_help,
            R.drawable.ic_action_settings
    };

    private List<TabViewHolder> tabViewHolders;

    private LayoutInflater inflater;

    public MangJekTabProvider(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.tabViewHolders = new ArrayList<>();
    }

    @Override
    public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
        View view;
        try {
            if (this.tabViewHolders.get(position) == null) {
                view = inflater.inflate(R.layout.item_tab_layout, container, false);
                tabViewHolders.add(position, new TabViewHolder(view, iconId[position], adapter.getPageTitle(position)));
            }
            view = this.tabViewHolders.get(position).getView();
        } catch (Exception e) {
            view = inflater.inflate(R.layout.item_tab_layout, container, false);
            tabViewHolders.add(position, new TabViewHolder(view, iconId[position], adapter.getPageTitle(position)));
        }
        return view;
    }

    @Override
    public void selectMenu(int position) {
        for (TabViewHolder holder : tabViewHolders) holder.setSelected(false);
        if (tabViewHolders.get(position) != null) tabViewHolders.get(position).setSelected(true);
    }


    static class TabViewHolder {
        @BindView(R.id.item_tab_icon)
        ImageView tabIcon;
        @BindView(R.id.item_tab_text)
        TextView tabText;
        private View view;

        public TabViewHolder(View view, @DrawableRes int iconRes, CharSequence pageTitle) {
            this.view = view;
            ButterKnife.bind(TabViewHolder.this, view);

            tabIcon.setImageResource(iconRes);
            tabText.setText(pageTitle);

            DrawableExtension.setTintSelector(tabIcon, R.color.tab_selector);
        }

        public View getView() {
            return this.view;
        }

        public void setSelected(boolean isSelected) {
            tabIcon.setSelected(isSelected);
            tabText.setSelected(isSelected);
        }
    }
}
