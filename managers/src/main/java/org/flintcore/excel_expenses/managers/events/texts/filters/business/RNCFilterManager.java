package org.flintcore.excel_expenses.managers.events.texts.filters.business;

import data.utils.NullableUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import javafx.util.Subscription;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.events.combo_box.keyboards.RemoteKeyListener;
import org.flintcore.excel_expenses.managers.events.texts.filters.TextFilterListenerManager;
import org.flintcore.excel_expenses.models.expenses.IBusiness;
import org.flintcore.utilities.lists.ObservableListUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Predicate;

@Log4j2
public class RNCFilterManager extends TextFilterListenerManager<IBusiness>
        implements Closeable {
    // Own properties
    public final Property<IBusiness> selectedBusinessProperty;
    // Listeners
    @Getter
    protected RemoteKeyListener remoteKeyListener;

    // Combo Filter
    protected final ComboBox<IBusiness> optionsBox;

    // Global bindings
    private BooleanBinding canFilterTextBinding, canSelectItemBinding;

    public RNCFilterManager(TextField textFilter, @NonNull ComboBox<IBusiness> optionsBox) {
        super(textFilter, null, IBusiness::getRNC);
        this.optionsBox = optionsBox;
        this.selectedBusinessProperty = new SimpleObjectProperty<>();
    }

    @Override
    public void setup() {
        this.subsManager.close();

        // Listen changes on the combo Box and set to main binding
        this.itemsFilteredProperty.bind(
                this.optionsBox.itemsProperty().map(ObservableListUtils::wrapInto)
        );

        setUpFilterItemsListen();
        setUpOptionsBoxListen();
        addKeyListeners();
    }

    protected void setUpFilterItemsListen() {
        ObservableValue<? extends ObservableList<? extends IBusiness>> sourceMap =
                this.itemsFilteredProperty.map(FilteredList::getSource);

        BooleanBinding mainListNotEmptyBinding = Bindings.createBooleanBinding(() -> {
            ObservableList<? extends IBusiness> value = sourceMap.getValue();
            return Objects.isNull(value) || value.isEmpty();
        }, sourceMap).not();

        this.canFilterTextBinding = this.itemsFilteredProperty.isNotNull()
                .or(this.textFilterProperty.isNotEmpty())
                .and(mainListNotEmptyBinding);

        final PauseTransition filterDebounce = new PauseTransition(
                Duration.millis(450)
        );

        Subscription onFilterSubscription = this.textFilterProperty
                .when(this.canFilterTextBinding)
                .when(this.textFilterNotEmptyBinding)
                .subscribe(text -> {
                    filterDebounce.stop();

                    filterDebounce.setOnFinished(e ->
                            this.itemsFilteredProperty.get()
                                    .setPredicate(buildFilter(text))
                    );

                    filterDebounce.playFromStart();
                });

        this.subsManager.appendSubscriptionOn(this, onFilterSubscription);
    }

    private void setUpOptionsBoxListen() {
        canSelectItemBinding = Bindings.createBooleanBinding(() -> {
            FilteredList<IBusiness> filteredList = this.itemsFilteredProperty.get();
            return Objects.isNull(filteredList) || filteredList.isEmpty();
        }, this.itemsFilteredProperty).not();
    }

    private void addKeyListeners() {
        initKeyFilterListener();

        this.subsManager.appendSubscriptionOn(this,
                this.remoteKeyListener.appendHandlerListener(KeyCode.DOWN,
                        () -> this.optionsBox.getSelectionModel().selectNext()
                )
        );

        this.subsManager.appendSubscriptionOn(this,
                this.remoteKeyListener.appendHandlerListener(KeyCode.UP,
                        () -> this.optionsBox.getSelectionModel().selectPrevious()
                )
        );

        this.remoteKeyListener.appendGeneralListener(() -> this.optionsBox.show());

        // select the current index selected.
        this.subsManager.appendSubscriptionOn(this,
                this.remoteKeyListener.appendHandlerListener(KeyCode.ENTER, () -> {
                    // can select item in list ?
                    if (!this.canSelectItemBinding.get()) return;
                    this.selectedBusinessProperty.setValue(this.optionsBox.valueProperty().get());
                    this.optionsBox.hide();
                })
        );
    }

    private void initKeyFilterListener() {
        NullableUtils.executeIsNull(this.remoteKeyListener, () -> {
            this.remoteKeyListener = new RemoteKeyListener();
            this.textFilter.setOnKeyPressed(this.remoteKeyListener);
        });
    }

    private Predicate<IBusiness> buildFilter(String toFilter) {
        return b -> this.filterComparator.apply(b).contains(toFilter);
    }

    @Override
    public void close() throws IOException {
        Platform.runLater(this.subsManager::close);
    }
}
