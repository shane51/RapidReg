package org.unicef.rapidreg.childcase;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.cache.CaseFieldValueCache;
import org.unicef.rapidreg.service.cache.SubformCache;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ListFragment extends MvpFragment<CaseListView, CaseListPresenter>
        implements CaseListView {

    private static final SpinnerState[] SPINNER_STATES = {
            SpinnerState.AGE_ASC,
            SpinnerState.AGE_DES,
            SpinnerState.DATE_ASC,
            SpinnerState.DATE_DES};

    private static final int DEFAULT_SPINNER_STATE_POSITION =
            Arrays.asList(SPINNER_STATES).indexOf(SpinnerState.DATE_DES);

    private static final int HAVE_RESULT_LIST = 0;
    private static final int HAVE_NO_RESULT = 1;

    @BindView(R.id.list_container)
    RecyclerView caseListContainer;

    @BindView(R.id.order_spinner)
    Spinner orderSpinner;

    @BindView(R.id.floating_menu)
    FloatingActionsMenu floatingMenu;

    @BindView(R.id.container)
    LinearLayout container;

    @BindView(R.id.list_result)
    ViewSwitcher viewSwitcher;

    private CaseListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cases_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        presenter.initView(getActivity());
    }

    @Override
    public CaseListPresenter createPresenter() {
        return new CaseListPresenter();
    }

    @Override
    public void initView(final CaseListAdapter adapter) {
        this.adapter = adapter;

        initCaseListContainer(adapter);
        initOrderSpinner(adapter);
        initFloatingMenu();
    }

    private void initCaseListContainer(CaseListAdapter adapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        caseListContainer.setLayoutManager(layoutManager);
        caseListContainer.setAdapter(adapter);

        List<Case> caseList = CaseService.getInstance().getCaseList();
        int index = caseList.isEmpty() ? HAVE_NO_RESULT : HAVE_RESULT_LIST;
        viewSwitcher.setDisplayedChild(index);

    }

    private void initOrderSpinner(final CaseListAdapter adapter) {
        orderSpinner.setAdapter(new SpinnerAdapter(getActivity(),
                R.layout.case_list_spinner_opened, Arrays.asList(SPINNER_STATES)));
        orderSpinner.setSelection(DEFAULT_SPINNER_STATE_POSITION);
        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleItemSelection(position);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            private void handleItemSelection(int position) {
                CaseService caseService = CaseService.getInstance();
                switch (SPINNER_STATES[position]) {
                    case AGE_ASC:
                        adapter.setCaseList(caseService.getCaseListOrderByAgeASC());
                        break;
                    case AGE_DES:
                        adapter.setCaseList(caseService.getCaseListOrderByAgeDES());
                        break;
                    case DATE_ASC:
                        adapter.setCaseList(caseService.getCaseListOrderByDateASC());
                        break;
                    case DATE_DES:
                        adapter.setCaseList(caseService.getCaseListOrderByDateDES());
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initFloatingMenu() {
        floatingMenu.setOnFloatingActionsMenuUpdateListener(
                new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                    @Override
                    public void onMenuExpanded() {
                        setListAlpha(0.5f);
                    }

                    @Override
                    public void onMenuCollapsed() {
                        setListAlpha(1.0f);
                    }
                });
    }

    @OnClick(R.id.add_case)
    public void onCaseAddClicked() {
        CaseService.getInstance().clearCaseCache();
        SubformCache.clear();
        CaseFieldValueCache.clearAudioFile();
        if (!CaseFormService.getInstance().isFormReady()) {
            Toast.makeText(getActivity(),
                    R.string.syncing_forms_text, Toast.LENGTH_LONG).show();
            return;
        }
        floatingMenu.collapseImmediately();

        CaseActivity activity = (CaseActivity) getActivity();
        activity.turnToFeature(Feature.ADD, null);
    }

    public void toggleMode(boolean isShow) {
        adapter.toggleViews(isShow);
    }

    private void setListAlpha(float value) {
        container.setAlpha(value);
    }

    private enum SpinnerState {
        AGE_ASC(R.drawable.age_up, "Age ascending order", "Age"),
        AGE_DES(R.drawable.age_down, "Age descending order", "Age"),
        DATE_ASC(R.drawable.date_up, "Registration date ascending order", "Registration date"),
        DATE_DES(R.drawable.date_down, "Registration date descending order", "Registration date");

        private int resId;
        private String longName;
        private String shortName;

        SpinnerState(int resId, String longName, String shortName) {
            this.resId = resId;
            this.longName = longName;
            this.shortName = shortName;
        }

        public int getResId() {
            return resId;
        }

        public String getLongName() {
            return longName;
        }

        public String getShortName() {
            return shortName;
        }
    }

    private class SpinnerAdapter extends ArrayAdapter<SpinnerState> {

        private final List<SpinnerState> states;

        public SpinnerAdapter(Context context, int resource, List<SpinnerState> states) {
            super(context, resource, states);
            this.states = states;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, parent, true);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, parent, false);
        }

        public View getCustomView(int position, ViewGroup parent, boolean isDropDownView) {
            SpinnerState state = states.get(position);

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            int layoutId = isDropDownView ?
                    R.layout.case_list_spinner_opened : R.layout.case_list_spinner_closed;
            View view = inflater.inflate(layoutId, parent, false);

            ImageView indicator = (ImageView) view.findViewById(R.id.indicator);
            TextView orderName = (TextView) view.findViewById(R.id.order_name);

            indicator.setImageResource(state.getResId());
            orderName.setText(isDropDownView ? state.getLongName() : state.getShortName());

            return view;
        }
    }
}