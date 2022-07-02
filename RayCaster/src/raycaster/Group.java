
package raycaster;

import java.util.Vector;
import raycaster.Sphere;
import raycaster.Plane;


public class Group extends Object3D {

    Object3D object[];  

    public Group(Vector<Float> color) {
        super(color);
    }

    
    public Group(Object3D[] object, Vector<Float> color) {
        super(color);
        this.object = object;
    }
    
    public Group(Object3D object, Vector<Float> color) {
        super(color);
        this.object[0] = object;
    }
    
    public Object3D[] getObject() {
        return object;
    }

    public Object3D getObjectWithIndex(int index) {
        return object[index];
    }
    
    public Vector<Float> getColor() {
        return color;
    }

    public void setObject(Object3D[] object) {
        this.object = object;
    }

    public void setColor(Vector<Float> color) {
        this.color = color;
    }

    public Object3D[] setObjectWithIndex(int index, Object3D obj) {
        object[index] = obj;
        return object;
    }
    
    @Override
    public Hit intersect(Ray ray, Hit hit, float tmin ) { 
     //   Sphere sphere = new Sphere( ((Sphere)(object[0])).getRadius(), ((Sphere)(object[0])).getCenter(), ((Sphere)(object[0])).getColor()); 
       // Plane plane = new Plane( ((Plane)(object[0])).getD(), ((Plane)(object[0])).getNormal(), ((Plane)(object[0])).getColor(), ((Plane)(object[0])).getColor());
               
        
        for(int i=0; i<object.length; i++){  
            if(object[i] instanceof Sphere){  
                Sphere sphere = new Sphere( ((Sphere)(object[i])).getRadius(), ((Sphere)(object[i])).getCenter(), ((Sphere)(object[i])).getColor());

                if(sphere.intersect(new Ray(RayCaster.subtractVectors_3_3Float_Int(ray.origin, sphere.center) , ray.direction),hit, tmin) != null){
                    hit = sphere.intersect(new Ray(RayCaster.subtractVectors_3_3Float_Int(ray.origin, sphere.center) , ray.direction),hit, tmin);
                }
            }
            else if(object[i] instanceof Plane){  
                Plane plane = new Plane( ((Plane)(object[i])).getD(), ((Plane)(object[i])).getNormal(), ((Plane)(object[i])).getColor(), ((Plane)(object[i])).getColor());

                if(plane.intersect(new Ray(ray.origin , ray.direction),hit, tmin) != null){
                    hit = plane.intersect(new Ray(ray.origin , ray.direction),hit, tmin);
                }
            }
            else if(object[i] instanceof Triangle){  
                Triangle triangle = new Triangle(((Triangle)(object[i])).getColor(), ((Triangle)(object[i])).getV1(), ((Triangle)(object[i])).getV2(), ((Triangle)(object[i])).getV3());

                if(triangle.intersect(new Ray(ray.origin , ray.direction),hit, tmin) != null){
                    hit = triangle.intersect(new Ray(ray.origin , ray.direction),hit, tmin);
                }
            }
        }
        return hit;
    }
    
}
