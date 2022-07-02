
//Sümeyra YANARDAĞ - 17050111029

package raycaster;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader; 
import java.util.Vector;
import javax.imageio.ImageIO;
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.JSONParser;


public class RayCaster {
   
    public static void main(String[] args) throws Exception {
    
        int resolution_x = 255;
        int resolution_y = 255;
        
        readJsonAndPaint(resolution_x, resolution_y, "scene1_diffuse.json", "scene1_diffuse.jpg");
        readJsonAndPaint(resolution_x, resolution_y, "scene2_ambient.json", "scene2_ambient.jpg");
        readJsonAndPaint(resolution_x, resolution_y, "scene3_perspective.json", "scene3_perspective.jpg");
        readJsonAndPaint(resolution_x, resolution_y, "scene4_plane.json", "scene4_plane.jpg");
        readJsonAndPaint(resolution_x, resolution_y, "scene5_sphere_triangle.json", "scene5_sphere_triangle.jpg");
        readJsonAndPaintTransformation(resolution_x, resolution_y, "scene6_squashed_sphere.json", "scene6_squashed_sphere.jpg");
        readJsonAndPaintTransformation(resolution_x, resolution_y, "scene7_squashed_rotated_sphere.json", "scene7_squashed_rotated_sphere.jpg");
       
    }
    
