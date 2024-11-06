package org.flintcore.excel_expenses.managers.holders;

import data.utils.NullableUtils;
import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.util.Duration;
import javafx.util.Subscription;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.factories.transitions.TransitionFactory;
import org.flintcore.excel_expenses.managers.factories.views.IItemViewHandler;
import org.flintcore.excel_expenses.managers.factories.views.receipts.ExpenseItemPreviewFactory;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.flintcore.utilities.lists.ObservableListUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;

/**
 * Class to handle the list of elements provided to manage filtering
 */
@Log4j2
@Component
public class ExpenseItemManager {
    public static final int ANIMATION_DURATION = 600;

    protected final ExpenseItemPreviewFactory expenseItemFactory;
    protected final TransitionFactory transitionFactory;

    protected ReadOnlyObjectWrapper<FilteredList<Receipt>> receiptsFilterList;
    protected ObservableSet<IItemViewHandler<Receipt, Node>> itemHandlers;
    protected Map<Receipt, Transition> transitionHolders;
    protected ObjectProperty<Predicate<Receipt>> predicateProperty;
    // Hold the list set current listener and refreshed it.
    protected Subscription filterListSubscription;

    public ExpenseItemManager(
            ExpenseItemPreviewFactory expenseItemFactory,
            TransitionFactory transitionFactory
    ) {
        this.expenseItemFactory = expenseItemFactory;
        this.transitionFactory = transitionFactory;
    }

    /**
     * Update the list based on filter predicate provided.
     */
    public void setFilter(Predicate<Receipt> filter) {
        this.predicateProperty.set(filter);
    }

    /**
     * Update the current list provided by the new one.
     */
    public void setItems(ObservableList<Receipt> receipts) {
        initProperties();

        // Set predicate
        this.receiptsFilterList.subscribe(
                filter -> filter.setPredicate(this.predicateProperty.get())
        );

        // Set the value in properties.
        this.receiptsFilterList.set(ObservableListUtils.wrapInto(receipts));
    }

    /**
     * Create a new unmodifiable list based on the available data by filter assigned.
     *
     * @return a new unmodifiable list with the current values.
     */
    public ObservableList<IItemViewHandler<Receipt, Node>> getReceipts() {
        initProperties();

        ObservableList<IItemViewHandler<Receipt, Node>> result = FXCollections.observableArrayList(
                this.itemHandlers
        );

        ObservableListUtils.listenSet(result, this.itemHandlers);

        return FXCollections.unmodifiableObservableList();
    }

    protected ListChangeListener<? super Receipt> updateListHandlers() {
        return c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Receipt receipt : c.getAddedSubList()) {
                        applyNewItemMember(receipt);
                        triggerAnimationOn(receipt, true);
                    }
                }

                if (c.wasRemoved()) {
                    for (Receipt receipt : c.getRemoved()) {
                        triggerAnimationOn(receipt, false);
                    }
                }
            }
        };
    }

    private void triggerAnimationOn(Receipt receipt, boolean hasEnter) {
        NullableUtils.executeNonNull(this.transitionHolders.get(receipt), anim -> {
            anim.stop();
            anim.setRate(hasEnter ? 1 : -1);
            anim.playFromStart();
        });
    }

    private void applyNewItemMember(Receipt receipt) {
        IItemViewHandler<Receipt, Node> handler = null;

        if (this.itemHandlers.stream().noneMatch(hd -> hd.getValue() == receipt)) {
            Optional<? extends IItemViewHandler<Receipt, Node>> viewHandler = this.expenseItemFactory
                    .buildView();

            if (viewHandler.isEmpty()) {
                log.warn("Unable to create item receipt {} for holder {}",
                        receipt, this.getClass().getSimpleName()
                );
                return;
            }

            // Assign found value to use it.
            // Add item to apply it.
            this.itemHandlers.add(handler = viewHandler.get());
        }

        // IF Handler was not created and already exists
        if (Objects.isNull(handler)) {
            // Find by stream
            Optional<IItemViewHandler<Receipt, Node>> receiptHandler = this.itemHandlers.stream()
                    .filter(hd -> hd.getValue() == receipt)
                    .findFirst();

            // If not found alert and continue.
            // TODO Create a user message and feedback way to get back lose data.
            if (receiptHandler.isEmpty()) {
                log.warn("Holder could not find the given receipt. Value: {}", receipt);
                return;
            }

            // Assign found value to use it.
            handler = receiptHandler.get();
        }

        // Create Transitions
        assignTransitionOf(handler);
    }

    private void assignTransitionOf(IItemViewHandler<Receipt, Node> handler) {
        Receipt handlerValue = handler.getValue();
        if (this.transitionHolders.containsKey(handlerValue)) return;

        final Transition showTransition = this.transitionFactory.createSequentialTransition(
                handler.getView(), Duration.millis(ANIMATION_DURATION),
                List.of(this.transitionFactory::createFadeInTransition,
                        this.transitionFactory::createSlideInHorizontallyTransition
                )
        );

        showTransition.setOnFinished(evt -> {
            if (this.receiptsFilterList.get().getSource().contains(handlerValue)) return;

            this.itemHandlers.remove(handler);
            this.transitionHolders.remove(handlerValue);
        });

        this.transitionHolders.putIfAbsent(handlerValue, showTransition);
    }

    @SuppressWarnings("unchecked")
    protected void initProperties() {
        // Init list predicate
        NullableUtils.executeIsNull(this.predicateProperty,
                () -> this.predicateProperty = new SimpleObjectProperty<>()
        );

        NullableUtils.executeIsNull(this.receiptsFilterList, () -> {
            this.receiptsFilterList = new ReadOnlyObjectWrapper<>();

            //Check on change list triggers to validate new data
            ListChangeListener<? super Receipt> listener = updateListHandlers();

            this.receiptsFilterList.subscribe((old, curr) -> {
                NullableUtils.executeNonNull(this.filterListSubscription, Subscription::unsubscribe);
                this.filterListSubscription = () -> curr.addListener(listener);
                curr.addListener(listener);

                curr.setPredicate(this.predicateProperty.getValue());
            });
        });
        // Set items handlers, view and value holder to preview.
        NullableUtils.executeIsNull(this.itemHandlers,
                () -> this.itemHandlers = FXCollections.observableSet()
        );

        // Set the transition holder
        NullableUtils.executeIsNull(this.transitionHolders,
                () -> this.transitionHolders = new HashMap<>()
        );
    }
}
