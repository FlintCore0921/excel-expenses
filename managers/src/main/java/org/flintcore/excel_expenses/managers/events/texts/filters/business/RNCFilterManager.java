package org.flintcore.excel_expenses.managers.events.texts.filters.business;

import data.utils.NullableUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
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
import org.flintcore.excel_expenses.managers.rules.ILocalBusinessRules;
import org.flintcore.excel_expenses.models.expenses.IBusiness;
import org.flintcore.excel_expenses.models.properties.formatters.RNCFormatter;
import org.flintcore.utilities.lists.ObservableListUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

@Log4j2
public class RNCFilterManager extends TextFilterListenerManager<IBusiness>
        implements Closeable {
    // Own properties
    public final ObjectProperty<IBusiness> selectedBusinessProperty;
    // Listeners
    @Getter
    protected RemoteKeyListener remoteKeyListener;

    // Combo Filter
    protected final ComboBox<IBusiness> optionsBox;

    // Global bindings
    protected BooleanBinding canFilterTextBinding, canSelectItemBinding;

    public RNCFilterManager(TextField textFilter, @NonNull ComboBox<IBusiness> optionsBox) {
        super(textFilter, null, IBusiness::getRNC);
        this.optionsBox = optionsBox;
        this.selectedBusinessProperty = new SimpleObjectProperty<>();
    }

    @Override
    public void setup() {
        this.subsManager.close();

        setupItemsFilterValues();

        setUpFilterItemsListen();
        setUpOptionsBoxListen();
        addKeyListeners();
    }

    @SuppressWarnings("unchecked")
    public <T extends IBusiness> void addItems(List<T> items) {
        ((ObservableList<T>) this.mainListProperty.get()).addAll(items);
    }

    @SuppressWarnings("unchecked")
    public <T extends IBusiness> void addItems(@NonNull T... items) {
        ((ObservableList<T>) this.mainListProperty.get()).addAll(items);
    }

    public <T extends IBusiness> void removeItems(List<T> items) {
        this.mainListProperty.get().removeAll(items);
    }

    @SuppressWarnings("unchecked")
    public <T extends IBusiness> void removeItems(@NonNull T... items) {
        ((ObservableList<T>) this.mainListProperty.get()).removeAll(items);
    }

    private void setupItemsFilterValues() {
        this.itemsFilteredProperty.set(
                ObservableListUtils.wrapInto(this.optionsBox.getItems())
        );

        this.optionsBox.itemsProperty().bind(this.itemsFilteredProperty);
    }

    protected void setUpFilterItemsListen() {
        this.canFilterTextBinding = this.textFilterProperty.isNotEmpty()
                .and(this.mainListSizeProperty.greaterThan(0));

        this.textFilter.setTextFormatter(
                new RNCFormatter(ILocalBusinessRules.RNC_SIZE)
        );

        final PauseTransition filterDebounce = new PauseTransition(
                Duration.millis(450)
        );

        Subscription onFilterSubscription = this.textFilterProperty
                .when(this.canFilterTextBinding)
                .subscribe((__o, text) -> {
                    filterDebounce.stop();

                    filterDebounce.setOnFinished(e -> this.itemsFilteredProperty.get()
                            .setPredicate(buildFilter(text))
                    );

                    filterDebounce.playFromStart();
                });

        Subscription onNoFilterSubscription = this.textFilterProperty
                .when(this.canFilterTextBinding.not())
                .subscribe((__o, __n) -> this.itemsFilteredProperty.get().setPredicate(null));

        this.subsManager.appendSubscriptionOn(this, onFilterSubscription);
        this.subsManager.appendSubscriptionOn(this, onNoFilterSubscription);
    }

    private void setUpOptionsBoxListen() {
        canSelectItemBinding = this.itemsFilteredSizeProperty.greaterThan(0);
    }

    private void addKeyListeners() {
        initKeyFilterListener();

        this.subsManager.appendSubscriptionOn(this,
                this.remoteKeyListener.appendHandlerListener(KeyCode.UP,
                        () -> this.optionsBox.getSelectionModel().selectPrevious()
                )
        );

        this.subsManager.appendSubscriptionOn(this,
                this.remoteKeyListener.appendHandlerListener(KeyCode.DOWN,
                        () -> this.optionsBox.getSelectionModel().selectNext()
                )
        );

        this.remoteKeyListener.appendGeneralListener(this.optionsBox::show);

        // select the current index selected.
        this.subsManager.appendSubscriptionOn(this,
                this.remoteKeyListener.appendHandlerListener(KeyCode.ENTER, () -> {
                    // can select item in list ?
                    if (!this.canSelectItemBinding.get()) return;
                    IBusiness currentSelection = this.optionsBox.valueProperty().get();
                    this.selectedBusinessProperty.setValue(currentSelection);
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
        if (this.canFilterTextBinding.not().get()) return null;
        return b -> this.filterComparator.apply(b).contains(toFilter);
    }

    @Override
    public void close() throws IOException {
        Platform.runLater(this.subsManager::close);
    }

    @SuppressWarnings("unchecked")
    public ObservableList<? super IBusiness> getItems() {
        return (ObservableList<? super IBusiness>) this.mainListProperty.get();
    }
}
