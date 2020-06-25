package co.cvdcc.brojekcustomer.home.submenu.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import co.cvdcc.brojekcustomer.MangJekApplication;
import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.adapter.HistoryAdapter;
import co.cvdcc.brojekcustomer.api.ServiceGenerator;
import co.cvdcc.brojekcustomer.api.service.UserService;
import co.cvdcc.brojekcustomer.model.ItemHistory;
import co.cvdcc.brojekcustomer.model.User;
import co.cvdcc.brojekcustomer.model.json.menu.HistoryRequestJson;
import co.cvdcc.brojekcustomer.model.json.menu.HistoryResponseJson;
import co.cvdcc.brojekcustomer.utils.Log;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CompletedHistoryFragment extends Fragment implements HistoryFragment.OnSwipeRefresh {

    @BindView(R.id.completed_recyclerView)
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter;

    public CompletedHistoryFragment() {
    }

    public static CompletedHistoryFragment newInstance() {
        CompletedHistoryFragment fragment = new CompletedHistoryFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_completed_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        requestData();

    }

    private void requestData(){
        User user = MangJekApplication.getInstance(getActivity()).getLoginUser();
        HistoryRequestJson request = new HistoryRequestJson();
        request.id = user.getId();

        UserService service = ServiceGenerator.createService(UserService.class, user.getEmail(), user.getPassword());
        service.getCompleteHistory(request).enqueue(new Callback<HistoryResponseJson>() {
            @Override
            public void onResponse(Call<HistoryResponseJson> call, Response<HistoryResponseJson> response) {
                if (response.isSuccessful()) {
                    ArrayList<ItemHistory> data = response.body().data;

//                    Log.e("HISTORY", data.get(0).toString());

                    for(int i = 0;i<data.size();i++){
                        switch (data.get(i).order_fitur){
                            case "M-ride":
                                data.get(i).image_id = R.drawable.ic_mride;
                                break;
                            case "M-car":
                                data.get(i).image_id = R.drawable.ic_mcar;
                                break;
                            case "M-send":
                                data.get(i).image_id = R.drawable.ic_msend;
                                break;
                            case "M-mart":
                                data.get(i).image_id = R.drawable.ic_mmart;
                                break;
                            case "M-box":
                                data.get(i).image_id = R.drawable.ic_mbox;
                                break;
                            case "M-service":
                                data.get(i).image_id = R.drawable.ic_mservice;
                                break;
                            case "M-massage":
                                data.get(i).image_id = R.drawable.ic_mmassage;
                                break;
                            case "M-food":
                                data.get(i).image_id = R.drawable.ic_mfood;
                                break;

                            default:
                                data.get(i).image_id = R.drawable.ic_mride;
                                break;
                        }
                    }

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

                    recyclerView.setLayoutManager(layoutManager);
                    historyAdapter = new HistoryAdapter(getContext(), data, true);
                    recyclerView.setAdapter(historyAdapter);
                    if (response.body().data.size() == 0) {
                        Log.d("HISTORY", "Empty");
                    }
                }
            }

            @Override
            public void onFailure(Call<HistoryResponseJson> call, Throwable t) {
                t.printStackTrace();
//                Toast.makeText(getActivity(), "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.e("System error:", t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        requestData();
    }

    @Override
    public void onRefresh() {
        requestData();
    }
}
