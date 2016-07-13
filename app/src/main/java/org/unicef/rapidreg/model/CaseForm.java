package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.db.PrimeroDB;

@Table(database = PrimeroDB.class)
public class CaseForm extends BaseForm {
    public CaseForm() {
    }

    public CaseForm(Blob form) {
        super(form);
    }
}