    public static void readJsonAndPaint(int resolution_x, int resolution_y, String jsonName, String outputName) throws Exception{
        JSONParser jsonparser = new JSONParser();
        FileReader reader = new FileReader(jsonName); 
        Object obj2 = jsonparser.parse(reader);
        JSONObject sceneJsonObj = (JSONObject)obj2;
   
        //getting orthocamera values
        JSONObject orthCamObj = (JSONObject) sceneJsonObj.get("orthocamera");
        JSONObject persCamObj = (JSONObject) sceneJsonObj.get("perspectivecamera");
        JSONArray center2;
        JSONArray direction2;
        JSONArray up2;
        OrthographicCamera orthCam = new OrthographicCamera();
        if(orthCamObj != null) { 
            
            center2 = (JSONArray) orthCamObj.get("center");
            direction2 = (JSONArray) orthCamObj.get("direction");
            up2 = (JSONArray) orthCamObj.get("up");                        

            for(int i=0; i<3; i++){ //3 integer values. it is constant
                //adding elements of center's to orthCam object's center, one by one.
                String str = center2.get(i).toString();
                int element = Integer.parseInt(str);
                orthCam.center.add(element);

                //adding elements of direction's to orthCam object's direction, one by one.
                str = direction2.get(i).toString();
                float element2 = Float.parseFloat(str);
                orthCam.direction.add(element2);

                //adding elements of up's to orthCam object's up, one by one.
                str = up2.get(i).toString();
                element = Integer.parseInt(str);
                orthCam.up.add(element);
            }

            //getting size in orthographicCamera
            float size = Float.valueOf(orthCamObj.get("size").toString());
            orthCam.size = size;
        }
        
        JSONArray center3;
        JSONArray direction3;
        JSONArray up3;
        PerspectiveCamera persCam = new PerspectiveCamera();
        if(persCamObj != null) { 
            
            center3 = (JSONArray) persCamObj.get("center");
            direction3 = (JSONArray) persCamObj.get("direction");
            up3 = (JSONArray) persCamObj.get("up");
            
            for(int i=0; i<3; i++){ //3 integer values. it is constant
                //adding elements of center's to persCam object's center, one by one.
                String str = center3.get(i).toString();
                int element = Integer.parseInt(str);
                persCam.center.add(element);

                //adding elements of direction's to persCam object's direction, one by one.
                str = direction3.get(i).toString();
                element = Integer.parseInt(str);
                persCam.direction.add(element);

                //adding elements of up's to persCam object's up, one by one.
                str = up3.get(i).toString();
                element = Integer.parseInt(str);
                persCam.up.add(element);
            }
            
            //getting angle in perspectiveCamera
            float angle = Float.valueOf(persCamObj.get("angle").toString());
            persCam.angle = angle*(float)Math.PI/180;
        }
        
        //getting background values
        JSONObject backGrObj = (JSONObject) sceneJsonObj.get("background");
        JSONArray backColor = (JSONArray) backGrObj.get("color");
        Vector<Float> backGrColor = new Vector<>();
        for(int i=0; i<3; i++){ //3 integer values. it is constant
            float element = Float.valueOf(backColor.get(i).toString());
            backGrColor.add(element);
        }
        JSONArray ambientArr = (JSONArray) backGrObj.get("ambient");
        Vector<Float> ambient = new Vector<>();
        for(int i=0; i<3; i++){ //3 integer values. it is constant
            float element = Float.valueOf(ambientArr.get(i).toString());
            ambient.add(element);
        }
        
        //getting light values
        JSONObject lightObj = (JSONObject) sceneJsonObj.get("light");
        JSONArray directionArr = (JSONArray) lightObj.get("direction");
        Vector<Float> lightDirection = new Vector<>();
        for(int i=0; i<3; i++){ //3 integer values. it is constant
            float element = Float.valueOf(directionArr.get(i).toString());
            lightDirection.add(element);
        }
        JSONArray lightColorArr = (JSONArray) lightObj.get("color");
        Vector<Float> lightColor = new Vector<>();
        for(int i=0; i<3; i++){ //3 integer values. it is constant
            float element = Float.valueOf(lightColorArr.get(i).toString());
            lightColor.add(element);
        }
        
        Vector<Float> L = multiplyFloatVectorWithFloatNumber(lightDirection, (float)-1.0);         
        //normalize L
        L = divideFloatVectorWithFloatNumber(L,  (float)Math.sqrt((Math.pow(L.get(0), 2)) + (Math.pow(L.get(1), 2)) + (Math.pow(L.get(2), 2))));        
        
        //getting group values
        JSONArray grouparr = (JSONArray) sceneJsonObj.get("group");
        
        Sphere[] sphObject = new Sphere[grouparr.size()];
        Plane[] planeObject = new Plane[grouparr.size()];
        Triangle[] triangleObject = new Triangle[grouparr.size()];
        int sphereCount = 0;
        int planeCount = 0; 
        int triangleCount = 0;
        
        for(int i=0; i<grouparr.size(); i++){
            JSONObject group = (JSONObject) grouparr.get(i);
            
            JSONObject sphereObj = (JSONObject) group.get("sphere");
            JSONObject planeObj = (JSONObject) group.get("plane");
            JSONObject triangleObj = (JSONObject) group.get("triangle");
            
            JSONArray center;
            float radius;
            JSONArray color;
            Vector<Integer> center4;
            Vector<Float> color4 = new Vector<>();
            
            Sphere sphere = new Sphere(color4);
            
            if(sphereObj != null){
                center = (JSONArray) sphereObj.get("center");
                radius = Float.valueOf(sphereObj.get("radius").toString());
                color = (JSONArray) sphereObj.get("color");
                
                center4 = new Vector<Integer>();
                color4 = new Vector<Float>();
                
                for(int j=0; j<3; j++){ //3 integer values. it is constant
                //converting JSONArray to Vector for sphere attributes
                int center5 = Integer.parseInt(center.get(j).toString());
                center4.add(center5);
                
                float color3 = Float.parseFloat(color.get(j).toString());
                color4.add(color3);
                }
                sphere = new Sphere(radius, center4, color4);
                sphObject[sphereCount] = sphere;
                sphereCount++;
            }

            
            //plane
            JSONArray normalPlane;
            float offsetPlane;
            JSONArray colorPlane;
            Vector<Float> normal;
            Vector<Float> color5 = new Vector<>();
            Plane plane = new Plane(color5);
            if(planeObj != null){
                normalPlane = (JSONArray) planeObj.get("normal");
                offsetPlane = Float.valueOf(planeObj.get("offset").toString());
                offsetPlane = offsetPlane * -1;       ///////// diğer türlü plane ters çıkıyor
                colorPlane = (JSONArray) planeObj.get("color");
                
                normal = new Vector<Float>();
                color5 = new Vector<Float>();
                
                for(int j=0; j<3; j++){ //3 integer values. it is constant
                //converting JSONArray to Vector for sphere attributes
                float normal1 = Float.parseFloat(normalPlane.get(j).toString());
                normal.add(normal1);
                
                float color3 = Float.parseFloat(colorPlane.get(j).toString());
                color5.add(color3);
                }
                plane = new Plane(offsetPlane, normal, color5, color5);
                planeObject[planeCount] = plane;
                planeCount++;
            }
            
            //triangle
            JSONArray v1Arr;
            JSONArray v2Arr;
            JSONArray v3Arr;
            JSONArray colorTriangle;
            Vector<Integer> v1;
            Vector<Integer> v2;
            Vector<Integer> v3;
            Vector<Float> color6 = new Vector<>();
            Triangle triangle = new Triangle(color6);
            if(triangleObj != null){
                v1Arr = (JSONArray) triangleObj.get("v1");
                v2Arr = (JSONArray) triangleObj.get("v2");
                v3Arr = (JSONArray) triangleObj.get("v3");
                colorTriangle = (JSONArray) triangleObj.get("color");
                
                v1 = new Vector<Integer>();
                v2 = new Vector<Integer>();
                v3 = new Vector<Integer>();
                color6 = new Vector<Float>();
                
                for(int j=0; j<3; j++){ //3 integer values. it is constant
                //converting JSONArray to Vector for sphere attributes
                int vv = Integer.parseInt(v1Arr.get(j).toString());
                v1.add(vv);
                
                vv = Integer.parseInt(v2Arr.get(j).toString());
                v2.add(vv);
                
                vv = Integer.parseInt(v3Arr.get(j).toString());
                v3.add(vv);
                
                float color3 = Float.parseFloat(colorTriangle.get(j).toString());
                color6.add(color3);
                }
                triangle = new Triangle(color6, v1, v2, v3);
                
            triangleObject[triangleCount] = triangle;
            triangleCount++;
            }
        }
        
        Object3D groupForAllObjects[] = new Object3D[grouparr.size()];
        int addedSphere = 0;
        int addedPlane = 0;
        int addedTriangle = 0;
        for(int k=0; k<grouparr.size(); k++){
            if(addedSphere<sphereCount){
                groupForAllObjects[k] = sphObject[addedSphere];
                addedSphere++;
            }
            else if(addedPlane<planeCount){
                groupForAllObjects[k] = planeObject[addedPlane];
                addedPlane++;
            }
            else if(addedTriangle<triangleCount){
                groupForAllObjects[k] = triangleObject[addedTriangle];
                addedTriangle++;
            }
        }
        
        Group groupObj = new Group(groupForAllObjects, backGrColor);//
        
        File image = new File(outputName);
        BufferedImage buffer = new BufferedImage(resolution_x, resolution_y, BufferedImage.TYPE_INT_RGB); 
        for(int i=0; i<resolution_x; i++){
            for(int j=0; j<resolution_y; j++){
                float x = ((float)i/resolution_x);
                float y = ((float)j/resolution_y);
                
                Ray ray = new Ray();
                if(orthCamObj != null){
                    ray = orthCam.generateRay(x, y);
                }
                else if(persCamObj != null){
                    ray = persCam.generateRay(x, y);
                }
           
                float tmin = (float)0.00001;
                
                Vector<Float> tempNormal = new Vector<Float>();
                tempNormal.add((float)0.0);
                tempNormal.add((float)0.0);
                tempNormal.add((float)0.0);
                Hit hit = new Hit(100000, backGrColor, tempNormal);
                hit = groupObj.intersect(ray, hit, tmin);
                
                if(hit != null){
                    Color rgb = 
                    //new Color((hit.color.get(0)), hit.color.get(1), hit.color.get(2));
                            new Color(
                            (int)Math.min((((ambient.get(0) * hit.color.get(0))
                                +   ( Math.max(dotProduct_3_3Float(L, hit.normal), 0)
                                *   hit.color.get(0) * lightColor.get(0)) 
                                    ) *255), 255) ,
                            (int)Math.min((((ambient.get(1) * hit.color.get(1))
                                +   ( Math.max(dotProduct_3_3Float(L, hit.normal), 0)
                                *   hit.color.get(1) * lightColor.get(1))
                                    ) *255), 255) ,
                            (int)Math.min((((ambient.get(2) * hit.color.get(2))
                                +   ( Math.max(dotProduct_3_3Float(L, hit.normal), 0)
                                *   hit.color.get(2) * lightColor.get(2))
                                    ) *255), 255) 
                            );
                    buffer.setRGB((int)i, 255-(int)j-1, rgb.getRGB()); //255-(int)j-1
                }    
            }
        }
        
        try{
            ImageIO.write(buffer, "JPG", image);
        }catch(Exception e){
            System.out.println(e);
            System.exit(1);
        }
    }
    
