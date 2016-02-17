package io.bretty.console.view;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

/**
 * The View that displays a list of options with index numbers, and asks the user to select one to proceed. It provides validation of user input.
 */

public class MenuView extends AbstractView {

    public static final String DEFAULT_SELECTION_MESSAGE = "Please enter a number to continue: ";
    public static final String DEFAULT_BACK_MENU_NAME = "Back";
    public static final String DEFAULT_QUIT_MENU_NAME = "Quit";
    /**
     * a list of {@code AbstractView} objects to be displayed in the menu as available options
     */
    protected List<AbstractView> menuItems = new ArrayList<>();
    private String selectionMessage = DEFAULT_SELECTION_MESSAGE;
    /**
     * Name of the menu "Back"; you can rename it something like "GO BACK"
     */
    private String backMenuName = DEFAULT_BACK_MENU_NAME;
    /**
     * Name of the menu "Quit"; you can rename it something like "Exit"
     */
    private String quitMenuName = DEFAULT_QUIT_MENU_NAME;
    private IndexNumberFormatter indexNumberFormatter = DefaultIndexNumberFormatter.INSTANCE;

    public MenuView(String runningTitle, String nameInParentMenu) {
        super(runningTitle, nameInParentMenu);
    }

    public MenuView(String runningTitle, String nameInParentMenu, String inputErrorMessage, String selectionMessage, String backMenuName, String quitMenuName, IndexNumberFormatter indexNumberFormatter) {
        super(runningTitle, nameInParentMenu, inputErrorMessage);
        this.selectionMessage = selectionMessage;
        this.backMenuName = backMenuName;
        this.quitMenuName = quitMenuName;
        this.indexNumberFormatter = indexNumberFormatter;
    }

    /**
     * Add an entry to the menu; similar to remove, setter and getter
     *
     * @param menuItem to be appended to the last
     */
    public void addMenuItem(AbstractView menuItem) {
        menuItem.parentView = this;
        this.menuItems.add(menuItem);
    }

    public void removeMenuItem(int index) {
        this.menuItems.remove(index);
    }

    public List<AbstractView> getMenuItems() {
        return new ArrayList<>(this.menuItems);
    }

    public void setMenuItems(List<AbstractView> menuItems) {
        this.menuItems = new ArrayList<>(menuItems);
    }

    private boolean isValidIndex(int index) {
        return index >= 1 && index <= this.menuItems.size() + 1;
    }

    private int readSelection() {

        int selection;

        try {
            selection = keyboard.nextInt();
        } catch (InputMismatchException e) {
            selection = -1;
        } finally {
            keyboard.nextLine();
        }

        return selection;
    }

    @Override
    public void display() {

        System.out.println();

        // print running title (e.g. "Create Item")
        System.out.println(this.runningTitle);

        // print all menu items
        // e.g.
        // 1) Create Item
        // 2) View Item
        // 3) ...

        for (int i = 0; i < this.menuItems.size(); ++i) {
            System.out.println(this.indexNumberFormatter.format(i) + this.menuItems.get(i).nameInParentMenu);
        }

        String backOrQuit = this.parentView == null ? this.quitMenuName : this.backMenuName;

        // 4) Back/quit; always the last index
        System.out.println(this.indexNumberFormatter.format(this.menuItems.size()) + backOrQuit);

        // prompt for selection
        System.out.print(this.selectionMessage);

        // get a valid integer
        int selection = this.readSelection();
        while (!isValidIndex(selection)) {
            System.out.print(this.inputErrorMessage);
            selection = this.readSelection();
        }

        // go parentView
        if (selection == this.menuItems.size() + 1) {
            this.goBack();
        } else {
            this.menuItems.get(selection - 1).display();
        }
    }

}
