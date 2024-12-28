package org.flintcore.excel_expenses.managers.routers;

import org.flintcore.excel_expenses.managers.routers.holders.RouteManager;
import org.flintcore.excel_expenses.managers.routers.routes.EMainRoute;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {
        RouterConfiguration.class
})
class RouteManagerTest {
    @Autowired
    private RouteManager<IRoute> routeManager;

    @Test
    void initTest() {
        assertNotNull(routeManager);
    }
// TODO
//    @Test
//    void shouldMoveTo() {
//        this.routeManager.navigateTo(EMainRoute.HOME);
//
//        checksRouteIs(EMainRoute.HOME);
//    }

//    @Test
//    void shouldMoveAnotherRoute() {
//        this.routeManager.navigateTo(EMainRoute.REGISTER);
//
//        checksRouteIs(EMainRoute.REGISTER);
//    }

//    @Test
//    void shouldMoveMultipleTimes() {
//        this.routeManager.navigateTo(EMainRoute.REGISTER);
//        this.routeManager.navigateTo(EMainRoute.LOCALS);
//
//        checksRouteIs(EMainRoute.LOCALS);
//        checksPreviousRouteIs(EMainRoute.REGISTER);
//    }

    // TODO

//    @Test
//    void shouldBack() {
//        this.routeManager.navigateTo(EMainRoute.REGISTER);
//
//        assertTrue(this.routeManager.canNavigateBack(),
//                "Route manager cannot navigate back after change route.");
//    }
//
//    @Test
//    void shouldBackMultipleTimes() {
//        this.routeManager.navigateTo(EMainRoute.REGISTER);
//        this.routeManager.navigateTo(EMainRoute.LOCALS);
//
//        assertTrue(this.routeManager.canNavigateBack(),
//                "Route manager cannot navigate back after change route.");
//    }

    private void checksRouteIs(EMainRoute expectedRoute) {
        IRoute currentRoute = this.routeManager.currentRoute();
        assertNotNull(currentRoute);
        assertEquals(expectedRoute, currentRoute, "Current route is not the same as expected.");
    }

    private void checksPreviousRouteIs(EMainRoute expectedRoute) {
        IRoute previousRoute = this.routeManager.previousRoute();
        assertNotNull(previousRoute);
        assertEquals(expectedRoute, previousRoute, "Previous route is not the same as expected.");
    }
}