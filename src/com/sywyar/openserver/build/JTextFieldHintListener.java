package com.sywyar.openserver.build;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class JTextFieldHintListener implements FocusListener {
    private final String hintText;
    private final JTextField textField;

    public JTextFieldHintListener(JTextField jTextField,String hintText) {
        this.textField = jTextField;
        this.hintText = hintText;
        jTextField.setText(hintText);
    }

    @Override
    public void focusGained(FocusEvent e) {
        String temp = textField.getText();
        if(temp.equals(hintText)) {
            textField.setText("");
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        String temp = textField.getText();
        if(temp.isEmpty()) {
            textField.setText(hintText);
        }
    }
}
