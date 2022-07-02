
package raycaster;

import java.util.Vector;

public class PerspectiveCamera extends Camera{
    
    Vector<Integer> center = new Vector<Integer>();
    Vector<Integer> direction = new Vector<Integer>();
    Vector<Integer> up = new Vector<Integer>();
    float angle;

    public PerspectiveCamera() {
    }

    public PerspectiveCamera(Vector<Integer> center, Vector<Integer> direction, Vector<Integer> up, float angle) {
        this.center = center;
        this.direction = direction;
        this.up = up;
        this.angle = angle;
    }

    public Vector<Integer> getCenter() {
        return center;
    }

    public void setCenter(Vector<Integer> center) {
        this.center = center;
    }

    public Vector<Integer> getDirection() {
        return direction;
    }

    public void setDirection(Vector<Integer> direction) {
        this.direction = direction;
    }

    public Vector<Integer> getUp() {
        return up;
    }

    public void setUp(Vector<Integer> up) {
        this.up = up;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
    
    
    @Override
    Ray generateRay(float x, float y) {
        PerspectiveCamera persCam = new PerspectiveCamera(center, direction, up, angle);
        Vector<Float> horizontal = new Vector<Float>();
        horizontal = RayCaster.crossProduct_3_3FloatResult(persCam.direction, persCam.up);
        //normalize the horizontal vector
        horizontal = RayCaster.divideFloatVectorWithFloatNumber(horizontal, 
                        (float)Math.sqrt((Math.pow(horizontal.get(0), 2)) + (Math.pow(horizontal.get(1), 2)) + (Math.pow(horizontal.get(2), 2)))); 
        
        Vector<Float> leftCorner = new Vector<Float>();
        Vector<Float> rightCorner = new Vector<Float>();
        
        leftCorner = RayCaster.subtractVectors_3_3Int_Float(
                        RayCaster.sumVectors_3_3(center, direction), 
                        RayCaster.multiplyFloatVectorWithFloatNumber(horizontal, (float)Math.tan(angle/2))); //angle/2
        leftCorner.setElementAt(leftCorner.get(0), 1);
        rightCorner = RayCaster.sumVectors_3_3Int_Float(
                        RayCaster.sumVectors_3_3(center, direction), 
                        RayCaster.multiplyFloatVectorWithFloatNumber(horizontal, (float)Math.tan(angle/2)));   //angle/2
        rightCorner.setElementAt(leftCorner.get(1), 1);
        
        Vector<Float> leftButtomCorner = new Vector<>();
        leftButtomCorner.add(leftCorner.get(0));
        leftButtomCorner.add(leftCorner.get(1) - (leftCorner.get(0) - rightCorner.get(0)));
        leftButtomCorner.add(leftCorner.get(2));
        
        //targetPoint = leftCorner + (x)*(rightCorner - leftCorner) + (y)*(leftButtomCorner - leftCorner)
        Vector<Float> targetPoint = new Vector<Float>();
        targetPoint = RayCaster.sumVectors_3_3Float( 
                            RayCaster.sumVectors_3_3Float(leftCorner,
                                RayCaster.multiplyFloatVectorWithFloatNumber(RayCaster.subtractVectors_3_3Float_Float(rightCorner, leftCorner), ((float)(x))))   ////****x-0.5
                                ,RayCaster.multiplyFloatVectorWithFloatNumber(RayCaster.subtractVectors_3_3Float_Float(leftButtomCorner, leftCorner), (float)(y)));  
        
        
                

        Vector<Float> centerFloatFormat = new Vector<Float>();
        centerFloatFormat.add((float)center.get(0)); 
        centerFloatFormat.add((float)center.get(1)); 
        centerFloatFormat.add((float)center.get(2)); 
        
        Ray ray = new Ray(centerFloatFormat, RayCaster.subtractVectors_3_3Float_Int(targetPoint, this.center));
        
        //normalize the direction
        ray.direction = RayCaster.divideFloatVectorWithFloatNumber(ray.direction, 
                        (float)Math.sqrt((Math.pow(ray.direction.get(0), 2)) + (Math.pow(ray.direction.get(1), 2)) + (Math.pow(ray.direction.get(2), 2))));        
         
        return ray;
    }
    
}
