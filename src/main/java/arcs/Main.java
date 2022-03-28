package arcs;

import arcs.commands.CommandResult;
import arcs.data.customer.CustomerManager;
import arcs.data.route.RouteManager;
import arcs.data.flightbooking.FlightBookingManager;
import arcs.data.menuitems.MenuItemManager;
import arcs.parser.Parser;
import arcs.storage.CustomerFileManager;
import arcs.storage.FlightBookingFileManager;
import arcs.storage.RouteFileManager;
import arcs.commands.Command;
import arcs.ui.MainUi;

import java.io.IOException;


public class Main {

    private RouteManager routeManager;
    private MenuItemManager menuItemManager;
    private MainUi mainUi;
    private RouteFileManager routeFileManager;
    private FlightBookingManager flightBookingManager;
    private FlightBookingFileManager flightBookingFileManager;
    private CustomerManager customerManager;
    private CustomerFileManager customerFileManager;
    /**
     * Parser object.
     */
    private final Parser parser;

    /**
     * Main entry-point for the ARCS application.
     */
    public static void main(String[] args) {
        new Main().run();
    }

    public Main() {
        // Initialize ui, logic and storage classes
        mainUi = new MainUi();
        parser = new Parser();
        routeFileManager = new RouteFileManager();
        customerFileManager = new CustomerFileManager();
        flightBookingFileManager = new FlightBookingFileManager();
        loadData();
    }

    public void run() {
        Command command;
        mainUi.displayWelcomeMessage();

        do {
            mainUi.displayGetNextUserCommand();
            String userCommandText = mainUi.getUserCommand();
            command = parser.parseCommand(userCommandText);
            command.setData(routeManager, flightBookingManager,
                    menuItemManager, customerManager);
            CommandResult result = command.execute();
            mainUi.displayResultToUser(result);
            mainUi.displayLineDivider();
        } while (!command.isExit());

        saveData();
        mainUi.displayExitMessage();
    }

    public void loadData() {

        try {
            routeManager = new RouteManager(routeFileManager.loadData());
            menuItemManager = new MenuItemManager();
            customerManager = new CustomerManager(customerFileManager.loadData());
            flightBookingManager = new FlightBookingManager(flightBookingFileManager.loadData());
        } catch (IOException e) {
            mainUi.displayMessages(e.getMessage());
            routeManager = new RouteManager();
            customerManager = new CustomerManager();
            flightBookingManager = new FlightBookingManager();
        }
    }

    public void saveData() {
        try {
            routeFileManager.saveData(routeManager.getAllRoutes());
            customerFileManager.saveData(customerManager.getAllCustomers());
            flightBookingFileManager.saveData(flightBookingManager.getAllFlightBookings());
        } catch (IOException e) {
            mainUi.displayMessages(e.getMessage());
        }
    }
}
