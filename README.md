## Installation

### Maven Repo

```xml
<dependency>
  <groupId>io.bretty</groupId>
  <artifactId>console-view</artifactId>
  <version>3.0</version>
</dependency>
```

### JAR Release

Download the package [`console-view-3.0.jar`](https://github.com/nathanielove/Java-Console-View/blob/master/console-view-3.0.jar?raw=true) and add it to your project resources.

## Content

* [Overview](#overview)
	* [Introduction](#introduction)
	* [Menu View](#menu-view)
	* [Action View](#action-view)
* [Quick Start](#quick-start)
	* [Create "Book Taxi" Action](#book-taxi-action)
	* [Create "Booking History" Menu](#history-menu)
	* [Create Root Menu](#root-menu)
	* [Run the App](#run-app)
* [UI Components](#ui-components)
	* [Print](#print)
	* [Prompt](#prompt)
	* [Quick Feedback](#quick-feedback)
	* [Confirmation Dialog](#confirmation-dialog)
	* [Pause](#pause)
* [Advanced Features](#advanced-features)
	* [Dynamically Create `MenuView` in Running `ActionView`](#menu-in-action)
	* [Customization - Building Your Own `ViewConfig`](#customization)
	* [Overriding `onBack()` and `onQuit()` in Your MenuView Subclass](menu-subclass)

## <a name="overview"></a> Overview

### <a name="introduction"></a> Introduction

This framework provides a way to quickly construct the View layer of a Java command line app.

**`AbstractView`** is the ultimate parent class of all classes in the View layer. This class has only one abstract public method, `.display()`, which starts this view. It also provides a handful of [UI components]() to gracefully handle user interaction, user input and validation, etc.

There are two subclasses of `AbstractView`:

* **`MenuView`**: the view that asks the user to select from a list of available options 
* **`ActionView`**: the view that executes a single custome app logic

### <a name="menu-view"></a> Menu View

The workflow of the `.display()` method in `MenuView`:

1. Print `this.runningTitle`
1. For each `AbstractMenu menuItem` in `this.menuItems`
	* Print index prefix (using `this.viewConfig.indexNumberFormatter`)
	* Print `menuItem.nameInParentMenu`
1. Print `this.viewConfig.selectionMessage`
1. Scan for an integer input from user

The console output of the root view of a sample taxi booking app is:

```text
Welcome to the taxi booking service center.		// this.runningTitle
1) Book a taxi									// item in this.menuItems
2) Check booking status							// item in this.menuItems
3) Booking history								// item in this.menuItems
4) Quit											// this.viewConfig.backMenuName or this.viewConfig.quitMenuName
Please enter a number to continue: 				// this.viewConfig.selectionMessage
```

If invalid input occurs, there will be an error message.

```text
Invalid input. Please try again: 				// this.viewConfig.inputErrorMessage
```

Once the user enters a valid selection number, the system will call the `.display()` method on the corresponding `menuItem`.

If the current `MenuView` is not the root, the last option will always be `Back` instead of `Quit`. For example, the "Booking History" menu, which is a submenu of the root menu, looks like this.

```text
Booking History
1) View history
2) Remove all histories
3) Back
```
By entering the number for "Back", the user will go back to the parent menu, which is "Welcome..." in this case.

### <a name="action-view"></a> Action View

`ActionView` represents a single leaf functionality of your app, for example, to book a taxi. The console out put of it will be something like:

```text
Booking a Taxi									// this.runningTitle
// ... custom logic here
// ...

Press enter to continue...						// this.viewConfig.pauseMessage
```

The custom logic is the part where you need to write the code specific to your app.

The view will pause after finishing all custom logic by displaying `this.viewConfig.pauseMessage`, which is "Press enter to continue..." by default. After the user presses enter, it will go back and display again the parent menu view.


## <a name="quick-start"></a> Quick Start

In this quick start, we will build a simple command line app "Taxi Booking", which prints the same result as the examples above.

### <a name="book-taxi-action"></a> Create "Book Taxi" Action

```java
class BookTaxiAction extends ActionView{

	public BookTaxiAction(){
		super("Booking a Taxi", "Book a taxi");
	}
	
	@override
	public void executeCustomAction() {
		// your custom logic and UI to book a taxi...
	}
}
```

Notice that the first string passed to the `super()` constructor will be printed in the console as title when your custom logic runs (i.e. `this.runningTitle`). The second string will be displayed as a descriptive name of this view in a parent menu, if there is any (i.e. `this.nameInParentMenu`).

### <a name="history-menu"></a> Create "History" Menu

First, create two classes called `ViewHistoryAction` and `RemoveHistoryAction` in the same fashion as we created `BookTaxiAction`. Now assuming we already created these two classes, we can build our `historyMenu` by registering the instances of these two classe as menu items.

```java
	MenuView historyMenu = new MenuView("Booking History", "Booking history");
	
	// register menu items
	historyMenu.addMenuItem(new ViewHistoryAction());
	historyMenu.addMenuItem(new RemoveHistoryAction());
```

Similarly, the first string in the constructor is the the title when this the `historyMenu` runs. The second string is the descriptive name of `historyMenu` in a parent menu, if there is any.

### <a name="root-menu"></a> Create Root Menu and Run the App

```java
	MenuView rootMenu = new MenuView("Welcome...", "");
	
	// register menu items
	rootMenu.addMenuItem(new BookTaxiAction());
	rootMenu.addMenuItem(new CheckBookingStatusAction());
	rootMenu.addMenuItem(historyMenu);	
```

### <a name="run-app"></a> Run the App

```java
	rootMenu.display();
```


## <a name="ui-components"></a> Use Powerful UI Components

`AbstractView` provides a variety of UI components (accessible in subclasses as inherited methods) that facilitate your interaction with the user, such as asking for user confirmation, reading and validating a user input, etc. These are especially useful when you implement your custom app logic in a sublcass of `ActionView`.

Let's take a quick look at each of them.

### <a name="print"></a> Print

You can use one of the print wrappers to avoid `System.out.println()`. For example,

```java
class BookTaxiAction extends ActionView{
	...
	
	@override
	public void executeCustomAction() {
		this.print("Hello");
		this.println(" World");
		this.println(new Date());
	}
}
```

The result will be:

```text
Hello World
Wed Feb 17 19:00:32 UTC 2016
```

### <a name="prompt"></a> Prompt

No more `new Scanner(System.in)` to read user input. Instead, use `prompt()` - a UI component that provides both user input and validation. If the user enters any invalid input, the frameworks will keep asking for retry, until a valid input is made. For example,

```java
	@override
	public void executeCustomAction() {
		int number = this.prompt("Please enter an integer: ", Integer.class);
		this.println("Your integer is " + number);
	}
```
A possible result might look like:

```text
Please enter an integer: lol
Invalid input. Please try again: 0.9
Invalid input. Please try again: true
Invalid input. Please try again: 1234567890987654321123451234
Invalid input. Please try again: 15
Your integer is 15
```

Also, you may use your custom validator to validate user input. For example:

```java
	@override
	public void executeCustomAction() {
		Validator<Double> bidValidator = new Validator<Double>(){
			@Override
			public boolean isValid(Double input) {
				// valid bid must be no less than zero
				return Double.compare(input, 0) >= 0;
			}
		};
		double bid = this.prompt("Please enter your bid: ", Double.class, bidValidator);
		this.println("Your bid is " + bid);
	}
```

Any user input will be first checked against the expected return type, then the custom validator, if the custom validator is presented. If the user input fails either of the two steps of validation, the system will keep asking for retry, until a valid input is made.

A possible console output using the custom validator above is:

```text
Please enter your bid: lol
Invalid input. Please try again: true
Invalid input. Please try again: -100
Invalid input. Please try again: 12.6
Your bid is 12.6
```

*To replace the default input error message, see [Customization](#customization).*

### <a name="quick-feedback"></a> Quick Feedback

There are three build-in quick feedbacks:

```java
	@override
	public void executeCustomAction() {
		this.actionCanceled();
		this.actionSuccessful();
		this.actionFailed();
	}
```

The console output will be:

```text
Action canceled.
Action successful!
Action failed.
```

*To replace the default feedback message, see [Customization](#customization).*


### <a name="confirmation-dialog"></a> Confirmation Dialog

Sometime you might want to ask for user's confirmation before performing some critical tasks. That's where we can help. Let's see an example:

```java
	@override
	public void executeCustomAction() {
		...
		
		boolean confirmed = this.confirmDialog("Make booking now?");
		if(confirmed){
			this.println("Your taxi is on the way!");
		}
	}
```

If the user chooses to confirm in the dialog, the console output will be like:

```text
Make booking now? (y/N) y
Your taxi is on the way!
```

Please notice that both lower case `"y"` and upper case `"Y"` are accepted for confirmation. 

If the user enters anything else, it will be taken as "cancel", and a canceled action feedback will be printed automatically. For example,

```text
Make booking now? (y/N) p
Action canceled.
```

*To replace the default confirmation options* `(y/N)`, *see [Customization](#customization).*

### <a name="pause"></a> Pause

As mentioned above, after an custom action view finishes executing the `executeCustomAction()` method, and before going back to the parent view, the system will pause and ask for the user to press enter to continue. If we consider the "Confirmation Dialog" example above, the actual output is:

```text
Make booking now? (y/N) p
Action canceled.

Press enter to continue...

```

Actually, you can call the `pause()` method anywhere anytime. But notice that there will be a new line above the pause message.

*To replace the default pause message, see [Customization](#customization).*

## <a name="advanced-features"></a> Advanced Features

### <a name="menu-in-action"></a> Dynamically Create `MenuView` in Running `ActionView`

In the latest update, we added support for setting an `ActionView` object as the parent view of a `MenuView` object, so that you can freely create any `MenuView` object dynamically at runtime.

The following code example demonstrates how it works.

```java
class ViewHistoryAction extends ActionView{
	ViewHistoryAction(){
		super("Booking History", "View history");
	}
	
	@Override
	public void executeCustomAction() {
	    String line = this.prompt("Please enter a line: ", String.class);
	    
	    // create a MenuView object dynamically from user input
	    MenuView menuView = new MenuView("Submenu " + line, "Submenu " + line);
	    
	    // if you want the users to be able to get back here from the menu,
	    // set the parent view as "this"; or if you want to redirect the users 
	    // to some other view, you may pass in that view object as parameter.
	    // however, if you don't set any parent view, the parenetView will be null,
	    // and user can only quit from this menu view
	    menuView.setParentView(this);
	    
	    // run the view you just created
	    menuView.display();
	}
}
```

### <a name="customization"></a> Customization - Building Your Own `ViewConfig`

As you might have noticed in the examples above, all the default string in the UI (e.g. "Please enter a number to continue: ") are taken from a `ViewConfig` object. Hence, by creating your own `ViewConfig` object, you can get full control of the choice of words.

To build a custom `ViewConfig` object, you'll have to use the `ViewConfig.Builder` class. For example,

```java
	 ViewConfig viewConfig = new ViewConfig.Builder()
	 	.setActionCanceledMessage("Uh oh~ Action canceled")
	 	.setActionSuccessfulMessage("Congrats! You did it!")
	 	.setPauseMessage("Wait... If you wanna continue, you gotta press enter.")
	 	.build();
``` 

Please notice that the `ViewConfig` is template-based. When you create a `ViewConfig.Builder` object, all the fields are initialized to the default values. When you change some of the fields using the setters, all others that you didn't change will continue to use the default value when you `.build()`.

The `ViewConfig.Builder` allows you to replace any string in the default UI with your own choice of words. To use your custom `ViewConfig` object, simply pass it in the constructor when you are creating a view object. For example,

```java
	ActionView actionView = new ActionView("Sample Action", "Sample action", viewConfig){
		@override
		public executeCustomAction(){
			// your custom app logic
		}
	};
	
	MenuView menuView = new MenuView("Sample menu view", "", viewConfig);
```

### <a name="menu-subclass"></a> Overriding `onBack()` and `onQuit()`

Although the current implementation covers most of your possible use cases, in case that you have to do something like subclass the `MenuView` class, these two methods are really important. 

If your menu view has a parent view, `onBack()` will be called by the system after the user selects "Back", just before the parent view is displayed.

Similarly, if your menu view doesn't have a parent view, then `onQuit()` will be called by the system just before the user quits the program.

Always remember to add `super.onBack()` or `super.onQuit()` as the last line of your overriding method.


