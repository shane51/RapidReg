package org.unicef.rapidreg.widgets.dialog;

import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.forms.Field;

public class DateDialog extends BaseDialog {
    private String result;
    private DatePicker datePicker;

    public DateDialog(Context context, Field field, TextView resultView, ViewSwitcher viewSwitcher) {
        super(context, field, resultView, viewSwitcher);
        result = resultView.getText().toString().trim();
    }

    @Override
    public void initView() {
        datePicker = new DatePicker(getContext());
        datePicker.setCalendarViewShown(false);
        if (!"".equals(result)) {
            String[] date = result.split("/");
            int year = Integer.valueOf(date[2]);
            int month = Integer.valueOf(date[0]) - 1;
            int day = Integer.valueOf(date[1]);
            datePicker.updateDate(year, month, day);
        }
        getBuilder().setView(datePicker);
    }

    @Override
    public String getResult() {
        return String.format("%s/%s/%s", datePicker.getMonth() + 1,
                datePicker.getDayOfMonth(), datePicker.getYear());
    }
}
