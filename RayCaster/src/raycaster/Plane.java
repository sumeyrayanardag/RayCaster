
package raycaster;

import java.util.Vector;

public class Plane extends Object3D{

    Vector<Float> normal = new Vector<Float>();
    Float d; //offset from the origin
    Vector<Float> color = new Vector<Float>();

    public Plane(Vector<Float> color) {
        super(color);
    }

    
    public Plane(Float offset, Vector<Float> normal, Vector<Float> color, Vector<Float> superColor) {
        super(superColor);
        this.d = offset;
        this.normal = normal;
        this.color = color;
    }

    public Vector<Float> getNormal() {
        return normal;
    }

    public void setNormal(Vector<Float> normal) {
        this.normal = normal;
    }

    public Float getD() {
        return d;
    }

    public void setD(Float d) {
        this.d = d;
    }

    public Vector<Float> getColor() {
        return color;
    }

    public void setColor(Vector<Float> color) {
        this.color = color;
    }


    @Override
    public Hit intersect(Ray ray, Hit hit, float tmin) {
        
        Hit hit2 = new Hit();
        float denom = RayCaster.dotProduct_3_3Float(normal, ray.direction);
        
        if(denom == 0){
            return null;
        }
        
        else if(Math.abs(denom) > tmin){
            float t = -(d + RayCaster.dotProduct_3_3Float(normal, ray.origin))/denom;    
            
            if(t>tmin && t<hit.t){                
                hit2.color = color;
                hit2.t = t;
                
                if(denom > 0){    
                    hit2.normal = RayCaster.multiplyFloatVectorWithFloatNumber(normal, (float)-1);
                }
                else{
                    hit2.normal = normal;
                }

                hit2.normal = RayCaster.divideFloatVectorWithFloatNumber(hit2.normal, 
                        (float)Math.sqrt((Math.pow(hit2.normal.get(0), 2)) + (Math.pow(hit2.normal.get(1), 2)) + (Math.pow(hit2.normal.get(2), 2))));        
               
                return hit2;
            }
            else{
                return null;
            }
        }  
        else{
            return null;
        }
     
    }    

}
