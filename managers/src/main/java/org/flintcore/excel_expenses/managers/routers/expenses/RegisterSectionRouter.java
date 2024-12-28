package org.flintcore.excel_expenses.managers.routers.expenses;

import data.utils.collections.ICollectionUtils;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.util.Pair;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.flintcore.excel_expenses.managers.routers.IRouter;
import org.flintcore.excel_expenses.managers.routers.factories.wrappers.FXMLWrapperRouteFactory;
import org.flintcore.excel_expenses.managers.routers.routes.EExpenseFormRoute;
import org.flintcore.excel_expenses.managers.routers.switchers.IViewSwitcher;
import org.flintcore.excel_expenses.models.NodeWrapper;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Log4j2
public final class RegisterSectionRouter implements IRouter<EExpenseFormRoute> {

    private final FXMLWrapperRouteFactory fxmlFactory;
    private final IViewSwitcher viewSwitcher;

    // Class
    private Map<EExpenseFormRoute, IRegisterSectionController> menuItemsMap;
    private AtomicReference<IRegisterSectionController> previousController;
    private AtomicInteger routeIndex;
    private AtomicReference<Transition> previousTransition;

    @Setter
    private Pane parentPane;

    public RegisterSectionRouter(FXMLWrapperRouteFactory fxmlFactory, IViewSwitcher viewSwitcher) {
        this.fxmlFactory = fxmlFactory;
        this.viewSwitcher = viewSwitcher;
    }

    @Override
    public void navigateBack() {
        if (this.routeIndex.get() <= 0) return;

        var currentIndex = this.routeIndex.decrementAndGet();
        this.navigateTo(getRoute(currentIndex));
    }

    public void navigateNext() {
        if (this.routeIndex.get() >= this.menuItemsMap.size()) return;

        var currentIndex = this.routeIndex.incrementAndGet();
        this.navigateTo(this.getRoute(currentIndex));
    }

    @Override
    public void navigateToHome() {
        this.routeIndex.set(0);
        this.navigateTo(getHomeRoute());
    }

    private EExpenseFormRoute getRoute(int currentIndex) {
        return List.copyOf(this.menuItemsMap.keySet()).get(currentIndex);
    }

    private EExpenseFormRoute getHomeRoute() {
        return getRoute(0);
    }

    @Override
    public void navigateTo(@NonNull EExpenseFormRoute route) {
        if (Objects.isNull(this.parentPane))
            log.error("Parent Pane is null! Unable to add view.");

        if (!this.menuItemsMap.containsKey(route)) return;

        if (Objects.isNull(this.parentPane)) {
            log.error("Parent Pane is null! Unable to add view.");
            return;
        }

        this.cancelNavigation();

        Optional<NodeWrapper<Node, ?>> nodeHolder = this.fxmlFactory.buildLoader(route);

        if (nodeHolder.isEmpty()) {
            log.error("Unable to load route {}", route);
            return;
        }

        NodeWrapper<Node, ?> nodeWrapper = nodeHolder.get();
        Node node = nodeWrapper.nodeView();
        IRegisterSectionController newViewController = (IRegisterSectionController) nodeWrapper
                .controller();

        IRegisterSectionController previousSectionController = this.previousController.get();

        Node previousView = Objects.nonNull(previousSectionController) ?
                previousSectionController.getNode() : null;

        var switchTransition = this.viewSwitcher.switchTo(
                this.parentPane, new Pair<>(node, Duration.millis(600D)),
                new Pair<>(previousView, Duration.millis(500D))
        );

        this.previousController.set(newViewController);
        this.previousTransition.set(switchTransition);

        switchTransition.play();
    }

    private void cancelNavigation() {
        var transition = this.previousTransition.get();

        if (Objects.isNull(transition)) return;

        if (transition.getStatus().equals(Animation.Status.RUNNING)) {
            transition.stop();
            this.previousTransition.set(null);
        }
    }

    /**
     * Method to add, in order the items to be show when requested to navigate.
     */
    public void setMenuItemsMap(@NonNull EExpenseFormRoute... menuItemsMap) {
        if (ArrayUtils.isEmpty(menuItemsMap)) return;

        if (Objects.isNull(this.menuItemsMap)) {
            this.routeIndex = new AtomicInteger();
            this.menuItemsMap = new EnumMap<>(EExpenseFormRoute.class);
            this.previousTransition = new AtomicReference<>();
        }

        this.routeIndex.set(0);
        this.menuItemsMap.keySet().removeIf(
                ICollectionUtils.Filter.notContains(Arrays.asList(menuItemsMap))
        );

        for (@NonNull EExpenseFormRoute formRoute : menuItemsMap) {
            this.menuItemsMap.putIfAbsent(formRoute, null);
        }
    }
}