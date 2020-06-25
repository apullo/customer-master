package co.cvdcc.brojekcustomer.home.submenu.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bradhawk on 10/30/2016.
 */

public class HelpFragment extends Fragment {

    @BindView(R.id.help_recyclerView)
    RecyclerView recyclerView;

    private FastItemAdapter<HelpItem> adapter;

    private List<HelpItem> helpItemList;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        context = getContext();

        helpItemList = new ArrayList<>();
        helpItemList.add(new HelpItem(R.drawable.ic_mcar, R.string.home_mCar));
        helpItemList.add(new HelpItem(R.drawable.ic_mride, R.string.home_mRide));
        helpItemList.add(new HelpItem(R.drawable.ic_msend, R.string.home_mSend));
        helpItemList.add(new HelpItem(R.drawable.ic_mbox, R.string.home_mBox));
        helpItemList.add(new HelpItem(R.drawable.ic_mmassage, R.string.home_mMassage));
        helpItemList.add(new HelpItem(R.drawable.ic_mfood, R.string.home_mFood));
        helpItemList.add(new HelpItem(R.drawable.ic_mservice, R.string.home_mService));


        adapter = new FastItemAdapter<>();
        adapter.add(helpItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent helpIntent = new Intent(context, HelpActivity.class);
                helpIntent.putExtra("id", position);
                startActivity(helpIntent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                // do whatever
            }
        }));



    }
}