    public static void readJsonAndPaintTransformation(int resolution_x, int resolution_y, String jsonName, String outputName) throws Exception{
        JSONParser jsonparser = new JSONParser();
        FileReader reader = new FileReader(jsonName);  //scene7_squashed_rotated_sphere.json  scene6_squashed_sphere.json
        Object obj2 = jsonparser.parse(reader);
        JSONObject sceneJsonObj = (JSONObject)obj2;
   
        //getting orthocamera values
        JSONObject orthCamObj = (JSONObject) sceneJsonObj.get("orthocamera");
        JSONObject persCamObj = (JSONObject) sceneJsonObj.get("perspectivecamera");
        JSONArray center2;
        JSONArray direction2;
        JSONArray up2;
        OrthographicCamera orthCam = new OrthographicCamera();
        if(orthCamObj != null) { 
            
            center2 = (JSONArray) orthCamObj.get("center");
            direction2 = (JSONArray) orthCamObj.get("direction");
            up2 = (JSONArray) orthCamObj.get("up");                        

            for(int i=0; i<3; i++){ //3 integer values. it is constant
                //adding elements of center's to orthCam object's center, one by one.
                String str = center2.get(i).toString();
                int element = Integer.parseInt(str);
                orthCam.center.add(element);

                //adding elements of direction's to orthCam object's direction, one by one.
                str = direction2.get(i).toString();
                float element2 = Float.parseFloat(str);
                orthCam.direction.add(element2);

                //adding elements of up's to orthCam object's up, one by one.
                str = up2.get(i).toString();
                element = Integer.parseInt(str);
                orthCam.up.add(element);
            }

            //getting size in orthographicCamera
            float size = Float.valueOf(orthCamObj.get("size").toString());
            orthCam.size = size;
        }
        
        JSONArray center3;
        JSONArray direction3;
        JSONArray up3;
        PerspectiveCamera persCam = new PerspectiveCamera();
        if(persCamObj != null) { 
            
            center3 = (JSONArray) persCamObj.get("center");
            direction3 = (JSONArray) persCamObj.get("direction");
            up3 = (JSONArray) persCamObj.get("up");
            
            for(int i=0; i<3; i++){ //3 integer values. it is constant
                //adding elements of center's to persCam object's center, one by one.
                String str = center3.get(i).toString();
                int element = Integer.parseInt(str);
                persCam.center.add(element);

                //adding elements of direction's to persCam object's direction, one by one.
                str = direction3.get(i).toString();
                element = Integer.parseInt(str);
                persCam.direction.add(element);

                //adding elements of up's to persCam object's up, one by one.
                str = up3.get(i).toString();
                element = Integer.parseInt(str);
                persCam.up.add(element);
            }
            
            //getting angle in perspectiveCamera
            float angle = Float.valueOf(persCamObj.get("angle").toString());
            persCam.angle = angle*(float)Math.PI/180;

        }
        
        //getting background values
        JSONObject backGrObj = (JSONObject) sceneJsonObj.get("background");
        JSONArray backColor = (JSONArray) backGrObj.get("color");
        Vector<Float> backGrColor = new Vector<>();
        for(int i=0; i<3; i++){ //3 integer values. it is constant
            float element = Float.valueOf(backColor.get(i).toString());
            backGrColor.add(element);
        }
        JSONArray ambientArr = (JSONArray) backGrObj.get("ambient");
        Vector<Float> ambient = new Vector<>();
        for(int i=0; i<3; i++){ //3 integer values. it is constant
            float element = Float.valueOf(ambientArr.get(i).toString());
            ambient.add(element);
        }
        
        //getting light values
        JSONObject lightObj = (JSONObject) sceneJsonObj.get("light");
        JSONArray directionArr = (JSONArray) lightObj.get("direction");
        Vector<Float> lightDirection = new Vector<>();
        for(int i=0; i<3; i++){ //3 integer values. it is constant
            float element = Float.valueOf(directionArr.get(i).toString());
            lightDirection.add(element);
        }
        JSONArray lightColorArr = (JSONArray) lightObj.get("color");
        Vector<Float> lightColor = new Vector<>();
        for(int i=0; i<3; i++){ //3 integer values. it is constant
            float element = Float.valueOf(lightColorArr.get(i).toString());
            lightColor.add(element);
        }
        
        Vector<Float> L = multiplyFloatVectorWithFloatNumber(lightDirection, (float)-1.0); 
        //normalize L
        L = divideFloatVectorWithFloatNumber(L, (float)Math.sqrt((Math.pow(L.get(0), 2)) + (Math.pow(L.get(1), 2)) + (Math.pow(L.get(2), 2))));        
        
        //getting group values
        JSONArray grouparr = (JSONArray) sceneJsonObj.get("group");       
        Sphere sphObject = new Sphere(backGrColor);
        Transformation transformation = new Transformation(backGrColor);
        
        Vector<Float> scale = new Vector<>();
        float zrotate = 0;
        
        for(int i=0; i<grouparr.size(); i++){
            JSONObject group = (JSONObject) grouparr.get(i);
            JSONObject transformObj = (JSONObject) group.get("transform");
            JSONArray transformationsArr = (JSONArray) transformObj.get("transformations");
            
            if(transformationsArr.size() > 1){
                JSONObject transformations = (JSONObject) transformationsArr.get(i);
                zrotate = Float.valueOf(transformations.get("zrotate").toString());
                zrotate = zrotate*(float)Math.PI/180;
                i++;
            }
            
            JSONObject transformations = (JSONObject) transformationsArr.get(i); //i+1
            JSONArray scaleArr = (JSONArray) transformations.get("scale");
            for(int k=0; k<3; k++){ //3 integer values. it is constant
                float element = Float.valueOf(scaleArr.get(k).toString());
                scale.add(element);
            }
            
            JSONObject objectObj = (JSONObject) transformObj.get("object");
            JSONObject sphereObj = (JSONObject) objectObj.get("sphere");
            
            JSONArray center;
            float radius;
            JSONArray color;
            Vector<Integer> center4;
            Vector<Float> color4 = new Vector<>();
            
            Sphere sphere = new Sphere(color4);
            
            if(sphereObj != null){
                center = (JSONArray) sphereObj.get("center");
                radius = Float.valueOf(sphereObj.get("radius").toString());
                color = (JSONArray) sphereObj.get("color");
                
                center4 = new Vector<Integer>();
                color4 = new Vector<Float>();
                
                for(int j=0; j<3; j++){ //3 integer values. it is constant
                //converting JSONArray to Vector for sphere attributes
                int center5 = Integer.parseInt(center.get(j).toString());
                center4.add(center5);
                
                float color3 = Float.parseFloat(color.get(j).toString());
                color4.add(color3);
                }
                
                sphere = new Sphere(radius, center4, color4);
                sphObject = sphere;
                transformation.object = sphere;
                if(zrotate != 0){
                    transformation.m = transformationMatrixCalculation(scale, zrotate); ///++++++++++++++++++
                }    
                else if(zrotate == 0){
                    transformation.m = transformationMatrixCalculation(scale);
                }
            }
        }
        
        File image = new File(outputName); //scene7_squashed_rotated_sphere scene6_squashed_sphere
        BufferedImage buffer = new BufferedImage(resolution_x, resolution_y, BufferedImage.TYPE_INT_RGB); 
        for(int i=0; i<resolution_x; i++){
            for(int j=0; j<resolution_y; j++){
                float x = ((float)i/resolution_x);
                float y = ((float)j/resolution_y);
                
                Ray ray = new Ray();
                if(orthCamObj != null){
                    ray = orthCam.generateRay(x, y);
                }
                else if(persCamObj != null){
                    ray = persCam.generateRay(x, y);
                }
           
                float tmin = (float)0.00001;
                
                Vector<Float> tempNormal = new Vector<Float>();
                tempNormal.add((float)0.0);
                tempNormal.add((float)0.0);
                tempNormal.add((float)0.0);
                Hit hit = new Hit(100000, backGrColor, tempNormal);
                hit = transformation.intersect(ray, hit, tmin);//groupObj.intersect(ray, hit, tmin);
                //hit.normal = transformDirection(hit.normal, inverse(transformation.m));
                
                if(hit != null){
                    //System.out.println("hit:" );
                    Color rgb = 
                    //new Color((hit.color.get(0)), hit.color.get(1), hit.color.get(2));
                            new Color(
                            (int)Math.min((((ambient.get(0) * hit.color.get(0))
                                +   ( Math.max(dotProduct_3_3Float(L, hit.normal), 0)
                                *   hit.color.get(0) * lightColor.get(0)) 
                                    ) *255), 255) ,
                            (int)Math.min((((ambient.get(1) * hit.color.get(1))
                                +   ( Math.max(dotProduct_3_3Float(L, hit.normal), 0)
                                *   hit.color.get(1) * lightColor.get(1))
                                    ) *255), 255) ,
                            (int)Math.min((((ambient.get(2) * hit.color.get(2))
                                +   ( Math.max(dotProduct_3_3Float(L, hit.normal), 0)
                                *   hit.color.get(2) * lightColor.get(2))
                                    ) *255), 255) 
                            );
                    buffer.setRGB((int)i, 255-(int)j-1, rgb.getRGB()); 
                }    
            }
        }
        
        try{
            ImageIO.write(buffer, "JPG", image);
        }catch(Exception e){
            System.out.println(e);
            System.exit(1);
        }
    }
    
