package io.bretty.console.view;

public class MenuViewTest {


    public static void main(String[] args) {
        ActionView simpleAction = new SimpleAction();
        ActionView simpleAction1 = new SimpleAction();
        MenuView menuView = new MenuView("Welcome!", "");
        MenuView menuView1 = new MenuView("Submenu", "Submenu");

        menuView1.addMenuItem(simpleAction1);

        menuView.addMenuItem(simpleAction);
        menuView.addMenuItem(menuView1);

        menuView.display();
    }
}
