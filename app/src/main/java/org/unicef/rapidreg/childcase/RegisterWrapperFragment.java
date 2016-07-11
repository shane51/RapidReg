package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentStatePagerItemAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.view.SwipeChangeLayout;
import org.unicef.rapidreg.childcase.media.CasePhotoAdapter;
import org.unicef.rapidreg.event.SaveCaseEvent;
import org.unicef.rapidreg.event.UpdateImageEvent;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.forms.childcase.CaseFormRoot;
import org.unicef.rapidreg.forms.childcase.CaseSection;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.cache.CaseFieldValueCache;
import org.unicef.rapidreg.service.cache.SubformCache;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterWrapperFragment extends Fragment {

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.viewpagertab)
    SmartTabLayout viewPagerTab;

    @BindView(R.id.mini_form_layout)
    RelativeLayout miniFormLayout;

    @BindView(R.id.full_form_layout)
    RelativeLayout fullFormLayout;

    @BindView(R.id.full_form_swipe_layout)
    SwipeChangeLayout fullFormSwipeLayout;

    @BindView(R.id.mini_form_swipe_layout)
    SwipeChangeLayout miniFormSwipeLayout;

    @BindView(R.id.mini_form_container)
    RecyclerView miniFormContainer;

    @BindView(R.id.edit_case)
    FloatingActionButton editCaseButton;

    private CaseFormRoot caseForm;
    private List<CaseSection> sections;
    private List<CaseField> miniFields;
    private CaseRegisterAdapter miniFormAdapter;
    private CaseRegisterAdapter fullFormAdapter;
    private CasePhotoAdapter casePhotoAdapter;

    private long caseId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_cases_register_wrapper, container, false);
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            caseId = getArguments().getLong("case_id");
        }

        initCaseFormData();
        initFloatingActionButton();

        miniFormAdapter = new CaseRegisterAdapter(getActivity(), miniFields, true);
        miniFormAdapter.setCasePhotoAdapter(initCasePhotoAdapter());

        initFullFormContainer();
        initMiniFormContainer();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.edit_case)
    public void onCaseEditClicked() {
        ((CaseActivity) getActivity()).turnToDetailOrEditPage(Feature.EDIT, caseId);
    }

    private CasePhotoAdapter initCasePhotoAdapter() {
        casePhotoAdapter = new CasePhotoAdapter(getContext(), new ArrayList<String>());

        List<CasePhoto> casesPhotoFlowQueryList = CasePhotoService.getInstance().getAllCasesPhotoFlowQueryList(caseId);
        for (int i = 0; i < casesPhotoFlowQueryList.size(); i++) {
            casePhotoAdapter.addItem(casesPhotoFlowQueryList.get(i).getId());
        }
        return casePhotoAdapter;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void updateImageAdapter(UpdateImageEvent event) {
        casePhotoAdapter.addItem(event.getImagePath());
        ImageButton view = (ImageButton) getActivity().findViewById(R.id.add_image_button);

        if (!casePhotoAdapter.isEmpty()) {
            view.setImageResource(R.drawable.photo_add);
        }
        if (casePhotoAdapter.isFull()) {
            view.setVisibility(View.GONE);
        }

        casePhotoAdapter.notifyDataSetChanged();
        EventBus.getDefault().removeStickyEvent(event);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void saveCase(SaveCaseEvent event) {
        List<String> photoPaths = casePhotoAdapter.getAllItems();
        CaseService.getInstance().saveOrUpdateCase(CaseFieldValueCache.getValues(),
                SubformCache.getValues(),
                photoPaths);
    }

    private void initFloatingActionButton() {
        if (((CaseActivity) getActivity()).getCurrentFeature() == Feature.DETAILS) {
            editCaseButton.setVisibility(View.VISIBLE);
        } else {
            editCaseButton.setVisibility(View.GONE);
        }
    }

    private void initMiniFormContainer() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        if (!miniFields.isEmpty()) {
            miniFormContainer.setLayoutManager(layoutManager);
            miniFormContainer.setAdapter(miniFormAdapter);
            miniFormSwipeLayout.setDragEdge(SwipeChangeLayout.DragEdge.BOTTOM);
            miniFormSwipeLayout.setShouldGoneContainer(miniFormLayout);
            miniFormSwipeLayout.setShouldShowContainer(fullFormLayout);
            miniFormSwipeLayout.setScrollChild(miniFormContainer);
            miniFormSwipeLayout.setOnSwipeBackListener(new SwipeChangeLayout.SwipeBackListener() {
                @Override
                public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {
                    if (fullFormAdapter != null) {
                        fullFormAdapter.setCasePhotoAdapter(casePhotoAdapter);
                        fullFormAdapter.notifyDataSetChanged();
                    }
                }
            });
        } else {
            miniFormSwipeLayout.setEnableFlingBack(false);
            miniFormLayout.setVisibility(View.GONE);
            fullFormLayout.setVisibility(View.VISIBLE);
            fullFormSwipeLayout.setEnableFlingBack(false);
        }
    }

    private void initFullFormContainer() {
        final FragmentStatePagerItemAdapter adapter = new FragmentStatePagerItemAdapter(
                getActivity().getSupportFragmentManager(), getPages());
        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);

        viewPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                fullFormSwipeLayout.setScrollChild(
                        adapter.getPage(position).getView()
                                .findViewById(R.id.register_forms_content));
                fullFormAdapter = ((CaseRegisterFragment) adapter.getPage(position)).getCaseRegisterAdapter();
                fullFormAdapter.setCasePhotoAdapter(casePhotoAdapter);
                fullFormAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (miniFields.size() != 0) {
            fullFormSwipeLayout.setDragEdge(SwipeChangeLayout.DragEdge.TOP);
            fullFormSwipeLayout.setShouldGoneContainer(fullFormLayout);
            fullFormSwipeLayout.setShouldShowContainer(miniFormLayout);
            fullFormSwipeLayout.setOnSwipeBackListener(new SwipeChangeLayout.SwipeBackListener() {
                @Override
                public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {
                    miniFormAdapter.notifyDataSetChanged();
                }
            });
        } else {
            fullFormSwipeLayout.setEnableFlingBack(false);
        }
    }

    private void initCaseFormData() {
        caseForm = CaseFormService.getInstance().getCurrentForm();
        sections = caseForm.getSections();
        miniFields = new ArrayList<>();
        if (caseForm != null) {
            getMiniFields();
        }
    }

    private void getMiniFields() {
        for (CaseSection section : sections) {
            for (CaseField caseField : section.getFields()) {
                if (caseField.isShowOnMiniForm()) {
                    if (caseField.isPhotoUploadBox()) {
                        miniFields.add(0, caseField);
                    } else {
                        miniFields.add(caseField);
                    }
                }
            }
        }
        addProfileFieldForDetailsPage();
    }

    private void addProfileFieldForDetailsPage() {
        if (((CaseActivity) getActivity()).getCurrentFeature() == Feature.DETAILS) {
            CaseField caseField = new CaseField();
            caseField.setType(CaseField.TYPE_MINI_FORM_PROFILE);
            try {
                miniFields.add(1, caseField);
            } catch (Exception e) {
                miniFields.add(caseField);
            }
        }
    }

    @NonNull
    private FragmentPagerItems getPages() {
        FragmentPagerItems pages = new FragmentPagerItems(getActivity());
        for (CaseSection section : sections) {
            String[] values = section.getName().values().toArray(new String[0]);
            Bundle bundle = new Bundle();

            bundle.putStringArrayList("case_photos",
                    (ArrayList<String>) casePhotoAdapter.getAllItems());

            pages.add(FragmentPagerItem.of(values[0], CaseRegisterFragment.class, bundle));
        }
        return pages;
    }

    public void clearFocus() {
        View focusedChild = miniFormContainer.getFocusedChild();
        if (focusedChild != null) {
            focusedChild.clearFocus();
        }

        FragmentStatePagerItemAdapter adapter =
                (FragmentStatePagerItemAdapter) viewPager.getAdapter();
        CaseRegisterFragment fragment = (CaseRegisterFragment) adapter
                .getPage(viewPager.getCurrentItem());
        if (fragment != null) {
            fragment.clearFocus();
        }
    }
}