    public static Vector<Integer> crossProduct_3_3(Vector<Integer> vec1, Vector<Integer> vec2){
            Vector<Integer> result = new Vector<Integer>();
            result.add((int)vec1.get(1)*vec2.get(2) - vec1.get(2)*vec2.get(1));
            result.add((int)vec1.get(2)*vec2.get(0) - vec1.get(0)*vec2.get(2));
            result.add((int)vec1.get(0)*vec2.get(1) - vec1.get(1)*vec2.get(0));
            return result;
    }
    
    public static Vector<Float> crossProduct_3_3FloatResult(Vector<Integer> vec1, Vector<Integer> vec2){
            Vector<Float> result = new Vector<Float>();
            result.add((float)vec1.get(1)*vec2.get(2) - vec1.get(2)*vec2.get(1));
            result.add((float)vec1.get(2)*vec2.get(0) - vec1.get(0)*vec2.get(2));
            result.add((float)vec1.get(0)*vec2.get(1) - vec1.get(1)*vec2.get(0));
            return result;
    }
    
    public static Vector<Float> crossProduct_3_3Float_Int(Vector<Float> vec1, Vector<Integer> vec2){
            Vector<Float> result = new Vector<Float>();
            result.add((float)vec1.get(1)*vec2.get(2) - vec1.get(2)*vec2.get(1));
            result.add((float)vec1.get(2)*vec2.get(0) - vec1.get(0)*vec2.get(2));
            result.add((float)vec1.get(0)*vec2.get(1) - vec1.get(1)*vec2.get(0));
            return result;
    }
    
