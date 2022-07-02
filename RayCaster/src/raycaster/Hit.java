
package raycaster;

import java.util.Vector;

public class Hit {
    
    float t;
    Vector<Float> color = new Vector<Float>();
    Vector<Float> normal = new Vector<Float>();

    public Hit() {
    }

    public Hit(float t, Vector<Float> color,  Vector<Float> normal) {
        this.t = t;
        this.color = color;
        this.normal = normal;
    }

    public float getT() {
        return t;
    }

    public Vector<Float> getColor() {
        return color;
    }

    public void setNormal(Vector<Float> normal) {
        this.normal = normal;
    }

    public Vector<Float> getNormal() {
        return normal;
    }

    public void setT(float t) {
        this.t = t;
    }

    public void setColor(Vector<Float> color) {
        this.color = color;
    }
    
    
}
