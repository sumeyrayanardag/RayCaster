
package raycaster;

import java.util.Vector;

public class Triangle extends Object3D{

    Vector<Integer> v1 = new Vector<Integer>();
    Vector<Integer> v2 = new Vector<Integer>();
    Vector<Integer> v3 = new Vector<Integer>();

    public Triangle(Vector<Float> color, Vector<Integer> v1, Vector<Integer> v2, Vector<Integer> v3) {
        super(color);
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public Triangle(Vector<Float> color) {
        super(color);
    }

    public Vector<Integer> getV1() {
        return v1;
    }

    public void setV1(Vector<Integer> v1) {
        this.v1 = v1;
    }

    public Vector<Integer> getV2() {
        return v2;
    }

    public void setV2(Vector<Integer> v2) {
        this.v2 = v2;
    }

    public Vector<Integer> getV3() {
        return v3;
    }

    public void setV3(Vector<Integer> v3) {
        this.v3 = v3;
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
        
        Vector<Integer> abVector = RayCaster.subtractVectors_3_3Int_Int(v2, v1);
        Vector<Integer> acVector = RayCaster.subtractVectors_3_3Int_Int(v3, v1);
        
        float ab = (float)Math.sqrt( Math.pow(abVector.get(0), 2) + Math.pow(abVector.get(1), 2) + Math.pow(abVector.get(2), 2) );
        float ac = (float)Math.sqrt( Math.pow(acVector.get(0), 2) + Math.pow(acVector.get(1), 2) + Math.pow(acVector.get(2), 2) );
        
        float A = (RayCaster.determinant(v1.get(0)-v2.get(0), v1.get(0)-v3.get(0), ray.direction.get(0), 
                                                      v1.get(1)-v2.get(1), v1.get(1)-v3.get(1), ray.direction.get(1), 
                                                      v1.get(2)-v2.get(2), v1.get(2)-v3.get(2), ray.direction.get(2)) );
        
        float beta = (RayCaster.determinant(v1.get(0)-ray.origin.get(0), v1.get(0)-v3.get(0), ray.direction.get(0), 
                                            v1.get(1)-ray.origin.get(1), v1.get(1)-v3.get(1), ray.direction.get(1), 
                                            v1.get(2)-ray.origin.get(2), v1.get(2)-v3.get(2), ray.direction.get(2)) )/ A;        
        
        float gama = (RayCaster.determinant(v1.get(0)-v2.get(0), v1.get(0)-ray.origin.get(0), ray.direction.get(0), 
                                            v1.get(1)-v2.get(1), v1.get(1)-ray.origin.get(1), ray.direction.get(1), 
                                            v1.get(2)-v2.get(2), v1.get(2)-ray.origin.get(2), ray.direction.get(2)) )/ A;
        
        float t = (RayCaster.determinant(v1.get(0)-v2.get(0), v1.get(0)-v3.get(0), v1.get(0)-ray.origin.get(0), 
                                         v1.get(1)-v2.get(1), v1.get(1)-v3.get(1), v1.get(1)-ray.origin.get(1), 
                                         v1.get(2)-v2.get(2), v1.get(2)-v3.get(2), v1.get(2)-ray.origin.get(2)) )/ A;

        if(beta+gama<1 && beta>0 && gama>0 && t>tmin && t<hit.t ){ //
            hit2.color = color;
            hit2.t = t;
            hit2.normal = RayCaster.crossProduct_3_3FloatResult(abVector, acVector);
            
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
