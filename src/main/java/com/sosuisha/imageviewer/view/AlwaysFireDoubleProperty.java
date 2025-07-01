package com.sosuisha.imageviewer.view;

import javafx.beans.property.SimpleDoubleProperty;

/**
 * A DoubleProperty that always fires listeners even when the same value is set.
 */
public class AlwaysFireDoubleProperty extends SimpleDoubleProperty {
    
    public AlwaysFireDoubleProperty() {
        super();
    }
    
    public AlwaysFireDoubleProperty(double initialValue) {
        super(initialValue);
    }
    
    public AlwaysFireDoubleProperty(Object bean, String name) {
        super(bean, name);
    }
    
    public AlwaysFireDoubleProperty(Object bean, String name, double initialValue) {
        super(bean, name, initialValue);
    }
    
    @Override
    public void set(double newValue) {
        double oldValue = get();
        if (Double.compare(newValue, oldValue) == 0) {
            super.set(newValue + 0.0000001); // Slightly adjust to ensure change is detected
        }
        super.set(newValue);
        
    }
    
    @Override
    public void setValue(Number v) {
        if (v == null) {
            set(0.0);
        } else {
            set(v.doubleValue());
        }
    }  
}

