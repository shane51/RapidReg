package org.unicef.rapidreg.childcase.fielddialog;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import org.unicef.rapidreg.forms.childcase.CaseField;

import java.util.Arrays;

public class SingleSelectDialog extends BaseDialog {

    private String result;
    private String[] optionItems;

    public SingleSelectDialog(Context context, CaseField caseField, TextView resultView) {
        super(context, caseField, resultView);
        result = resultView.getText().toString().trim();
    }

    @Override
    public void initView() {
        String fieldType = caseField.getType();
        optionItems = getSelectOptions(fieldType, caseField);

        int selectIndex = Arrays.asList(optionItems).indexOf(result);
        selectIndex = selectIndex == -1 ? 0 : selectIndex;
        result = optionItems[selectIndex];

        getBuilder().setSingleChoiceItems(optionItems,
                selectIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result = optionItems[which];
                    }
                });
    }

    @Override
    public String getResult() {
        return result;
    }
}
