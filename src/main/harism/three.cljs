(ns harism.three
  (:require 
   ["three" :refer [Scene 
                    PerspectiveCamera
                    TorusKnotGeometry 
                    MeshNormalMaterial
                    Mesh
                    WebGLRenderer]]
   ["three/addons/controls/OrbitControls.js" :refer [OrbitControls]]))

(defn scene []
  (Scene.))

(defn camera [fov aspect near far]
  (PerspectiveCamera. fov aspect near far))

(defn torus-knot-geometry 
  [{:keys [radius tube tubular-segments radial-segments p q]}]
  (TorusKnotGeometry. 
    radius 
    tube 
    tubular-segments 
    radial-segments 
    (or p 2) 
    (or q 3)))
  
(defn mesh-material []
 (MeshNormalMaterial.))

(defn mesh [geometry material]
  (Mesh. geometry material))

(defn webgl-renderer [canvas-id]
  (WebGLRenderer. #js {:canvas (js/document.getElementById canvas-id)
                       :antialias true
                       :alpha true}))

(defn renderer [width height canvas-id]
  (doto (webgl-renderer canvas-id)
    (.setSize width height)
    (.setPixelRatio (.-devicePixelRatio js/window))))

(defn orbit-controls [camera elem]
  (OrbitControls. camera elem))

(defn animate! [orbit renderer scene camera]
  (.update orbit)
  (.render renderer scene camera)
  (js/requestAnimationFrame 
    #(animate! orbit renderer scene camera))) 
