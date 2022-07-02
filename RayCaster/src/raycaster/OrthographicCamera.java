
package raycaster;

import java.util.Vector;

public class OrthographicCamera extends Camera{
        
    Vector<Integer> center = new Vector<Integer>();
    Vector<Float> direction = new Vector<Float>();
    Vector<Integer> up = new Vector<Integer>();
    float size;

    public OrthographicCamera() {
    }
    
    public OrthographicCamera(Vector<Integer> center, Vector<Float> direction, Vector<Integer> up, float size) {
        this.center = center;
        this.direction = direction;
        this.up = up;
        this.size = size;
    }

    public Vector<Integer> getCenter() {
        return center;
    }

    public Vector<Float> getDirection() {
        return direction;
    }

    public Vector<Integer> getUp() {
        return up;
    }

    public float getSize() {
        return size;
    }

    public void setCenter(Vector<Integer> center) {
        this.center = center;
    }

    public void setDirection(Vector<Float> direction) {
        this.direction = direction;
    }

    public void setUp(Vector<Integer> up) {
        this.up = up;
    }

    public void setSize(float size) {
        this.size = size;
    }

    @Override
    Ray generateRay(float x, float y) {
        OrthographicCamera orthCam = new OrthographicCamera(center, direction, up, size);
        Vector<Float> horizontal = new Vector<Float>();
        horizontal = RayCaster.crossProduct_3_3Float_Int(orthCam.direction, orthCam.up);
        Vector<Float> origin = new Vector<Float>();
        origin = RayCaster.sumVectors_3_3Float(
                            RayCaster.sumVectors_3_3Int_Float(orthCam.center, 
                                    RayCaster.multiplyFloatVectorWithFloatNumber(horizontal, (float)((x-0.5)*size))) , 
                            RayCaster.multiplyVectorWithNumber(orthCam.up, (float)((y-0.5)*size)));
        
        Ray ray = new Ray(origin, orthCam.direction);
        
        return ray;
    }
    
    
    
}
