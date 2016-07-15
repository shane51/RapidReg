package org.unicef.rapidreg.tracing;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.RecordPhotoAdapter;
import org.unicef.rapidreg.base.RecordRegisterWrapperFragment;
import org.unicef.rapidreg.event.SaveTracingEvent;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.forms.TracingFormRoot;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.model.TracingPhoto;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.service.TracingPhotoService;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.service.cache.ItemValues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.OnClick;

public class TracingRegisterWrapperFragment extends RecordRegisterWrapperFragment {
    @Override
    protected void initItemValues() {
        if (getArguments() != null) {
            recordId = getArguments().getLong(RecordService.RECORD_ID);
            Tracing tracingItem = TracingService.getInstance().getById(recordId);
            String tracingJson = new String(tracingItem.getContent().getBlob());
            String subFormJson = new String(tracingItem.getSubform().getBlob());
            itemValues = ItemValues.generateItemValues(tracingJson, subFormJson);
            itemValues.addStringItem(TracingService.TRACING_ID, tracingItem.getUniqueId());
            initProfile(tracingItem);
        }
    }

    @Override
    protected void initFormData() {
        form = TracingFormService.getInstance().getCurrentForm();
        sections = form.getSections();
        miniFields = new ArrayList<>();
        if (form != null) {
            getMiniFields();
        }
    }

    @Override
    protected RecordPhotoAdapter initPhotoAdapter() {
        recordPhotoAdapter = new TracingPhotoAdapter(getContext(), new ArrayList<String>());

        List<TracingPhoto> tracings = TracingPhotoService.getInstance().getByTracingId(recordId);
        for (int i = 0; i < tracings.size(); i++) {
            recordPhotoAdapter.addItem(tracings.get(i).getId());
        }
        return recordPhotoAdapter;
    }

    @NonNull
    protected FragmentPagerItems getPages() {
        FragmentPagerItems pages = new FragmentPagerItems(getActivity());
        for (Section section : sections) {
            String[] values = section.getName().values().toArray(new String[0]);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(RecordService.RECORD_PHOTOS,
                    (ArrayList<String>) recordPhotoAdapter.getAllItems());
            bundle.putString(RecordService.ITEM_VALUES, new Gson().toJson(itemValues.getValues()));
            pages.add(FragmentPagerItem.of(values[0], TracingRegisterFragment.class, bundle));
        }
        return pages;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void saveTracing(SaveTracingEvent event) {
        if (validateRequiredField()) {
            List<String> photoPaths = recordPhotoAdapter.getAllItems();
            TracingService.getInstance().saveOrUpdate(itemValues, photoPaths);
        }
    }

    @OnClick(R.id.edit)
    public void onEditClicked() {
        ((TracingActivity) getActivity()).turnToDetailOrEditPage(TracingFeature.EDIT, recordId);
    }

    private boolean validateRequiredField() {
        TracingFormRoot tracingForm = TracingFormService.getInstance().getCurrentForm();
        List<String> requiredFieldNames = new ArrayList<>();

        for (Section section : tracingForm.getSections()) {
            Collections.addAll(requiredFieldNames, RecordService
                    .fetchRequiredFiledNames(section.getFields()).toArray(new String[0]));
        }

        for (String field : requiredFieldNames) {
            if (TextUtils.isEmpty((CharSequence) itemValues.getValues().get(field))) {
                Toast.makeText(getActivity(), R.string.required_field_is_not_filled,
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }
}
