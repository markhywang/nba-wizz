package view;

import interface_adapter.ViewManagerModel;

import javax.swing.*;
import java.awt.*;

public class ViewManager implements java.beans.PropertyChangeListener {
    private final CardLayout cardLayout;
    private final JPanel views;

    public ViewManager(JPanel views, CardLayout cardLayout, ViewManagerModel viewManagerModel) {
        this.views = views;
        this.cardLayout = cardLayout;

        viewManagerModel.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("view")) {
            String viewModelName = (String) evt.getNewValue();
            cardLayout.show(views, viewModelName);
        }
    }
}
