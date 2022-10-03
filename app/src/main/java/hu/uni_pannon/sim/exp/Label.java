package hu.uni_pannon.sim.exp;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Custom Label implementation that can rotate the label and align it to
 * a specific VPos and HPos. Rotate can be true or false.
 * 
 * LEFT CENTER   RIGHT
 *   O----O----O       TOP
 *   |         |
 *   O    O    O       CENTER
 *   |         |
 *   O----O----O       BOTTOM
 * 
 * @author Tóth Bálint
 */
public class Label {
    
    private VBox wrapper;
    private Text t;

    private HPos alignX;
    private VPos alignY;
    private final double rotateAngle = 270;
    
    private boolean isRotate = false;
    
    
    private final boolean DEBUG_BORDER = false;
    
    private DoubleProperty heightProperty;
    private DoubleProperty widthProperty;
    
    public Label() {        

        this.t = new Text("Title");
        t.setTextOrigin(VPos.CENTER);
        Group inner = new Group();
        inner.getChildren().add(t);
        wrapper = new VBox();
        wrapper.getChildren().add(inner);
        wrapper.setManaged(true);
        if (DEBUG_BORDER)
            wrapper.setStyle("-fx-border-color: red;");
        
        
        alignX = HPos.CENTER;
        alignY = VPos.CENTER;
        
        align(alignX,alignY);    
        
        widthProperty = new SimpleDoubleProperty();
        widthProperty.bind(wrapper.widthProperty());
        heightProperty = new SimpleDoubleProperty();
        heightProperty.bind(wrapper.heightProperty());
    }
    
    public Label(String text) {
        this();
        t.setText(text);
    }
    
    private void refresh() {
        rotate(isRotate);
        align(alignX,alignY);
        // width and height
    }
    
    public void setText(String text) {
        t.setText(text);
        refresh();
    }

    public void setFont(String font,FontWeight weight,int size) {
        t.setFont(Font.font(font,weight,size));
        refresh();
    }

    public void rotate(boolean isRotate) {
        this.isRotate = isRotate;
        
        if (this.isRotate) {
            wrapper.getTransforms().clear();            
            wrapper.setRotate(rotateAngle);
        }
        align(alignX,alignY);
    }
    
    public void position(DoubleBinding translateX, DoubleBinding translateY) {
        wrapper.layoutXProperty().bind(translateX);
        wrapper.layoutYProperty().bind(translateY);
    }

    public void align(HPos alignX, VPos alignY) {
        this.alignX = alignX;
        this.alignY = alignY;
        wrapper.translateXProperty().unbind();
        wrapper.translateYProperty().unbind();

        switch (alignX) {
            case CENTER: {
                //hOffsetProperty.set(-1 * (t.getLayoutBounds().getWidth() / 2));
                wrapper.translateXProperty().bind(wrapper.widthProperty().divide(-2));
                break;
            } 
            case LEFT: {
                //hOffsetProperty.set(t.wrappingWidthProperty().get() / 2);
                if (isRotate)
                    wrapper.translateXProperty().bind(wrapper.widthProperty().divide(-2).add(wrapper.heightProperty().divide(2)));
                else
                    wrapper.translateXProperty().set(0);
                break;
            }
            case RIGHT: {
                if (isRotate)
                    wrapper.translateXProperty().bind(wrapper.widthProperty().divide(-2).subtract(wrapper.heightProperty().divide(2)));
                else
                    wrapper.translateXProperty().bind(wrapper.widthProperty().multiply(-1));
                break;
            }
            default: break;
        }

        switch (alignY) {
            case BOTTOM:
                wrapper.translateYProperty().bind(wrapper.heightProperty().multiply(-1));
                break;
            case TOP:
                wrapper.translateYProperty().bind(wrapper.heightProperty().multiply(0));
                break;
            case CENTER:
                wrapper.translateYProperty().bind(wrapper.heightProperty().divide(-2));
                break;
            default: break;
        }
        
    }

    public Node getNode() {
        return wrapper;
    }

    public ReadOnlyDoubleProperty heightProperty() {
        if (isRotate)
            return wrapper.widthProperty();
        else
            return wrapper.heightProperty();
    }
    
    public ReadOnlyDoubleProperty widthProperty() {
        if (isRotate)
            return wrapper.heightProperty();
        else
            return wrapper.widthProperty();
    }
    
    public double textLength() {
        return t.getText().length();
    }
    
}
