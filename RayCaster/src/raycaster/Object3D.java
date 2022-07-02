
package raycaster;

import java.util.Vector;

public abstract class Object3D {
    
    Vector<Float> color = new Vector<Float>(); 

    public Object3D(Vector<Float> color) {
        this.color = color;
    }

    public Vector<Float> getColor() {
        return color;
    }

    public void setColor(Vector<Float> color) {
        this.color = color;
    }
    
    public abstract Hit intersect(Ray ray ,Hit hit ,float tmin );
}
