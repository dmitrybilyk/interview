package com.conduct.interview._7_patterns.creational.factory.factory_method;

class FactoryMethod {
	private static Dialog dialog;

    public static void main(String[] args) {
        configure();
        runBusinessLogic();
    }

    /**
     * The concrete factory is usually chosen depending on configuration or
     * environment options.
     */
    static void configure() {
        if (System.getProperty("os.name").equals("Windows 10")) {
            dialog = new WindowsDialog();
        } else {
            dialog = new HtmlDialog();
        }
    }

    /**
     * All client code should work with factories and products through
     * abstract interfaces. This way it does not care which factory it works
     * with and what kind of product it returns.
     */
    static void runBusinessLogic() {
        dialog.renderWindow();
    }

}

/**
 * Product
 */
interface Button {
    void render();
    void onClick();
}

/**
 * Concrete Product 1
 */
class HtmlButton implements Button {

    public void render() {
        System.out.println("<button>Test Button</button>");
        onClick();
    }

    public void onClick() {
        System.out.println("Click! Button says - 'Hello World!'");
    }
}



/**
 * Concrete Product 2
 */
class WindowsButton implements Button {
    public void render() {
        System.out.println("Windows: Test Button");
        onClick();
    }

    public void onClick() {
        System.out.println("Windows: Click! Button says - 'Hello World!'");
    }
}

/**
 * Base factory class. Note that "factory" is merely a role for the class. It
 * should have some core business logic which needs different products to be
 * created.
 */
abstract class Dialog {

    public void renderWindow() {
        // ... other code ...

        Button okButton = createButton();
        okButton.render();
    }

    /**
     * Subclasses will override this method in order to create specific button
     * objects.
     */
    public abstract Button createButton();
}

/**
 * HTML Dialog will produce HTML buttons.
 */
class HtmlDialog extends Dialog {

    @Override
    public Button createButton() {
        return new HtmlButton();
    }
}

/**
 * Windows Dialog will produce Windows buttons.
 */
class WindowsDialog extends Dialog {

    @Override
    public Button createButton() {
        return new WindowsButton();
    }
}