    public static int dotProduct_3_3(Vector<Integer> vec1, Vector<Integer> vec2){
        return vec1.get(0)*vec2.get(0) + vec1.get(1)*vec2.get(1) + vec1.get(2)*vec2.get(2);
    }
    
    public static float dotProduct_3_3Float(Vector<Float> vec1, Vector<Float> vec2){
        return vec1.get(0)*vec2.get(0) + vec1.get(1)*vec2.get(1) + vec1.get(2)*vec2.get(2);
    }
    
    public static float dotProduct_3_3IntFloat(Vector<Integer> vec1, Vector<Float> vec2){
        return vec1.get(0)*vec2.get(0) + vec1.get(1)*vec2.get(1) + vec1.get(2)*vec2.get(2);
    }
    
    public static Vector<Integer> multiplyVectorWithNumber(Vector<Integer> vec1, int x){
        Vector<Integer> result = new Vector<Integer>();
        result.add(vec1.get(0)*x);
        result.add(vec1.get(1)*x);
        result.add(vec1.get(2)*x);
        return result;
    }
    
    public static Vector<Float> multiplyVectorWithNumber(Vector<Integer> vec1, float x){
        Vector<Float> result = new Vector<Float>();
        result.add(vec1.get(0)*x);
        result.add(vec1.get(1)*x);
        result.add(vec1.get(2)*x);
        return result;
    }
    
