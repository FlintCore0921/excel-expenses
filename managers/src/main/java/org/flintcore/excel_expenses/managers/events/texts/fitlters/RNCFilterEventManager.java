package org.flintcore.excel_expenses.managers.events.texts.fitlters;

import data.utils.NullableUtils;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.StringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import javafx.util.Subscription;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.events.combo_box.keyboards.ComboBoxRemoteKeyListener;
import org.flintcore.excel_expenses.models.expenses.IBusiness;
import org.flintcore.utilities.lists.ObservableListUtils;

import java.util.function.Predicate;

@Log4j2
public class RNCFilterEventManager extends TextFieldListenerEventManager<IBusiness> {
    protected ComboBox<IBusiness> optionsBox;
    @Getter
    protected ComboBoxRemoteKeyListener remoteRNCKeyListener;

    private BooleanBinding mainListNotEmptyBinding, canFilterTextBinding,
            canSelectItemBinding;

    public RNCFilterEventManager(TextField textFilter, @NonNull ComboBox<IBusiness> optionsBox) {
        super(textFilter, null, IBusiness::getRNC);
        this.optionsBox = optionsBox;
    }

    @Override
    public void setup() {
        this.subsManager.close();
        this.itemsSupplier = () -> {
            FilteredList<IBusiness> filteredList = ObservableListUtils
                    .wrapInto(optionsBox.getItems());
            optionsBox.setItems(filteredList);
            return filteredList;
        };
        setUpFilterItemsListen();
        setUpOptionsBox();
    }

    protected void setUpFilterItemsListen() {
        StringProperty textFilterProperty = this.textFilter.textProperty();

        mainListNotEmptyBinding = Bindings.createBooleanBinding(
                this.itemsSupplier.get().getSource()::isEmpty,
                textFilterProperty
        ).not();

        canFilterTextBinding = textFilterProperty.isNotEmpty()
                .and(mainListNotEmptyBinding);

        PauseTransition filterDebounce = new PauseTransition(
                Duration.seconds(.45)
        );

        Subscription onFilterSubscription = textFilterProperty.subscribe(textField -> {
            // Ends if filter cannot filter
            boolean canFilter = canFilterTextBinding.get();

            log.info("Can filter: {}", canFilter);

            if (!canFilter) {
                filterDebounce.stop();
                this.itemsSupplier.get().setPredicate(null);
                return;
            }

            filterDebounce.setOnFinished(e ->
                    this.itemsSupplier.get().setPredicate(buildFilter(textField))
            );
            filterDebounce.playFromStart();
        });

        this.subsManager.appendSubscriptionOn(this, onFilterSubscription);
    }

    private void setUpOptionsBox() {
        NullableUtils.executeIsNull(this.remoteRNCKeyListener,
                () -> this.remoteRNCKeyListener = new ComboBoxRemoteKeyListener(
                        this.optionsBox.getItems()::size
                )
        );

        // select the current index selected.
        this.remoteRNCKeyListener.appendOnEnterListener(
                this.optionsBox.getSelectionModel()::select
        );

        this.remoteRNCKeyListener.appendOnHandleListener(this.optionsBox::show);

        setUpOptionsValuesListen();
    }

    private void setUpOptionsValuesListen() {
        BooleanBinding isFilterListNotEmpty = Bindings.createBooleanBinding(
                this.optionsBox.getItems()::isEmpty, this.optionsBox.getItems()
        ).not();

        canSelectItemBinding = Bindings.and(
                mainListNotEmptyBinding,
                isFilterListNotEmpty
        );
    }

    private Predicate<IBusiness> buildFilter(String toFilter) {
        return b -> this.filterComparator.apply(b).contains(toFilter);
    }
}
