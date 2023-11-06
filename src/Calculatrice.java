import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Objects;

class Label extends JLabel {
    public Label(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
    }
    public void setText(String text) {
        if (text.contains("Infinity"))
            text = text.replace("Infinity", "±∞");
        super.setText(text);
    }
}
public class Calculatrice extends JFrame implements ActionListener, KeyListener {
    private Label label, miniLabel;
    private String numberA = "0", numberB = "", operator = "";
    private boolean fillNumberA = true, afterEqual = false;

    public Calculatrice() {
        super("Calculatrice");
    }

    private String compute() {
        miniLabel.setText(numberA + " " + operator + " " + numberB + " =");
        double a = Double.parseDouble(numberA), b = Double.parseDouble(numberB);
        switch (operator) {
            case "+" -> a += b;
            case "-" -> a -= b;
            case "×" -> a *= b;
            case "÷" -> a /= b;
        }
        numberA = Double.toString(a);
        if (numberA.endsWith(".0"))
            numberA = numberA.substring(0, numberA.length() - 2);
        label.setText(numberA);
        return numberA;
    }
    /**
     * Define Action on numbers : 0 to 9, and "."
     */
    private void actionOnNumber(String s) {
        String number = fillNumberA ? numberA : numberB;
        if (s.equals("."))                              // Case: Number cannot start by '.'
            if (number.contains("."))
                return;
        if (number.equals("0") && !s.equals(".") || afterEqual)         // Case: Remove useless 0 on the left
            number = "";                                                // Case: After '=' is pressed
        if (afterEqual) {
            afterEqual = false;
            fillNumberA = true;
            miniLabel.setText(numberA + " =");
        }
        number += s;                                    // Character is added inline
        if (number.equals("."))                         // Case: Number can't start with "."
            number = "0.";
        if (fillNumberA) {                              // Case
            numberA = number;
            label.setText(numberA);
        } else {
            numberB = number;
            label.setText(numberA + operator + numberB);
        }
    }
    private void actionOnOperator(String s) {
        operator = s;

        if (afterEqual) {           // When typing operator, keep numberA and start filling numberB
            numberB = "";
            fillNumberA = false;
            afterEqual = false;
        }
        if (fillNumberA) {
            fillNumberA = false;
            numberB = "";
        } else if (!(numberB.equals("") || numberB.equals("-"))) {
            numberA = compute();
            numberB = "";
        }
        label.setText(numberA + operator);
    }
    private void actionOnClear() {
        numberA = "0";
        numberB = "";
        operator = "";
        label.setText(numberA);
        miniLabel.setText("");
        fillNumberA = true;
    }
    private void actionOnEqual() {
        if (!fillNumberA) {
            numberA = compute();
            afterEqual = true;
        } else
            miniLabel.setText(numberA + "=");
    }
    private void actionOnBackspace() {
        if (fillNumberA && !numberA.equals("")) {
            numberA = numberA.substring(0, numberA.length() - 1);
            if (numberA.equals("") || numberA.equals("-")) {        // Case: Clear on left
                numberA = "0";
            }
            label.setText(numberA);
        } else if (numberB.equals("") || numberB.equals("-")) {        // Case: Clear numberB
            label.setText(numberA);
            fillNumberA = true;
            operator = "";
        } else {
            numberB = numberB.substring(0, numberB.length() - 1);
            label.setText(numberA+operator+numberB);
        }
    }
    private void actionOnSign() {
        if (fillNumberA && !numberA.equals("0") || afterEqual) {
            if (numberA.startsWith("-"))
                numberA = numberA.substring(1);
            else numberA = "-" + numberA;
            label.setText(numberA);
        } else if (!fillNumberA && !numberB.equals("0")) {
            if (numberB.startsWith("-"))
                numberB = numberB.substring(1);
            else numberB = "-" + numberB;
            label.setText(numberA+operator+numberB);
        }
    }
    // Redirects the Action Click Event to respective action functions
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "." -> actionOnNumber(e.getActionCommand());
            case "+", "-", "×", "÷" -> actionOnOperator(e.getActionCommand());
            case "←" -> actionOnBackspace();
            case "C" -> actionOnClear();
            case "±" -> actionOnSign();
            case "=" -> actionOnEqual();
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        System.out.println(e.getKeyChar());
        if (0x60 <= key && key <= 0x69)
            actionOnNumber(Integer.toString(key-0x60));
        switch (key) {
            case KeyEvent.VK_DECIMAL -> actionOnNumber(".");
            case KeyEvent.VK_ADD -> actionOnOperator("+");
            case KeyEvent.VK_SUBTRACT -> actionOnOperator("-");
            case KeyEvent.VK_MULTIPLY -> actionOnOperator("×");
            case KeyEvent.VK_DIVIDE -> actionOnOperator("÷");
            case KeyEvent.VK_SHIFT -> actionOnSign();
            case KeyEvent.VK_DELETE -> actionOnClear();
            case KeyEvent.VK_BACK_SPACE -> actionOnBackspace();
            case KeyEvent.VK_ENTER -> actionOnEqual();
        }
    }
    // Button Creation pattern
    private JButton createButton(String text, int i, int j, Color color, GridBagConstraints c) {
        JButton button = new JButton(text);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = j;
        c.gridy = i;
        button.setFont(new Font("Segoe UI Variable", Font.PLAIN, 24));
        button.addActionListener(this);
        button.addKeyListener(this);
        button.setFocusable(false);
        if (color != null)
            button.setForeground(color);
        getContentPane().add(button, c);
        return button;
    }
    private void createButton(String text, int i, int j, GridBagConstraints c) {
        createButton(text, i, j, null, c);
    }

    private JButton buttonEqual;
    /**
     * Adding Buttons and Labels to GridBagLayout set Pane
     */
    private void addComponentsToPane() {
        Container pane = getContentPane();
        GridBagLayout grid = new GridBagLayout();
        pane.setLayout(grid);
        pane.setMinimumSize(new Dimension(300, 500));
        GridBagConstraints c = new GridBagConstraints();

        // Adding Buttons
        int firstRow = 2;
        for (int i = 0; i < 9; i++)
            createButton(Integer.toString(i + 1), firstRow + 3 - i / 3, i % 3, c);
        String[] operatorSymbols = {"÷", "×", "-", "+"};
        for (int j = 0; j < operatorSymbols.length; j++)
            createButton(operatorSymbols[j], firstRow + j, 3, Color.GRAY, c);
        createButton("±", firstRow, 0, Color.GRAY, c);
        createButton("C", firstRow, 1, Color.RED, c);
        createButton("←", firstRow, 2, Color.GRAY, c);
        createButton(".", firstRow + 4, 2, c);
        buttonEqual = createButton("=", firstRow + 4, 3, Color.BLUE, c);
        c.gridwidth = 2;
        createButton("0", firstRow + 4, 0, c);

        // Adding labels
        label = new Label("0", SwingConstants.RIGHT);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI Variable", Font.BOLD, 42));
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(0, 10, 10, 10);
        c.ipady = -20; c.ipadx = 0;
        c.gridx = 0; c.gridy = 1;
        pane.add(label, c);

        miniLabel = new Label(" ", SwingConstants.RIGHT);
        miniLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        miniLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 12));
        miniLabel.setForeground(Color.DARK_GRAY);
        c.ipady = 0;
        c.ipadx = 0;
        c.gridx = 0;
        c.gridy = 0;
        pane.add(miniLabel, c);
    }
    /**
     * Creates the GUI and shows it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Set the system look and feel.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Create and set up the window.
        Calculatrice frame = new Calculatrice();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon(Objects.requireNonNull(Calculatrice.class.getResource("calculatrice.png"))).getImage());

        //Set up the content pane.
        frame.addComponentsToPane();

        //Display the window.
        frame.setMinimumSize(new Dimension(280, 400));
        frame.setSize(330, 520);
        frame.setVisible(true);
        frame.buttonEqual.setFocusable(true);
        frame.buttonEqual.setFocusPainted(false);
        frame.buttonEqual.requestFocus();
    }

    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(Calculatrice::createAndShowGUI);
    }
}