    public static Vector<Integer> sumVectors_3_3(Vector<Integer> vec1, Vector<Integer> vec2){
        Vector<Integer> result = new Vector<Integer>();
        result.add(vec1.get(0) + vec2.get(0));
        result.add(vec1.get(1) + vec2.get(1));
        result.add(vec1.get(2) + vec2.get(2));
        return result;
    }
    
    public static Vector<Float> sumVectors_3_3Float(Vector<Float> vec1, Vector<Float> vec2){
        Vector<Float> result = new Vector<Float>();
        result.add(vec1.get(0) + vec2.get(0));
        result.add(vec1.get(1) + vec2.get(1));
        result.add(vec1.get(2) + vec2.get(2));
        return result;
    }
    
    public static Vector<Float> sumVectors_3_3Int_Float(Vector<Integer> vec1, Vector<Float> vec2){
        Vector<Float> result = new Vector<Float>();
        result.add(vec1.get(0) + vec2.get(0));
        result.add(vec1.get(1) + vec2.get(1));
        result.add(vec1.get(2) + vec2.get(2));
        return result;
    }
    
    public static Vector<Integer> subtractVectors_3_3Int_Int(Vector<Integer> vec1, Vector<Integer> vec2){
        Vector<Integer> result = new Vector<Integer>();
        result.add(vec1.get(0) - vec2.get(0));
        result.add(vec1.get(1) - vec2.get(1));
        result.add(vec1.get(2) - vec2.get(2));
        return result;
    }
    
