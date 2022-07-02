
package raycaster;

import java.util.Vector;

public class Ray {
    
    Vector<Float> origin = new Vector<Float>();
    Vector<Float> direction = new Vector<Float>();

    public Ray() {
    }

    
    public Ray(Vector<Float> origin, Vector<Float> direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public Vector<Float> getOrigin() {
        return origin;
    }

    public Vector<Float> getDirection() {
        return direction;
    }

    public void setOrigin(Vector<Float> origin) {
        this.origin = origin;
    }

    public void setDirection(Vector<Float> direction) {
        this.direction = direction;
    }
 
}
