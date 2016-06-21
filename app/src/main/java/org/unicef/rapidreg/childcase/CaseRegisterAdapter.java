package org.unicef.rapidreg.childcase;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.widgets.viewholder.BaseViewHolder;
import org.unicef.rapidreg.widgets.viewholder.GenericViewHolder;
import org.unicef.rapidreg.widgets.viewholder.PhotoUploadHolder;
import org.unicef.rapidreg.widgets.viewholder.SeparatorViewHolder;
import org.unicef.rapidreg.widgets.viewholder.TickBoxViewHolder;

import java.io.Serializable;
import java.util.List;

public class CaseRegisterAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final static int VIEW_HOLDER_GENERIC = 0;
    private final static int VIEW_HOLDER_SEPARATOR = 1;
    private final static int VIEW_HOLDER_TICK_BOX = 2;
    private final static int VIEW_HOLDER_PHOTO_UPLOAD_BOX = 3;

    private List<CaseField> fields;
    private Context context;
    protected LayoutInflater inflater;
    protected Resources resources;
    protected String packageName;

    public CaseRegisterAdapter(Context context, List<CaseField> fields) {
        this.fields = fields;
        this.context = context;
        inflater = LayoutInflater.from(context);
        resources = context.getResources();
        packageName = context.getPackageName();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_HOLDER_GENERIC:
                return new GenericViewHolder(context, inflater.inflate(resources
                                .getIdentifier(CaseField.TYPE_TEXT_FIELD, "layout", packageName),
                        parent, false));
            case VIEW_HOLDER_TICK_BOX:
                return new TickBoxViewHolder(context, inflater.inflate(resources
                                .getIdentifier(CaseField.TYPE_TICK_BOX, "layout", packageName),
                        parent, false));
            case VIEW_HOLDER_PHOTO_UPLOAD_BOX:
                return new PhotoUploadHolder(context, inflater.inflate(resources
                                .getIdentifier(CaseField.TYPE_PHOTO_UPLOAD_LAYOUT, "layout", packageName),
                        parent, false));
            default:
                return new SeparatorViewHolder(context, new View(context));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        CaseField field = fields.get(position);
        holder.setValue(field);

        Serializable caseMode = ((Activity) context).getIntent()
                .getSerializableExtra(CaseActivity.INTENT_KEY_CASE_MODE);
        if (CaseActivity.CaseMode.DETAIL != caseMode) {
            holder.setOnClickListener(field);
        }
    }

    @Override
    public int getItemViewType(int position) {
        CaseField field = fields.get(position);
        if (field.isSeparator()) {
            return VIEW_HOLDER_SEPARATOR;
        }
        if (field.isTickBox()) {
            return VIEW_HOLDER_TICK_BOX;
        }
        if (field.isPhotoUploadBox()) {
            return VIEW_HOLDER_PHOTO_UPLOAD_BOX;
        }
        return VIEW_HOLDER_GENERIC;
    }

    @Override
    public int getItemCount() {
        return fields.size();
    }
}
