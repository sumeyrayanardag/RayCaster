
package raycaster;

import java.util.Vector;

public class Sphere extends Object3D {

    float radius;
    Vector<Integer> center = new Vector<Integer>();

    public Sphere(Vector<Float> color) {
        super(color);
    }

    public Sphere(float radius, Vector<Integer> center, Vector<Float> color) {
        super(color);
        this.radius = radius;
        this.center = center;
    }

    public float getRadius() {
        return radius;
    }

    public Vector<Integer> getCenter() {
        return center;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setCenter(Vector<Integer> center) {
        this.center = center;
    }
    
    @Override   
    public Hit intersect(Ray ray, Hit hit, float tmin ) { 
        float a = RayCaster.dotProduct_3_3Float(ray.direction, ray.direction);
        float b = 2*(RayCaster.dotProduct_3_3Float(ray.direction, ray.origin ));
        float c = RayCaster.dotProduct_3_3Float(ray.origin, ray.origin) - radius*radius;
        float discriminant = (b*b) - (4*a*c);
     
        Hit hit2 = new Hit();
        if(discriminant < 0.0){
            return null;
        }
        else{
            float t = (-b - (float)Math.sqrt(discriminant))/(2*a);
            
            if(t>tmin && t<hit.t){ 
                hit2.color = color;
                hit2.t = t; 
                hit2.normal = RayCaster.sumVectors_3_3Float(ray.origin, RayCaster.multiplyFloatVectorWithFloatNumber(ray.direction, hit2.t));
                
                //normalize the hit2.normal
                hit2.normal = RayCaster.divideFloatVectorWithFloatNumber(hit2.normal, 
                        (float)Math.sqrt((Math.pow(hit2.normal.get(0), 2)) + (Math.pow(hit2.normal.get(1), 2)) + (Math.pow(hit2.normal.get(2), 2))));        
  
                return hit2;
            }
            else{
                return null;
            }
        }        
    }   
}