    public static Vector<Float> subtractVectors_3_3Float_Int(Vector<Float> vec1, Vector<Integer> vec2){
        Vector<Float> result = new Vector<Float>();
        result.add(vec1.get(0) - vec2.get(0));
        result.add(vec1.get(1) - vec2.get(1));
        result.add(vec1.get(2) - vec2.get(2));
        return result;
    }
    
    public static Vector<Float> subtractVectors_3_3Int_Float(Vector<Integer> vec1, Vector<Float> vec2){
        Vector<Float> result = new Vector<Float>();
        result.add(vec1.get(0) - vec2.get(0));
        result.add(vec1.get(1) - vec2.get(1));
        result.add(vec1.get(2) - vec2.get(2));
        return result;
    }
    
    public static Vector<Float> subtractVectors_3_3Float_Float(Vector<Float> vec1, Vector<Float> vec2){
        Vector<Float> result = new Vector<Float>();
        result.add(vec1.get(0) - vec2.get(0));
        result.add(vec1.get(1) - vec2.get(1));
        result.add(vec1.get(2) - vec2.get(2));
        return result;
    }
    
    public static float multiplyVectors_3_3Float(Vector<Float> vec1, Vector<Float> vec2){
        return vec1.get(0)*vec2.get(0) + vec1.get(1)*vec2.get(1) + vec1.get(2)*vec2.get(2);
    }
    
    public static Vector<Float> divideFloatVectorWithFloatNumber(Vector<Float> vec1, float x){
        Vector<Float> result = new Vector<Float>();
        result.add(vec1.get(0)/x);
        result.add(vec1.get(1)/x);
        result.add(vec1.get(2)/x);
        return result;
    }
    
    public static Vector<Float> divideIntegerVectorWithFloatNumber(Vector<Integer> vec1, float x){
        Vector<Float> result = new Vector<Float>();
        result.add((float)vec1.get(0)/x);
        result.add((float)vec1.get(1)/x);
        result.add((float)vec1.get(2)/x);
        return result;
    }
    
    public static Vector<Float> multiplyFloatVectorWithFloatNumber(Vector<Float> vec1, float x){
        Vector<Float> result = new Vector<Float>();
        result.add(vec1.get(0)*x);
        result.add(vec1.get(1)*x);
        result.add(vec1.get(2)*x);
        return result;
    }
    
    public static float determinant(float a, float b, float c,
                                    float d, float e, float f,
                                    float g, float h, float i){
        float result;
        result = ( (a*((e*i) - (f*h))) - (b*((d*i) - (f*g))) + (c*((d*h) - (e*g))));
        return result;
    }
  
    public static float[][] transformationMatrixCalculation(Vector<Float> scale){
        float[][] m = new float[4][4];
        m[0][0] = scale.get(0);
            m[0][1] = 0;
                m[0][2] = 0;
                    m[0][3] = 0;
        m[1][0] = 0;
            m[1][1] = scale.get(1);
                m[1][2] = 0;
                    m[1][3] = 0;
        m[2][0] = 0;
            m[2][1] = 0;
                m[2][2] = scale.get(2);
                    m[2][3] = 0;
        m[3][0] = 0;
            m[3][1] = 0;
                m[3][2] = 0;
                    m[3][3] = 1;
       
        return m;
    }
    
    public static float[][] transformationMatrixCalculation(Vector<Float> scale, float zrotateAngle){
        //zrotateAngle = zrotateAngle*(float)Math.PI/180;
        float[][] m = new float[4][4];
        m[0][0] = scale.get(0) * (float)Math.cos(zrotateAngle);
            m[0][1] = -(float)Math.sin(zrotateAngle) * scale.get(1);
                m[0][2] = 0;
                    m[0][3] = 0;
        m[1][0] = (float)Math.sin(zrotateAngle) * scale.get(0);
            m[1][1] = scale.get(1) * (float)Math.cos(zrotateAngle);
                m[1][2] = 0;
                    m[1][3] = 0;
        m[2][0] = 0;
            m[2][1] = 0;
                m[2][2] = scale.get(2);
                    m[2][3] = 0;
        m[3][0] = 0;
            m[3][1] = 0;
                m[3][2] = 0;
                    m[3][3] = 1;
        
        return m;
    }
    
