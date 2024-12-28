package org.flintcore.excel_expenses.models.business;

import java.io.Serializable;

public interface IBusiness extends Serializable, Comparable<IBusiness> {
    String getRNC();
    String getName();
}
