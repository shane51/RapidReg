package org.unicef.rapidreg.widgets.dialog;

import android.content.Context;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.exception.DialogException;
import org.unicef.rapidreg.forms.childcase.CaseField;

public class FiledDialogFactory {
    public static BaseDialog createDialog(CaseField.FieldType fieldType, Context context,
                                          CaseField caseField,
                                          TextView resultView) throws DialogException {
        try {
            return fieldType.getClz().getConstructor(Context.class, CaseField.class, TextView.class)
                    .newInstance(context, caseField, resultView);
        } catch (Exception e) {
            throw new DialogException(String.format("fieldType: %s", fieldType), e);
        }
    }

    public static BaseDialog createDialog(CaseField.FieldType fieldType, Context context,
                                          CaseField caseField, TextView resultView,
                                          ViewSwitcher viewSwitcher) throws DialogException {
        try {
            return fieldType.getClz().getConstructor(Context.class, CaseField.class,
                    TextView.class, ViewSwitcher.class)
                    .newInstance(context, caseField, resultView, viewSwitcher);
        } catch (Exception e) {
            throw new DialogException(String.format("fieldType: %s", fieldType), e);
        }
    }
}
