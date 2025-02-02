package org.flintcore.excel_expenses.managers.events.texts.filters.business;

import javafx.animation.PauseTransition;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import javafx.util.Subscription;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.events.combo_box.keyboards.RemoteKeyListener;
import org.flintcore.excel_expenses.managers.events.texts.filters.TextFilterListenerManager;
import org.flintcore.excel_expenses.managers.filters.BasicBusinessStringConverter;
import org.flintcore.excel_expenses.managers.rules.ILocalBusinessRules;
import org.flintcore.excel_expenses.models.business.IBusiness;
import org.flintcore.excel_expenses.models.properties.formatters.RNCFormatter;
import org.flintcore.utilities.bindings.NoChangeObjectObservable;
import org.flintcore.utilities.lists.ObservableListUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Log4j2
public class RNCFilterManager extends TextFilterListenerManager<IBusiness> {
    // Own properties
    private final ReadOnlyObjectWrapper<IBusiness> selectedBusinessProperty;
    private final NoChangeObjectObservable<IBusiness> hasNotUpdateSelectBinding;
    // Listeners
    private final RemoteKeyListener remoteKeyListener;

    // Combo Filter
    protected final ComboBox<IBusiness> optionsBox;

    // Global bindings
    protected BooleanBinding canFilterTextBinding, canSelectItemBinding;

    public RNCFilterManager(
            TextField textFilter,
            RemoteKeyListener remoteKeyListener,
            @NonNull ComboBox<IBusiness> optionsBox
    ) {
        super(textFilter, null, IBusiness::getRNC);
        this.remoteKeyListener = remoteKeyListener;
        this.optionsBox = optionsBox;
        this.selectedBusinessProperty = new ReadOnlyObjectWrapper<>();
        this.hasNotUpdateSelectBinding = NoChangeObjectObservable.bind(this.selectedBusinessProperty);
    }

    @Override
    public void setup() {
        addKeyListeners();

        setupItemsFilterValues();

        setUpFilterItemsListen();
        setUpOptionsBoxListen();
    }

    public ReadOnlyObjectProperty<IBusiness> getSelectedBusiness() {
        return this.selectedBusinessProperty.getReadOnlyProperty();
    }

    @SuppressWarnings("unchecked")
    public <T extends IBusiness> void addItems(List<T> items) {
        ((ObservableList<T>) this.mainListProperty.get()).addAll(items);
    }

    @SuppressWarnings("unchecked")
    public <T extends IBusiness> void addItems(@NonNull T... items) {
        ((ObservableList<T>) this.mainListProperty.get()).addAll(items);
    }

    @SuppressWarnings("all")
    public <T extends IBusiness> void removeItems(List<T> items) {
        this.mainListProperty.get().removeAll(items);
    }

    @SuppressWarnings("unchecked")
    public <T extends IBusiness> void removeItems(@NonNull T... items) {
        ((ObservableList<T>) this.mainListProperty.get()).removeAll(items);
    }

    // Settings

    private void setupItemsFilterValues() {
        this.itemsFilteredProperty.set(
                ObservableListUtils.wrapInto(this.optionsBox.getItems())
        );

        this.optionsBox.itemsProperty().bind(this.itemsFilteredProperty);

        this.selectedBusinessProperty.subscribe(b -> {
            if (Objects.isNull(b)) return;
            this.textFilterProperty.set(b.getRNC());
        });

        // Listen on changes inside the selection list.
        this.optionsBox.valueProperty().subscribe(this.selectedBusinessProperty::set);
    }

    protected void setUpFilterItemsListen() {
        BooleanBinding mainListNotEmpty = this.mainListSizeProperty.greaterThan(0);

        this.canFilterTextBinding = this.textFilterProperty.isNotEmpty()
                .and(mainListNotEmpty);

        this.textFilter.setTextFormatter(
                new RNCFormatter(ILocalBusinessRules.RNC_SIZE)
        );

        final PauseTransition filterDebounce = new PauseTransition(Duration.millis(500));

        // TODO Create better subscription when set textField value
        //  when select programmatically.
        Subscription onFilterSubscription = this.textFilterProperty
                .when(mainListNotEmpty).when(hasNotUpdateSelectBinding)
                .subscribe((__o, text) -> {
                    filterDebounce.stop();
                    log.info("Text changed");
                    filterDebounce.setOnFinished(e ->
                            this.itemsFilteredProperty.get().setPredicate(buildFilter(text))
                    );

                    filterDebounce.playFromStart();
                });


        // When there is a selection and text field lost focus and updated to a different value
        // Revert value

        this.textFilterProperty
                .when(hasNotUpdateSelectBinding)
                .when(this.textFilter.focusedProperty().not())
                .subscribe((old, curr) -> {
                    ObjectProperty<IBusiness> businessProperty = this.selectedBusinessProperty;

                    if (businessProperty.isNull().get()) return;
                    this.textFilterProperty.set(businessProperty.get().getRNC());
                });
    }

    private void setUpOptionsBoxListen() {
        canSelectItemBinding = this.itemsFilteredSizeProperty.greaterThan(0);

        this.optionsBox.setConverter(
                new BasicBusinessStringConverter<>(this.optionsBox::getItems)
        );
    }

    private void addKeyListeners() {
        initKeyFilterListener();

        this.remoteKeyListener.appendListener(this.optionsBox::show);

        this.remoteKeyListener.appendListener(KeyCode.ESCAPE,
                () -> this.optionsBox.setValue(null)
        );

        this.remoteKeyListener.appendListener(KeyCode.UP,
                () -> this.optionsBox.getSelectionModel().selectPrevious()
        );

        this.remoteKeyListener.appendListener(KeyCode.DOWN,
                () -> this.optionsBox.getSelectionModel().selectNext()
        );

        // select the current index selected.
        this.remoteKeyListener.appendListener(KeyCode.ENTER, () -> {
            // can select item in list ?
            if (!this.canSelectItemBinding.get()) return;
            IBusiness currentSelection = this.optionsBox.getValue();
            this.selectedBusinessProperty.setValue(currentSelection);
            this.optionsBox.hide();
        });
    }

    protected Predicate<IBusiness> buildFilter(String toFilter) {
        if (this.canFilterTextBinding.not().get()) return null;
        return b -> this.filterComparator.apply(b).contains(toFilter);
    }

    private void initKeyFilterListener() {
        this.textFilter.setOnKeyPressed(this.remoteKeyListener);
    }

    @Override
    public void close() {
        // Do nothing
    }

    @SuppressWarnings("unchecked")
    public ObservableList<? super IBusiness> getItems() {
        return (ObservableList<? super IBusiness>) this.mainListProperty.get();
    }

    public void clearSelection() {
        this.optionsBox.setValue(null);
    }
}