    public static Vector<Float> transformPoint(Vector<Float> point, float[][] tM){ //tM means transformationMatrix
        Vector<Float> transformedPoint = new Vector<Float>();
        transformedPoint.add((point.get(0)*tM[0][0]) + (point.get(1)*tM[0][1]) + (point.get(2)*tM[0][2]) + tM[0][3]);
        transformedPoint.add((point.get(0)*tM[1][0]) + (point.get(1)*tM[1][1]) + (point.get(2)*tM[1][2]) + tM[1][3]);
        transformedPoint.add((point.get(0)*tM[2][0]) + (point.get(1)*tM[2][1]) + (point.get(2)*tM[2][2]) + tM[2][3]);
        transformedPoint.add((float)1);
        return transformedPoint;
    }
    
    public static Vector<Float> transformDirection(Vector<Float> direction, float[][] tM){ //tM means transformationMatrix
        Vector<Float> transformedDirection = new Vector<Float>();
        transformedDirection.add((direction.get(0)*tM[0][0]) + (direction.get(1)*tM[0][1]) + (direction.get(2)*tM[0][2]));
        transformedDirection.add((direction.get(0)*tM[1][0]) + (direction.get(1)*tM[1][1]) + (direction.get(2)*tM[1][2]));
        transformedDirection.add((direction.get(0)*tM[2][0]) + (direction.get(1)*tM[2][1]) + (direction.get(2)*tM[2][2]));
        transformedDirection.add((float)0);
        return transformedDirection;
    }
    

    //find adjoint and inverse of a matrix	
    static final int N = 4;

    // Function to get cofactor of A[p][q] in temp[][]. n is current
    // dimension of A[][]
    static void getCofactor(float A[][], float temp[][], float p, float q, float n){
            int i = 0, j = 0;
            for (int row = 0; row < n; row++)
            {
                    for (int col = 0; col < n; col++)
                    {
                            if (row != p && col != q)
                            {
                                    temp[i][j++] = A[row][col];
                                    if (j == n - 1)
                                    {
                                            j = 0;
                                            i++;
                                    }
                            }
                    }
            }
    }

    /* Recursive function for finding determinant of matrix.
    n is current dimension of A[][]. */
    static float determinant(float A[][], float n){
            float D = 0; // Initialize result
            // Base case : if matrix contains single element
            if (n == 1)
                    return A[0][0];
            float [][]temp = new float[N][N]; // To store cofactors
            float sign = 1; // To store sign multiplier
            // Iterate for each element of first row
            for (int f = 0; f < n; f++)
            {
                    // Getting Cofactor of A[0][f]
                    getCofactor(A, temp, 0, f, n);
                    D += sign * A[0][f] * determinant(temp, n - 1);
                    // terms are to be added with alternate sign
                    sign = -sign;
            }
            return D;
    }

    // Function to get adjoint of A[N][N] in adj[N][N].
    static void adjoint(float A[][],float [][]adj){
            if (N == 1)
            {
                    adj[0][0] = 1;
                    return;
            }
            
            // temp is used to store cofactors of A[][]
            float sign = 1;
            float [][]temp = new float[N][N];
            
            for (int i = 0; i < N; i++)
            {
                    for (int j = 0; j < N; j++)
                    {
                            // Get cofactor of A[i][j]
                            getCofactor(A, temp, i, j, N);

                            // sign of adj[j][i] positive if sum of row
                            // and column indexes is even.
                            sign = ((i + j) % 2 == 0)? 1: -1;

                            // Interchanging rows and columns to get the
                            // transpose of the cofactor matrix
                            adj[j][i] = (sign)*(determinant(temp, N-1));
                    }
            }
    }

    static float [][] inverse(float A[][]){
            // Find determinant of A[][]
            float [][]inverse = new float[N][N];
            float det = determinant(A, N);
            if (det == 0){
                    System.out.print("Singular matrix, can't find its inverse");
                    //return false;
            }

            // Find adjoint
            float [][]adj = new float[N][N];
            adjoint(A, adj);

            // Find Inverse using formula "inverse(A) = adj(A)/det(A)"
            for (int i = 0; i < N; i++)
                    for (int j = 0; j < N; j++)
                            inverse[i][j] = adj[i][j]/(float)det;

            return inverse;
    }
    
    static float[][] transpose(float A[][]){
        float B[][] = new float[4][4];
        int i, j;
        for (i = 0; i < 4; i++)
            for (j = 0; j < 4; j++)
                B[i][j] = A[j][i];
        return B;
    }

}
