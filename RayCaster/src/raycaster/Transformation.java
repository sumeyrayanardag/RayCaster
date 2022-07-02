
package raycaster;

import java.util.Vector;

public class Transformation extends Object3D{ 

    Object3D object;
    float[][] m = new float[4][4]; //4x4 transformation matrix

    public Transformation(Vector<Float> color) {
        super(color);
    }

    public Transformation(Object3D object, float[][] m, Vector<Float> color) {
        super(color);
        this.object = object;
        this.m = m;
    }

    public Object3D getObject() {
        return object;
    }

    public void setObject(Object3D object) {
        this.object = object;
    }

    public float[][] getM() {
        return m;
    }

    public void setM(float[][] m) {
        this.m = m;
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
        
        ray.origin = RayCaster.transformPoint(ray.origin, RayCaster.inverse(m));
        
        Vector<Float> selectedPoint = new Vector<Float>();
        selectedPoint = RayCaster.sumVectors_3_3Float(ray.origin, RayCaster.multiplyFloatVectorWithFloatNumber(ray.direction, hit.t));
        selectedPoint = RayCaster.transformPoint(selectedPoint, RayCaster.inverse(m));
        ray.direction = RayCaster.subtractVectors_3_3Float_Float(selectedPoint, ray.origin);
        
        if(object instanceof Sphere){
            hit2 = object.intersect(ray, hit, tmin);
            if(hit2 != null){
                hit2.normal = RayCaster.transformDirection(hit2.normal, RayCaster.transpose(RayCaster.inverse(m)));
                hit2.normal.add((float)0);
                //normalize the hit2.normal
                hit2.normal = RayCaster.divideFloatVectorWithFloatNumber(hit2.normal, 
                        (float)Math.sqrt((Math.pow(hit2.normal.get(0), 2)) + (Math.pow(hit2.normal.get(1), 2)) + (Math.pow(hit2.normal.get(2), 2)) + + (Math.pow(hit2.normal.get(3), 2))));              
            
            }    
            return hit2;
        }
        return hit2;
    }    
}
